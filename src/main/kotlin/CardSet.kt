import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.random.Random

class CardSet {
	val fullImages: ArrayList<BufferedImage> = ArrayList()
	val samples: ArrayList<BufferedImage> = ArrayList()

	fun randomCardId(): Int {
		return Random.nextInt(fullImages.size)
	}

	fun numCards(): Int {
		return fullImages.size
	}

	fun load(directoryName: String, sampleArea: Int) {
		val directory = File(directoryName)
		if (!directory.exists() || !directory.isDirectory) {
			throw Exception("$directoryName is not a directory")
		}

		val images = directory.listFiles { file -> file.extension == "jpg" }!!

		fullImages.ensureCapacity(images.size)
		samples.ensureCapacity(images.size)

		for (image in images) {
			val baseImage = ImageIO.read(image)
			val sampleImage = Util.createSampleImage(baseImage, sampleArea)

			fullImages.add(baseImage)
			samples.add(sampleImage)
		}
	}
}