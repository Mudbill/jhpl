#version 330 core

/* Input from vertex shader */
in vec2 texFrag;
in vec3 pass_tangent;

/* Output to OpenGL */
out vec4 FragColor;

void main()
{
	vec4 texColor = vec4(pass_tangent, 1.0);
	FragColor = texColor;
	FragColor = vec4(pass_tangent, 1.0);
}
