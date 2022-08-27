#version 330 core

/* Input from vertex shader */
in vec2 texFrag;

/* Output to OpenGL */
out vec4 FragColor;

/* Statically set variables */
uniform sampler2D sampler;

void main()
{
	vec4 texColor = texture(sampler, texFrag);
	if(texColor.a < 0.1) discard;
	FragColor = texColor;
}