#version 330 core
layout (location = 0) in vec3 inPosition;
layout (location = 3) in vec3 inNormal;
layout (location = 4) in vec3 inTangent;

out VS_OUT {
	vec3 T;
	vec3 B;
	vec3 N;
    vec4 normal;
} vs_out;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

void main()
{
    //gl_Position = projection * view * model * vec4(inPosition, 1.0); 
	vec4 pos = projection * view * model * vec4(inPosition, 1.0);
	gl_Position = pos.xyww;
    
    //vs_out.T = normalize(vec3(model * vec4(inTangent, 0.0)));
	//vs_out.N = normalize(vec3(model * vec4(inNormal, 0.0)));
	//vs_out.B = normalize(cross(vs_out.N, vs_out.T));
    
    mat4 normalMatrix = mat4(mat3(transpose(inverse(model))));
    vs_out.normal = normalize(vec4(projection * view * normalMatrix * vec4(inNormal, 0.0)));
    vs_out.normal = vs_out.normal.xyww;
}