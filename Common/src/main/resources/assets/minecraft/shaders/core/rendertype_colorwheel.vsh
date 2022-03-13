#version 150


in vec3 Position;
in vec2 UV0;

out vec2 texCoord;

uniform mat4 ModelViewMat, ProjMat;

void main() {
    texCoord = UV0;
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
}