#!/usr/bin/env python3
"""Exercise *real* P0 dedicated worlds, not only configuration parsing.

Run manually or from an isolated GitHub Actions matrix job after ./gradle build:
    python3 scripts/p0_world_restart_smoke.py --size 10000 --mode FINITE_VICTORY

The script creates a fresh server world in run/world, stops it, modifies the
global defaults, restarts the SAME world and verifies that its persisted P0
configuration still wins. It intentionally refuses to erase any existing world.
"""
from __future__ import annotations

import argparse
import os
import re
from pathlib import Path
import signal
import subprocess
import time

SIZES = (10000, 15000, 25000)
MODES = ("ENDLESS_SANDBOX", "FINITE_VICTORY")
ROOT = Path(__file__).resolve().parent.parent
RUN = ROOT / "run"
CONFIG = RUN / "config" / "civcraft-reborn-world-defaults.properties"
WORLD = RUN / "world"
LOGDIR = ROOT / "build" / "p0-world-smoke"


def write_defaults(size: int, mode: str) -> None:
    CONFIG.parent.mkdir(parents=True, exist_ok=True)
    CONFIG.write_text(
        f"overworld_side_blocks={size}\nsession_mode={mode}\n", encoding="utf-8"
    )


def boot(label: str, size: int, mode: str, timeout_seconds: int) -> tuple[str, str]:
    LOGDIR.mkdir(parents=True, exist_ok=True)
    log = LOGDIR / f"{label}.log"
    expected = f"Foundation world settings: Overworld {size}x{size} blocks, mode {mode}"
    args = ["gradle", "runServer", "--no-daemon", "--console=plain"]

    with log.open("w", encoding="utf-8") as output:
        proc = subprocess.Popen(
            args, cwd=ROOT, stdin=subprocess.PIPE, stdout=output,
            stderr=subprocess.STDOUT, start_new_session=True,
        )
        try:
            deadline = time.monotonic() + timeout_seconds
            ready = False
            while time.monotonic() < deadline:
                time.sleep(2)
                text = log.read_text(encoding="utf-8", errors="replace")
                if expected in text and "Done (" in text:
                    ready = True
                    break
                if proc.poll() is not None:
                    break
            if not ready:
                raise RuntimeError(
                    f"{label}: dedicated server did not start with expected settings; inspect {log}"
                )
            identity_match = re.search(
                r"Foundation world identity: id=([0-9a-f-]{36}), seed=(-?\\d+)", text
            )
            if not identity_match:
                raise RuntimeError(f"{label}: no world UUID/seed evidence in {log}")
            identity = identity_match.group(1), identity_match.group(2)

            # Gradle's runServer may or may not forward stdin in all Loom versions.
            # If it does not, treat shutdown as a blocker instead of asserting
            # that the world was durably saved.
            assert proc.stdin is not None
            proc.stdin.write(b"save-all flush\nstop\n")
            proc.stdin.flush()
            try:
                result = proc.wait(timeout=40)
            except subprocess.TimeoutExpired as error:
                raise RuntimeError(
                    f"{label}: server failed to stop cleanly; cannot prove persisted restart"
                ) from error
            if result != 0:
                raise RuntimeError(f"{label}: server exited with {result}; inspect {log}")
            return identity
        finally:
            if proc.poll() is None:
                os.killpg(proc.pid, signal.SIGTERM)
                try:
                    proc.wait(timeout=8)
                except subprocess.TimeoutExpired:
                    os.killpg(proc.pid, signal.SIGKILL)
                    proc.wait()


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--size", type=int, choices=SIZES, required=True)
    parser.add_argument("--mode", choices=MODES, required=True)
    parser.add_argument("--timeout-seconds", type=int, default=150)
    args = parser.parse_args()

    if WORLD.exists():
        raise SystemExit(
            "Existing run/world detected. Refusing to delete or overwrite a saved world."
        )
    RUN.mkdir(parents=True, exist_ok=True)
    (RUN / "eula.txt").write_text("eula=true\n", encoding="utf-8")

    write_defaults(args.size, args.mode)
    first_identity = boot("first-create", args.size, args.mode, args.timeout_seconds)

    found = list(WORLD.rglob("*world_settings*.dat"))
    if not found:
        raise RuntimeError("No CivCraft world SavedData file found after clean stop")
    initial_bytes = {str(f.relative_to(WORLD)): f.read_bytes() for f in found}
    identity_files = list(WORLD.rglob("*world_identity*.dat"))
    if not identity_files:
        raise RuntimeError("No stable CivCraft UUID/seed SavedData file after clean stop")

    different_size = next(s for s in SIZES if s != args.size)
    different_mode = next(m for m in MODES if m != args.mode)
    write_defaults(different_size, different_mode)
    restarted_identity = boot("restarted-after-defaults-changed", args.size, args.mode, args.timeout_seconds)
    if restarted_identity != first_identity:
        raise RuntimeError(
            f"World UUID/seed changed after restart: {first_identity} -> {restarted_identity}"
        )

    for filename in initial_bytes:
        if not (WORLD / filename).exists():
            raise RuntimeError(f"Persisted P0 SavedData disappeared after restart: {filename}")
    print(
        f"PASS real-world restart: {args.size}x{args.size} {args.mode}; "
        "changed defaults did not change the stored world settings or identity"
    )


if __name__ == "__main__":
    main()
