#!/usr/bin/env python3
"""Static integrity checks; these are NOT a substitute for a Fabric runtime test."""
from pathlib import Path
import json
import re

root = Path(__file__).resolve().parent.parent
metadata = json.loads((root / "src/main/resources/fabric.mod.json").read_text(encoding="utf-8"))
props = dict(re.findall(r"(?m)^([\w.]+)=(.*)$", (root / "gradle.properties").read_text(encoding="utf-8")))
assert metadata["id"] == "civcraft_reborn"
assert props["minecraft_version"] == "26.3"
assert props["loader_version"] == "0.20.1"
assert props["fabric_api_version"] == "0.166.2+26.3"
assert metadata["entrypoints"]["main"] == ["dev.civcraftreborn.CivCraftReborn"]
assert metadata["depends"]["minecraft"] == "~26.3"
source = root / "src/main/java/dev/civcraftreborn/CivCraftReborn.java"
assert source.exists()
print("P0 manifest checked; runtime compatibility remains to be tested.")
