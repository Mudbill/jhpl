#version 330 core

in vec3 texFrag;

out vec4 FragColor;

uniform samplerCube sampler;

void main()
{
	vec4 color = texture(sampler, texFrag);
	color.a = 1;
	FragColor = color;
}