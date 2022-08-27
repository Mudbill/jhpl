#version 330 core

/* Input from OpenGL */
layout (location = 0) in vec3 inPos;
layout (location = 2) in vec2 inTex;
layout (location = 3) in vec2 inNor;
layout (location = 4) in vec3 inTan;

/* Output to fragment shader */
out vec2 texFrag;
out vec3 pass_tangent;

/* Statically set variables */
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() 
{
	gl_Position = projection * view * model * vec4(inPos, 1.0);
	texFrag = inTex;
	pass_tangent = inTan;
}