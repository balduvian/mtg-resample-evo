package graphics

import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import java.nio.ByteBuffer

class Graphics {
	val window = Window.create("Mtg Resample Evo") { key ->
		println(key)
	}

	val projection = Matrix4f()
	val frameBuffer = FrameBuffer.create(1, 1, TextureParams())

	fun loadShader(vertFilename: String, fragFilename: String, vararg uniforms: String): Shader {
		val vertSource = this::class.java.getResource(vertFilename)?.readText()
			?: throw Exception("Resource $vertFilename does not exist")

		val fragSource = this::class.java.getResource(fragFilename)?.readText()
			?: throw Exception("Resource $fragFilename does not exist")

		return Shader.create(vertSource, fragSource, *uniforms)
	}

	val textureShader = loadShader("/shaders/texture/vert.glsl", "/shaders/texture/frag.glsl", "pvm")
	val tileShader = loadShader("/shaders/texture/tile.glsl", "/shaders/texture/tile.glsl")

	fun setProjection(width: Int, height: Int) {
		projection.setOrtho2D(0.0f, width.toFloat(), height.toFloat(), 0.0f)
	}

	fun setFrameBuffer(width: Int, height: Int) {
		frameBuffer.resize(width, height)
	}

	fun grabRenderedPixels(width: Int, height: Int): ByteBuffer {
		val buffer = BufferUtils.createByteBuffer(width * height * 4)

		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer)

		return buffer
	}
}
