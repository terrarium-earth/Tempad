#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D WorldDepthSampler;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    float timedoorDepth = texture(DiffuseDepthSampler, texCoord).r;
    float worldDepth = texture(WorldDepthSampler, texCoord).r;

    if (worldDepth < timedoorDepth) {
        discard;
    }

    fragColor = texture(DiffuseSampler, texCoord);
}
