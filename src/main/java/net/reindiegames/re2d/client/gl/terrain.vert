#version 400 core

layout(location = 0) in vec2 position;
layout(location = 1) in vec2 texture_coords;

uniform vec2 camera;

out vec2 pass_texture_coords;

void main() {
    gl_Position = vec4(position.xy, 0.0, 1.0);
    pass_texture_coords = texture_coords;
}
