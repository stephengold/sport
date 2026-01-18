/*
 * fragment shader for the Unshaded/Clipspace/Texture program in SPORT
 */
#version 330 core

uniform sampler2D ColorMaterialTexture;
in vec2 UV;
out vec4 fragColor;

void main() {
    fragColor = texture(ColorMaterialTexture, UV);
}
