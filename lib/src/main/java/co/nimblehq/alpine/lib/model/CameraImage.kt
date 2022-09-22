package co.nimblehq.alpine.lib.model

import android.graphics.Rect
import java.nio.ByteBuffer

data class CameraImage(
    val width: Int,
    val height: Int,
    val cropRect: Rect,
    val format: Int,
    val rotationDegrees: Int,
    val planes: List<Plane>
) {
    data class Plane(
        val rowStride: Int,
        val pixelStride: Int,
        val buffer: ByteBuffer
    )
}
