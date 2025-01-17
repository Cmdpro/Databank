var duplicateNames = {}

function getNewName(name) {
    if (Object.hasOwn(duplicateNames, name)) {
        duplicateNames[name] += 1
        return name + duplicateNames[name].toString()
    }
    duplicateNames[name] = 0
    return name
}

function goThroughChildren(array, parentOffset) {
    var parts = []
    array.forEach(element => {
        if (element instanceof Cube) {
            var part = {}
            part.name = getNewName(element.name)
            part.rotation = [
                Math.degToRad(-element.rotation[0]),
                Math.degToRad(-element.rotation[1]),
                Math.degToRad(element.rotation[2])
            ]
            part.offset = [
                element.origin[0],
                element.origin[1],
                element.origin[2]
            ]
            if (element.parent instanceof Group) {
                part.offset[0] -= element.parent.origin[0]
                part.offset[1] -= element.parent.origin[1]
                part.offset[2] -= element.parent.origin[2]
            }
            part.offset[0] *= -1;
            if (Project.modded_entity_flip_y) {
                part.offset[1] *= -1;
                if (element.parent instanceof Group === false) {
                    part.offset[1] += 24
                }
            }
            part.isCube = true
            part.texOffset = [
                element.uv_offset[0],
                element.uv_offset[1]
            ]
            part.mirror = element.mirror_uv
            if (element.parent instanceof Group) {
                if (Project.modded_entity_flip_y) {
                    part.origin = [
                        element.origin[0] - element.to[0],
                        -element.from[1] - element.size(1) + element.origin[1],
                        element.from[2] - element.origin[2]
                    ]
                } else {
                    part.origin = [
                        element.origin[0] - element.to[0],
                        element.from[1] - element.origin[1],
                        element.from[2] - element.origin[2]
                    ]
                }
            }
            part.dimensions = [
                element.size(0, false),
                element.size(1, false),
                element.size(2, false)
            ]
            part.inflate = element.inflate
            parts.push(part)
        } else if (element instanceof Group) {
            var part = {}
            part.name = element.name
            part.rotation = [
                Math.degToRad(-element.rotation[0]),
                Math.degToRad(-element.rotation[1]),
                Math.degToRad(element.rotation[2])
            ]
            part.offset = [
                element.origin[0],
                element.origin[1],
                element.origin[2]
            ]
            if (element.parent instanceof Group) {
                part.offset[0] -= element.parent.origin[0]
                part.offset[1] -= element.parent.origin[1]
                part.offset[2] -= element.parent.origin[2]
            }
            part.offset[0] *= -1;
            if (Project.modded_entity_flip_y) {
                part.offset[1] *= -1;
                if (element.parent instanceof Group === false) {
                    part.offset[1] += 24
                }
            }
            part.isCube = false
            if (element.children != null) {
                part.children = goThroughChildren(element.children, part.offset)
            }
            parts.push(part)
        }
    });
    return parts
}
let modelFormat, codec
Plugin.register('databank_blockbench', {
    title: 'Databank Utils',
    author: 'Cmdpro',
    icon: 'icon',
    description: 'Useful utilities for the Databank mod',
    version: '1.0.0',
    variant: 'both',
    onload() {
        console.log("Loaded Databank Utils Plugin")
        modelFormat = new ModelFormat({
            id: 'databank',
            name: "Databank Model",
            icon: 'icon-format_java',
            category: 'minecraft',
            target: 'Databank',
            box_uv: true,
            box_uv_float_size: true,
            single_texture: true,
            bone_rig: true,
            centered_grid: true,
            rotate_cubes: true,
            integer_size: true,
            animation_mode: true,
            display_mode: true,
        })
        button = new Action('export_to_databank_model', {
            name: 'Export to Databank Model',
            description: 'Exports to a Databank Model json',
            icon: "archive",
            category: "file",
            condition: () => Format.id === 'databank',
            click: function() {
                var model = {}

                duplicateNames = {}

                var animations = {}
                Project.animations.forEach(anim => {
                    animParts = []
                    var channelTypes = ['rotation', 'position', 'scale']
                    for (let animatorId in anim.animators) {
                        var animator = anim.animators[animatorId]
                        if (animator instanceof BoneAnimator) {
                            channelTypes.forEach(id => {
                                var currentKeyframes = animator[id].slice().sort((a, b) => a.time - b.time)
                                var keyframes = []
                                currentKeyframes.forEach(keyframe => {
                                    function addKeyframe(timestamp, x, y, z, interpolation) {
                                        var interpolationStr = keyframe.interpolation == 'catmullrom' ? "smooth" : "linear"
                                        keyframes.push({
                                            timestamp: timestamp,
                                            target: [
                                                x,
                                                y,
                                                z
                                            ],
                                            interpolation: interpolationStr
                                        })
                                    }
                                    addKeyframe(keyframe.time, keyframe.calc('x'), keyframe.calc('y'), keyframe.calc('z'), keyframe.interpolation);
                                    if (keyframe.data_points[1]) {
                                        addKeyframe(kf.time+0.001, keyframe.calc('x', 1), keyframe.calc('y', 1), keyframe.calc('z', 1), keyframe.interpolation);
                                    } else if (keyframe.interpolation == 'step' && currentKeyframes[i+1]) {
                                        let next = currentKeyframes[i+1];
                                        addKeyframe(next.time-0.001, keyframe.calc('x'), keyframe.calc('y'), keyframe.calc('z'), 'linear');
                                    }

                                })
                                if (keyframes.length > 0) {
                                    animParts.push({
                                        bone: animator.name,
                                        target: id,
                                        keyframes: keyframes
                                    })
                                }
                            })
                        }
                    }
                    animations[anim.name] = {
                        length: anim.length,
                        looping: anim.loop == 'loop',
                        animation: animParts
                    }
                })
                model.animations = animations

                var rootGroups = []
                Project.groups.forEach(element => {
                    if (element.parent == 'root') {
                        rootGroups.push(element)
                    }
                })
                var parts = goThroughChildren(rootGroups, parts)
                model.parts = parts

                model.textureSize = [
                    Project.texture_width,
                    Project.texture_height
                ];

                var databankModelString = JSON.stringify(model, null, 2)
                Blockbench.export({
                    type: "Databank Model",
                    extensions: ['json'],
                    content: databankModelString
                });
            }
        })
        MenuBar.addAction(button, 'file.export');
    },
    onunload() {
        button.delete()
        modelFormat.delete()
    }
});
