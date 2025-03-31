#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;

out vec4 color;
out vec2 uv;
out vec2 uv2;

uniform mat4 ModelViewMat, ProjMat;

void main() {
    color = Color;
    uv = UV0;
    uv2 = UV2 / 16.0;
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
}