#version 150

#define TWO_PI 6.28318530718
in vec2 texCoord;

out vec4 fragColor;

vec3 hsb2rgb( in vec3 c ){
    vec3 rgb = clamp(abs(mod(c.x * 6.0 + vec3(0.0, 4.0, 2.0), 6.0)-3.0)-1.0, 0.0, 1.0);
    rgb = rgb*rgb*(3.0-2.0*rgb);
    return rgb;
}

void main(){
    vec3 color = vec3(0.0);

    vec2 toCenter = vec2(0.5) - texCoord;
    float angle = atan(toCenter.y, toCenter.x);
    float radius = length(toCenter) * 2.0;

    color = hsb2rgb(vec3((angle / TWO_PI) + 0.5, radius, 1.0));
    float innerCutout = step(radius - .15, .5);
    float outsideWheel = step(radius - .3, .5);
    fragColor = vec4(color, outsideWheel - innerCutout);

}