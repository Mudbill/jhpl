#version 330 core

/* Input from OpenGL */
layout (location = 0) in vec3 position;
layout (location = 2) in vec2 texCoord;

/* Output to fragment shader */
out vec2 texFrag;

/* Statically set variables */
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() 
{
	gl_Position = projection * view * model * vec4(position, 1.0);
	texFrag = texCoord;
}