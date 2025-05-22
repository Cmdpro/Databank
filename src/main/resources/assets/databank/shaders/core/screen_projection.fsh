#version 150

#moj_import <fog.glsl>

uniform sampler2D ProjectedTarget;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in vec2 texCoord0;
in float vertexDistance;

out vec4 fragColor;

void main() {
    vec4 color = texture(ProjectedTarget, vec2(texCoord0.x, texCoord0.y));
    if (color.a == 0.0) {
        discard;
    }
    color = color * ColorModulator;
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}