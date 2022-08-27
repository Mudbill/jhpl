#version 330 core

/* Structures */
struct PointLight
{
	vec3 position;
	vec3 diffuse;
	vec3 specular;
	float constant;
	float linear;
	float quadratic;
};

/* Input from OpenGL */
layout (location = 0) in vec3 inPos;
layout (location = 2) in vec2 inTex;
layout (location = 3) in vec3 inNor;
layout (location = 4) in vec3 inTan;

/* Output to fragment shader */
out vec3 normalVector;
out vec3 fragPos;
out vec2 texCoord;
out mat3 TBN;
out vec3 tLightPos;
out vec3 tViewPos;
out vec3 tFragPos;

/* Statically set variables */
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform vec3 viewPos;
uniform PointLight pLight;

/* Shader execution */
void main() 
{
	vec4 pos = vec4(inPos, 1.0); 
	gl_Position = projection * view * model * pos;
	fragPos = vec3(model * pos);
	
	normalVector = mat3(model) * inNor;
	texCoord = inTex;
	
	vec3 T = normalize(vec3(model * vec4(inTan, 0.0)));
	vec3 N = normalize(vec3(model * vec4(inNor, 0.0)));
	vec3 B = normalize(cross(N, T));
	TBN = mat3(T, B, N);
		
	tLightPos = pLight.position * TBN;
	tViewPos = viewPos * TBN;
	tFragPos = vec3(model * vec4(inPos, 0.0)) * TBN;
}
