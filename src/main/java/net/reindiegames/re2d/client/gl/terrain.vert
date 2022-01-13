#version 400 core

layout(location = 0) in vec2 position;
layout(location = 1) in vec2 texture_coords;

uniform mat4 transformation;
uniform mat4 projection;
uniform mat4 view;

uniform vec2 camera;

out vec2 pass_texture_coords;

void main() {
    vec4 world_position = transformation * vec4(position.xy, 0.0, 1.0);
    gl_Position = projection * view * world_position;
    pass_texture_coords = texture_coords;
}
