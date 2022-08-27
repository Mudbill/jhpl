#version 330 core

/* Input from vertex shader */
in vec3 normalVector;
in vec3 fragPos;
in vec2 texCoord;

/* Output to OpenGL */
out vec4 COLOR;

/* Statically set variables */
uniform vec3 camPos;

struct Material 
{
	sampler2D 	diffuse;
	sampler2D 	specular;
	float 		shininess;
};

struct DirLight 
{
	vec3 direction;
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
};

struct PointLight
{
	vec3 position;
	float constant;
	float linear;
	float quadratic;
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
};

struct SpotLight
{
	vec3 position;
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
};

#define NR_POINT_LIGHTS 4

uniform Material material;
uniform DirLight dirLight;
uniform PointLight pointLights[NR_POINT_LIGHTS];

/* Custom functions */
vec3 CalcDirLight(DirLight light, vec3 normal, vec3 viewDir);
vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir);

//------------------------------------------

void main()
{
	vec3 norm = normalize(normalVector);
	vec3 viewDir = normalize(camPos - fragPos);
	
	// Lightning calculations
	vec3 result = CalcDirLight(dirLight, norm, viewDir);
	
	for(int i = 0; i < NR_POINT_LIGHTS; i++)
	{
		result += CalcPointLight(pointLights[i], norm, fragPos, viewDir);
	}
	
	COLOR = vec4(result, 1.0); 
}

//------------------------------------------

vec3 CalcDirLight(DirLight light, vec3 normal, vec3 viewDir)
{
	vec3 lightDir = normalize(-light.direction);
	float diff = max(dot(normal, lightDir), 0.0);
	vec3 reflectDir = reflect(-lightDir, normal);
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
	vec3 ambient 	= light.ambient 		* vec3(texture(material.diffuse, texCoord));
	vec3 diffuse 	= light.diffuse  * diff * vec3(texture(material.diffuse, texCoord));
	vec3 specular 	= light.specular * spec * vec3(texture(material.specular, texCoord));
	return (ambient + diffuse + specular);
}

//------------------------------------------

vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir)
{
	vec3 lightDir = normalize(-light.position);
	float diff = max(dot(normal, lightDir), 0.0);
	vec3 reflectDir = reflect(-lightDir, normal);
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
	float distance = length(light.position - fragPos);
	float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
	vec3 ambient = light.ambient * vec3(texture(material.diffuse, texCoord));
	vec3 diffuse = light.diffuse * diff * vec3(texture(material.diffuse, texCoord));
	vec3 specular = light.specular * spec * vec3(texture(material.specular, texCoord));
	ambient *= attenuation;
	diffuse *= attenuation;
	specular *= attenuation;
	return (ambient + diffuse + specular);
}

//vec3 CalcSpotLight(SpotLight light)
//{
	
//}
