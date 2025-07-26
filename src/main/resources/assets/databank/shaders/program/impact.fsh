#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D ImpactSampler;
uniform sampler2D FrozenImpactSampler;

uniform mat4 invViewMat;
uniform mat4 invProjMat;
uniform vec3 CameraPosition;
uniform float alpha;

in vec2 texCoord;
in vec2 oneTexel;
out vec4 fragColor;

void main() {
    vec4 color = texture(ImpactSampler, texCoord);
    vec4 colorFrozen = texture(FrozenImpactSampler, texCoord);
    float impactAlpha = max(color.a, colorFrozen.a);
    fragColor = vec4(mix(texture(DiffuseSampler, texCoord).rgb, mix(vec3(0.0, 0.0, 0.0), vec3(1.0, 1.0, 1.0), impactAlpha), alpha), 1.0);
}