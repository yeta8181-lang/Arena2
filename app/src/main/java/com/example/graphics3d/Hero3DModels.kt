package com.example.graphics3d

import androidx.compose.ui.graphics.Color
import kotlin.math.*

object Hero3DModels {

    // Helper: Create a 3D Box / Cuboid
    fun createBox(
        center: Vec3,
        size: Vec3,
        color: Color,
        isEmissive: Boolean = false
    ): List<Polygon3D> {
        val hx = size.x / 2f
        val hy = size.y / 2f
        val hz = size.z / 2f

        val p000 = center + Vec3(-hx, -hy, -hz)
        val p001 = center + Vec3(-hx, -hy, hz)
        val p010 = center + Vec3(-hx, hy, -hz)
        val p011 = center + Vec3(-hx, hy, hz)
        val p100 = center + Vec3(hx, -hy, -hz)
        val p101 = center + Vec3(hx, -hy, hz)
        val p110 = center + Vec3(hx, hy, -hz)
        val p111 = center + Vec3(hx, hy, hz)

        return listOf(
            // Front (Z+)
            Polygon3D(listOf(p001, p101, p111, p011), color, isEmissive),
            // Back (Z-)
            Polygon3D(listOf(p100, p000, p010, p110), color.copy(alpha = color.alpha), isEmissive),
            // Top (Y+)
            Polygon3D(listOf(p011, p111, p110, p010), color.copy(alpha = color.alpha), isEmissive),
            // Bottom (Y-)
            Polygon3D(listOf(p000, p100, p101, p001), color.copy(alpha = color.alpha), isEmissive),
            // Right (X+)
            Polygon3D(listOf(p101, p100, p110, p111), color.copy(alpha = color.alpha), isEmissive),
            // Left (X-)
            Polygon3D(listOf(p000, p001, p011, p010), color.copy(alpha = color.alpha), isEmissive)
        )
    }

    // Helper: Create a Prism / Cylinder column
    fun createPrism(
        center: Vec3,
        radius: Float,
        height: Float,
        segments: Int,
        color: Color,
        isEmissive: Boolean = false
    ): List<Polygon3D> {
        val polygons = mutableListOf<Polygon3D>()
        val halfH = height / 2f
        val angleStep = (2 * Math.PI / segments).toFloat()

        val topVerts = mutableListOf<Vec3>()
        val bottomVerts = mutableListOf<Vec3>()

        for (i in 0 until segments) {
            val a = i * angleStep
            val x = cos(a) * radius
            val z = sin(a) * radius
            topVerts.add(center + Vec3(x, halfH, z))
            bottomVerts.add(center + Vec3(x, -halfH, z))
        }

        // Side walls
        for (i in 0 until segments) {
            val next = (i + 1) % segments
            polygons.add(
                Polygon3D(
                    listOf(bottomVerts[i], bottomVerts[next], topVerts[next], topVerts[i]),
                    color,
                    isEmissive
                )
            )
        }

        // Top cap
        polygons.add(Polygon3D(topVerts, color, isEmissive))
        // Bottom cap
        polygons.add(Polygon3D(bottomVerts.reversed(), color, isEmissive))

        return polygons
    }

    // Common 3D Rotating Pedestal
    fun createPedestal(themeColor: Color): Mesh3D {
        val base = createPrism(
            center = Vec3(0f, -1.1f, 0f),
            radius = 1.35f,
            height = 0.25f,
            segments = 8,
            color = Color(0xFF1E2638)
        )
        val ring = createPrism(
            center = Vec3(0f, -0.96f, 0f),
            radius = 1.15f,
            height = 0.06f,
            segments = 8,
            color = themeColor,
            isEmissive = true
        )
        return Mesh3D(base + ring)
    }

    // 1. Okçu Elf
    fun createElfArcherMesh(animTick: Float): Mesh3D {
        val polys = mutableListOf<Polygon3D>()
        val green = Color(0xFF10B981)
        val darkGreen = Color(0xFF047857)
        val wood = Color(0xFF92400E)
        val skin = Color(0xFFFDE68A)
        val gold = Color(0xFFFCD34D)

        val breath = sin(animTick * 3f) * 0.03f

        // Pedestal
        polys.addAll(createPedestal(green).polygons)

        // Boots & Legs
        polys.addAll(createBox(Vec3(-0.25f, -0.6f, 0f), Vec3(0.2f, 0.6f, 0.25f), darkGreen))
        polys.addAll(createBox(Vec3(0.25f, -0.6f, 0f), Vec3(0.2f, 0.6f, 0.25f), darkGreen))

        // Torso / Tunic
        polys.addAll(createBox(Vec3(0f, breath, 0f), Vec3(0.6f, 0.65f, 0.4f), green))
        // Belt
        polys.addAll(createBox(Vec3(0f, -0.28f + breath, 0f), Vec3(0.62f, 0.1f, 0.42f), wood))

        // Head & Hood
        polys.addAll(createBox(Vec3(0f, 0.55f + breath, 0.05f), Vec3(0.38f, 0.42f, 0.38f), skin))
        // Pointed Hood Top
        polys.addAll(createPrism(Vec3(0f, 0.82f + breath, -0.05f), 0.24f, 0.28f, 4, darkGreen))
        // Headband
        polys.addAll(createBox(Vec3(0f, 0.62f + breath, 0.06f), Vec3(0.42f, 0.08f, 0.42f), gold, isEmissive = true))

        // Quiver on Back
        polys.addAll(createPrism(Vec3(0.2f, 0.1f + breath, -0.26f), 0.12f, 0.65f, 6, wood))
        // Arrows in quiver
        polys.addAll(createBox(Vec3(0.2f, 0.5f + breath, -0.26f), Vec3(0.12f, 0.2f, 0.12f), gold, isEmissive = true))

        // Curved Longbow in Left Hand
        val bowX = -0.55f
        polys.addAll(createBox(Vec3(bowX, breath, 0.2f), Vec3(0.08f, 1.3f, 0.08f), wood))
        polys.addAll(createBox(Vec3(bowX, 0.65f + breath, 0.05f), Vec3(0.08f, 0.25f, 0.2f), wood))
        polys.addAll(createBox(Vec3(bowX, -0.65f + breath, 0.05f), Vec3(0.08f, 0.25f, 0.2f), wood))
        // Glowing Arrow
        polys.addAll(createBox(Vec3(-0.1f, breath, 0.2f), Vec3(0.9f, 0.05f, 0.05f), green, isEmissive = true))

        return Mesh3D(polys)
    }

    // 2. Siyah Ork
    fun createBlackOrcMesh(animTick: Float): Mesh3D {
        val polys = mutableListOf<Polygon3D>()
        val darkGreenSkin = Color(0xFF1E3A1E)
        val ironBlack = Color(0xFF1F242D)
        val bloodRed = Color(0xFFDC2626)
        val bone = Color(0xFFE5E7EB)

        val breath = sin(animTick * 2.5f) * 0.04f

        polys.addAll(createPedestal(bloodRed).polygons)

        // Massive Legs
        polys.addAll(createBox(Vec3(-0.35f, -0.55f, 0f), Vec3(0.32f, 0.7f, 0.35f), ironBlack))
        polys.addAll(createBox(Vec3(0.35f, -0.55f, 0f), Vec3(0.32f, 0.7f, 0.35f), ironBlack))

        // Broad Heavy Torso
        polys.addAll(createBox(Vec3(0f, 0.05f + breath, 0f), Vec3(0.95f, 0.75f, 0.55f), darkGreenSkin))
        // Iron Chestplate
        polys.addAll(createBox(Vec3(0f, 0.05f + breath, 0.1f), Vec3(0.85f, 0.6f, 0.45f), ironBlack))

        // Spiked Shoulder Pauldrons
        polys.addAll(createBox(Vec3(-0.62f, 0.35f + breath, 0f), Vec3(0.35f, 0.35f, 0.45f), ironBlack))
        polys.addAll(createPrism(Vec3(-0.62f, 0.55f + breath, 0f), 0.08f, 0.25f, 4, bone))

        polys.addAll(createBox(Vec3(0.62f, 0.35f + breath, 0f), Vec3(0.35f, 0.35f, 0.45f), ironBlack))
        polys.addAll(createPrism(Vec3(0.62f, 0.55f + breath, 0f), 0.08f, 0.25f, 4, bone))

        // Brutal Orc Head & Horned Helm
        polys.addAll(createBox(Vec3(0f, 0.58f + breath, 0.1f), Vec3(0.5f, 0.45f, 0.45f), darkGreenSkin))
        polys.addAll(createBox(Vec3(0f, 0.68f + breath, 0.1f), Vec3(0.54f, 0.25f, 0.48f), ironBlack))
        // Glowing Red Visor / Eyes
        polys.addAll(createBox(Vec3(0f, 0.58f + breath, 0.34f), Vec3(0.3f, 0.08f, 0.04f), bloodRed, isEmissive = true))

        // Massive Double-Bitted Battleaxe
        val axeX = 0.75f
        polys.addAll(createBox(Vec3(axeX, breath, 0.2f), Vec3(0.1f, 1.6f, 0.1f), Color(0xFF4B5563)))
        // Axe Blades
        polys.addAll(createBox(Vec3(axeX + 0.3f, 0.55f + breath, 0.2f), Vec3(0.45f, 0.45f, 0.06f), ironBlack))
        polys.addAll(createBox(Vec3(axeX - 0.3f, 0.55f + breath, 0.2f), Vec3(0.45f, 0.45f, 0.06f), ironBlack))
        // Crimson Edge
        polys.addAll(createBox(Vec3(axeX + 0.53f, 0.55f + breath, 0.2f), Vec3(0.06f, 0.5f, 0.07f), bloodRed, isEmissive = true))
        polys.addAll(createBox(Vec3(axeX - 0.53f, 0.55f + breath, 0.2f), Vec3(0.06f, 0.5f, 0.07f), bloodRed, isEmissive = true))

        return Mesh3D(polys)
    }

    // 3. Zırhlı İnsan
    fun createArmoredKnightMesh(animTick: Float): Mesh3D {
        val polys = mutableListOf<Polygon3D>()
        val steel = Color(0xFF94A3B8)
        val brightSteel = Color(0xFFCBD5E1)
        val royalGold = Color(0xFFF59E0B)
        val blueTrim = Color(0xFF2563EB)

        val breath = sin(animTick * 3f) * 0.03f

        polys.addAll(createPedestal(royalGold).polygons)

        // Greaves & Sabatons
        polys.addAll(createBox(Vec3(-0.25f, -0.55f, 0f), Vec3(0.24f, 0.7f, 0.28f), steel))
        polys.addAll(createBox(Vec3(0.25f, -0.55f, 0f), Vec3(0.24f, 0.7f, 0.28f), steel))

        // Cuirass / Breastplate
        polys.addAll(createBox(Vec3(0f, 0.05f + breath, 0f), Vec3(0.72f, 0.7f, 0.45f), steel))
        polys.addAll(createBox(Vec3(0f, 0.05f + breath, 0.05f), Vec3(0.6f, 0.58f, 0.4f), brightSteel))
        // Gold Cross Trim
        polys.addAll(createBox(Vec3(0f, 0.05f + breath, 0.26f), Vec3(0.12f, 0.45f, 0.04f), royalGold, isEmissive = true))
        polys.addAll(createBox(Vec3(0f, 0.12f + breath, 0.26f), Vec3(0.42f, 0.12f, 0.04f), royalGold, isEmissive = true))

        // Pauldrons
        polys.addAll(createBox(Vec3(-0.48f, 0.35f + breath, 0f), Vec3(0.28f, 0.28f, 0.38f), royalGold))
        polys.addAll(createBox(Vec3(0.48f, 0.35f + breath, 0f), Vec3(0.28f, 0.28f, 0.38f), royalGold))

        // Helmet with Visor
        polys.addAll(createBox(Vec3(0f, 0.6f + breath, 0.05f), Vec3(0.42f, 0.45f, 0.42f), steel))
        polys.addAll(createBox(Vec3(0f, 0.62f + breath, 0.24f), Vec3(0.35f, 0.12f, 0.08f), Color(0xFF0F172A)))
        // Plume
        polys.addAll(createBox(Vec3(0f, 0.88f + breath, -0.05f), Vec3(0.08f, 0.25f, 0.38f), blueTrim))

        // Greatsword
        val swordX = 0.58f
        polys.addAll(createBox(Vec3(swordX, -0.1f + breath, 0.2f), Vec3(0.1f, 1.4f, 0.05f), brightSteel))
        polys.addAll(createBox(Vec3(swordX, 0.28f + breath, 0.2f), Vec3(0.42f, 0.08f, 0.08f), royalGold))
        polys.addAll(createBox(Vec3(swordX, -0.1f + breath, 0.2f), Vec3(0.04f, 1.3f, 0.06f), Color(0xFF60A5FA), isEmissive = true))

        return Mesh3D(polys)
    }

    // 4. Muhafız (Guardian / Paladin)
    fun createGuardianPaladinMesh(animTick: Float): Mesh3D {
        val polys = mutableListOf<Polygon3D>()
        val whiteArmor = Color(0xFFF8FAFC)
        val divineGold = Color(0xFFFBBF24)
        val azureShield = Color(0xFF0284C7)

        val breath = sin(animTick * 2.8f) * 0.03f

        polys.addAll(createPedestal(divineGold).polygons)

        // Golden Halo Ring
        polys.addAll(createPrism(Vec3(0f, 1.05f + breath, 0f), 0.4f, 0.05f, 12, divineGold, isEmissive = true))

        // Body
        polys.addAll(createBox(Vec3(-0.28f, -0.55f, 0f), Vec3(0.26f, 0.7f, 0.3f), whiteArmor))
        polys.addAll(createBox(Vec3(0.28f, -0.55f, 0f), Vec3(0.26f, 0.7f, 0.3f), whiteArmor))
        polys.addAll(createBox(Vec3(0f, 0.05f + breath, 0f), Vec3(0.8f, 0.75f, 0.5f), whiteArmor))
        polys.addAll(createBox(Vec3(0f, 0.62f + breath, 0.05f), Vec3(0.45f, 0.45f, 0.45f), divineGold))

        // Giant Tower Shield in Left Hand
        val shieldX = -0.55f
        polys.addAll(createBox(Vec3(shieldX, -0.05f + breath, 0.35f), Vec3(0.6f, 1.25f, 0.1f), azureShield))
        polys.addAll(createBox(Vec3(shieldX, -0.05f + breath, 0.42f), Vec3(0.12f, 0.85f, 0.04f), divineGold, isEmissive = true))
        polys.addAll(createBox(Vec3(shieldX, 0.15f + breath, 0.42f), Vec3(0.45f, 0.12f, 0.04f), divineGold, isEmissive = true))

        // Divine Warhammer in Right Hand
        val hammerX = 0.58f
        polys.addAll(createBox(Vec3(hammerX, breath, 0.2f), Vec3(0.1f, 1.2f, 0.1f), divineGold))
        polys.addAll(createBox(Vec3(hammerX, 0.5f + breath, 0.2f), Vec3(0.45f, 0.32f, 0.35f), whiteArmor))

        return Mesh3D(polys)
    }

    // 5. Robot Tank
    fun createRobotTankMesh(animTick: Float): Mesh3D {
        val polys = mutableListOf<Polygon3D>()
        val mechMetal = Color(0xFF334155)
        val mechCyan = Color(0xFF06B6D4)
        val hazardOrange = Color(0xFFF97316)
        val treadDark = Color(0xFF0F172A)

        val bob = sin(animTick * 4f) * 0.02f

        polys.addAll(createPedestal(mechCyan).polygons)

        // Heavy Tank Caterpillar Treads
        polys.addAll(createBox(Vec3(-0.48f, -0.6f, 0f), Vec3(0.3f, 0.45f, 1.0f), treadDark))
        polys.addAll(createBox(Vec3(0.48f, -0.6f, 0f), Vec3(0.3f, 0.45f, 1.0f), treadDark))

        // Main Chassis Hull
        polys.addAll(createBox(Vec3(0f, -0.2f + bob, 0f), Vec3(0.85f, 0.45f, 0.8f), mechMetal))

        // Upper Rotating Turret
        polys.addAll(createBox(Vec3(0f, 0.25f + bob, 0f), Vec3(0.75f, 0.55f, 0.65f), Color(0xFF1E293B)))
        // Neon Cyan Visor Sensor Line
        polys.addAll(createBox(Vec3(0f, 0.28f + bob, 0.34f), Vec3(0.55f, 0.1f, 0.04f), mechCyan, isEmissive = true))

        // Dual Shoulder Rocket Pods with Missiles
        polys.addAll(createBox(Vec3(-0.55f, 0.52f + bob, 0f), Vec3(0.35f, 0.32f, 0.5f), mechMetal))
        polys.addAll(createPrism(Vec3(-0.55f, 0.52f + bob, 0.28f), 0.08f, 0.15f, 6, hazardOrange, isEmissive = true))

        polys.addAll(createBox(Vec3(0.55f, 0.52f + bob, 0f), Vec3(0.35f, 0.32f, 0.5f), mechMetal))
        polys.addAll(createPrism(Vec3(0.55f, 0.52f + bob, 0.28f), 0.08f, 0.15f, 6, hazardOrange, isEmissive = true))

        // Forward Heavy Plasma Cannon
        polys.addAll(createPrism(Vec3(0f, 0.15f + bob, 0.6f), 0.12f, 0.65f, 8, Color(0xFF0F172A)))
        polys.addAll(createBox(Vec3(0f, 0.15f + bob, 0.95f), Vec3(0.18f, 0.18f, 0.06f), mechCyan, isEmissive = true))

        return Mesh3D(polys)
    }

    // 6. Taş Golemi
    fun createStoneGolemMesh(animTick: Float): Mesh3D {
        val polys = mutableListOf<Polygon3D>()
        val granite = Color(0xFF57534E)
        val darkRock = Color(0xFF292524)
        val runeGold = Color(0xFFFBBF24)

        val heave = sin(animTick * 1.8f) * 0.04f

        polys.addAll(createPedestal(runeGold).polygons)

        // Colossal Granite Legs
        polys.addAll(createBox(Vec3(-0.4f, -0.55f, 0f), Vec3(0.38f, 0.65f, 0.42f), darkRock))
        polys.addAll(createBox(Vec3(0.4f, -0.55f, 0f), Vec3(0.38f, 0.65f, 0.42f), darkRock))

        // Massive Boulder Torso
        polys.addAll(createBox(Vec3(0f, 0.08f + heave, 0f), Vec3(1.15f, 0.85f, 0.75f), granite))
        // Glowing Rune Cracks in Torso
        polys.addAll(createBox(Vec3(0f, 0.15f + heave, 0.39f), Vec3(0.5f, 0.12f, 0.04f), runeGold, isEmissive = true))
        polys.addAll(createBox(Vec3(0f, 0.0f + heave, 0.39f), Vec3(0.12f, 0.35f, 0.04f), runeGold, isEmissive = true))

        // Giant Rocky Shoulders & Fists
        polys.addAll(createBox(Vec3(-0.78f, 0.35f + heave, 0.1f), Vec3(0.48f, 0.48f, 0.52f), darkRock))
        polys.addAll(createBox(Vec3(-0.85f, -0.1f + heave, 0.25f), Vec3(0.42f, 0.42f, 0.45f), granite))

        polys.addAll(createBox(Vec3(0.78f, 0.35f + heave, 0.1f), Vec3(0.48f, 0.48f, 0.52f), darkRock))
        polys.addAll(createBox(Vec3(0.85f, -0.1f + heave, 0.25f), Vec3(0.42f, 0.42f, 0.45f), granite))

        // Head Block
        polys.addAll(createBox(Vec3(0f, 0.65f + heave, 0.15f), Vec3(0.55f, 0.38f, 0.45f), granite))
        // Glowing Golden Eyes
        polys.addAll(createBox(Vec3(-0.14f, 0.68f + heave, 0.39f), Vec3(0.1f, 0.08f, 0.04f), runeGold, isEmissive = true))
        polys.addAll(createBox(Vec3(0.14f, 0.68f + heave, 0.39f), Vec3(0.1f, 0.08f, 0.04f), runeGold, isEmissive = true))

        // Orbiting Rock Shard
        val shardAngle = animTick * 2.5f
        val sx = cos(shardAngle) * 1.1f
        val sz = sin(shardAngle) * 1.1f
        polys.addAll(createBox(Vec3(sx, 0.4f + sin(animTick * 3f) * 0.1f, sz), Vec3(0.2f, 0.2f, 0.2f), runeGold, isEmissive = true))

        return Mesh3D(polys)
    }

    // 7. Alev Büyücüsü
    fun createFireMageMesh(animTick: Float): Mesh3D {
        val polys = mutableListOf<Polygon3D>()
        val robeRed = Color(0xFFDC2626)
        val flameOrange = Color(0xFFF97316)
        val emberGold = Color(0xFFFBBF24)

        val floatY = sin(animTick * 3f) * 0.05f

        polys.addAll(createPedestal(flameOrange).polygons)

        // Robe Base (Conical Prism)
        polys.addAll(createPrism(Vec3(0f, -0.4f + floatY, 0f), 0.55f, 0.85f, 8, robeRed))
        polys.addAll(createBox(Vec3(0f, 0.15f + floatY, 0f), Vec3(0.6f, 0.55f, 0.4f), robeRed))

        // Hood & Mask
        polys.addAll(createBox(Vec3(0f, 0.58f + floatY, 0.05f), Vec3(0.42f, 0.42f, 0.4f), flameOrange))
        // Wizard Pointed Hat Top
        polys.addAll(createPrism(Vec3(0f, 0.95f + floatY, -0.05f), 0.22f, 0.45f, 4, robeRed))
        // Fiery Eyes
        polys.addAll(createBox(Vec3(0f, 0.58f + floatY, 0.26f), Vec3(0.28f, 0.08f, 0.04f), emberGold, isEmissive = true))

        // Staff of Fire in Right Hand
        val staffX = 0.55f
        polys.addAll(createPrism(Vec3(staffX, floatY, 0.15f), 0.06f, 1.45f, 6, Color(0xFF78350F)))
        // Orbiting Fireball on staff tip
        val fireY = 0.85f + floatY + sin(animTick * 5f) * 0.05f
        polys.addAll(createBox(Vec3(staffX, fireY, 0.15f), Vec3(0.32f, 0.32f, 0.32f), emberGold, isEmissive = true))
        polys.addAll(createBox(Vec3(staffX, fireY, 0.15f), Vec3(0.22f, 0.22f, 0.22f), flameOrange, isEmissive = true))

        return Mesh3D(polys)
    }

    // 8. Gölge Suikastçı
    fun createShadowAssassinMesh(animTick: Float): Mesh3D {
        val polys = mutableListOf<Polygon3D>()
        val shadowPurple = Color(0xFF6B21A8)
        val deepNight = Color(0xFF1E1B4B)
        val violetBlade = Color(0xFFA855F7)

        val sway = sin(animTick * 3.5f) * 0.03f

        polys.addAll(createPedestal(violetBlade).polygons)

        // Agile Legs
        polys.addAll(createBox(Vec3(-0.22f, -0.55f, 0f), Vec3(0.2f, 0.7f, 0.24f), deepNight))
        polys.addAll(createBox(Vec3(0.22f, -0.55f, 0f), Vec3(0.2f, 0.7f, 0.24f), deepNight))

        // Torso & Cloak
        polys.addAll(createBox(Vec3(0f, 0.02f + sway, 0f), Vec3(0.58f, 0.65f, 0.36f), deepNight))
        // Jagged Purple Cloak Back
        polys.addAll(createBox(Vec3(0f, -0.1f + sway, -0.22f), Vec3(0.68f, 0.95f, 0.08f), shadowPurple))

        // Cowl & Mask
        polys.addAll(createBox(Vec3(0f, 0.52f + sway, 0.05f), Vec3(0.38f, 0.38f, 0.38f), deepNight))
        // Glowing Violet Eye Slits
        polys.addAll(createBox(Vec3(0f, 0.54f + sway, 0.25f), Vec3(0.24f, 0.06f, 0.04f), violetBlade, isEmissive = true))

        // Dual Curved Obsidian Daggers in Reverse Grip
        polys.addAll(createBox(Vec3(-0.48f, -0.2f + sway, 0.2f), Vec3(0.06f, 0.65f, 0.12f), violetBlade, isEmissive = true))
        polys.addAll(createBox(Vec3(0.48f, -0.2f + sway, 0.2f), Vec3(0.06f, 0.65f, 0.12f), violetBlade, isEmissive = true))

        return Mesh3D(polys)
    }

    // 9. Şifacı Peri
    fun createHealerFairyMesh(animTick: Float): Mesh3D {
        val polys = mutableListOf<Polygon3D>()
        val petalTeal = Color(0xFF14B8A6)
        val paleDress = Color(0xFFCCFBF1)
        val wingCyan = Color(0xFF2DD4BF)
        val bloomGold = Color(0xFFFDE047)

        val levitate = sin(animTick * 4f) * 0.08f
        val wingFlap = sin(animTick * 12f) * 0.35f

        polys.addAll(createPedestal(petalTeal).polygons)

        // Floating Slender Body
        polys.addAll(createPrism(Vec3(0f, -0.15f + levitate, 0f), 0.32f, 0.7f, 6, paleDress))
        polys.addAll(createBox(Vec3(0f, 0.45f + levitate, 0.05f), Vec3(0.32f, 0.35f, 0.32f), Color(0xFFFEF3C7)))
        // Floral Circlet
        polys.addAll(createBox(Vec3(0f, 0.58f + levitate, 0.05f), Vec3(0.36f, 0.06f, 0.36f), bloomGold, isEmissive = true))

        // 4 Translucent Fluttering Butterfly Wings
        val wingZ = -0.18f
        // Top Left Wing
        polys.add(Polygon3D(listOf(
            Vec3(0f, 0.2f + levitate, wingZ),
            Vec3(-0.85f, 0.85f + levitate, wingZ + wingFlap),
            Vec3(-0.65f, 0.1f + levitate, wingZ)
        ), wingCyan.copy(alpha = 0.75f), isEmissive = true))
        // Top Right Wing
        polys.add(Polygon3D(listOf(
            Vec3(0f, 0.2f + levitate, wingZ),
            Vec3(0.85f, 0.85f + levitate, wingZ + wingFlap),
            Vec3(0.65f, 0.1f + levitate, wingZ)
        ), wingCyan.copy(alpha = 0.75f), isEmissive = true))

        // Lower Wings
        polys.add(Polygon3D(listOf(
            Vec3(0f, 0.1f + levitate, wingZ),
            Vec3(-0.6f, -0.45f + levitate, wingZ + wingFlap * 0.7f),
            Vec3(-0.25f, -0.1f + levitate, wingZ)
        ), wingCyan.copy(alpha = 0.75f), isEmissive = true))
        polys.add(Polygon3D(listOf(
            Vec3(0f, 0.1f + levitate, wingZ),
            Vec3(0.6f, -0.45f + levitate, wingZ + wingFlap * 0.7f),
            Vec3(0.25f, -0.1f + levitate, wingZ)
        ), wingCyan.copy(alpha = 0.75f), isEmissive = true))

        // Blossom Healing Staff
        val staffX = 0.45f
        polys.addAll(createBox(Vec3(staffX, levitate, 0.15f), Vec3(0.06f, 1.25f, 0.06f), bloomGold))
        polys.addAll(createBox(Vec3(staffX, 0.68f + levitate, 0.15f), Vec3(0.25f, 0.25f, 0.25f), petalTeal, isEmissive = true))

        return Mesh3D(polys)
    }

    // 10. Buz Cadısı
    fun createFrostWitchMesh(animTick: Float): Mesh3D {
        val polys = mutableListOf<Polygon3D>()
        val iceNavy = Color(0xFF0369A1)
        val frostCyan = Color(0xFF38BDF8)
        val pureIce = Color(0xFFE0F2FE)

        val hover = sin(animTick * 3.2f) * 0.05f

        polys.addAll(createPedestal(frostCyan).polygons)

        // Glacial Gown
        polys.addAll(createPrism(Vec3(0f, -0.35f + hover, 0f), 0.52f, 0.9f, 6, iceNavy))
        polys.addAll(createBox(Vec3(0f, 0.18f + hover, 0f), Vec3(0.55f, 0.52f, 0.38f), frostCyan))

        // Head & Face
        polys.addAll(createBox(Vec3(0f, 0.55f + hover, 0.05f), Vec3(0.36f, 0.38f, 0.36f), pureIce))

        // Floating Ice Crystal Tiara (3 Icicle Spikes)
        polys.addAll(createPrism(Vec3(0f, 0.85f + hover, 0.1f), 0.08f, 0.35f, 4, pureIce, isEmissive = true))
        polys.addAll(createPrism(Vec3(-0.16f, 0.8f + hover, 0.1f), 0.06f, 0.25f, 4, frostCyan, isEmissive = true))
        polys.addAll(createPrism(Vec3(0.16f, 0.8f + hover, 0.1f), 0.06f, 0.25f, 4, frostCyan, isEmissive = true))

        // Glacial Frost Staff
        val staffX = 0.52f
        polys.addAll(createBox(Vec3(staffX, hover, 0.15f), Vec3(0.06f, 1.4f, 0.06f), iceNavy))
        // Rotating Crystal Snowflake
        val crysAngle = animTick * 2f
        val crysRot = Vec3(cos(crysAngle) * 0.1f, 0.8f + hover, sin(crysAngle) * 0.1f + 0.15f)
        polys.addAll(createBox(crysRot, Vec3(0.3f, 0.3f, 0.3f), pureIce, isEmissive = true))

        return Mesh3D(polys)
    }

    fun getHeroMesh(heroId: String, animTick: Float): Mesh3D {
        return when (heroId) {
            "okcu_elf" -> createElfArcherMesh(animTick)
            "siyah_ork" -> createBlackOrcMesh(animTick)
            "zirhli_insan" -> createArmoredKnightMesh(animTick)
            "muhafiz" -> createGuardianPaladinMesh(animTick)
            "robot_tank" -> createRobotTankMesh(animTick)
            "tas_golemi" -> createStoneGolemMesh(animTick)
            "alev_buyucusu" -> createFireMageMesh(animTick)
            "golge_suikastci" -> createShadowAssassinMesh(animTick)
            "sifaci_peri" -> createHealerFairyMesh(animTick)
            "buz_cadisi" -> createFrostWitchMesh(animTick)
            else -> createElfArcherMesh(animTick)
        }
    }
}
