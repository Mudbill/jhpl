#version 330 core

layout (location = 0) in vec3 position;

out vec2 texFrag;

uniform mat4 transform;

void main() 
{
	gl_Position = transform * vec4(position, 1.0);
	texFrag = vec2((position.x + 1.0) / 2.0, 1.0 - (position.y + 1.0) / 2.0);
}