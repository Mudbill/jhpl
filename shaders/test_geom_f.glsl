#version 330 core

in GS_OUT {
	vec3 color;
} gs_out;

out vec4 COLOR;

void main()
{
    COLOR = vec4(gs_out.color, 1.0);
}