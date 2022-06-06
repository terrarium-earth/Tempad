#version 150

in vec2 texCoord;
in vec4 color;

out vec4 fragColor;

vec3 rgb2hsv(vec3 c)
{
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}
vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    float sat =  sqrt(0.5 * (texCoord.x * 2 - texCoord.y) * (texCoord.x * 2- texCoord.y));
    float lum = texCoord.x * (1 - texCoord.y) + .5;


    vec3 RGBColor = color.rgb;
    vec3 HSLcolor = rgb2hsv(RGBColor);
    float hue = HSLcolor.r;
    fragColor = vec4(hsv2rgb(vec3(hue, sat, lum)), 1);
    //fragColor = vec4(texCoord, 0, 1);
}

