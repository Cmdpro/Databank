#version 150

uniform sampler2D ProjectedTarget;

uniform vec4 ColorModulator;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 color = texture(ProjectedTarget, vec2(texCoord0.x, texCoord0.y));
    if (color.a == 0.0) {
        discard;
    }
    fragColor = color * ColorModulator;
}