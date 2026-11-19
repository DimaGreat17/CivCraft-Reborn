#!/usr/bin/env python3
"""Convert an authored Minecraft Java Structure Block .nbt into a verified
CivCraft blueprint manifest (JSON).

Only Python standard library is required; it works on Windows.
No untrusted pickle, eval, code execution, or network dependencies.

Usage:
    python scripts/import_structure_nbt.py camp_heart_v2.nbt --id camp_heart_v2 \
        --output src/main/resources/data/civcraft_reborn/blueprints/camp_heart_v2.json

The output is a *staging format*; this script does not switch existing in-game
blueprints or reassign confirmed construction projects.
"""
from __future__ import annotations

import argparse
import gzip
import json
import re
import struct
from collections import Counter
from pathlib import Path
from typing import Any

MAX_FILE_BYTES = 8_000_000
MAX_UNCOMPRESSED = 32_000_000
MAX_CELLS = 100_000
NAMESPACE_ID = re.compile(r"^[a-z0-9_.-]+:[a-z0-9_./-]+$")
SCHEME_ID = re.compile(r"^[a-z0-9_/-]+$")


class InvalidStructure(ValueError):
    pass


class NBTReader:
    def __init__(self, data: bytes):
        self.data = memoryview(data)
        self.at = 0

    def take(self, count: int) -> bytes:
        if count < 0 or count > len(self.data) - self.at:
            raise InvalidStructure("Truncated or invalid NBT payload")
        value = bytes(self.data[self.at:self.at+count])
        self.at += count
        return value

    def number(self, fmt: str) -> int | float:
        return struct.unpack(">" + fmt, self.take(struct.calcsize(fmt)))[0]

    def string(self) -> str:
        length = self.number("H")
        if length > 32767:
            raise InvalidStructure("Overlong NBT string")
        return self.take(length).decode("utf-8")

    def payload(self, tag: int, depth: int = 0) -> Any:
        if depth > 48:
            raise InvalidStructure("NBT nesting too deep")
        if tag == 1: return self.number("b")
        if tag == 2: return self.number("h")
        if tag == 3: return self.number("i")
        if tag == 4: return self.number("q")
        if tag == 5: return self.number("f")
        if tag == 6: return self.number("d")
        if tag == 7:
            length = self.number("i")
            if length < 0 or length > MAX_UNCOMPRESSED:
                raise InvalidStructure("Invalid byte array length")
            return list(self.take(length))
        if tag == 8: return self.string()
        if tag == 9:
            element = self.number("B")
            length = self.number("i")
            if length < 0 or length > MAX_CELLS or (element == 0 and length > 0):
                raise InvalidStructure("Invalid list type or length")
            return [self.payload(element, depth + 1) for _ in range(length)]
        if tag == 10:
            result = {}
            while True:
                nested = self.number("B")
                if nested == 0: break
                key = self.string()
                if key in result:
                    raise InvalidStructure("Duplicate NBT compound key: " + key)
                result[key] = self.payload(nested, depth + 1)
            return result
        if tag == 11:
            length = self.number("i")
            if length < 0 or length > MAX_UNCOMPRESSED // 4:
                raise InvalidStructure("Invalid int array length")
            return [self.number("i") for _ in range(length)]
        if tag == 12:
            length = self.number("i")
            if length < 0 or length > MAX_UNCOMPRESSED // 8:
                raise InvalidStructure("Invalid long array length")
            return [self.number("q") for _ in range(length)]
        raise InvalidStructure("Unknown NBT tag " + str(tag))


def parse_nbt(data: bytes) -> dict:
    reader = NBTReader(data)
    if reader.number("B") != 10:
        raise InvalidStructure("Expected a root compound tag")
    reader.string()  # root tag name (normally empty)
    root = reader.payload(10)
    if reader.at != len(reader.data):
        raise InvalidStructure("Trailing data after NBT root")
    return root


def load_nbt(path: Path) -> dict:
    if path.stat().st_size > MAX_FILE_BYTES:
        raise InvalidStructure("NBT input file too large")
    data = path.read_bytes()
    if data.startswith(b"\x1f\x8b"):
        try:
            with gzip.GzipFile(fileobj=__import__("io").BytesIO(data)) as stream:
                data = stream.read(MAX_UNCOMPRESSED + 1)
        except (OSError, EOFError) as exc:
            raise InvalidStructure("Corrupt gzip-compressed NBT") from exc
    if len(data) > MAX_UNCOMPRESSED:
        raise InvalidStructure("Decompressed NBT exceeds safe size")
    return parse_nbt(data)


def integer3(value: Any, field: str) -> list[int]:
    if not isinstance(value, list) or len(value) != 3 or any(
            type(x) is not int for x in value):
        raise InvalidStructure(field + " must contain three integer coordinates")
    return list(value)


def convert(root: dict, scheme_id: str, require_air: bool = False) -> dict:
    if not SCHEME_ID.fullmatch(scheme_id) or ".." in scheme_id.split("/"):
        raise InvalidStructure("Invalid blueprint ID")
    size = integer3(root.get("size"), "size")
    if not all(1 <= n <= 96 for n in size) or size[0]*size[1]*size[2] > MAX_CELLS:
        raise InvalidStructure("Structure dimensions are too large or invalid")
    palette = root.get("palette")
    # Palette variants have different state indices; do not guess their mapping.
    if palette is None and "palettes" in root:
        palettes = root["palettes"]
        if not isinstance(palettes, list) or len(palettes) != 1:
            raise InvalidStructure("Multiple NBT palettes require explicit selection")
        palette = palettes[0]
    if not isinstance(palette, list) or not palette:
        raise InvalidStructure("Structure must contain a nonempty block palette")
    if len(palette) > MAX_CELLS:
        raise InvalidStructure("Palette too large")

    states = []
    for i, state in enumerate(palette):
        if not isinstance(state, dict):
            raise InvalidStructure("Invalid palette entry " + str(i))
        name = state.get("Name")
        if not isinstance(name, str) or not NAMESPACE_ID.fullmatch(name):
            raise InvalidStructure("Invalid namespaced block ID in palette")
        props = state.get("Properties", {})
        if not isinstance(props, dict) or any(
                not isinstance(k, str) or not isinstance(v, str)
                or not k or not v for k, v in props.items()):
            raise InvalidStructure("Invalid block state properties")
        states.append({"id": name, "properties": dict(sorted(props.items()))})

    blocks = root.get("blocks")
    if not isinstance(blocks, list) or len(blocks) > MAX_CELLS:
        raise InvalidStructure("Invalid structure block list")
    seen = set()
    included = []
    ignored_air = 0
    for block in blocks:
        if not isinstance(block, dict):
            raise InvalidStructure("Invalid block record")
        pos = integer3(block.get("pos"), "block.pos")
        if any(coord < 0 or coord >= limit for coord, limit in zip(pos, size)):
            raise InvalidStructure("Block coordinates outside structure bounds")
        poskey = tuple(pos)
        if poskey in seen:
            raise InvalidStructure("Duplicate block position " + str(pos))
        seen.add(poskey)
        index = block.get("state")
        if type(index) is not int or not 0 <= index < len(states):
            raise InvalidStructure("Block references invalid palette state")
        if "nbt" in block:
            raise InvalidStructure(
                "Block entities (NBT) are not supported by P1. Remove chests/signs "
                "or explicitly design a separate safe block-entity policy.")
        state = states[index]
        name = state["id"]
        if name in ("minecraft:structure_block", "minecraft:jigsaw",
                    "minecraft:structure_void"):
            raise InvalidStructure("Structure contains control/void block: " + name)
        if name in ("minecraft:air", "minecraft:cave_air", "minecraft:void_air"):
            if not require_air:
                ignored_air += 1
                continue
            if name != "minecraft:air":
                raise InvalidStructure("Only normal air is allowed in required-air mode")
        included.append({"pos": pos, "state": state})

    included.sort(key=lambda c: (c["pos"][1], c["pos"][2], c["pos"][0]))
    count_by_block = Counter(c["state"]["id"] for c in included)
    result = {
        "schema": "civcraft_reborn:blueprint/v1",
        "id": scheme_id,
        "size": size,
        "cells": included,
        "required_cells": len(included),
        "ignored_air_cells": ignored_air,
        "materials_by_block_state_position": dict(sorted(count_by_block.items())),
        "notes": {
            "air_policy": "required" if require_air else "ignored",
            "block_entities": "rejected",
            "authoring": "Minecraft Java Structure Block; independently authored",
        },
    }
    if not included:
        raise InvalidStructure("No required cells in structure")
    return result


def main() -> int:
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("input", type=Path, help="Minecraft Java structure .nbt")
    p.add_argument("--id", required=True, help="Versioned logical blueprint name")
    p.add_argument("--output", required=True, type=Path, help="Staging JSON destination")
    p.add_argument("--require-air", action="store_true",
                   help="Require explicit air blocks to be cleared inside the structure")
    args = p.parse_args()
    try:
        blueprint = convert(load_nbt(args.input), args.id, args.require_air)
    except (InvalidStructure, OSError, UnicodeDecodeError) as exc:
        p.error(str(exc))
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(blueprint, ensure_ascii=False, indent=2)+"\n",
                           encoding="utf-8")
    print(f"Blueprint {args.id}: {blueprint['required_cells']} required cells, "
          f"{blueprint['ignored_air_cells']} ignored air cells -> {args.output}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
