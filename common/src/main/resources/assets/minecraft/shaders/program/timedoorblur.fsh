#version 150

const int blur = 4;
uniform sampler2D ScreenSampler;
uniform sampler2D ScreenDepthSampler;
uniform sampler2D TimedoorSampler;
uniform sampler2D TimedoorDepthSampler;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

void main() {

    vec4 worldColor = texture(ScreenSampler, texCoord);
    float worldDepth = texture(ScreenDepthSampler, texCoord).r;

    vec4 timedoorColor = texture(TimedoorSampler, texCoord);
    float timedoorDepth = texture(TimedoorDepthSampler, texCoord).r;

    vec2 refractedCoord = texCoord + vec2(0.01, -0.02);
    vec4 sum = vec4(0);

    sum += texture(ScreenSampler, vec2(refractedCoord.x - 4.0 * blur * oneTexel.x, refractedCoord.y - 4.0 * blur * oneTexel.y)) * 0.0162162162;
    sum += texture(ScreenSampler, vec2(refractedCoord.x - 3.0 * blur * oneTexel.x, refractedCoord.y - 3.0 * blur * oneTexel.y)) * 0.0540540541;
    sum += texture(ScreenSampler, vec2(refractedCoord.x - 2.0 * blur * oneTexel.x, refractedCoord.y - 2.0 * blur * oneTexel.y)) * 0.1216216216;
    sum += texture(ScreenSampler, vec2(refractedCoord.x - 1.0 * blur * oneTexel.x, refractedCoord.y - 1.0 * blur * oneTexel.y)) * 0.1945945946;

    sum += worldColor * 0.2270270270;

    sum += texture(ScreenSampler, vec2(refractedCoord.x + 1.0 * blur * oneTexel.x, refractedCoord.y + 1.0 * blur * oneTexel.y)) * 0.1945945946;
    sum += texture(ScreenSampler, vec2(refractedCoord.x + 2.0 * blur * oneTexel.x, refractedCoord.y + 2.0 * blur * oneTexel.y)) * 0.1216216216;
    sum += texture(ScreenSampler, vec2(refractedCoord.x + 3.0 * blur * oneTexel.x, refractedCoord.y + 3.0 * blur * oneTexel.y)) * 0.0540540541;
    sum += texture(ScreenSampler, vec2(refractedCoord.x + 4.0 * blur * oneTexel.x, refractedCoord.y + 4.0 * blur * oneTexel.y)) * 0.0162162162;

    sum += timedoorColor;

    vec4 color = mix(worldColor, sum, step(0.01, timedoorColor.a) * step(worldDepth, timedoorDepth));
    fragColor = vec4(color.rgb, 1);
}
