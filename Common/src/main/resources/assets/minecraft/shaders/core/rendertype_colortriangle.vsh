#version 150


in vec3 Position;
in vec2 UV0;
in vec4 Color;

out vec2 texCoord;
out vec4 color;

uniform mat4 ModelViewMat, ProjMat;

void main() {
    texCoord = UV0;
    color = Color;
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
}