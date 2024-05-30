#version 150

const int blur = 4;
uniform sampler2D DiffuseSampler;
uniform sampler2D TimedoorSampler;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

void main() {

    vec4 worldColor = texture(DiffuseSampler, texCoord);
    vec4 timedoorColor = texture(TimedoorSampler, texCoord);

    vec2 refractedCoord = texCoord + vec2(0.01, -0.02);
    vec4 sum = vec4(0);

    sum += texture(DiffuseSampler, vec2(refractedCoord.x - 4.0 * blur * oneTexel.x, refractedCoord.y - 4.0 * blur * oneTexel.y)) * 0.0162162162;
    sum += texture(DiffuseSampler, vec2(refractedCoord.x - 3.0 * blur * oneTexel.x, refractedCoord.y - 3.0 * blur * oneTexel.y)) * 0.0540540541;
    sum += texture(DiffuseSampler, vec2(refractedCoord.x - 2.0 * blur * oneTexel.x, refractedCoord.y - 2.0 * blur * oneTexel.y)) * 0.1216216216;
    sum += texture(DiffuseSampler, vec2(refractedCoord.x - 1.0 * blur * oneTexel.x, refractedCoord.y - 1.0 * blur * oneTexel.y)) * 0.1945945946;

    sum += worldColor * 0.2270270270;

    sum += texture(DiffuseSampler, vec2(refractedCoord.x + 1.0 * blur * oneTexel.x, refractedCoord.y + 1.0 * blur * oneTexel.y)) * 0.1945945946;
    sum += texture(DiffuseSampler, vec2(refractedCoord.x + 2.0 * blur * oneTexel.x, refractedCoord.y + 2.0 * blur * oneTexel.y)) * 0.1216216216;
    sum += texture(DiffuseSampler, vec2(refractedCoord.x + 3.0 * blur * oneTexel.x, refractedCoord.y + 3.0 * blur * oneTexel.y)) * 0.0540540541;
    sum += texture(DiffuseSampler, vec2(refractedCoord.x + 4.0 * blur * oneTexel.x, refractedCoord.y + 4.0 * blur * oneTexel.y)) * 0.0162162162;

    sum += timedoorColor;

    fragColor = vec4(mix(worldColor, sum, step(0.01, timedoorColor.a)).rgb, 1);
}
