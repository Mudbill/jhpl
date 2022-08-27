#version 330 core

/* Constant values */
const float defaultBrightness = 0.1;

/* Structures */
struct Material
{
	sampler2D aDiffuseMap;
	sampler2D aSpecularMap;
	sampler2D aNormalMap;
	float shininess;
};

struct PointLight
{
	vec3 position;
	vec3 diffuse;
	vec3 specular;
	float constant;
	float linear;
	float quadratic;
};

/* Input from vertex shader */
in vec3 fragPos;
in vec2 texCoord;
in vec3 normalVector;
in vec3 tLightPos;
in vec3 tViewPos;
in vec3 tFragPos;
in mat3 TBN;

/* Output to OpenGL */
out vec4 COLOR;

/* Statically set variables */
uniform bool useNMap;
uniform int renderMode;
uniform vec3 viewPos;
uniform Material material;
uniform PointLight pLight;

/* Function definitions */
vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir);

/* Shader execution */
void main()
{
	vec3 output = vec3(0.0);
	
	vec4 mappedTexture = texture(material.aDiffuseMap, texCoord);
	if(mappedTexture.a < 0.1)
		discard;

	// normal calc
	vec3 normal;
	vec3 normalMap;
	if(useNMap) {
		normalMap = texture(material.aNormalMap, texCoord).rgb;
		normal = normalMap;
		normal.y = 1.0 - normal.y; // Flip the Y co-ordinate
		normal = normalize(normal * 2.0 - 1.0); 
		normal = normalize(TBN * normal);
	} else {
		normal = normalize(normalVector);
	}
	
	vec3 viewDir = normalize(viewPos - fragPos);
	//vec3 lightDir = normalize(pLight.position - fragPos);

	//float diff = max(dot(normal, lightDir), 0.0);
	//vec3 diffuse = pLight.diffuse * diff * vec3(mappedTexture);
	
	//vec3 reflectDir = reflect(-lightDir, normal);
	
	//float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
	//vec3 specular = pLight.specular * spec * vec3(texture(material.aSpecularMap, texCoord));
	
	output += CalcPointLight(pLight, normal, fragPos, viewDir);
	
	COLOR = vec4(output, 1.0);
	
	// For debugging
	if(renderMode == 1) 
	{
		normal = (normal + 0.5)/2.0;
		COLOR = vec4(normal, 1.0);
	}
}

vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir)
{
	vec4 spec_texture = texture(material.aSpecularMap, texCoord);
	float fSpecPower = exp2(spec_texture.g * 10.0) + 1.0;
	vec3 lightDir = normalize(light.position - fragPos);
	float diff = max(dot(normal, lightDir), defaultBrightness);
	vec3 reflectDir = reflect(-lightDir, normal);
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), fSpecPower);
	float distance = length(light.position - fragPos);
	float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance)); // Default
	vec3 diffuse = light.diffuse * diff * vec3(texture(material.aDiffuseMap, texCoord));
	vec3 specular = light.specular * spec * spec_texture.r; //Specular calculation - red channel
	diffuse *= attenuation;
	specular *= attenuation;
	return (diffuse + specular);
}
