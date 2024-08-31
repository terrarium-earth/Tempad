#version 150

const float pixelsPerUnit = 16.0;

in vec4 color;
in vec2 uv2;
in vec2 uv;

out vec4 fragColor;

void main() {
    float pixelWidth = (1.0 / 16.0) / (1 + ((uv2.x - uv.x) / uv.x)); //pixel width
    float pixelHeight = (1.0 / 16.0) / (1 + ((uv2.y - uv.y) / uv.y)); //pixel height
    vec2 pixelateduv2 = vec2(uv.x - mod(uv.x, pixelWidth) + pixelWidth / 2, uv.y - mod(uv.y, pixelHeight) + pixelHeight / 2);

    float x = 2 * pixelateduv2.x - 1;
    float y = 2 * pixelateduv2.y - 1;
    float alpha = x * x / 3 + y * y / 3;
    fragColor = vec4(color.rgb, alpha - mod(alpha, 0.07) + 0.1);
}
