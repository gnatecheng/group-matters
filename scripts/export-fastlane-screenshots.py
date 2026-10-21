#!/usr/bin/env python3
"""Pad Roborazzi homepage PNGs to max 2:1 aspect ratio and copy to fastlane phoneScreenshots."""
from __future__ import annotations

import shutil
from pathlib import Path

try:
    from PIL import Image
except ImportError:
    raise SystemExit("Install Pillow: pip install Pillow")

ROOT = Path(__file__).resolve().parents[1]
RAW = ROOT / "app" / "build" / "homepage-screenshots-raw"
ICON_SRC = ROOT / "docs" / "launcher-icon-512.png"
STEMS = ["01-home", "02-attendance", "03-payment", "04-ledger", "05-members"]
LOCALES = (
    ("zh", "zh-CN"),
    ("en", "en-US"),
)


def pad_to_max_ratio(im: Image.Image, max_ratio: float = 2.0) -> Image.Image:
    w, h = im.size
    ratio = max(w, h) / min(w, h)
    if ratio <= max_ratio + 1e-6:
        return im
    if h >= w:
        target_w = int(round(h / max_ratio))
        canvas = Image.new("RGBA", (target_w, h), (18, 18, 18, 255) if im.mode == "RGBA" else (18, 18, 18))
        x = (target_w - w) // 2
        canvas.paste(im, (x, 0), im if im.mode == "RGBA" else None)
        return canvas
    target_h = int(round(w / max_ratio))
    canvas = Image.new("RGBA", (w, target_h), (18, 18, 18, 255))
    y = (target_h - h) // 2
    canvas.paste(im, (0, y), im if im.mode == "RGBA" else None)
    return canvas


def export_locale(lang: str, fastlane_locale: str) -> None:
    meta = ROOT / "fastlane" / "metadata" / "android" / fastlane_locale
    shots = meta / "images" / "phoneScreenshots"
    shots.mkdir(parents=True, exist_ok=True)
    for old in shots.glob("*.png"):
        old.unlink()
    for index, stem in enumerate(STEMS, start=1):
        for theme in ("light", "dark"):
            src = RAW / lang / theme / f"{stem}.png"
            if not src.is_file():
                raise SystemExit(f"Missing capture: {src}")
        # Prefer light theme for store listings
        src = RAW / lang / "light" / f"{stem}.png"
        im = Image.open(src).convert("RGBA")
        im = pad_to_max_ratio(im)
        out = shots / f"{index}.png"
        im.save(out, "PNG", optimize=True)
        rw, rh = im.size
        print(out.relative_to(ROOT), rw, rh, f"ratio={max(rw,rh)/min(rw,rh):.3f}")


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
    print("Fastlane screenshots exported.")


if __name__ == "__main__":
    main()
