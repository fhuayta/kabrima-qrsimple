import os
import math
from PIL import Image, ImageDraw, ImageFont, ImageFilter

ASSETS_DIR = "/home/franz/PROYECTOS/KBM-QRSimple/google_play_assets"
RES_DIR = "/home/franz/PROYECTOS/KBM-QRSimple/app/src/main/res"
os.makedirs(ASSETS_DIR, exist_ok=True)

FONT_BOLD = "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf"
FONT_REGULAR = "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"

def get_font(path, size):
    try:
        return ImageFont.truetype(path, size)
    except Exception:
        return ImageFont.load_default()

def draw_rounded_rect(draw, bounds, radius, fill=None, outline=None, width=1):
    draw.rounded_rectangle(bounds, radius=radius, fill=fill, outline=outline, width=width)

def draw_qr_corner_pattern(draw, x, y, size, color):
    # Outer box
    draw.rounded_rectangle([x, y, x + size, y + size], radius=size // 5, fill=color)
    # Inner cutout (dark background)
    inner_pad = size // 4
    draw.rounded_rectangle([x + inner_pad, y + inner_pad, x + size - inner_pad, y + size - inner_pad],
                           radius=size // 8, fill=(11, 15, 25, 255))
    # Center dot
    dot_pad = size * 3 // 8
    draw.rounded_rectangle([x + dot_pad, y + dot_pad, x + size - dot_pad, y + size - dot_pad],
                           radius=size // 10, fill=color)

def generate_app_icon():
    """Generates 512x512 App Icon with emerald viewfinder, glowing reticle, and QR matrix."""
    size = 512
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # 1. Base Squircle background with gradient
    bg_pad = 28
    bg_radius = 110
    
    # Glow layer
    glow = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    g_draw = ImageDraw.Draw(glow)
    g_draw.rounded_rectangle([bg_pad - 10, bg_pad - 10, size - bg_pad + 10, size - bg_pad + 10],
                             radius=bg_radius + 8, fill=(16, 185, 129, 60))
    glow = glow.filter(ImageFilter.GaussianBlur(16))
    img.alpha_composite(glow)

    # Background gradient fill (Dark Slate to Charcoal)
    bg_img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    b_draw = ImageDraw.Draw(bg_img)
    for i in range(bg_pad, size - bg_pad):
        ratio = (i - bg_pad) / (size - 2 * bg_pad)
        r = int(12 + ratio * 10)
        g = int(18 + ratio * 16)
        b = int(28 + ratio * 22)
        b_draw.line([(bg_pad, i), (size - bg_pad, i)], fill=(r, g, b, 255))

    # Mask with rounded rect
    mask = Image.new("L", (size, size), 0)
    m_draw = ImageDraw.Draw(mask)
    m_draw.rounded_rectangle([bg_pad, bg_pad, size - bg_pad, size - bg_pad], radius=bg_radius, fill=255)
    img.paste(bg_img, (0, 0), mask)

    # Border
    draw.rounded_rectangle([bg_pad, bg_pad, size - bg_pad, size - bg_pad], radius=bg_radius,
                           outline=(16, 185, 129, 140), width=3)

    # 2. Viewfinder Frame with Emerald Corners
    vf_pad = 96
    vf_x1, vf_y1 = vf_pad, vf_pad
    vf_x2, vf_y2 = size - vf_pad, size - vf_pad
    c_len = 54
    c_thick = 8
    emerald = (16, 185, 129, 255)
    mint = (52, 211, 153, 255)

    # Corner brackets (Top-Left)
    draw.rounded_rectangle([vf_x1, vf_y1, vf_x1 + c_len, vf_y1 + c_thick], radius=4, fill=emerald)
    draw.rounded_rectangle([vf_x1, vf_y1, vf_x1 + c_thick, vf_y1 + c_len], radius=4, fill=emerald)

    # Top-Right
    draw.rounded_rectangle([vf_x2 - c_len, vf_y1, vf_x2, vf_y1 + c_thick], radius=4, fill=emerald)
    draw.rounded_rectangle([vf_x2 - c_thick, vf_y1, vf_x2, vf_y1 + c_len], radius=4, fill=emerald)

    # Bottom-Left
    draw.rounded_rectangle([vf_x1, vf_y2 - c_thick, vf_x1 + c_len, vf_y2], radius=4, fill=emerald)
    draw.rounded_rectangle([vf_x1, vf_y2 - c_len, vf_x1 + c_thick, vf_y2], radius=4, fill=emerald)

    # Bottom-Right
    draw.rounded_rectangle([vf_x2 - c_len, vf_y2 - c_thick, vf_x2, vf_y2], radius=4, fill=emerald)
    draw.rounded_rectangle([vf_x2 - c_thick, vf_y2 - c_len, vf_x2, vf_y2], radius=4, fill=emerald)

    # 3. QR Code Patterns inside the viewfinder
    qr_x = vf_pad + 24
    qr_y = vf_pad + 24
    qr_w = vf_x2 - vf_x1 - 48
    pt_size = 64

    # Top-Left Finder
    draw_qr_corner_pattern(draw, qr_x, qr_y, pt_size, mint)
    # Top-Right Finder
    draw_qr_corner_pattern(draw, qr_x + qr_w - pt_size, qr_y, pt_size, mint)
    # Bottom-Left Finder
    draw_qr_corner_pattern(draw, qr_x, qr_y + qr_w - pt_size, pt_size, mint)

    # Scattered QR data modules (clean geometric design)
    mod_size = 14
    matrix = [
        # (col, row)
        (5, 1), (6, 1), (7, 1), (8, 2), (9, 2), (6, 3), (8, 4),
        (1, 5), (3, 5), (5, 5), (6, 5), (8, 5), (10, 5),
        (2, 6), (4, 6), (7, 6), (9, 6), (11, 6),
        (5, 7), (7, 7), (8, 7), (10, 7),
        (5, 8), (6, 8), (9, 8), (11, 8),
        (5, 9), (7, 9), (8, 9), (10, 9), (12, 9),
        (5, 10), (8, 10), (9, 10), (11, 10),
        (5, 11), (7, 11), (9, 11), (12, 11),
        (6, 12), (7, 12), (9, 12), (10, 12), (11, 12)
    ]
    grid_start_x = qr_x
    grid_start_y = qr_y
    step = qr_w / 14.0

    for col, row in matrix:
        mx = int(grid_start_x + col * step)
        my = int(grid_start_y + row * step)
        draw.rounded_rectangle([mx, my, mx + mod_size, my + mod_size], radius=3, fill=(240, 253, 244, 230))

    # 4. Animated Laser Beam across middle
    laser_y = size // 2 + 10
    # Laser Glow
    laser_glow = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    lg_draw = ImageDraw.Draw(laser_glow)
    lg_draw.line([(vf_x1 + 10, laser_y), (vf_x2 - 10, laser_y)], fill=(16, 185, 129, 180), width=14)
    laser_glow = laser_glow.filter(ImageFilter.GaussianBlur(6))
    img.alpha_composite(laser_glow)

    # Laser core (bright mint/white)
    draw.line([(vf_x1 + 12, laser_y), (vf_x2 - 12, laser_y)], fill=(255, 255, 255, 255), width=4)

    # Save 512x512
    out_path = os.path.join(ASSETS_DIR, "app_icon_512.png")
    img.save(out_path, "PNG")
    print(f"Generated {out_path}")
    return img

def generate_tip_coffee():
    """Generates 512x512 Coffee Tip Icon."""
    size = 512
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Outer circle badge
    cx, cy, r = size // 2, size // 2, 210
    
    # Glow
    glow = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    g_draw = ImageDraw.Draw(glow)
    g_draw.ellipse([cx - r - 8, cy - r - 8, cx + r + 8, cy + r + 8], fill=(16, 185, 129, 60))
    glow = glow.filter(ImageFilter.GaussianBlur(16))
    img.alpha_composite(glow)

    # Circle background (Dark Slate)
    draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=(18, 24, 38, 255),
                 outline=(16, 185, 129, 230), width=6)

    # Inner circular decorative ring
    draw.ellipse([cx - r + 14, cy - r + 14, cx + r - 14, cy + r - 14],
                 outline=(16, 185, 129, 60), width=2)

    # Coffee Cup
    cup_w = 170
    cup_h = 130
    cup_x = cx - cup_w // 2 - 15
    cup_y = cy - 20

    # Saucer / Base plate
    draw.rounded_rectangle([cup_x - 30, cup_y + cup_h + 8, cup_x + cup_w + 30, cup_y + cup_h + 24],
                           radius=8, fill=(100, 116, 139, 255))
    draw.rounded_rectangle([cup_x - 30, cup_y + cup_h + 8, cup_x + cup_w + 30, cup_y + cup_h + 24],
                           radius=8, outline=(16, 185, 129, 200), width=2)

    # Cup Body
    draw.rounded_rectangle([cup_x, cup_y, cup_x + cup_w, cup_y + cup_h],
                           radius=26, fill=(248, 250, 252, 255))

    # Cup Handle
    handle_x = cup_x + cup_w - 6
    handle_y = cup_y + 20
    draw.arc([handle_x - 10, handle_y, handle_x + 56, handle_y + 70],
             start=-90, end=90, fill=(248, 250, 252, 255), width=18)

    # Emerald Heart on cup
    heart_cx, heart_cy = cup_x + cup_w // 2, cup_y + cup_h // 2
    draw.ellipse([heart_cx - 22, heart_cy - 16, heart_cx, heart_cy + 6], fill=(16, 185, 129, 255))
    draw.ellipse([heart_cx, heart_cy - 16, heart_cx + 22, heart_cy + 6], fill=(16, 185, 129, 255))
    draw.polygon([(heart_cx - 21, heart_cy - 2), (heart_cx + 21, heart_cy - 2), (heart_cx, heart_cy + 22)],
                 fill=(16, 185, 129, 255))

    # Steam swirls above cup
    for sx, sy in [(cx - 40, cup_y - 25), (cx - 5, cup_y - 35), (cx + 30, cup_y - 22)]:
        draw.arc([sx - 15, sy - 40, sx + 15, sy + 10], start=45, end=225,
                 fill=(52, 211, 153, 220), width=6)

    # Text pill at bottom
    font_badge = get_font(FONT_BOLD, 26)
    badge_text = "CAFÉ"
    bbox = draw.textbbox((0, 0), badge_text, font=font_badge)
    tw = bbox[2] - bbox[0]
    th = bbox[3] - bbox[1]
    bx = cx - tw // 2
    by = cy + 130
    draw.rounded_rectangle([bx - 20, by - 6, bx + tw + 20, by + th + 10], radius=14,
                           fill=(16, 185, 129, 255))
    draw.text((bx, by - 2), badge_text, fill=(11, 15, 25, 255), font=font_badge)

    out_path = os.path.join(ASSETS_DIR, "tip_coffee_512.png")
    img.save(out_path, "PNG")
    print(f"Generated {out_path}")

def generate_tip_lunch():
    """Generates 512x512 Pizza/Lunch Tip Icon."""
    size = 512
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    cx, cy, r = size // 2, size // 2, 210

    # Glow
    glow = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    g_draw = ImageDraw.Draw(glow)
    g_draw.ellipse([cx - r - 8, cy - r - 8, cx + r + 8, cy + r + 8], fill=(245, 158, 11, 60))
    glow = glow.filter(ImageFilter.GaussianBlur(16))
    img.alpha_composite(glow)

    # Circle background (Dark Slate)
    draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=(18, 24, 38, 255),
                 outline=(245, 158, 11, 230), width=6)

    # Inner circular decorative ring
    draw.ellipse([cx - r + 14, cy - r + 14, cx + r - 14, cy + r - 14],
                 outline=(245, 158, 11, 60), width=2)

    # Pizza slice (Triangle pointing downward/right)
    p_tip = (cx, cy + 90)
    p_left = (cx - 110, cy - 80)
    p_right = (cx + 110, cy - 80)

    # Golden Cheese Slice
    draw.polygon([p_left, p_right, p_tip], fill=(251, 191, 36, 255))

    # Crust (Top rounded arc/capsule)
    draw.rounded_rectangle([cx - 120, cy - 105, cx + 120, cy - 75], radius=15,
                           fill=(217, 119, 6, 255))

    # Pepperoni / Tomato slices
    pepperonis = [(cx - 35, cy - 40), (cx + 35, cy - 35), (cx, cy + 10), (cx - 25, cy + 45)]
    for px, py in pepperonis:
        draw.ellipse([px - 16, py - 16, px + 16, py + 16], fill=(225, 29, 72, 255))
        # Highlight
        draw.ellipse([px - 10, py - 10, px - 2, py - 2], fill=(251, 113, 133, 255))

    # Emerald Oregano herbs
    herbs = [(cx - 60, cy - 60), (cx + 10, cy - 55), (cx + 55, cy - 10), (cx - 10, cy + 30)]
    for hx, hy in herbs:
        draw.rounded_rectangle([hx - 6, hy - 3, hx + 6, hy + 3], radius=2, fill=(16, 185, 129, 255))

    # Text pill at bottom
    font_badge = get_font(FONT_BOLD, 26)
    badge_text = "ALMUERZO"
    bbox = draw.textbbox((0, 0), badge_text, font=font_badge)
    tw = bbox[2] - bbox[0]
    th = bbox[3] - bbox[1]
    bx = cx - tw // 2
    by = cy + 130
    draw.rounded_rectangle([bx - 20, by - 6, bx + tw + 20, by + th + 10], radius=14,
                           fill=(245, 158, 11, 255))
    draw.text((bx, by - 2), badge_text, fill=(11, 15, 25, 255), font=font_badge)

    out_path = os.path.join(ASSETS_DIR, "tip_lunch_512.png")
    img.save(out_path, "PNG")
    print(f"Generated {out_path}")

def generate_tip_supporter():
    """Generates 512x512 Supporter Rocket Tip Icon."""
    size = 512
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    cx, cy, r = size // 2, size // 2, 210

    # Glow
    glow = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    g_draw = ImageDraw.Draw(glow)
    g_draw.ellipse([cx - r - 8, cy - r - 8, cx + r + 8, cy + r + 8], fill=(139, 92, 246, 70))
    glow = glow.filter(ImageFilter.GaussianBlur(16))
    img.alpha_composite(glow)

    # Circle background (Dark Slate)
    draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=(18, 24, 38, 255),
                 outline=(139, 92, 246, 230), width=6)

    # Inner circular decorative ring
    draw.ellipse([cx - r + 14, cy - r + 14, cx + r - 14, cy + r - 14],
                 outline=(139, 92, 246, 60), width=2)

    # Twinkling Stars in background
    stars = [(cx - 120, cy - 80), (cx + 110, cy - 100), (cx + 130, cy + 40), (cx - 100, cy + 70)]
    for sx, sy in stars:
        draw.line([(sx - 6, sy), (sx + 6, sy)], fill=(255, 255, 255, 180), width=2)
        draw.line([(sx, sy - 6), (sx, sy + 6)], fill=(255, 255, 255, 180), width=2)

    # Rocket Body (Facing up)
    rx, ry = cx, cy - 20
    # Thruster Flame
    draw.polygon([(rx - 25, ry + 70), (rx + 25, ry + 70), (rx, ry + 130)], fill=(249, 115, 22, 255))
    draw.polygon([(rx - 14, ry + 70), (rx + 14, ry + 70), (rx, ry + 105)], fill=(250, 204, 21, 255))

    # Rocket Fins (Wings)
    draw.polygon([(rx - 30, ry + 20), (rx - 70, ry + 70), (rx - 25, ry + 65)], fill=(124, 58, 237, 255))
    draw.polygon([(rx + 30, ry + 20), (rx + 70, ry + 70), (rx + 25, ry + 65)], fill=(124, 58, 237, 255))

    # Rocket Fuselage
    draw.polygon([(rx, ry - 110), (rx - 36, ry + 10), (rx - 36, ry + 65),
                  (rx + 36, ry + 65), (rx + 36, ry + 10)], fill=(241, 245, 249, 255))

    # Red nose cone
    draw.polygon([(rx, ry - 110), (rx - 25, ry - 40), (rx + 25, ry - 40)], fill=(239, 68, 68, 255))

    # Rocket Porthole Window (Emerald glass)
    draw.ellipse([rx - 20, ry - 15, rx + 20, ry + 25], fill=(30, 41, 59, 255),
                 outline=(148, 163, 184, 255), width=4)
    draw.ellipse([rx - 14, ry - 9, rx + 14, ry + 19], fill=(16, 185, 129, 255))

    # Text pill at bottom
    font_badge = get_font(FONT_BOLD, 24)
    badge_text = "PATROCINADOR"
    bbox = draw.textbbox((0, 0), badge_text, font=font_badge)
    tw = bbox[2] - bbox[0]
    th = bbox[3] - bbox[1]
    bx = cx - tw // 2
    by = cy + 130
    draw.rounded_rectangle([bx - 18, by - 6, bx + tw + 18, by + th + 10], radius=14,
                           fill=(139, 92, 246, 255))
    draw.text((bx, by - 2), badge_text, fill=(255, 255, 255, 255), font=font_badge)

    out_path = os.path.join(ASSETS_DIR, "tip_supporter_512.png")
    img.save(out_path, "PNG")
    print(f"Generated {out_path}")

def generate_feature_graphic(lang="es"):
    """Generates 1024x500 Feature Graphic for Google Play Store."""
    w, h = 1024, 500
    img = Image.new("RGB", (w, h), (11, 15, 25))
    draw = ImageDraw.Draw(img)

    # 1. Background Gradient (Dark Navy/Slate with Emerald Ambient)
    for x in range(w):
        rx = x / w
        r = int(10 + rx * 8)
        g = int(15 + rx * 20)
        b = int(24 + rx * 14)
        draw.line([(x, 0), (x, h)], fill=(r, g, b))

    # Ambient Emerald Glow on Right
    glow = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    g_draw = ImageDraw.Draw(glow)
    g_draw.ellipse([650, 50, 1050, 450], fill=(16, 185, 129, 45))
    glow = glow.filter(ImageFilter.GaussianBlur(60))
    img.paste(glow, (0, 0), glow)

    # 2. Left Side: Brand Typography & Badges
    font_title = get_font(FONT_BOLD, 54)
    font_sub = get_font(FONT_REGULAR, 22)
    font_pill = get_font(FONT_BOLD, 15)

    # App Title with Emerald Accent
    draw.text((64, 75), "QR Simple", fill=(16, 185, 129), font=font_title)
    draw.text((375, 75), "2.0", fill=(255, 255, 255), font=font_title)

    # Subtitle
    if lang == "es":
        sub_line1 = "Lector y Creador de QR Ultra Rápido"
        sub_line2 = "100% Offline • Sin Publicidad • Máxima Privacidad"
        pills = [("100% OFFLINE", (16, 185, 129)),
                 ("CERO PUBLICIDAD", (59, 130, 246)),
                 ("SIN RASTREO", (168, 85, 247))]
    else:
        sub_line1 = "Fast, Private & Ad-Free QR Scanner & Generator"
        sub_line2 = "100% Offline • Zero Tracking • Instant Actions"
        pills = [("100% OFFLINE", (16, 185, 129)),
                 ("ZERO ADS", (59, 130, 246)),
                 ("ZERO TRACKING", (168, 85, 247))]

    draw.text((64, 150), sub_line1, fill=(241, 245, 249), font=font_sub)
    draw.text((64, 185), sub_line2, fill=(148, 163, 184), font=font_sub)

    # Feature Pill Badges
    px = 64
    py = 245
    for label, col in pills:
        bbox = draw.textbbox((0, 0), label, font=font_pill)
        bw = bbox[2] - bbox[0] + 28
        bh = 38
        draw.rounded_rectangle([px, py, px + bw, py + bh], radius=19,
                               fill=(18, 24, 38), outline=col, width=2)
        draw.text((px + 14, py + 10), label, fill=col, font=font_pill)
        px += bw + 16

    # Bottom capability bullets
    font_cap = get_font(FONT_REGULAR, 15)
    caps_es = [
        "✓ Wi-Fi, Enlaces, Contactos, Teléfono, SMS, Email",
        "✓ Generador QR offline (PNG / Guardar en Galería)",
        "✓ Historial local con búsqueda instantánea y filtros"
    ]
    caps_en = [
        "✓ Wi-Fi, Links, Contacts, Phone, SMS, Email",
        "✓ Offline QR Generator (PNG / Save to Gallery)",
        "✓ Local history with instant search & smart filters"
    ]
    caps = caps_es if lang == "es" else caps_en
    cy = 315
    for cap in caps:
        draw.text((64, cy), cap, fill=(203, 213, 225), font=font_cap)
        cy += 28

    # 3. Right Side: Phone Viewfinder Mockup
    phone_x1 = 700
    phone_y1 = 45
    phone_w = 260
    phone_h = 410
    phone_r = 32

    # Phone outer frame
    draw.rounded_rectangle([phone_x1, phone_y1, phone_x1 + phone_w, phone_y1 + phone_h],
                           radius=phone_r, fill=(18, 24, 38), outline=(51, 65, 85), width=4)
    # Camera notch pill
    draw.rounded_rectangle([phone_x1 + phone_w // 2 - 24, phone_y1 + 12, phone_x1 + phone_w // 2 + 24, phone_y1 + 22],
                           radius=5, fill=(11, 15, 25))

    # Scanner Reticle inside screen
    ret_x = phone_x1 + 35
    ret_y = phone_y1 + 65
    ret_size = 190
    c_len = 36
    c_thick = 5
    em = (16, 185, 129)

    # Reticle corners
    draw.line([(ret_x, ret_y), (ret_x + c_len, ret_y)], fill=em, width=c_thick)
    draw.line([(ret_x, ret_y), (ret_x, ret_y + c_len)], fill=em, width=c_thick)
    draw.line([(ret_x + ret_size - c_len, ret_y), (ret_x + ret_size, ret_y)], fill=em, width=c_thick)
    draw.line([(ret_x + ret_size, ret_y), (ret_x + ret_size, ret_y + c_len)], fill=em, width=c_thick)
    draw.line([(ret_x, ret_y + ret_size), (ret_x + c_len, ret_y + ret_size)], fill=em, width=c_thick)
    draw.line([(ret_x, ret_y + ret_size - c_len), (ret_x, ret_y + ret_size)], fill=em, width=c_thick)
    draw.line([(ret_x + ret_size - c_len, ret_y + ret_size), (ret_x + ret_size, ret_y + ret_size)], fill=em, width=c_thick)
    draw.line([(ret_x + ret_size, ret_y + ret_size - c_len), (ret_x + ret_size, ret_y + ret_size)], fill=em, width=c_thick)

    # Laser beam in reticle
    laser_y = ret_y + ret_size // 2
    draw.line([(ret_x + 8, laser_y), (ret_x + ret_size - 8, laser_y)], fill=(52, 211, 153), width=4)

    # Smart Result Card at bottom of screen
    card_x1 = phone_x1 + 20
    card_y1 = phone_y1 + 280
    card_w = phone_w - 40
    card_h = 105
    draw.rounded_rectangle([card_x1, card_y1, card_x1 + card_w, card_y1 + card_h],
                           radius=16, fill=(30, 41, 59), outline=(16, 185, 129), width=2)

    font_card_title = get_font(FONT_BOLD, 14)
    font_card_url = get_font(FONT_REGULAR, 12)
    font_btn = get_font(FONT_BOLD, 12)

    draw.text((card_x1 + 14, card_y1 + 12), "🔗 " + ("Sitio Web Detectado" if lang == "es" else "Website Detected"),
              fill=(16, 185, 129), font=font_card_title)
    draw.text((card_x1 + 14, card_y1 + 32), "https://kabrima.com", fill=(241, 245, 249), font=font_card_url)

    # Primary Action Button inside card
    btn_w = card_w - 28
    btn_h = 32
    draw.rounded_rectangle([card_x1 + 14, card_y1 + 58, card_x1 + 14 + btn_w, card_y1 + 58 + btn_h],
                           radius=8, fill=(16, 185, 129))
    btn_text = "Abrir Enlace" if lang == "es" else "Open Link"
    bbox_btn = draw.textbbox((0, 0), btn_text, font=font_btn)
    bw = bbox_btn[2] - bbox_btn[0]
    draw.text((card_x1 + 14 + (btn_w - bw) // 2, card_y1 + 66), btn_text, fill=(11, 15, 25), font=font_btn)

    # Filename
    suffix = "_es" if lang == "es" else ""
    out_filename = f"feature_graphic{suffix}_1024x500.png"
    out_path = os.path.join(ASSETS_DIR, out_filename)
    img.save(out_path, "PNG")
    print(f"Generated {out_path}")

def draw_phone_frame(draw, x, y, w, h, r):
    # Outer bezel
    draw.rounded_rectangle([x, y, x + w, y + h], radius=r, fill=(18, 24, 38), outline=(51, 65, 85), width=8)
    # Speaker / punch hole
    draw.rounded_rectangle([x + w // 2 - 30, y + 18, x + w // 2 + 30, y + 30], radius=6, fill=(11, 15, 25))

def generate_screenshots(lang="es"):
    """Generates 4 high-res 1080x2400 screenshots for Google Play Store."""
    w, h = 1080, 2400
    font_hl = get_font(FONT_BOLD, 68)
    font_sub = get_font(FONT_REGULAR, 34)
    font_ui_title = get_font(FONT_BOLD, 36)
    font_ui_body = get_font(FONT_REGULAR, 26)
    font_ui_bold = get_font(FONT_BOLD, 26)
    font_ui_sm = get_font(FONT_REGULAR, 20)

    # ---------------- SCREENSHOT 1: SCANNER ----------------
    s1 = Image.new("RGB", (w, h), (11, 15, 25))
    d1 = ImageDraw.Draw(s1)
    
    # Top text
    hl1 = "Escáner Ultra Rápido" if lang == "es" else "Lightning Fast Scanner"
    sub1 = "Detección instantánea con ML Kit y linterna" if lang == "es" else "Instant on-device detection with ML Kit & torch"
    d1.text((70, 140), hl1, fill=(16, 185, 129), font=font_hl)
    d1.text((70, 230), sub1, fill=(203, 213, 225), font=font_sub)

    # Phone Frame
    px, py, pw, ph, pr = 110, 360, 860, 1940, 70
    draw_phone_frame(d1, px, py, pw, ph, pr)

    # Inner Screen (Dark Camera View)
    sx, sy, sw, sh = px + 16, py + 45, pw - 32, ph - 60
    d1.rounded_rectangle([sx, sy, sx + sw, sy + sh], radius=pr - 10, fill=(7, 10, 18))

    # Top Bar inside phone
    d1.text((sx + 40, sy + 40), "QR Simple", fill=(255, 255, 255), font=font_ui_title)
    # Torch circle
    d1.ellipse([sx + sw - 160, sy + 30, sx + sw - 100, sy + 90], fill=(30, 41, 59), outline=(16, 185, 129), width=2)
    d1.text((sx + sw - 142, sy + 42), "⚡", fill=(255, 255, 255), font=font_ui_bold)
    # Gallery circle
    d1.ellipse([sx + sw - 80, sy + 30, sx + sw - 20, sy + 90], fill=(30, 41, 59), outline=(16, 185, 129), width=2)
    d1.text((sx + sw - 62, sy + 42), "🖼", fill=(255, 255, 255), font=font_ui_bold)

    # Reticle Center
    rc_size = 540
    rx = sx + (sw - rc_size) // 2
    ry = sy + 380
    c_len = 80
    c_th = 12
    em = (16, 185, 129)

    # Emerald corners
    d1.line([(rx, ry), (rx + c_len, ry)], fill=em, width=c_th)
    d1.line([(rx, ry), (rx, ry + c_len)], fill=em, width=c_th)
    d1.line([(rx + rc_size - c_len, ry), (rx + rc_size, ry)], fill=em, width=c_th)
    d1.line([(rx + rc_size, ry), (rx + rc_size, ry + c_len)], fill=em, width=c_th)
    d1.line([(rx, ry + rc_size), (rx + c_len, ry + rc_size)], fill=em, width=c_th)
    d1.line([(rx, ry + rc_size - c_len), (rx, ry + rc_size)], fill=em, width=c_th)
    d1.line([(rx + rc_size - c_len, ry + rc_size), (rx + rc_size, ry + rc_size)], fill=em, width=c_th)
    d1.line([(rx + rc_size, ry + rc_size - c_len), (rx + rc_size, ry + rc_size)], fill=em, width=c_th)

    # QR Matrix preview inside
    draw_qr_corner_pattern(d1, rx + 60, ry + 60, 110, (52, 211, 153))
    draw_qr_corner_pattern(d1, rx + rc_size - 170, ry + 60, 110, (52, 211, 153))
    draw_qr_corner_pattern(d1, rx + 60, ry + rc_size - 170, 110, (52, 211, 153))

    # Glowing Laser line
    laser_y = ry + rc_size // 2
    d1.line([(rx + 20, laser_y), (rx + rc_size - 20, laser_y)], fill=(52, 211, 153), width=8)

    # Zoom pill at bottom of camera
    d1.rounded_rectangle([sx + sw // 2 - 100, ry + rc_size + 80, sx + sw // 2 + 100, ry + rc_size + 140],
                         radius=30, fill=(18, 24, 38), outline=(51, 65, 85), width=2)
    d1.text((sx + sw // 2 - 30, ry + rc_size + 95), "1.0x", fill=(16, 185, 129), font=font_ui_bold)

    # Save Screenshot 1
    s1_path = os.path.join(ASSETS_DIR, f"screenshot_1_{lang}_scanner.png")
    s1.save(s1_path, "PNG")
    print(f"Generated {s1_path}")

    # ---------------- SCREENSHOT 2: SMART ACTIONS ----------------
    s2 = Image.new("RGB", (w, h), (11, 15, 25))
    d2 = ImageDraw.Draw(s2)
    hl2 = "Acciones Inteligentes" if lang == "es" else "Smart Instant Actions"
    sub2 = "Conexión Wi-Fi, enlaces web y contactos vCard" if lang == "es" else "Wi-Fi auto-connect, links, contacts & more"
    d2.text((70, 140), hl2, fill=(16, 185, 129), font=font_hl)
    d2.text((70, 230), sub2, fill=(203, 213, 225), font=font_sub)

    draw_phone_frame(d2, px, py, pw, ph, pr)
    d2.rounded_rectangle([sx, sy, sx + sw, sy + sh], radius=pr - 10, fill=(7, 10, 18))

    # Dimmed camera background
    d2.rounded_rectangle([sx, sy, sx + sw, sy + 700], radius=pr - 10, fill=(15, 23, 42))
    d2.text((sx + 50, sy + 80), "📷 " + ("Código Escaneado" if lang == "es" else "Scanned Code"), fill=(148, 163, 184), font=font_ui_bold)

    # Bottom Sheet card (Wi-Fi Detected)
    sheet_y = sy + 600
    sheet_h = sh - 600
    d2.rounded_rectangle([sx, sheet_y, sx + sw, sy + sh], radius=40, fill=(18, 24, 38))
    # Handle bar
    d2.rounded_rectangle([sx + sw // 2 - 40, sheet_y + 20, sx + sw // 2 + 40, sheet_y + 28], radius=4, fill=(71, 85, 105))

    # Sheet Header
    d2.text((sx + 60, sheet_y + 60), "📶 Red Wi-Fi Detectada" if lang == "es" else "📶 Wi-Fi Network Detected", fill=(16, 185, 129), font=font_ui_title)
    d2.text((sx + 60, sheet_y + 115), "Fibra_Optica_Kabrima_5G", fill=(255, 255, 255), font=font_ui_bold)
    d2.text((sx + 60, sheet_y + 155), "Seguridad: WPA2-PSK", fill=(148, 163, 184), font=font_ui_body)

    # Password box
    d2.rounded_rectangle([sx + 60, sheet_y + 210, sx + sw - 60, sheet_y + 300], radius=16, fill=(30, 41, 59))
    d2.text((sx + 90, sheet_y + 240), "Contraseña:  ••••••••••••" if lang == "es" else "Password:  ••••••••••••", fill=(203, 213, 225), font=font_ui_body)
    d2.text((sx + sw - 140, sheet_y + 235), "👁", fill=(16, 185, 129), font=font_ui_bold)

    # Primary Action Button: Conectar a Wi-Fi
    d2.rounded_rectangle([sx + 60, sheet_y + 340, sx + sw - 60, sheet_y + 430], radius=20, fill=(16, 185, 129))
    d2.text((sx + 180, sheet_y + 368), "Conectar a Wi-Fi" if lang == "es" else "Connect to Wi-Fi", fill=(11, 15, 25), font=font_ui_bold)

    # Secondary Action Button: Copiar Contraseña
    d2.rounded_rectangle([sx + 60, sheet_y + 460, sx + sw - 60, sheet_y + 550], radius=20, fill=(30, 41, 59), outline=(71, 85, 105), width=2)
    d2.text((sx + 180, sheet_y + 488), "Copiar Contraseña" if lang == "es" else "Copy Password", fill=(255, 255, 255), font=font_ui_bold)

    # Favorite & Share Pill Row
    d2.rounded_rectangle([sx + 60, sheet_y + 580, sx + sw // 2 - 20, sheet_y + 660], radius=16, fill=(30, 41, 59))
    d2.text((sx + 120, sheet_y + 605), "⭐ Favorito" if lang == "es" else "⭐ Favorite", fill=(250, 204, 21), font=font_ui_bold)
    d2.rounded_rectangle([sx + sw // 2 + 20, sheet_y + 580, sx + sw - 60, sheet_y + 660], radius=16, fill=(30, 41, 59))
    d2.text((sx + sw // 2 + 70, sheet_y + 605), "↗ Compartir" if lang == "es" else "↗ Share", fill=(255, 255, 255), font=font_ui_bold)

    s2_path = os.path.join(ASSETS_DIR, f"screenshot_2_{lang}_actions.png")
    s2.save(s2_path, "PNG")
    print(f"Generated {s2_path}")

    # ---------------- SCREENSHOT 3: GENERATOR ----------------
    s3 = Image.new("RGB", (w, h), (11, 15, 25))
    d3 = ImageDraw.Draw(s3)
    hl3 = "Crea Códigos QR Offline" if lang == "es" else "Offline QR Generator"
    sub3 = "Para Wi-Fi, WhatsApp, URLs, Texto y Email" if lang == "es" else "Create QRs for URLs, Wi-Fi, WhatsApp & more"
    d3.text((70, 140), hl3, fill=(16, 185, 129), font=font_hl)
    d3.text((70, 230), sub3, fill=(203, 213, 225), font=font_sub)

    draw_phone_frame(d3, px, py, pw, ph, pr)
    d3.rounded_rectangle([sx, sy, sx + sw, sy + sh], radius=pr - 10, fill=(11, 15, 25))

    # Top Bar
    d3.text((sx + 50, sy + 40), "Crear Código QR" if lang == "es" else "Create QR Code", fill=(255, 255, 255), font=font_ui_title)

    # Category Selector Pills
    cats = ["🔗 Enlace", "📶 Wi-Fi", "📝 Texto", "💬 WhatsApp"] if lang == "es" else ["🔗 URL", "📶 Wi-Fi", "📝 Text", "💬 WhatsApp"]
    cx = sx + 40
    for idx, cat in enumerate(cats):
        is_sel = idx == 0
        bg_col = (16, 185, 129) if is_sel else (18, 24, 38)
        fg_col = (11, 15, 25) if is_sel else (203, 213, 225)
        d3.rounded_rectangle([cx, sy + 110, cx + 180, sy + 170], radius=16, fill=bg_col)
        d3.text((cx + 20, sy + 125), cat, fill=fg_col, font=font_ui_bold)
        cx += 190

    # Input Fields
    d3.text((sx + 50, sy + 210), "Enlace o Sitio Web" if lang == "es" else "Website URL", fill=(148, 163, 184), font=font_ui_body)
    d3.rounded_rectangle([sx + 40, sy + 250, sx + sw - 40, sy + 330], radius=16, fill=(18, 24, 38), outline=(51, 65, 85), width=2)
    d3.text((sx + 60, sy + 275), "https://kabrima.com", fill=(255, 255, 255), font=font_ui_body)

    # QR Code Card in Center
    card_y = sy + 380
    d3.rounded_rectangle([sx + 100, card_y, sx + sw - 100, card_y + 600], radius=24, fill=(255, 255, 255))
    draw_qr_corner_pattern(d3, sx + 160, card_y + 60, 110, (11, 15, 25))
    draw_qr_corner_pattern(d3, sx + sw - 270, card_y + 60, 110, (11, 15, 25))
    draw_qr_corner_pattern(d3, sx + 160, card_y + 430, 110, (11, 15, 25))

    # Action Buttons: Guardar en Galería & Compartir
    d3.rounded_rectangle([sx + 50, card_y + 660, sx + sw - 50, card_y + 750], radius=20, fill=(16, 185, 129))
    d3.text((sx + 160, card_y + 688), "💾 Guardar en Galería (PNG)" if lang == "es" else "💾 Save to Gallery (PNG)", fill=(11, 15, 25), font=font_ui_bold)

    d3.rounded_rectangle([sx + 50, card_y + 780, sx + sw - 50, card_y + 870], radius=20, fill=(18, 24, 38), outline=(71, 85, 105), width=2)
    d3.text((sx + 240, card_y + 808), "↗ Compartir QR" if lang == "es" else "↗ Share QR", fill=(255, 255, 255), font=font_ui_bold)

    s3_path = os.path.join(ASSETS_DIR, f"screenshot_3_{lang}_generator.png")
    s3.save(s3_path, "PNG")
    print(f"Generated {s3_path}")

    # ---------------- SCREENSHOT 4: HISTORY & PRIVACY ----------------
    s4 = Image.new("RGB", (w, h), (11, 15, 25))
    d4 = ImageDraw.Draw(s4)
    hl4 = "Historial y Privacidad" if lang == "es" else "History & Total Privacy"
    sub4 = "Búsqueda instantánea • 100% Offline • Cero Anuncios" if lang == "es" else "Instant search • 100% Offline • Zero Ads"
    d4.text((70, 140), hl4, fill=(16, 185, 129), font=font_hl)
    d4.text((70, 230), sub4, fill=(203, 213, 225), font=font_sub)

    draw_phone_frame(d4, px, py, pw, ph, pr)
    d4.rounded_rectangle([sx, sy, sx + sw, sy + sh], radius=pr - 10, fill=(11, 15, 25))

    # Top Bar with search icon and count
    d4.text((sx + 50, sy + 40), "Historial" if lang == "es" else "History", fill=(255, 255, 255), font=font_ui_title)
    d4.text((sx + 50, sy + 85), "18 códigos guardados" if lang == "es" else "18 saved codes", fill=(148, 163, 184), font=font_ui_sm)
    d4.text((sx + sw - 80, sy + 45), "🔍", fill=(255, 255, 255), font=font_ui_bold)

    # Search Bar
    d4.rounded_rectangle([sx + 40, sy + 130, sx + sw - 40, sy + 210], radius=16, fill=(18, 24, 38), outline=(51, 65, 85), width=2)
    d4.text((sx + 70, sy + 155), "Buscar por título, contenido..." if lang == "es" else "Search by title, URL...", fill=(148, 163, 184), font=font_ui_body)

    # History Items List
    items = [
        ("🌐 Kabrima Oficial", "https://kabrima.com", "Hace 5 minutos", True),
        ("📶 Wi-Fi Oficina Central", "Red: WPA2 | SSID: TechHub_5G", "Hace 2 horas", True),
        ("👤 Tarjeta Carlos Mendoza", "Tel: +591 70012345 | vCard", "Ayer", False),
        ("💬 Pedido WhatsApp #402", "https://wa.me/59170012345", "Hace 3 días", False),
        ("📝 Código de Activación", "KBM-2026-X992", "Hace 1 semana", False)
    ]
    iy = sy + 240
    for title, desc, date, is_fav in items:
        d4.rounded_rectangle([sx + 40, iy, sx + sw - 40, iy + 130], radius=18, fill=(18, 24, 38))
        d4.text((sx + 70, iy + 25), title, fill=(255, 255, 255), font=font_ui_bold)
        d4.text((sx + 70, iy + 65), desc, fill=(148, 163, 184), font=font_ui_sm)
        d4.text((sx + 70, iy + 95), date, fill=(100, 116, 139), font=font_ui_sm)
        if is_fav:
            d4.text((sx + sw - 90, iy + 45), "⭐", fill=(250, 204, 21), font=font_ui_bold)
        iy += 150

    s4_path = os.path.join(ASSETS_DIR, f"screenshot_4_{lang}_history.png")
    s4.save(s4_path, "PNG")
    print(f"Generated {s4_path}")

def generate_mipmaps(app_icon_img):
    """Generates ic_launcher.png and ic_launcher_round.png for all densities."""
    densities = {
        "mipmap-mdpi": 48,
        "mipmap-hdpi": 72,
        "mipmap-xhdpi": 96,
        "mipmap-xxhdpi": 144,
        "mipmap-xxxhdpi": 192
    }

    for folder, dim in densities.items():
        folder_path = os.path.join(RES_DIR, folder)
        os.makedirs(folder_path, exist_ok=True)

        # Standard square/squircle
        resized = app_icon_img.resize((dim, dim), Image.Resampling.LANCZOS)
        out_square = os.path.join(folder_path, "ic_launcher.png")
        resized.save(out_square, "PNG")

        # Round icon with circular mask
        mask = Image.new("L", (dim, dim), 0)
        m_draw = ImageDraw.Draw(mask)
        m_draw.ellipse([0, 0, dim, dim], fill=255)
        
        round_img = Image.new("RGBA", (dim, dim), (0, 0, 0, 0))
        round_img.paste(resized, (0, 0), mask)
        out_round = os.path.join(folder_path, "ic_launcher_round.png")
        round_img.save(out_round, "PNG")
        print(f"Generated {out_square} and {out_round} ({dim}x{dim})")

if __name__ == "__main__":
    icon = generate_app_icon()
    generate_tip_coffee()
    generate_tip_lunch()
    generate_tip_supporter()
    generate_feature_graphic(lang="es")
    generate_feature_graphic(lang="en")
    generate_screenshots(lang="es")
    generate_screenshots(lang="en")
    generate_mipmaps(icon)
    print("All graphic assets successfully created!")

