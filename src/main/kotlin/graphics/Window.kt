package graphics

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL

class Window(val window: Long) {
	companion object {
		const val MAJOR_VERSION = 4
		const val MINOR_VERSION = 6

		fun init(onError: (String) -> Unit): Boolean {
			GLFW.glfwSetErrorCallback { code, description ->
				onError("GLFW ERROR CODE $code | ${GLFWErrorCallback.getDescription(description)}")
			}

			return GLFW.glfwInit()
		}

		fun create(title: String, onKeyPressed: (Int) -> Unit): Window {
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, MAJOR_VERSION)
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, MINOR_VERSION)
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE)
			GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, 1)
			GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, 1)

			val monitor = GLFW.glfwGetPrimaryMonitor()
			if (monitor == 0L) throw Exception("No monitor")
			val videoMode = GLFW.glfwGetVideoMode(monitor)
				?: throw Exception("No video mode")

			val width = videoMode.width() / 2
			val height = videoMode.height() / 2

			val window = GLFW.glfwCreateWindow(
				width,
				height,
				title,
				0L,
				0L,
			)
			if (window == 0L) throw Exception("Could not create window")

			GLFW.glfwFocusWindow(window)
			GLFW.glfwMakeContextCurrent(window)
			GL.createCapabilities()
			GLFW.glfwSwapInterval(1)

			GLFW.glfwSetKeyCallback(window) { _, key, _, action, _ ->
				if (action == GLFW.GLFW_PRESS) {
					onKeyPressed(key)
				}
			}

			return Window(window)
		}
	}
}
