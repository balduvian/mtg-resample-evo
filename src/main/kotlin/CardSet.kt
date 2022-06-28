import graphics.Texture
import graphics.TextureParams
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.GL_LINEAR
import org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER
import org.lwjgl.opengl.GL13.GL_CLAMP_TO_EDGE
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

class CardSet(
	val aspectRatio: Float,
	val images: ArrayList<BufferedImage>,
	val tilesheet: Texture,
	val tilesWide: Int,
	val tilesTall: Int,
) {
	companion object {
		fun create(directoryname: String, aspectRatio: Float, sampleArea: Int): CardSet {
			val directory = File(directoryname)
			if (!directory.exists() || !directory.isDirectory) {
				throw Exception("$directoryname is not a directory")
			}

			val files = directory.listFiles { file -> file.extension == "jpg" || file.extension == "png" }!!

			val images = ArrayList<BufferedImage>()
			images.ensureCapacity(images.size)

			for (file in files) {
				val baseImage = ImageIO.read(file)
				images.add(cropToAspectRatio(baseImage, aspectRatio))
			}

			val (sampleWidth, sampleHeight) = Util.areaToDims(aspectRatio, sampleArea)
			val tilesheet = createTileSheet(images, sampleWidth, sampleHeight)

			return CardSet(aspectRatio, images, tilesheet)
		}

		fun cropToAspectRatio(image: BufferedImage, aspectRatio: Float): BufferedImage {
			val imageAspect = image.width.toFloat() / image.height

			if (abs(imageAspect - aspectRatio) < 0.05) return image

			if (aspectRatio > imageAspect) {
				val newWidth = (image.width * (1.0f / aspectRatio)).roundToInt()
				val ratioImage = BufferedImage(newWidth, image.height, BufferedImage.TYPE_INT_ARGB)
				val graphics = ratioImage.createGraphics()

				graphics.drawImage(image, -(image.width - newWidth) / 2, 0, null)
				graphics.dispose()

				return ratioImage

			} else {
				val newHeight = (image.height * aspectRatio).roundToInt()
				val ratioImage = BufferedImage(image.width, newHeight, BufferedImage.TYPE_INT_ARGB)
				val graphics = ratioImage.createGraphics()

				graphics.drawImage(image, 0, -(image.height - newHeight) / 2, null)
				graphics.dispose()

				return ratioImage
			}
		}

		fun createTileSheet(images: ArrayList<BufferedImage>, sampleWidth: Int, sampleHeight: Int): Texture {
			val sideLength = ceil(sqrt(images.size.toFloat())).toInt()

			val imageWidth = sideLength * (sampleWidth + 2)
			val imageHeight = sideLength * (sampleHeight + 2)

			val byteBuffer = BufferUtils.createByteBuffer(imageWidth * imageHeight * 4)

			for (iy in 0 until sideLength) {
				for (ix in 0 until sideLength) {
					val cardId = iy * sideLength + ix
					val sampleImage = Util.createSampleImage(images[cardId], sampleWidth, sampleHeight)

					for (cy in -1 until sampleHeight + 1) {
						for (cx in -1 until sampleWidth + 1) {
							val rgb = sampleImage.getRGB(cx.coerceIn(0 until sampleWidth), cy.coerceIn(0 until sampleHeight))

							val a = rgb.ushr(24).toByte()
							val r = rgb.ushr(16).toByte()
							val g = rgb.ushr(8).toByte()
							val b = rgb.toByte()

							val y = iy * (imageHeight + 2) + cy + 1
							val x = ix * (imageWidth + 2) + cx + 1

							byteBuffer.put((y * imageWidth + x) * 4, r)
							byteBuffer.put((y * imageWidth + x) * 4 + 1, g)
							byteBuffer.put((y * imageWidth + x) * 4 + 2, b)
							byteBuffer.put((y * imageWidth + x) * 4 + 3, a)
						}
					}
				}
			}

			//DEBUG
			//val g = BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB)
			//g.setRGB(0, 0, imageWidth, imageHeight, byteBuffer.array(), 0, imageWidth)
			//ImageIO.write(g, "PNG", File("./tilemap.png"))

			return Texture.create(imageWidth, imageHeight, byteBuffer, TextureParams().filter(GL_LINEAR).wrap(GL_CLAMP_TO_EDGE))
		}
	}

	fun numCards(): Int {
		return images.size
	}

	fun randomCardId(): Int {
		return Random.nextInt(numCards())
	}

	private val tileReturn = FloatArray(4)

	fun getCardTile(cardId: Int): FloatArray {
		return tileReturn
	}
}
