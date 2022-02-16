#version 400 core

in vec2 pass_texture_coords;

out vec4 out_color;

uniform sampler2D texture_sampler;
uniform bool hurt;

void main() {
    out_color = texture(texture_sampler, pass_texture_coords);

    if(hurt) {
        out_color = mix(out_color, vec4(1.0f, 0.0f, 0.0f, 1.0f), 0.5);
    }
}
