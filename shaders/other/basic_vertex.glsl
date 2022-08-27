#version 330 core

/* Input from OpenGL */
layout (location = 0) in vec3 position;
layout (location = 2) in vec2 texCoord;

/* Output to fragment shader */
out vec2 texFrag;

/* Statically set variables */


void main() 
{
	gl_Position = vec4(position.x, position.y, position.z, 1.0);
	texFrag = texCoord;
}