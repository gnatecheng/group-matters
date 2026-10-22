#!/usr/bin/env python3
"""Copy distribution Roborazzi PNGs into docs/screenshots/distribution/."""
from __future__ import annotations

import shutil
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RAW = ROOT / "app" / "build" / "distribution-screenshots-raw"
OUT = ROOT / "docs" / "screenshots" / "distribution"


def main() -> None:
    if not RAW.is_dir():
        raise SystemExit(f"Missing raw captures: {RAW} (run DistributionScreenshotTest)")
    OUT.mkdir(parents=True, exist_ok=True)
    for png in sorted(RAW.glob("*.png")):
        dest = OUT / png.name
        shutil.copy2(png, dest)
        print(dest.relative_to(ROOT), dest.stat().st_size, "bytes")


if __name__ == "__main__":
    main()
