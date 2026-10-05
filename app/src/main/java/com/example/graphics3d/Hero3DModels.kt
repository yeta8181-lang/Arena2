package com.example.graphics3d

import androidx.compose.ui.graphics.Color
import kotlin.math.*

object Hero3DModels {

    // Helper: Create a Tapered Cylinder / Frustum for limbs, necks, and weapons
    fun createFrustum(
        start: Vec3,
        end: Vec3,
        radiusStart: Float,
        radiusEnd: Float,
        segments: Int = 8,
        color: Color,
        isMetallic: Boolean = false,
        isEmissive: Boolean = false
    ): List<Polygon3D> {
        val polygons = mutableListOf<Polygon3D>()
        val dir = end - start
        val length = dir.length()
        if (length < 0.0001f) return emptyList()

        val normDir = dir.normalize()
        // Find perpendicular axes
        val up = if (abs(normDir.y) < 0.9f) Vec3(0f, 1f, 0f) else Vec3(1f, 0f, 0f)
        val right = normDir.cross(up).normalize()
        val forward = normDir.cross(right).normalize()

        val startVerts = mutableListOf<Vec3>()
        val endVerts = mutableListOf<Vec3>()
        val angleStep = (2 * Math.PI / segments).toFloat()

        for (i in 0 until segments) {
            val a = i * angleStep
            val cosA = cos(a)
            val sinA = sin(a)
            val offsetStart = (right * cosA + forward * sinA) * radiusStart
            val offsetEnd = (right * cosA + forward * sinA) * radiusEnd
            startVerts.add(start + offsetStart)
            endVerts.add(end + offsetEnd)
        }

        // Side walls
        for (i in 0 until segments) {
            val next = (i + 1) % segments
            polygons.add(
                Polygon3D(
                    vertices = listOf(startVerts[i], startVerts[next], endVerts[next], endVerts[i]),
                    baseColor = color,
                    isMetallic = isMetallic,
                    isEmissive = isEmissive
                )
            )
        }

        // Caps
        polygons.add(Polygon3D(endVerts, color, isMetallic = isMetallic, isEmissive = isEmissive))
        polygons.add(Polygon3D(startVerts.reversed(), color, isMetallic = isMetallic, isEmissive = isEmissive))

        return polygons
    }

    // Helper: Create Beveled Polyhedral Armor Plate
    fun createBeveledBox(
        center: Vec3,
        size: Vec3,
        color: Color,
        isMetallic: Boolean = false,
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
            Polygon3D(listOf(p001, p101, p111, p011), color, isMetallic = isMetallic, isEmissive = isEmissive),
            Polygon3D(listOf(p100, p000, p010, p110), color, isMetallic = isMetallic, isEmissive = isEmissive),
            Polygon3D(listOf(p011, p111, p110, p010), color, isMetallic = isMetallic, isEmissive = isEmissive),
            Polygon3D(listOf(p000, p100, p101, p001), color, isMetallic = isMetallic, isEmissive = isEmissive),
            Polygon3D(listOf(p101, p100, p110, p111), color, isMetallic = isMetallic, isEmissive = isEmissive),
            Polygon3D(listOf(p000, p001, p011, p010), color, isMetallic = isMetallic, isEmissive = isEmissive)
        )
    }

    // Common Realistic Turntable Pedestal
    fun createRealisticPedestal(themeColor: Color): Mesh3D {
        val polys = mutableListOf<Polygon3D>()
        // Lower beveled plinth
        polys.addAll(createFrustum(Vec3(0f, -1.2f, 0f), Vec3(0f, -1.05f, 0f), 1.35f, 1.25f, 12, Color(0xFF151C2C)))
        // Upper runic disc
        polys.addAll(createFrustum(Vec3(0f, -1.05f, 0f), Vec3(0f, -0.96f, 0f), 1.25f, 1.15f, 12, Color(0xFF1E2638)))
        // Glowing neon energy ring
        polys.addAll(createFrustum(Vec3(0f, -0.96f, 0f), Vec3(0f, -0.92f, 0f), 1.05f, 0.98f, 12, themeColor, isMetallic = true, isEmissive = true))
        return Mesh3D(polys)
    }

    // 1. Okçu Elf (Realistic Elven Archer)
    fun createElfArcherMesh(animTick: Float): Mesh3D {
        val polys = mutableListOf<Polygon3D>()
        val elvenGreen = Color(0xFF059669)
        val forestLeather = Color(0xFF064E3B)
        val skin = Color(0xFFFDE68A)
        val gold = Color(0xFFFBBF24)
        val wood = Color(0xFF78350F)
        val silver = Color(0xFFE2E8F0)

        val breath = sin(animTick * 3f) * 0.03f

        polys.addAll(createRealisticPedestal(elvenGreen).polygons)

        // Anatomical Muscular Legs (Thigh -> Knee -> Calf -> Boot)
        // Left Leg
        polys.addAll(createFrustum(Vec3(-0.24f, -0.3f, 0f), Vec3(-0.25f, -0.65f, 0f), 0.14f, 0.11f, 8, forestLeather))
        polys.addAll(createFrustum(Vec3(-0.25f, -0.65f, 0f), Vec3(-0.26f, -0.92f, 0.05f), 0.11f, 0.08f, 8, forestLeather))
        polys.addAll(createBeveledBox(Vec3(-0.26f, -0.94f, 0.08f), Vec3(0.16f, 0.1f, 0.26f), Color(0xFF022C22)))
        // Right Leg
        polys.addAll(createFrustum(Vec3(0.24f, -0.3f, 0f), Vec3(0.25f, -0.65f, 0f), 0.14f, 0.11f, 8, forestLeather))
        polys.addAll(createFrustum(Vec3(0.25f, -0.65f, 0f), Vec3(0.26f, -0.92f, 0.05f), 0.11f, 0.08f, 8, forestLeather))
        polys.addAll(createBeveledBox(Vec3(0.26f, -0.94f, 0.08f), Vec3(0.16f, 0.1f, 0.26f), Color(0xFF022C22)))

        // Sculpted Torso (Waist -> Chest)
        polys.addAll(createFrustum(Vec3(0f, -0.28f + breath, 0f), Vec3(0f, 0.28f + breath, 0f), 0.24f, 0.32f, 10, elvenGreen))
        // Leather Belt with Gold Buckle
        polys.addAll(createFrustum(Vec3(0f, -0.25f + breath, 0f), Vec3(0f, -0.16f + breath, 0f), 0.26f, 0.26f, 10, Color(0xFF451A03)))
        polys.addAll(createBeveledBox(Vec3(0f, -0.2f + breath, 0.27f), Vec3(0.1f, 0.08f, 0.04f), gold, isMetallic = true))

        // Shoulders & Arms
        polys.addAll(createFrustum(Vec3(-0.35f, 0.24f + breath, 0f), Vec3(-0.5f, -0.05f + breath, 0.12f), 0.11f, 0.08f, 8, elvenGreen))
        polys.addAll(createFrustum(Vec3(-0.5f, -0.05f + breath, 0.12f), Vec3(-0.52f, 0.22f + breath, 0.3f), 0.08f, 0.07f, 8, skin))

        polys.addAll(createFrustum(Vec3(0.35f, 0.24f + breath, 0f), Vec3(0.48f, 0.0f + breath, -0.05f), 0.11f, 0.08f, 8, elvenGreen))

        // Neck & Head
        polys.addAll(createFrustum(Vec3(0f, 0.28f + breath, 0f), Vec3(0f, 0.42f + breath, 0.02f), 0.1f, 0.09f, 8, skin))
        polys.addAll(createFrustum(Vec3(0f, 0.42f + breath, 0.04f), Vec3(0f, 0.72f + breath, 0.04f), 0.18f, 0.16f, 10, skin))
        // Hood & Elven Circlet
        polys.addAll(createFrustum(Vec3(0f, 0.58f + breath, -0.02f), Vec3(0f, 0.88f + breath, -0.08f), 0.21f, 0.06f, 8, elvenGreen))
        polys.addAll(createFrustum(Vec3(0f, 0.58f + breath, 0.04f), Vec3(0f, 0.64f + breath, 0.04f), 0.2f, 0.2f, 10, gold, isMetallic = true, isEmissive = true))

        // Pointed Elven Ears
        polys.addAll(createFrustum(Vec3(-0.16f, 0.52f + breath, 0f), Vec3(-0.34f, 0.62f + breath, -0.1f), 0.05f, 0.01f, 4, skin))
        polys.addAll(createFrustum(Vec3(0.16f, 0.52f + breath, 0f), Vec3(0.34f, 0.62f + breath, -0.1f), 0.05f, 0.01f, 4, skin))

        // Back Quiver with Arrows
        polys.addAll(createFrustum(Vec3(0.2f, -0.15f + breath, -0.25f), Vec3(0.22f, 0.5f + breath, -0.28f), 0.1f, 0.13f, 8, wood))
        polys.addAll(createFrustum(Vec3(0.22f, 0.5f + breath, -0.28f), Vec3(0.24f, 0.72f + breath, -0.3f), 0.04f, 0.08f, 4, silver, isMetallic = true))

        // Realistic Curved Recurve Bow in Hand
        val bx = -0.52f
        polys.addAll(createFrustum(Vec3(bx, -0.55f + breath, 0.25f), Vec3(bx, 0.0f + breath, 0.35f), 0.04f, 0.06f, 6, wood))
        polys.addAll(createFrustum(Vec3(bx, 0.0f + breath, 0.35f), Vec3(bx, 0.55f + breath, 0.25f), 0.06f, 0.04f, 6, wood))
        polys.addAll(createFrustum(Vec3(bx, 0.55f + breath, 0.25f), Vec3(bx, 0.72f + breath, 0.15f), 0.04f, 0.02f, 4, gold, isMetallic = true))
        polys.addAll(createFrustum(Vec3(bx, -0.55f + breath, 0.25f), Vec3(bx, -0.72f + breath, 0.15f), 0.04f, 0.02f, 4, gold, isMetallic = true))
        // Glowing Arrow Ready to Fire
        polys.addAll(createFrustum(Vec3(bx, 0.15f + breath, 0.32f), Vec3(0.15f, 0.15f + breath, 0.32f), 0.025f, 0.025f, 4, silver, isMetallic = true))
        polys.addAll(createFrustum(Vec3(bx, 0.15f + breath, 0.32f), Vec3(bx - 0.12f, 0.15f + breath, 0.32f), 0.05f, 0.01f, 4, elvenGreen, isMetallic = true, isEmissive = true))

        return Mesh3D(polys)
    }

    // 2. Siyah Ork (Realistic Savage Orc Brawler)
    fun createBlackOrcMesh(animTick: Float): Mesh3D {
        val polys = mutableListOf<Polygon3D>()
        val darkOrcSkin = Color(0xFF1E3A20)
        val ironBlack = Color(0xFF1F242D)
        val steel = Color(0xFF64748B)
        val crimson = Color(0xFFDC2626)
        val bone = Color(0xFFF3F4F6)

        val breath = sin(animTick * 2.4f) * 0.04f

        polys.addAll(createRealisticPedestal(crimson).polygons)

        // Bulky Heavy Muscular Legs
        polys.addAll(createFrustum(Vec3(-0.35f, -0.25f, 0f), Vec3(-0.36f, -0.65f, 0f), 0.22f, 0.17f, 8, darkOrcSkin))
        polys.addAll(createFrustum(Vec3(-0.36f, -0.65f, 0f), Vec3(-0.38f, -0.92f, 0.05f), 0.18f, 0.14f, 8, ironBlack, isMetallic = true))
        polys.addAll(createBeveledBox(Vec3(-0.38f, -0.95f, 0.1f), Vec3(0.24f, 0.12f, 0.34f), ironBlack, isMetallic = true))

        polys.addAll(createFrustum(Vec3(0.35f, -0.25f, 0f), Vec3(0.36f, -0.65f, 0f), 0.22f, 0.17f, 8, darkOrcSkin))
        polys.addAll(createFrustum(Vec3(0.36f, -0.65f, 0f), Vec3(0.38f, -0.92f, 0.05f), 0.18f, 0.14f, 8, ironBlack, isMetallic = true))
        polys.addAll(createBeveledBox(Vec3(0.38f, -0.95f, 0.1f), Vec3(0.24f, 0.12f, 0.34f), ironBlack, isMetallic = true))

        // Broad Massive Torso with Iron Harness & Spikes
        polys.addAll(createFrustum(Vec3(0f, -0.25f + breath, 0f), Vec3(0f, 0.35f + breath, 0f), 0.38f, 0.52f, 10, darkOrcSkin))
        polys.addAll(createBeveledBox(Vec3(0f, 0.1f + breath, 0.12f), Vec3(0.72f, 0.48f, 0.35f), ironBlack, isMetallic = true))

        // Spiked Curved Shoulder Armor (Pauldrons)
        polys.addAll(createFrustum(Vec3(-0.6f, 0.32f + breath, 0f), Vec3(-0.75f, 0.48f + breath, 0f), 0.26f, 0.18f, 8, ironBlack, isMetallic = true))
        polys.addAll(createFrustum(Vec3(-0.72f, 0.48f + breath, 0f), Vec3(-0.78f, 0.72f + breath, 0f), 0.08f, 0.02f, 4, bone))

        polys.addAll(createFrustum(Vec3(0.6f, 0.32f + breath, 0f), Vec3(0.75f, 0.48f + breath, 0f), 0.26f, 0.18f, 8, ironBlack, isMetallic = true))
        polys.addAll(createFrustum(Vec3(0.72f, 0.48f + breath, 0f), Vec3(0.78f, 0.72f + breath, 0f), 0.08f, 0.02f, 4, bone))

        // Orc Brawny Arms
        polys.addAll(createFrustum(Vec3(-0.55f, 0.22f + breath, 0f), Vec3(-0.72f, -0.1f + breath, 0.15f), 0.16f, 0.13f, 8, darkOrcSkin))
        polys.addAll(createFrustum(Vec3(-0.72f, -0.1f + breath, 0.15f), Vec3(-0.62f, -0.38f + breath, 0.35f), 0.14f, 0.12f, 8, darkOrcSkin))

        polys.addAll(createFrustum(Vec3(0.55f, 0.22f + breath, 0f), Vec3(0.75f, 0.05f + breath, 0.2f), 0.16f, 0.13f, 8, darkOrcSkin))

        // Orc Jaw, Tusks & War Helm
        polys.addAll(createFrustum(Vec3(0f, 0.35f + breath, 0.05f), Vec3(0f, 0.72f + breath, 0.12f), 0.26f, 0.24f, 10, darkOrcSkin))
        // Horned War Helm
        polys.addAll(createBeveledBox(Vec3(0f, 0.72f + breath, 0.1f), Vec3(0.54f, 0.28f, 0.46f), ironBlack, isMetallic = true))
        // Curved Horns on Helm
        polys.addAll(createFrustum(Vec3(-0.25f, 0.75f + breath, 0.1f), Vec3(-0.52f, 0.98f + breath, -0.05f), 0.09f, 0.02f, 6, bone))
        polys.addAll(createFrustum(Vec3(0.25f, 0.75f + breath, 0.1f), Vec3(0.52f, 0.98f + breath, -0.05f), 0.09f, 0.02f, 6, bone))
        // Lower Tusks
        polys.addAll(createFrustum(Vec3(-0.12f, 0.42f + breath, 0.34f), Vec3(-0.14f, 0.58f + breath, 0.36f), 0.04f, 0.015f, 4, bone))
        polys.addAll(createFrustum(Vec3(0.12f, 0.42f + breath, 0.34f), Vec3(0.14f, 0.58f + breath, 0.36f), 0.04f, 0.015f, 4, bone))
        // Glowing Red Eyes
        polys.addAll(createBeveledBox(Vec3(0f, 0.62f + breath, 0.33f), Vec3(0.3f, 0.06f, 0.04f), crimson, isEmissive = true))

        // Heavy Two-Handed Greataxe
        val axeX = 0.78f
        polys.addAll(createFrustum(Vec3(axeX, -0.6f + breath, 0.3f), Vec3(axeX, 0.95f + breath, 0.3f), 0.06f, 0.06f, 6, Color(0xFF374151), isMetallic = true))
        // Double Curved Blades
        polys.addAll(createFrustum(Vec3(axeX, 0.65f + breath, 0.3f), Vec3(axeX + 0.42f, 0.65f + breath, 0.3f), 0.32f, 0.45f, 4, steel, isMetallic = true))
        polys.addAll(createFrustum(Vec3(axeX, 0.65f + breath, 0.3f), Vec3(axeX - 0.42f, 0.65f + breath, 0.3f), 0.32f, 0.45f, 4, steel, isMetallic = true))
        // Bloodied Red Cutting Edge
        polys.addAll(createBeveledBox(Vec3(axeX + 0.45f, 0.65f + breath, 0.3f), Vec3(0.04f, 0.55f, 0.06f), crimson, isMetallic = true, isEmissive = true))
        polys.addAll(createBeveledBox(Vec3(axeX - 0.45f, 0.65f + breath, 0.3f), Vec3(0.04f, 0.55f, 0.06f), crimson, isMetallic = true, isEmissive = true))

        return Mesh3D(polys)
    }

    // 3. Zırhlı İnsan (Realistic Medieval Plate Armor Knight)
    fun createArmoredKnightMesh(animTick: Float): Mesh3D {
        val polys = mutableListOf<Polygon3D>()
        val polishedSteel = Color(0xFFCBD5E1)
        val darkSteel = Color(0xFF475569)
        val royalGold = Color(0xFFF59E0B)
        val velvetBlue = Color(0xFF1D4ED8)

        val breath = sin(animTick * 2.8f) * 0.03f

        polys.addAll(createRealisticPedestal(royalGold).polygons)

        // Articulated Steel Plate Greaves & Sabatons
        polys.addAll(createFrustum(Vec3(-0.25f, -0.25f, 0f), Vec3(-0.25f, -0.65f, 0f), 0.17f, 0.13f, 8, polishedSteel, isMetallic = true))
        polys.addAll(createFrustum(Vec3(-0.25f, -0.65f, 0f), Vec3(-0.26f, -0.92f, 0.04f), 0.13f, 0.1f, 8, polishedSteel, isMetallic = true))
        polys.addAll(createBeveledBox(Vec3(-0.26f, -0.95f, 0.08f), Vec3(0.18f, 0.1f, 0.28f), darkSteel, isMetallic = true))

        polys.addAll(createFrustum(Vec3(0.25f, -0.25f, 0f), Vec3(0.25f, -0.65f, 0f), 0.17f, 0.13f, 8, polishedSteel, isMetallic = true))
        polys.addAll(createFrustum(Vec3(0.25f, -0.65f, 0f), Vec3(0.26f, -0.92f, 0.04f), 0.13f, 0.1f, 8, polishedSteel, isMetallic = true))
        polys.addAll(createBeveledBox(Vec3(0.26f, -0.95f, 0.08f), Vec3(0.18f, 0.1f, 0.28f), darkSteel, isMetallic = true))

        // Contoured Steel Cuirass (Breastplate with central ridge)
        polys.addAll(createFrustum(Vec3(0f, -0.25f + breath, 0f), Vec3(0f, 0.32f + breath, 0f), 0.28f, 0.38f, 10, polishedSteel, isMetallic = true))
        // Golden Cross Emblem on Chest
        polys.addAll(createBeveledBox(Vec3(0f, 0.08f + breath, 0.28f), Vec3(0.1f, 0.35f, 0.03f), royalGold, isMetallic = true))
        polys.addAll(createBeveledBox(Vec3(0f, 0.15f + breath, 0.28f), Vec3(0.32f, 0.09f, 0.03f), royalGold, isMetallic = true))

        // Flanged Steel Pauldrons (Shoulder Guards with Gold Rim)
        polys.addAll(createFrustum(Vec3(-0.45f, 0.25f + breath, 0f), Vec3(-0.58f, 0.42f + breath, 0f), 0.2f, 0.14f, 8, polishedSteel, isMetallic = true))
        polys.addAll(createFrustum(Vec3(0.45f, 0.25f + breath, 0f), Vec3(0.58f, 0.42f + breath, 0f), 0.2f, 0.14f, 8, polishedSteel, isMetallic = true))

        // Arms & Vambraces
        polys.addAll(createFrustum(Vec3(-0.45f, 0.22f + breath, 0f), Vec3(-0.55f, -0.1f + breath, 0.1f), 0.11f, 0.09f, 8, polishedSteel, isMetallic = true))
        polys.addAll(createFrustum(Vec3(0.45f, 0.22f + breath, 0f), Vec3(0.58f, 0.0f + breath, 0.15f), 0.11f, 0.09f, 8, polishedSteel, isMetallic = true))

        // Armet Helmet with Slotted Visor & Royal Blue Plume
        polys.addAll(createFrustum(Vec3(0f, 0.32f + breath, 0f), Vec3(0f, 0.72f + breath, 0.02f), 0.22f, 0.2f, 10, polishedSteel, isMetallic = true))
        polys.addAll(createBeveledBox(Vec3(0f, 0.52f + breath, 0.22f), Vec3(0.24f, 0.06f, 0.06f), Color(0xFF0F172A), isMetallic = true))
        // Blue Feathered Crest
        polys.addAll(createFrustum(Vec3(0f, 0.7f + breath, -0.05f), Vec3(0f, 0.98f + breath, -0.18f), 0.06f, 0.18f, 6, velvetBlue))

        // Realistic Medieval Arming Longsword
        val swordX = 0.62f
        // Pommel & Grip
        polys.addAll(createFrustum(Vec3(swordX, -0.45f + breath, 0.2f), Vec3(swordX, 0.85f + breath, 0.2f), 0.03f, 0.015f, 4, polishedSteel, isMetallic = true))
        // Crossguard
        polys.addAll(createBeveledBox(Vec3(swordX, 0.05f + breath, 0.2f), Vec3(0.38f, 0.06f, 0.06f), royalGold, isMetallic = true))
        // Glowing Blue Edge Energy
        polys.addAll(createBeveledBox(Vec3(swordX, -0.2f + breath, 0.2f), Vec3(0.02f, 0.5f, 0.04f), Color(0xFF60A5FA), isMetallic = true, isEmissive = true))

        return Mesh3D(polys)
    }

    // 4. Muhafız (Realistic Paladin Guardian)
    fun createGuardianPaladinMesh(animTick: Float): Mesh3D {
        val polys = mutableListOf<Polygon3D>()
        val divineWhite = Color(0xFFF8FAFC)
        val gold = Color(0xFFFBBF24)
        val sapphire = Color(0xFF0284C7)

        val breath = sin(animTick * 2.6f) * 0.03f

        polys.addAll(createRealisticPedestal(gold).polygons)

        // Floating Divine Halo Ring
        polys.addAll(createFrustum(Vec3(0f, 1.05f + breath, 0f), Vec3(0f, 1.1f + breath, 0f), 0.38f, 0.38f, 12, gold, isMetallic = true, isEmissive = true))

        // Legs
        polys.addAll(createFrustum(Vec3(-0.26f, -0.25f, 0f), Vec3(-0.27f, -0.92f, 0.04f), 0.18f, 0.11f, 8, divineWhite, isMetallic = true))
        polys.addAll(createFrustum(Vec3(0.26f, -0.25f, 0f), Vec3(0.27f, -0.92f, 0.04f), 0.18f, 0.11f, 8, divineWhite, isMetallic = true))

        // Heavy Curving Armor & Helmet
        polys.addAll(createFrustum(Vec3(0f, -0.25f + breath, 0f), Vec3(0f, 0.38f + breath, 0f), 0.32f, 0.42f, 10, divineWhite, isMetallic = true))
        polys.addAll(createFrustum(Vec3(0f, 0.38f + breath, 0f), Vec3(0f, 0.76f + breath, 0.04f), 0.24f, 0.22f, 10, gold, isMetallic = true))

        // Large Realistic Curved Kite Shield
        val sx = -0.58f
        polys.addAll(createFrustum(Vec3(sx, 0.45f + breath, 0.32f), Vec3(sx, -0.55f + breath, 0.32f), 0.34f, 0.18f, 6, sapphire, isMetallic = true))
        polys.addAll(createBeveledBox(Vec3(sx, -0.05f + breath, 0.38f), Vec3(0.08f, 0.8f, 0.04f), gold, isMetallic = true))
        polys.addAll(createBeveledBox(Vec3(sx, 0.12f + breath, 0.38f), Vec3(0.42f, 0.08f, 0.04f), gold, isMetallic = true))

        // Heavy Flanged Warhammer in Right Hand
        val hx = 0.58f
        polys.addAll(createFrustum(Vec3(hx, -0.4f + breath, 0.2f), Vec3(hx, 0.65f + breath, 0.2f), 0.05f, 0.05f, 6, gold, isMetallic = true))
        polys.addAll(createBeveledBox(Vec3(hx, 0.55f + breath, 0.2f), Vec3(0.36f, 0.24f, 0.26f), divineWhite, isMetallic = true))

        return Mesh3D(polys)
    }

    // 5. Robot Tank (Realistic High-Detail Mech Tank)
    fun createRobotTankMesh(animTick: Float): Mesh3D {
        val polys = mutableListOf<Polygon3D>()
        val armorPlate = Color(0xFF334155)
        val darkChassis = Color(0xFF0F172A)
        val cyanVisor = Color(0xFF06B6D4)
        val alertOrange = Color(0xFFF97316)

        val bob = sin(animTick * 4f) * 0.02f

        polys.addAll(createRealisticPedestal(cyanVisor).polygons)

        // Articulated Tank Treads with Road Wheels
        polys.addAll(createBeveledBox(Vec3(-0.48f, -0.55f, 0f), Vec3(0.28f, 0.4f, 1.1f), darkChassis, isMetallic = true))
        polys.addAll(createBeveledBox(Vec3(0.48f, -0.55f, 0f), Vec3(0.28f, 0.4f, 1.1f), darkChassis, isMetallic = true))

        // Sloped Reactive Glacis Plate
        polys.addAll(createFrustum(Vec3(0f, -0.3f + bob, 0f), Vec3(0f, 0.15f + bob, 0f), 0.5f, 0.42f, 8, armorPlate, isMetallic = true))
        // Rotating Turret
        polys.addAll(createFrustum(Vec3(0f, 0.15f + bob, 0f), Vec3(0f, 0.55f + bob, 0f), 0.42f, 0.36f, 8, darkChassis, isMetallic = true))
        // Horizontal Glowing Sensor Visor
        polys.addAll(createBeveledBox(Vec3(0f, 0.38f + bob, 0.34f), Vec3(0.55f, 0.08f, 0.04f), cyanVisor, isMetallic = true, isEmissive = true))

        // Heavy Autocannon Barrel with Muzzle Brake
        polys.addAll(createFrustum(Vec3(0f, 0.25f + bob, 0.35f), Vec3(0f, 0.25f + bob, 1.05f), 0.1f, 0.07f, 8, darkChassis, isMetallic = true))
        polys.addAll(createFrustum(Vec3(0f, 0.25f + bob, 1.05f), Vec3(0f, 0.25f + bob, 1.15f), 0.12f, 0.12f, 8, cyanVisor, isMetallic = true, isEmissive = true))

        // Dual Shoulder Missile Pods
        polys.addAll(createBeveledBox(Vec3(-0.55f, 0.55f + bob, 0f), Vec3(0.28f, 0.26f, 0.45f), armorPlate, isMetallic = true))
        polys.addAll(createFrustum(Vec3(-0.55f, 0.55f + bob, 0.22f), Vec3(-0.55f, 0.55f + bob, 0.32f), 0.07f, 0.02f, 6, alertOrange, isEmissive = true))

        polys.addAll(createBeveledBox(Vec3(0.55f, 0.55f + bob, 0f), Vec3(0.28f, 0.26f, 0.45f), armorPlate, isMetallic = true))
        polys.addAll(createFrustum(Vec3(0.55f, 0.55f + bob, 0.22f), Vec3(0.55f, 0.55f + bob, 0.32f), 0.07f, 0.02f, 6, alertOrange, isEmissive = true))

        return Mesh3D(polys)
    }

    // 6. Taş Golemi (Realistic Monolith Rock Golem)
    fun createStoneGolemMesh(animTick: Float): Mesh3D {
        val polys = mutableListOf<Polygon3D>()
        val granite = Color(0xFF57534E)
        val basalt = Color(0xFF292524)
        val magmaGold = Color(0xFFFBBF24)

        val heave = sin(animTick * 1.8f) * 0.04f

        polys.addAll(createRealisticPedestal(magmaGold).polygons)

        // Giant Chiseled Granite Legs
        polys.addAll(createFrustum(Vec3(-0.4f, -0.25f, 0f), Vec3(-0.42f, -0.92f, 0.05f), 0.26f, 0.2f, 6, basalt))
        polys.addAll(createFrustum(Vec3(0.4f, -0.25f, 0f), Vec3(0.42f, -0.92f, 0.05f), 0.26f, 0.2f, 6, basalt))

        // Massive Monolithic Torso
        polys.addAll(createFrustum(Vec3(0f, -0.25f + heave, 0f), Vec3(0f, 0.45f + heave, 0f), 0.48f, 0.65f, 7, granite))
        // Glowing Magma Fissure Lines
        polys.addAll(createBeveledBox(Vec3(0f, 0.15f + heave, 0.44f), Vec3(0.52f, 0.08f, 0.04f), magmaGold, isEmissive = true))
        polys.addAll(createBeveledBox(Vec3(0f, 0.0f + heave, 0.44f), Vec3(0.12f, 0.38f, 0.04f), magmaGold, isEmissive = true))

        // Giant Rock Boulder Fists
        polys.addAll(createFrustum(Vec3(-0.75f, 0.35f + heave, 0.1f), Vec3(-0.85f, -0.2f + heave, 0.25f), 0.28f, 0.24f, 6, basalt))
        polys.addAll(createFrustum(Vec3(0.75f, 0.35f + heave, 0.1f), Vec3(0.85f, -0.2f + heave, 0.25f), 0.28f, 0.24f, 6, basalt))

        // Head with Glowing Rune Eye Slits
        polys.addAll(createFrustum(Vec3(0f, 0.45f + heave, 0.1f), Vec3(0f, 0.82f + heave, 0.12f), 0.32f, 0.24f, 6, granite))
        polys.addAll(createBeveledBox(Vec3(0f, 0.68f + heave, 0.32f), Vec3(0.32f, 0.07f, 0.04f), magmaGold, isEmissive = true))

        return Mesh3D(polys)
    }

    // 7. Alev Büyücüsü (Realistic Pyromancer)
    fun createFireMageMesh(animTick: Float): Mesh3D {
        val polys = mutableListOf<Polygon3D>()
        val robeRed = Color(0xFFB91C1C)
        val flameOrange = Color(0xFFF97316)
        val gold = Color(0xFFFBBF24)

        val hover = sin(animTick * 3f) * 0.05f

        polys.addAll(createRealisticPedestal(flameOrange).polygons)

        // Flowing Conical Mage Robe
        polys.addAll(createFrustum(Vec3(0f, -0.92f, 0f), Vec3(0f, 0.28f + hover, 0f), 0.52f, 0.28f, 10, robeRed))
        polys.addAll(createFrustum(Vec3(0f, 0.28f + hover, 0f), Vec3(0f, 0.65f + hover, 0.04f), 0.22f, 0.18f, 10, flameOrange))
        // Pointed Archmage Cowl
        polys.addAll(createFrustum(Vec3(0f, 0.65f + hover, 0.04f), Vec3(0f, 1.15f + hover, -0.1f), 0.22f, 0.02f, 6, robeRed))

        // Staff with Orbiting Fire Core
        val staffX = 0.55f
        polys.addAll(createFrustum(Vec3(staffX, -0.4f + hover, 0.2f), Vec3(staffX, 0.95f + hover, 0.2f), 0.04f, 0.04f, 6, Color(0xFF451A03)))
        polys.addAll(createFrustum(Vec3(staffX, 0.95f + hover, 0.2f), Vec3(staffX, 1.18f + hover, 0.2f), 0.14f, 0.05f, 6, gold, isEmissive = true))
        polys.addAll(createBeveledBox(Vec3(staffX, 1.1f + hover, 0.2f), Vec3(0.24f, 0.24f, 0.24f), flameOrange, isEmissive = true))

        return Mesh3D(polys)
    }

    // 8. Gölge Suikastçı (Realistic Stealth Assassin)
    fun createShadowAssassinMesh(animTick: Float): Mesh3D {
        val polys = mutableListOf<Polygon3D>()
        val darkLeather = Color(0xFF0F172A)
        val shadowPurple = Color(0xFF581C87)
        val violetBlade = Color(0xFFA855F7)

        val sway = sin(animTick * 3.4f) * 0.03f

        polys.addAll(createRealisticPedestal(violetBlade).polygons)

        // Agile Wrapped Shinobi Limbs
        polys.addAll(createFrustum(Vec3(-0.22f, -0.25f, 0f), Vec3(-0.24f, -0.92f, 0.04f), 0.14f, 0.08f, 8, darkLeather))
        polys.addAll(createFrustum(Vec3(0.22f, -0.25f, 0f), Vec3(0.24f, -0.92f, 0.04f), 0.14f, 0.08f, 8, darkLeather))

        // Contoured Leather Tunic & Jagged Scarf
        polys.addAll(createFrustum(Vec3(0f, -0.25f + sway, 0f), Vec3(0f, 0.32f + sway, 0f), 0.24f, 0.28f, 10, darkLeather))
        polys.addAll(createFrustum(Vec3(0f, 0.32f + sway, 0f), Vec3(0f, 0.68f + sway, 0.04f), 0.18f, 0.16f, 8, darkLeather))
        polys.addAll(createBeveledBox(Vec3(0f, 0.58f + sway, 0.22f), Vec3(0.26f, 0.05f, 0.04f), violetBlade, isEmissive = true))

        // Flowing Twin Scarf Tails
        polys.addAll(createFrustum(Vec3(0f, 0.35f + sway, -0.15f), Vec3(-0.15f, -0.4f + sway, -0.32f), 0.14f, 0.04f, 4, shadowPurple))
        polys.addAll(createFrustum(Vec3(0f, 0.35f + sway, -0.15f), Vec3(0.15f, -0.4f + sway, -0.32f), 0.14f, 0.04f, 4, shadowPurple))

        // Dual Reverse-Grip Curved Daggers
        polys.addAll(createFrustum(Vec3(-0.48f, -0.35f + sway, 0.18f), Vec3(-0.48f, 0.25f + sway, 0.18f), 0.04f, 0.01f, 4, violetBlade, isMetallic = true, isEmissive = true))
        polys.addAll(createFrustum(Vec3(0.48f, -0.35f + sway, 0.18f), Vec3(0.48f, 0.25f + sway, 0.18f), 0.04f, 0.01f, 4, violetBlade, isMetallic = true, isEmissive = true))

        return Mesh3D(polys)
    }

    // 9. Şifacı Peri (Realistic Enchanted Fairy)
    fun createHealerFairyMesh(animTick: Float): Mesh3D {
        val polys = mutableListOf<Polygon3D>()
        val paleDress = Color(0xFFCCFBF1)
        val teal = Color(0xFF0D9488)
        val wingCyan = Color(0xFF2DD4BF)
        val bloomGold = Color(0xFFFDE047)

        val levitate = sin(animTick * 4f) * 0.08f
        val wingFlap = sin(animTick * 12f) * 0.35f

        polys.addAll(createRealisticPedestal(teal).polygons)

        // Graceful Levitating Gown & Silhouette
        polys.addAll(createFrustum(Vec3(0f, -0.6f + levitate, 0f), Vec3(0f, 0.25f + levitate, 0f), 0.35f, 0.22f, 8, paleDress))
        polys.addAll(createFrustum(Vec3(0f, 0.25f + levitate, 0f), Vec3(0f, 0.62f + levitate, 0.04f), 0.16f, 0.14f, 8, Color(0xFFFEF3C7)))
        polys.addAll(createFrustum(Vec3(0f, 0.64f + levitate, 0.04f), Vec3(0f, 0.7f + levitate, 0.04f), 0.16f, 0.16f, 10, bloomGold, isMetallic = true, isEmissive = true))

        // Translucent 4-Lobe Butterfly Wings with Vein Structure
        val wingZ = -0.15f
        polys.add(Polygon3D(listOf(
            Vec3(0f, 0.2f + levitate, wingZ),
            Vec3(-0.95f, 0.95f + levitate, wingZ + wingFlap),
            Vec3(-0.75f, 0.15f + levitate, wingZ)
        ), wingCyan.copy(alpha = 0.85f), isEmissive = true))
        polys.add(Polygon3D(listOf(
            Vec3(0f, 0.2f + levitate, wingZ),
            Vec3(0.95f, 0.95f + levitate, wingZ + wingFlap),
            Vec3(0.75f, 0.15f + levitate, wingZ)
        ), wingCyan.copy(alpha = 0.85f), isEmissive = true))

        // Flower Blossom Staff
        val fx = 0.45f
        polys.addAll(createFrustum(Vec3(fx, -0.2f + levitate, 0.15f), Vec3(fx, 0.82f + levitate, 0.15f), 0.035f, 0.035f, 6, bloomGold, isMetallic = true))
        polys.addAll(createFrustum(Vec3(fx, 0.82f + levitate, 0.15f), Vec3(fx, 1.05f + levitate, 0.15f), 0.14f, 0.06f, 6, teal, isEmissive = true))

        return Mesh3D(polys)
    }

    // 10. Buz Cadısı (Realistic Frost Sorceress)
    fun createFrostWitchMesh(animTick: Float): Mesh3D {
        val polys = mutableListOf<Polygon3D>()
        val iceNavy = Color(0xFF0369A1)
        val frostCyan = Color(0xFF38BDF8)
        val pureIce = Color(0xFFE0F2FE)

        val hover = sin(animTick * 3.2f) * 0.05f

        polys.addAll(createRealisticPedestal(frostCyan).polygons)

        // Tiered Glacial Gown
        polys.addAll(createFrustum(Vec3(0f, -0.85f, 0f), Vec3(0f, 0.28f + hover, 0f), 0.48f, 0.26f, 8, iceNavy))
        polys.addAll(createFrustum(Vec3(0f, 0.28f + hover, 0f), Vec3(0f, 0.68f + hover, 0.04f), 0.2f, 0.16f, 8, frostCyan))

        // Crown of 5 Graduated Glacial Icicle Spikes
        polys.addAll(createFrustum(Vec3(0f, 0.72f + hover, 0.08f), Vec3(0f, 1.15f + hover, 0.12f), 0.05f, 0.01f, 4, pureIce, isEmissive = true))
        polys.addAll(createFrustum(Vec3(-0.14f, 0.7f + hover, 0.08f), Vec3(-0.2f, 1.0f + hover, 0.1f), 0.04f, 0.01f, 4, frostCyan, isEmissive = true))
        polys.addAll(createFrustum(Vec3(0.14f, 0.7f + hover, 0.08f), Vec3(0.2f, 1.0f + hover, 0.1f), 0.04f, 0.01f, 4, frostCyan, isEmissive = true))

        // Glacial Frost Staff topped with 6-pointed Snowflake Crystal
        val staffX = 0.52f
        polys.addAll(createFrustum(Vec3(staffX, -0.3f + hover, 0.18f), Vec3(staffX, 0.95f + hover, 0.18f), 0.04f, 0.04f, 6, iceNavy, isMetallic = true))
        polys.addAll(createFrustum(Vec3(staffX, 0.95f + hover, 0.18f), Vec3(staffX, 1.25f + hover, 0.18f), 0.18f, 0.04f, 6, pureIce, isEmissive = true))

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
