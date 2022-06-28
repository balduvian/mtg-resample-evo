#version 460 core

layout (location = 0) in vec2 vertex;
layout (location = 1) in vec2 texVertex;

uniform mat3 pvm;

out vec2 texCoords;

void main() {
    gl_Position = vec4(pvm * vec3(vertex, 0), 1);

    texCoords = texVertex;
}
