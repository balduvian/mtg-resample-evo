#version 460 core

uniform sampler2D sampler;

in vec2 texCoords;
out vec4 outColor;

void main() {
	outColor = texture(sampler, texCoords);
}
