#version 330 core

/* Input from vertex shader */
in vec2 texFrag;

/* Output to OpenGL */
out vec4 FragColor;

/* Statically set variables */
uniform sampler2D sDiffuse;
uniform sampler2D sSpecular;

void main()
{
	FragColor = texture(sampler, texFrag);
}