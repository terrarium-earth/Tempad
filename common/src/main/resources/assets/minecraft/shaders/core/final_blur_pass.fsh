#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D TimedoorSampler;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    vec4 timedoor = texture(TimedoorSampler, texCoord);

    fragColor = mix(color, timedoor, step(timedoor.a, 0.01));
}
