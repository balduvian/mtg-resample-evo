import java.awt.image.BufferedImage

/**
 * @param poolSize must be a multiple of 4
 */
class Evolution(val poolSize: Int) {
	val pool = ArrayList<Resample>(poolSize)

	fun seed(baseSample: BufferedImage, cardSet: CardSet) {
		for (i in 0 until poolSize) {
			pool.add(Resample(baseSample, cardSet, Resample.generateRandomPositionList(baseSample, cardSet)))
		}
	}

	class FitnessAnd(var fitness: Int, val resample: Resample)

	fun select(baseImage: BufferedImage): Int {
		val test = Array(poolSize) { i -> FitnessAnd(0, pool[i]) }

		val cores = Runtime.getRuntime().availableProcessors()
		val threads = Array(cores) { core ->
			val thread = Thread {
				for (i in core until poolSize step cores) {
					test[i].fitness = test[i].resample.fitness()
					println("completed $i")
				}
			}
			thread.start()
			thread
		}

		for (thread in threads) thread.join()

		/* put the fittest back into the pool, bottom half are garbage values now */
		test.sortBy { it.fitness }

		val survivorSize = poolSize / 2
		for (i in survivorSize until poolSize) {
			pool[i] = test[i].resample
		}

		return test.maxOf{ it.fitness }
	}

	fun variate(baseImage: BufferedImage, cardSet: CardSet, mutationRate: Double) {
		/* top half survives into the next generation */
		val survivorSize = poolSize / 2
		val survivors = pool.subList(survivorSize, poolSize)

		/* bottom half become mutations of the top half */
		for (i in 0 until survivorSize) {
			pool[i] = pool[i + survivorSize].createMutated(mutationRate)
		}
	}

	fun getBest(): Resample {
		return pool.last()
	}
}