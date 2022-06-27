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

	fun scaleDown(width: Int, height: Int, desiredArea: Int): Pair<Int, Int> {
		val w = (desiredArea / (width * height).toDouble() * width * width)
		val h = (desiredArea / (width * height).toDouble() * height * height)

		val sw = sqrt(w)
		val sh = sqrt(h)

		return sw.roundToInt() to sh.roundToInt()
	}

	fun createSampleImage(baseImage: BufferedImage, desiredArea: Int): BufferedImage {
		val (samplesWide, samplesTall) = scaleDown(baseImage.width, baseImage.height, desiredArea)
		val newBaseImage = BufferedImage(samplesWide, samplesTall, BufferedImage.TYPE_INT_ARGB)

		val graphics = newBaseImage.createGraphics()
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
		graphics.drawImage(baseImage, 0, 0, samplesWide, samplesTall, 0, 0, baseImage.width, baseImage.height, null)
		graphics.dispose()

		return newBaseImage
	}
}
