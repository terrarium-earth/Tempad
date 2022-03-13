#version 150


in vec3 Position;
in vec4 Color;
in vec2 UV0, UV2;

out vec4 color;
out vec2 texCoord, lightmap;

uniform mat4 ModelViewMat, ProjMat;

void main() {
    color = Color;
    texCoord = UV0;
    lightmap = UV2;
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1);
}