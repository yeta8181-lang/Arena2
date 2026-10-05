package com.example.graphics3d

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import kotlin.math.*

data class Vec3(val x: Float, val y: Float, val z: Float) {
    operator fun plus(other: Vec3) = Vec3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vec3) = Vec3(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Float) = Vec3(x * scalar, y * scalar, z * scalar)

    fun dot(other: Vec3): Float = x * other.x + y * other.y + z * other.z

    fun cross(other: Vec3): Vec3 = Vec3(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )

    fun length(): Float = sqrt(x * x + y * y + z * z)

    fun normalize(): Vec3 {
        val l = length()
        return if (l > 0.0001f) Vec3(x / l, y / l, z / l) else Vec3(0f, 1f, 0f)
    }

    fun rotateX(radians: Float): Vec3 {
        val cos = cos(radians)
        val sin = sin(radians)
        return Vec3(x, y * cos - z * sin, y * sin + z * cos)
    }

    fun rotateY(radians: Float): Vec3 {
        val cos = cos(radians)
        val sin = sin(radians)
        return Vec3(x * cos + z * sin, y, -x * sin + z * cos)
    }

    fun rotateZ(radians: Float): Vec3 {
        val cos = cos(radians)
        val sin = sin(radians)
        return Vec3(x * cos - y * sin, x * sin + y * cos, z)
    }
}

data class Polygon3D(
    val vertices: List<Vec3>,
    val baseColor: Color,
    val isEmissive: Boolean = false,
    val isMetallic: Boolean = false,
    val roughness: Float = 0.5f,
    val isWireframeOnly: Boolean = false
) {
    fun normal(): Vec3 {
        if (vertices.size < 3) return Vec3(0f, 0f, 1f)
        val v0 = vertices[0]
        val v1 = vertices[1]
        val v2 = vertices[2]
        return (v1 - v0).cross(v2 - v0).normalize()
    }

    fun centerZ(): Float {
        if (vertices.isEmpty()) return 0f
        return vertices.sumOf { it.z.toDouble() }.toFloat() / vertices.size
    }
}

data class Mesh3D(val polygons: List<Polygon3D>) {
    operator fun plus(other: Mesh3D) = Mesh3D(polygons + other.polygons)

    fun transform(rotationX: Float, rotationY: Float, rotationZ: Float, translation: Vec3 = Vec3(0f, 0f, 0f)): Mesh3D {
        val transformed = polygons.map { poly ->
            val rotatedVerts = poly.vertices.map { v ->
                v.rotateX(rotationX)
                    .rotateY(rotationY)
                    .rotateZ(rotationZ) + translation
            }
            poly.copy(vertices = rotatedVerts)
        }
        return Mesh3D(transformed)
    }
}

enum class RenderStyle {
    SHADED,
    WIREFRAME,
    GLOW_CRYSTAL
}
