#version 400 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texture_coords;

uniform mat4 transformation;
uniform mat4 view;
uniform mat4 projection;
uniform float depth;

out vec2 pass_texture_coords;

void main() {
    gl_Position = projection * view * transformation * vec4(position.xy, position.z + depth, 1.0);
    pass_texture_coords = texture_coords;
}
