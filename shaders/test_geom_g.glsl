#version 330 core
layout (triangles) in;
layout (line_strip, max_vertices = 6) out;

in VS_OUT {
	vec3 T;
	vec3 B;
	vec3 N;
    vec4 normal;
} gs_in[];

out GS_OUT {
	vec3 color;
} gs_out;

const float MAGNITUDE = 0.1;

void GenerateLine(int index)
{
    gl_Position = gl_in[index].gl_Position;
    EmitVertex();
    gl_Position = gl_in[index].gl_Position + gs_in[index].normal * MAGNITUDE;
    EmitVertex();
    EndPrimitive();
}

void main()
{
	gs_out.color = vec3(1, 0, 0);
    GenerateLine(0); // first vertex normal
	gs_out.color = vec3(0, 1, 0);
    GenerateLine(1); // second vertex normal
	gs_out.color = vec3(0, 0, 1);
    GenerateLine(2); // third vertex normal
}