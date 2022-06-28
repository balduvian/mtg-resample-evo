#version 460 core

layout (location = 0) in vec2 vertex;
layout (location = 1) in vec2 texVertex;
layout (location = 2) in mat3 pvm;

uniform vec4 tile;

out vec2 texCoords;

void main() {
    gl_Position = vec4(vm * vec3(vertex, 0), 1);

    texCoords = (texVertex * tile.zw) + tile.xy;
}