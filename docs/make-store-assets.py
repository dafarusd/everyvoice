#!/usr/bin/env python3
"""
Builds the two graphics Google Play requires, from the app's own icon.

The mark is the speech bubble already in app/src/main/res/drawable/ic_launcher.xml
— same geometry, same blue. A store icon that differs from the launcher icon is a
seam the user notices at exactly the moment they are deciding to trust the app.

    python3 docs/make-store-assets.py

Writes docs/store/icon-512.png and docs/store/feature-1024x500.png.
"""
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

BLUE = (26, 86, 219)        # #1A56DB — Theme.kt primary
WHITE = (255, 255, 255)

OUT = Path(__file__).resolve().parent / "store"
FONT_BOLD = "/usr/share/fonts/truetype/ubuntu/Ubuntu-B.ttf"

SS = 4  # supersample factor; PIL has no antialiased polygon fill


def draw_bubble(draw, scale, ox, oy, body, detail):
    """The ic_launcher path, in its original 48-unit space.

    body   — colour of the bubble
    detail — colour of the two text lines inside it
    """
    def p(x, y):
        return (ox + x * scale, oy + y * scale)

    # Rounded body, 4,4 to 44,36, corner radius 6.
    draw.rounded_rectangle([p(4, 4), p(44, 36)], radius=6 * scale, fill=body)
    # The tail.
    draw.polygon([p(22, 36), p(12, 44), p(12, 36)], fill=body)
    # Two lines of "text", the thing the bubble is saying.
    draw.rounded_rectangle([p(14, 16), p(34, 19.5)], radius=0.6 * scale, fill=detail)
    draw.rounded_rectangle([p(14, 22.5), p(28, 26)], radius=0.6 * scale, fill=detail)


def store_icon(size=512):
    """Full-bleed blue, white bubble. Play masks the corners itself, so the
    background must reach the edges or the mask cuts into white."""
    # Play's spec asks for a 32-bit PNG, so this carries an alpha channel even
    # though it is fully opaque.
    img = Image.new("RGBA", (size * SS, size * SS), BLUE + (255,))
    d = ImageDraw.Draw(img)
    # Mark occupies 320 of 512, centred: scale 8, offset 64.
    s = (size / 512) * 8 * SS
    off = (size / 512) * 64 * SS
    draw_bubble(d, s, off, off, WHITE + (255,), BLUE + (255,))
    return img.resize((size, size), Image.LANCZOS)


def feature_graphic(w=1024, h=500):
    """Mark on the left, name and line on the right. Large type only — Play
    crops this image on several surfaces and small print does not survive."""
    img = Image.new("RGB", (w * SS, h * SS), BLUE)
    d = ImageDraw.Draw(img)

    # Bubble, ~300px tall on the left.
    s = (300 / 40) * SS
    draw_bubble(d, s, (90 - 4 * (300 / 40)) * SS, (95 - 4 * (300 / 40)) * SS, WHITE, BLUE)

    name = ImageFont.truetype(FONT_BOLD, 96 * SS)
    line = ImageFont.truetype(FONT_BOLD, 42 * SS)
    d.text((440 * SS, 175 * SS), "EveryVoice", font=name, fill=WHITE)
    d.text((446 * SS, 290 * SS), "Point. Tap. Be heard.", font=line, fill=(200, 218, 255))

    return img.resize((w, h), Image.LANCZOS)


if __name__ == "__main__":
    OUT.mkdir(parents=True, exist_ok=True)
    icon = store_icon()
    icon.save(OUT / "icon-512.png")
    feature_graphic().save(OUT / "feature-1024x500.png")
    # A 192px copy for the README, where 512 is oversized.
    icon.resize((192, 192), Image.LANCZOS).save(OUT / "icon-192.png")
    for f in sorted(OUT.iterdir()):
        with Image.open(f) as im:
            print(f"{f.name:26s} {im.size[0]}x{im.size[1]}  {im.mode}  {f.stat().st_size:,} bytes")
