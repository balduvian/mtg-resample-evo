import java.awt.RenderingHints
import java.awt.image.BufferedImage
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

object Util {
	fun lerp(low: Int, high: Int, along: Float): Int {
		return ((high - low + 1) * along + low).toInt()
	}

	fun lerp(low: Int, high: Int, along: Double): Int {
		return ((high - low + 1) * along + low).toInt()
	}

	fun lerp(low: Float, high: Float, along: Float): Float {
		return (high - low) * along + low
	}

	fun lerp(low: Double, high: Double, along: Double): Double {
		return (high - low) * along + low
	}

	fun randomFloat(low: Float, high: Float): Float {
		return lerp(low, high, Random.nextFloat())
	}

	fun areaToDims(aspect: Float, desiredArea: Int): Pair<Int, Int> {
		return sqrt(desiredArea * aspect).roundToInt() to sqrt(desiredArea / aspect).roundToInt()
	}

	fun createSampleImage(baseImage: BufferedImage, samplesWide: Int, samplesTall: Int): BufferedImage {
		val newBaseImage = BufferedImage(samplesWide, samplesTall, BufferedImage.TYPE_INT_ARGB)

		val graphics = newBaseImage.createGraphics()
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
		graphics.drawImage(baseImage, 0, 0, samplesWide, samplesTall, 0, 0, baseImage.width, baseImage.height, null)
		graphics.dispose()

		return newBaseImage
	}
}
