#version 330 core

in vec2 texFrag;

out vec4 FragColor;

uniform sampler2D sampler;

void main()
{
	vec4 color = texture(sampler, texFrag);
	FragColor = color;
}