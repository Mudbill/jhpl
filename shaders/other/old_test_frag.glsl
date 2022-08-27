#version 330 core

const float defaultBrightness = 0.1;

/* Input from vertex shader */
in vec3 fragPos;
in vec2 texCoord;
in vec3 normalVector;
in mat3 TBN;

/* Output to OpenGL */
out vec4 COLOR;

/* Statically set variables */
uniform vec3 camPos;
uniform bool useNMap;
uniform bool setSpecMap;

struct Material {
	sampler2D diffuse;
	sampler2D specular;
	sampler2D normalMap;
	float shininess;
};
uniform Material material;

struct Light {
	vec3 position;
	vec3 diffuse;
	vec3 specular;
};
uniform Light light;

struct PointLight
{
	vec3 position;
	vec3 diffuse;
	vec3 specular;
	float constant;
	float linear;
	float quadratic;
};
uniform PointLight pLight;

//------------------------------------------
vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir);
//------------------------------------------

void main()
{
	vec3 output = vec3(0.0);
	
	if(texture(material.diffuse, texCoord).a < 0.1)
		discard;

	// normal calc
	vec3 norm;
	if(useNMap) {
		norm = texture(material.normalMap, texCoord).rgb;
		norm.y = 1.0 - norm.y;
		norm = normalize(norm * 2.0 - 1.0); 
		norm = normalize(TBN * norm);
	} else {
		norm = normalize(normalVector);
	}
	
	vec3 viewDir = TBN * normalize(camPos - fragPos);
	vec3 lightDir = TBN * normalize(light.position - fragPos);

	float diff = max(dot(norm, lightDir), 0.0);
	vec3 diffuse = light.diffuse * diff * vec3(texture(material.diffuse, texCoord));
	
	vec3 reflectDir = reflect(-lightDir, norm);
	
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
	vec3 specular = light.specular * spec * vec3(texture(material.specular, texCoord));
		
	//output = (diffuse + specular);
	
	output += CalcPointLight(pLight, norm, fragPos, viewDir);
	
	COLOR = vec4(output, 1.0); 
}

//------------------------------------

vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir)
{
	vec3 lightDir = normalize(light.position - fragPos);
	float diff = max(dot(normal, lightDir), defaultBrightness);
	vec3 reflectDir = reflect(-lightDir, normal);
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
	float distance = length(light.position - fragPos);
	float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance)); // Default
	vec3 diffuse = light.diffuse * diff * vec3(texture(material.diffuse, texCoord));
	vec3 specular = light.specular * spec * vec3(texture(material.specular, texCoord).r); //Specular calculation - red channel
	diffuse *= attenuation;
	specular *= attenuation;
	return (diffuse + specular);
}

//------------------------------------------
