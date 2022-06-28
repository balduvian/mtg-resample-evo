package graphics

import org.joml.Matrix3f
import org.joml.Matrix4f
import org.lwjgl.opengl.GL46.*

class Shader(
	val program: Int,
	val locations: Array<Int>
) {
	companion object {
		val matrixValues = FloatArray(16)
		val matrixValues2 = FloatArray(16)
		val pvm = Matrix4f()
		val normalMatrix = Matrix3f()

		val includeCache = HashMap<String, String>()

		fun create(
			vertexSource: String,
			fragmentSource: String,
			vararg uniforms: String,
		): Shader {
			val program = glCreateProgram()

			val fragmentShader = createShader(fragmentSource, GL_FRAGMENT_SHADER)
			val vertexShader = createShader(vertexSource, GL_VERTEX_SHADER)

			glAttachShader(program, vertexShader)
			glAttachShader(program, fragmentShader)

			glLinkProgram(program)
			if (glGetProgrami(program, GL_LINK_STATUS) != 1) {
				throw Exception(glGetProgramInfoLog(program))
			}

			glDetachShader(program, vertexShader)
			glDetachShader(program, fragmentShader)

			glDeleteShader(vertexShader)
			glDeleteShader(fragmentShader)

			val locations = Array(uniforms.size) {
				glGetUniformLocation(program, uniforms[it])
			}

			for (i in locations.indices) {
				if (locations[i] == -1) throw Exception("Uniform ${uniforms[i]} was not found")
			}

			return Shader(program, locations)
		}

		private fun createShader(source: String, type: Int): Int {
			val shader = glCreateShader(type)

			glShaderSource(shader, source)
			glCompileShader(shader)

			val shaderError = glGetShaderi(shader, GL_COMPILE_STATUS)

			if (shaderError != 1) throw Exception(glGetShaderInfoLog(shader))

			return shader
		}
	}

	fun destroy() {
		glDeleteProgram(program)
	}

	/* uniform helpers */

	fun uniformFloat(index: Int, value: Float): Shader {
		glUniform1f(locations[index], value)
		return this
	}
	fun uniformVector2(index: Int, x: Float, y: Float): Shader {
		glUniform2f(locations[index], x, y)
		return this
	}
	fun uniformVector3(index: Int, x: Float, y: Float, z: Float): Shader {
		glUniform3f(locations[index], x, y, z)
		return this
	}
	fun uniformVector4(index: Int, x: Float, y: Float, z: Float, w: Float): Shader {
		glUniform4f(locations[index], x, y, z, w)
		return this
	}
	fun uniformVector4(index: Int, values: Array<Float>): Shader {
		glUniform4f(locations[index], values[0], values[1], values[2], values[3])
		return this
	}
	fun uniformVector4(index: Int, values: FloatArray): Shader {
		glUniform4f(locations[index], values[0], values[1], values[2], values[3])
		return this
	}
	fun uniformVector2Array(index: Int, values: FloatArray): Shader {
		glUniform2fv(locations[index], values)
		return this
	}
}
