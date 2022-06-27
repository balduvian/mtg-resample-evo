import java.io.File
import java.util.*
import javax.imageio.ImageIO

fun main() {
	println("MTG Resample EVO Starting")

	println("Loading base image")
	val baseImage = ImageIO.read(File("./test/nancy-crop.jpg"))
	val sampleImage = Util.createSampleImage(baseImage, 512)

	println("Loading card images")
	val cardSet = CardSet()
	cardSet.load("./ankiImages", 256)

	val poolSize = 128

	println("Loading initial breeding pool of size $poolSize")
	val evolution = Evolution(poolSize)
	evolution.seed(baseImage, cardSet)

	var going = true

	val inputThread = Thread {
		val scanner = Scanner(System.`in`)
		while (true) {
			if (scanner.hasNext()) {
				going = false
				break
			}
		}
	}
	inputThread.start()

	var round = 0
	while (going) {
		val bestFitness = evolution.select(baseImage)
		println("Best fitness after round $round: $bestFitness")
		evolution.variate(baseImage, cardSet, 0.1)

		++round
	}

	inputThread.join()

	val bestFitness = evolution.select(baseImage)
	println("Final best fitness: $bestFitness")

	println("Rendering final image")
	val rendered = evolution.getBest().render(baseImage, 1.0f)

	println("Writing rendered image to disk")
	ImageIO.write(rendered, "PNG", File("./test/sampled.png"))
}