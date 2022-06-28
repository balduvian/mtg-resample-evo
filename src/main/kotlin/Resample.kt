
import org.joml.Matrix3f
import org.joml.Matrix4f
import java.awt.RenderingHints
import java.awt.geom.AffineTransform
import java.awt.geom.Point2D
import java.awt.image.BufferedImage
import kotlin.math.*
import kotlin.random.Random

class CardPosition(val cardId: Int, val x: Float, val y: Float, val scale: Float, val rotation: Float) {
	fun mutateClone(baseSample: BufferedImage, mutationRate: Double, cardSet: CardSet): CardPosition {
		val minAxis = min(baseSample.width, baseSample.height)
		val posVariation = Util.lerp(0.0, minAxis.toDouble(), mutationRate)
		//TODO scale change
		val rotationVariation = Util.lerp(0.0, PI, mutationRate)

		val newImageId = if (Random.nextDouble() < mutationRate)
			Random.nextInt(cardSet.numCards())
		else cardId

		return CardPosition(
			newImageId,
			(x + Random.nextDouble(-posVariation, posVariation)).coerceIn(0.0, baseSample.width.toDouble()),
			(y + Random.nextDouble(-posVariation, posVariation)).coerceIn(0.0, baseSample.height.toDouble()),
			scale,
			(rotation + Random.nextDouble(-rotationVariation, rotationVariation)) % (PI * 2.0)
		)
	}

	fun transform(cardSet: CardSet): Matrix3f {
		return Matrix3f(
			Matrix4f()
			.scale(scale)
			.rotateZ(rotation)
			.translate(x, y, 0.0f)
		)
	}

	fun transformFull(cardSet: CardSet, baseSample: BufferedImage, baseFullImage: BufferedImage): AffineTransform {
		val cardSample = sample(cardSet)
		val cardFullImage = fullImage(cardSet)

		val cardScale = cardFullImage.width / cardSample.width.toDouble()
		val baseScale = baseFullImage.width / baseSample.width.toDouble()

		val transform = AffineTransform()
		transform.translate(x * baseScale, y * baseScale)
		transform.scale(scale * cardScale, scale * cardScale)
		transform.rotate(rotation)

		val cardImage = sample(cardSet)
		transform.translate(-cardFullImage.width / 2.0, -cardFullImage.height / 2.0)

		return transform
	}

	fun fullImage(cardSet: CardSet): BufferedImage {
		return cardSet.fullImages[cardId]
	}

	fun sample(cardSet: CardSet): BufferedImage {
		return cardSet.samples[cardId]
	}
}

class Resample(
	val baseSample: BufferedImage,
	val cardSet: CardSet,
	val cardPositions: ArrayList<CardPosition>
) {
	fun findTopCard(transforms: List<AffineTransform>, x: Int, y: Int): Int? {
		for (i in cardPositions.indices.reversed()) {
			val cardPosition = cardPositions[i]
			val transform = transforms[i]
			val sampleCardImage = cardPosition.sample(cardSet)

			val cardSpace = Point2D.Double()
			transform.inverseTransform(Point2D.Double(x.toDouble(), y.toDouble()), cardSpace)

			val cardX = floor(cardSpace.x).toInt()
			val cardY = floor(cardSpace.y).toInt()

			if (cardX in 0 until sampleCardImage.width && cardY in 0 until sampleCardImage.height) {
				return sampleCardImage.getRGB(cardX, cardY)
			}
		}

		return null
	}

	fun fitness(): Int {
		var fitness = 0

		val transforms = cardPositions.map { it.transform(cardSet) }

		for (y in 0 until baseSample.height) {
			for (x in 0 until baseSample.width) {

				val baseRGB = baseSample.getRGB(x, y)

				val cardRGB = findTopCard(transforms, x, y)

				if (cardRGB != null) {
					fitness += pixelCompare(baseRGB, cardRGB)
				}
			}
		}

		return fitness
	}

	/**
	 * @param mutationRate from 0 (no mutations) to 1 (many mutations)
	 */
	fun createMutated(mutationRate: Double): Resample {
		val newCardPositions = cardPositions.map {
			it.mutateClone(baseSample, mutationRate, cardSet)
		} as ArrayList<CardPosition>

		/* random shuffles */
		val numShuffles = Util.lerp(0, newCardPositions.size / 2, mutationRate)
		for (i in 0 until numShuffles) {
			val index0 = Random.nextInt(newCardPositions.size)
			val index1 = Random.nextInt(newCardPositions.size)

			val temp = newCardPositions[index0]
			newCardPositions[index0] = newCardPositions[index1]
			newCardPositions[index1] = temp
		}

		return Resample(baseSample, cardSet, newCardPositions)
	}

	fun render(baseFullImage: BufferedImage, scale: Float): BufferedImage {
		val finalWidth = (baseFullImage.width * scale).roundToInt()
		val finalHeight = (baseFullImage.height * scale).roundToInt()

		val newImage = BufferedImage(finalWidth, finalHeight, BufferedImage.TYPE_INT_ARGB)
		val graphics = newImage.createGraphics()

		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
		for (cardPosition in cardPositions) {
			graphics.drawImage(
				cardPosition.fullImage(cardSet),
				cardPosition.transformFull(
					cardSet,
					baseSample,
					baseFullImage,
				),
				null
			)
		}

		graphics.dispose()
		return newImage
	}

	companion object {
		fun pixelCompare(rgb0: Int, rgb1: Int): Int {
			val r0 = rgb0.shr(16).and(0xff)
			val g0 = rgb0.shr(8).and(0xff)
			val b0 = rgb0.and(0xff)

			val r1 = rgb1.shr(16).and(0xff)
			val g1 = rgb1.shr(8).and(0xff)
			val b1 = rgb1.and(0xff)

			return (0xff - abs(r1 - r0)) + (0xff - abs(g1 - g0)) + (0xff - abs(b1 - b0))
		}

		fun generateRandomPositionList(baseSample: BufferedImage, cardSet: CardSet): ArrayList<CardPosition> {
			val sideLength = sqrt(cardSet.fullImages.size.toDouble())

			val ret = ArrayList<CardPosition>(cardSet.fullImages.size)

			for (card in cardSet.fullImages) {
				ret.add(CardPosition(
					cardSet.randomCardId(),
					Random.nextDouble() * baseSample.width,
					Random.nextDouble() * baseSample.height,
					((baseSample.width / sideLength) / card.width) * 1.5,
					Random.nextDouble() * 2.0 * PI
				))
			}

			return ret
		}
	}
}