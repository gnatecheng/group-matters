#!/usr/bin/env python3
"""Copy Roborazzi fastlane PNG captures (native 2:1 viewport, no padding)."""
from __future__ import annotations

import shutil
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RAW = ROOT / "app" / "build" / "fastlane-screenshots-raw"
ICON_SRC = ROOT / "docs" / "launcher-icon-512.png"
STEMS = ["01-home", "02-attendance", "03-payment", "04-ledger", "05-members"]
LOCALES = (
    ("zh", "zh-CN"),
    ("en", "en-US"),
)
MAX_RATIO = 2.0


def check_ratio(w: int, h: int) -> float:
    return max(w, h) / min(w, h)


def export_locale(lang: str, fastlane_locale: str) -> None:
    meta = ROOT / "fastlane" / "metadata" / "android" / fastlane_locale
    shots = meta / "images" / "phoneScreenshots"
    shots.mkdir(parents=True, exist_ok=True)
    for old in shots.glob("*.png"):
        old.unlink()
    for index, stem in enumerate(STEMS, start=1):
        src = RAW / lang / "light" / f"{stem}.png"
        if not src.is_file():
            raise SystemExit(f"Missing capture: {src} (run FastlaneScreenshotTest with recordRoborazziDebug)")
        out = shots / f"{index}.png"
        shutil.copy2(src, out)
        try:
            from PIL import Image

            w, h = Image.open(out).size
            ratio = check_ratio(w, h)
            if ratio > MAX_RATIO + 0.02:
                raise SystemExit(f"{out}: edge ratio {ratio:.3f} exceeds {MAX_RATIO}")
            print(out.relative_to(ROOT), w, h, f"ratio={ratio:.3f}")
        except ImportError:
            print(out.relative_to(ROOT), out.stat().st_size, "bytes")


def main() -> None:
    if not RAW.is_dir():
        raise SystemExit(f"Missing raw captures: {RAW}")
    if not ICON_SRC.is_file():
        raise SystemExit(f"Missing icon: {ICON_SRC}")
    for _, fastlane_locale in LOCALES:
        icon_dir = ROOT / "fastlane" / "metadata" / "android" / fastlane_locale / "images"
        icon_dir.mkdir(parents=True, exist_ok=True)
        shutil.copy2(ICON_SRC, icon_dir / "icon.png")
    for lang, fastlane_locale in LOCALES:
        export_locale(lang, fastlane_locale)
    print("Fastlane screenshots exported (no padding).")


if __name__ == "__main__":
    main()
