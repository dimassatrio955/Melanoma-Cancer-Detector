package buffml.com.melanoma_cancer_detector

import android.annotation.SuppressLint
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.Resources.getSystem
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.res.TypedArrayUtils.getString
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*

class imageClassification internal constructor(
    assetManager: AssetManager,
    modelPath: String,
    private val INPUT_SIZE: Int
) {
    private val interpreter: Interpreter
    private val PIXEL_SIZE = 3
    private val IMAGE_MEAN = 0
    private val IMAGE_STD = 255.0f
    private val gpuDelegate: GpuDelegate
    private var height = 0
    private var width = 0
    private val red = Scalar(255.0, 0.0, 0.0, 50.0)
    private val green = Scalar(0.0, 255.0, 0.0, 50.0)

    init {
        val options = Interpreter.Options()
        gpuDelegate = GpuDelegate()
        options.addDelegate(gpuDelegate)
        options.setNumThreads(6)
        interpreter = Interpreter(loadModelFile(assetManager, modelPath), options)
    }

    @SuppressLint("RestrictedApi")
    fun recognizeImage(mat_image: Mat): Mat {
        val rotate_mat_image = Mat()
        Core.flip(mat_image.t(), rotate_mat_image, 1)
        height = rotate_mat_image.height()
        width = rotate_mat_image.width()
        val roi_cropped = Rect((width - 400) / 2, (height - 400) / 2, 400, 400)
        val cropped_image = Mat(rotate_mat_image, roi_cropped)
        var bitmap: Bitmap? = null
        bitmap =
            Bitmap.createBitmap(cropped_image.cols(), cropped_image.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(cropped_image, bitmap)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false)
        val byteBuffer = converBitmapToByteBuffer(scaledBitmap)
        val output = Array(1) { FloatArray(1) }
        val out = arrayOfNulls<Any>(1)
        out[0] = output
        val input = arrayOfNulls<Any>(1)
        input[0] = byteBuffer
        interpreter.run(byteBuffer, output)
        Log.d("imageClassification", "Out" + Arrays.deepToString(output))
        val val_prediction =
            java.lang.reflect.Array.get(java.lang.reflect.Array.get(output, 0), 0) as Float
        if (val_prediction > 0.4) {
            Imgproc.putText(
                rotate_mat_image,
                "Melanoma",
                Point(((width - 200) / 2 + 30).toDouble(), 200.0),
                3,
                1.0,
                red,
                2
            )
            Imgproc.rectangle(
                rotate_mat_image,
                Point(((width - 400) / 2).toDouble(), ((height - 400) / 2).toDouble()),
                Point(((width + 400) / 2).toDouble(), ((height + 400) / 2).toDouble()),
                red,
                2
            )
        } else {
            Imgproc.putText(
                rotate_mat_image,
                "Normal",
                Point(((width - 200) / 2 + 30).toDouble(), 200.0),
                3,
                1.0,
                green,
                2
            )
            Imgproc.rectangle(
                rotate_mat_image,
                Point(((width - 400) / 2).toDouble(), ((height - 400) / 2).toDouble()),
                Point(((width + 400) / 2).toDouble(), ((height + 400) / 2).toDouble()),
                green,
                2
            )
        }
        Core.flip(rotate_mat_image.t(), rotate_mat_image, 0)
        return rotate_mat_image
    }

    private fun converBitmapToByteBuffer(scaledBitmap: Bitmap): ByteBuffer {
        val byteBuffer: ByteBuffer
        byteBuffer = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * PIXEL_SIZE)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(INPUT_SIZE * INPUT_SIZE)
        scaledBitmap.getPixels(
            intValues,
            0,
            scaledBitmap.width,
            0,
            0,
            scaledBitmap.width,
            scaledBitmap.height
        )
        var pixel = 0
        for (i in 0 until INPUT_SIZE) {
            for (j in 0 until INPUT_SIZE) {
                val `val` = intValues[pixel++]
                byteBuffer.putFloat(((`val` shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                byteBuffer.putFloat(((`val` shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                byteBuffer.putFloat(((`val` and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
            }
        }
        return byteBuffer
    }

    @Throws(IOException::class)
    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val assetFileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffSet = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.length
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffSet, declaredLength)
    }
}