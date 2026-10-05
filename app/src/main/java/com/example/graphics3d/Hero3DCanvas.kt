package com.example.graphics3d

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.*

@Composable
fun Hero3DCanvas(
    heroId: String,
    modifier: Modifier = Modifier,
    initialRotationY: Float = 0.35f,
    initialRotationX: Float = 0.2f,
    interactiveRotation: Boolean = true,
    autoSpin: Boolean = false,
    renderStyle: RenderStyle = RenderStyle.SHADED,
    zoomScale: Float = 1.0f,
    isAttacking: Boolean = false,
    isDead: Boolean = false
) {
    var rotY by remember { mutableFloatStateOf(initialRotationY) }
    var rotX by remember { mutableFloatStateOf(initialRotationX) }

    val infiniteTransition = rememberInfiniteTransition(label = "hero_anim")
    val animTick by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "anim_tick"
    )

    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "auto_spin"
    )

    val currentRotY = if (autoSpin) spinAngle else rotY
    val currentRotX = rotX.coerceIn(-0.8f, 0.8f)

    val dragModifier = if (interactiveRotation) {
        Modifier.pointerInput(Unit) {
            detectDragGestures { _, dragAmount ->
                rotY += dragAmount.x * 0.015f
                rotX -= dragAmount.y * 0.015f
            }
        }
    } else Modifier

    Box(modifier = modifier.then(dragModifier)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height
            val centerX = canvasW / 2f
            val centerY = canvasH / 2f

            val baseSize = min(canvasW, canvasH)
            val pixelsPerUnit = (baseSize / 3.4f) * zoomScale

            val cameraDist = 6.0f
            val lightDir = Vec3(0.5f, 0.8f, 0.6f).normalize()

            // Attack lunge offset
            val lungeZ = if (isAttacking) 0.35f else 0f
            val deadRotationZ = if (isDead) 1.57f else 0f // falls over when dead
            val deadY = if (isDead) -0.5f else 0f

            // Generate Mesh
            val baseMesh = Hero3DModels.getHeroMesh(heroId, animTick)
            val transformedMesh = baseMesh.transform(
                rotationX = currentRotX,
                rotationY = currentRotY,
                rotationZ = deadRotationZ,
                translation = Vec3(0f, deadY, lungeZ)
            )

            // Draw Ground Shadow Ellipse
            drawOval(
                color = Color.Black.copy(alpha = 0.45f),
                topLeft = Offset(centerX - baseSize * 0.32f, centerY + baseSize * 0.28f),
                size = androidx.compose.ui.geometry.Size(baseSize * 0.64f, baseSize * 0.22f)
            )

            // Sort Polygons (Painter's Algorithm: farthest Z first)
            val sortedPolys = transformedMesh.polygons.sortedBy { it.centerZ() }

            // Render each polygon
            for (poly in sortedPolys) {
                if (poly.vertices.size < 3) continue

                val normal = poly.normal()
                // Simple backface culling for non-emissive solids
                // Note: keep emissive and transparent wings visible from both sides
                val dotNormalEye = normal.z
                if (!poly.isEmissive && dotNormalEye < -0.1f) {
                    continue
                }

                // Calculate PBR Realistic Shading
                val viewDir = Vec3(0f, 0f, 1f)
                val halfVec = (lightDir + viewDir).normalize()
                val nDotL = max(0f, normal.dot(lightDir))
                val nDotV = max(0f, normal.dot(viewDir))
                val nDotH = max(0f, normal.dot(halfVec))

                val ambient = 0.38f
                val diffuse = if (poly.isEmissive) 1.25f else (ambient + 0.62f * nDotL)
                val shininess = if (poly.isMetallic) 32f else 12f
                val specMult = if (poly.isMetallic) 0.65f else 0.22f
                val specular = if (poly.isEmissive) 0f else (nDotH.pow(shininess) * specMult)
                val rim = if (poly.isEmissive) 0f else ((1f - nDotV).pow(2.8f) * 0.32f)

                // Project 3D vertices to 2D screen coordinates
                val path = Path()
                var first = true

                for (v in poly.vertices) {
                    val z = v.z
                    val perspective = cameraDist / (cameraDist + z)
                    val sx = centerX + v.x * pixelsPerUnit * perspective
                    val sy = centerY - v.y * pixelsPerUnit * perspective

                    if (first) {
                        path.moveTo(sx, sy)
                        first = false
                    } else {
                        path.lineTo(sx, sy)
                    }
                }
                path.close()

                val baseC = poly.baseColor
                val shadedColor = if (poly.isEmissive) {
                    baseC
                } else {
                    val totalR = (baseC.red * diffuse + specular + rim * 0.8f).coerceIn(0f, 1f)
                    val totalG = (baseC.green * diffuse + specular + rim * 0.8f).coerceIn(0f, 1f)
                    val totalB = (baseC.blue * diffuse + specular + rim * 0.8f).coerceIn(0f, 1f)
                    Color(
                        red = totalR,
                        green = totalG,
                        blue = totalB,
                        alpha = baseC.alpha
                    )
                }

                when (renderStyle) {
                    RenderStyle.SHADED -> {
                        drawPath(path, shadedColor, style = Fill)
                        // Smooth soft facet outline
                        drawPath(path, Color.Black.copy(alpha = 0.12f), style = Stroke(width = 0.9f))
                    }
                    RenderStyle.WIREFRAME -> {
                        drawPath(path, shadedColor.copy(alpha = 0.85f), style = Stroke(width = 1.6f))
                    }
                    RenderStyle.GLOW_CRYSTAL -> {
                        drawPath(path, shadedColor.copy(alpha = 0.35f), style = Fill)
                        drawPath(path, shadedColor.copy(alpha = 0.95f), style = Stroke(width = 2.0f))
                    }
                }
            }
        }
    }
}
