#version 150

#define blur 4
uniform sampler2D DiffuseSampler, TimedoorSampler;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

void main() {

    //fragColor = vec4(sum.rgb, 1.0);
    vec4 final = texture(DiffuseSampler, texCoord);
    if (texture(TimedoorSampler, texCoord).a > 0.01) {

        vec2 refractedCoord = texCoord + vec2(0.01, -0.02);
        vec4 sum = vec4(0);
        vec2 textureCoord;

        float horizontalStep = 1;//Set to 0 for no horizontal blur
        float verticalStep = 1;//Set 0 for no vertical blur

        sum += texture(DiffuseSampler, vec2(refractedCoord.x - 4.0 * blur * oneTexel.x, refractedCoord.y - 4.0 * blur * oneTexel.y)) * 0.0162162162;
        sum += texture(DiffuseSampler, vec2(refractedCoord.x - 3.0 * blur * oneTexel.x, refractedCoord.y - 3.0 * blur * oneTexel.y)) * 0.0540540541;
        sum += texture(DiffuseSampler, vec2(refractedCoord.x - 2.0 * blur * oneTexel.x, refractedCoord.y - 2.0 * blur * oneTexel.y)) * 0.1216216216;
        sum += texture(DiffuseSampler, vec2(refractedCoord.x - 1.0 * blur * oneTexel.x, refractedCoord.y - 1.0 * blur * oneTexel.y)) * 0.1945945946;

        sum += texture(DiffuseSampler, texCoord) * 0.2270270270;

        sum += texture(DiffuseSampler, vec2(refractedCoord.x + 1.0 * blur * oneTexel.x, refractedCoord.y + 1.0 * blur * oneTexel.y)) * 0.1945945946;
        sum += texture(DiffuseSampler, vec2(refractedCoord.x + 2.0 * blur * oneTexel.x, refractedCoord.y + 2.0 * blur * oneTexel.y)) * 0.1216216216;
        sum += texture(DiffuseSampler, vec2(refractedCoord.x + 3.0 * blur * oneTexel.x, refractedCoord.y + 3.0 * blur * oneTexel.y)) * 0.0540540541;
        sum += texture(DiffuseSampler, vec2(refractedCoord.x + 4.0 * blur * oneTexel.x, refractedCoord.y + 4.0 * blur * oneTexel.y)) * 0.0162162162;

        // length(sum.rgb) * 1/sqrt3
        final = sum;
        final += texture(TimedoorSampler, texCoord);
    }
    final.a = 1;
    fragColor = final;
}