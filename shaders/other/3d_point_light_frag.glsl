#version 330 core

/* Input from vertex shader */
in vec3 normalVector;
in vec3 fragPos;
in vec2 texCoord;

/* Output to OpenGL */
out vec4 COLOR;

/* Statically set variables */
uniform vec3 objectColor;
uniform vec3 camPos;

struct Material 
{
	sampler2D 	diffuse;
	sampler2D 	specular;
	float 		shininess;
};

struct Light 
{
	vec3 position;
	vec3 direction;
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
	
	float constant;
	float linear;
	float quadratic;
};

uniform Material material;
uniform Light light;

/* Custom functions */
vec3 getDiffuse(vec3 normal, vec3 lightDirection);
vec3 getSpecular(vec3 reflectDirection);

//------------------------------------------

void main()
{
	vec3 norm = normalize(normalVector);
	vec3 lightDir = normalize(light.position - fragPos);
	
	vec3 ambient = light.ambient * vec3(texture(material.diffuse, texCoord));
	vec3 diffuse = getDiffuse(norm, lightDir);
	vec3 specular = getSpecular(reflect(-lightDir, norm));
	
	float distance = length(light.position - fragPos);
	float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
	
	//ambient *= attenuation;
	diffuse *= attenuation;
	specular *= attenuation;
	
	vec3 result = ambient + diffuse + specular;
	
	COLOR = vec4(result, 1.0); 
}

//------------------------------------------

vec3 getDiffuse(vec3 normal, vec3 lightDirection)
{
	float diff = max(dot(normal, lightDirection), 0.0);
	vec3 diffuse = light.diffuse * diff * vec3(texture(material.diffuse, texCoord));
	return diffuse;
}

//------------------------------------------

vec3 getSpecular(vec3 reflectDirection)
{
	vec3 viewDir = normalize(camPos - fragPos);
	float spec = pow(max(dot(viewDir, reflectDirection), 0.0), material.shininess);
	vec3 specular = light.specular * spec * vec3(texture(material.specular, texCoord));
	return specular;
}