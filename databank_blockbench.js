function makeCodec() {
    return new Codec('databank_block_model', {
        name: 'Java Block/Item Model',
        remember: true,
        extension: 'json',
        support_partial_export: true,
        load_filter: {
            type: 'json',
            extensions: ['json'],
            condition(model) {
                return false
            }
        },
        compile(options) {
            if (options === undefined) options = {}
            var textures_used = []
            var element_index_lut = []

            function checkExport(key, condition) {
                key = options[key]
                if (key === undefined) {
                    return condition;
                } else {
                    return key
                }
            }
            var isTexturesOnlyModel = checkExport('parent', Project.parent != '')
            var texturesObj = {}
            Texture.all.forEach(function(t, i){
                var link = t.javaTextureLink()
                if (t.particle) {
                    texturesObj.particle = link
                }
                if (!textures_used.includes(t) && !isTexturesOnlyModel) return;
                if (t.id !== link.replace(/^#/, '')) {
                    texturesObj[t.id] = link
                }
            })

            var blockmodel = {}
            if (checkExport('comment', Project.credit || settings.credit.value)) {
                blockmodel.credit = Project.credit || settings.credit.value
            }
            if (checkExport('parent', Project.parent != '')) {
                blockmodel.parent = Project.parent
            }
            if (checkExport('ambientocclusion', Project.ambientocclusion === false)) {
                blockmodel.ambientocclusion = false
            }
            if (Project.unhandled_root_fields.render_type) {
                blockmodel.render_type = Project.unhandled_root_fields.render_type;
            }
            if (Project.texture_width !== 16 || Project.texture_height !== 16) {
                blockmodel.texture_size = [Project.texture_width, Project.texture_height]
            }
            if (checkExport('textures', Object.keys(texturesObj).length >= 1)) {
                blockmodel.textures = texturesObj
            }
            if (checkExport('front_gui_light', Project.front_gui_light)) {
                blockmodel.gui_light = 'front';
            }
            if (checkExport('overrides', Project.overrides instanceof Array && Project.overrides.length)) {
                Project.overrides.forEach(override => delete override._uuid)
                blockmodel.overrides = Project.overrides.map(override => new oneLiner(override));
            }
            if (checkExport('display', Object.keys(Project.display_settings).length >= 1)) {
                var new_display = {}
                var entries = 0;
                for (var i in DisplayMode.slots) {
                    var key = DisplayMode.slots[i]
                    if (DisplayMode.slots.hasOwnProperty(i) && Project.display_settings[key] && Project.display_settings[key].export) {
                        new_display[key] = Project.display_settings[key].export()
                        entries++;
                    }
                }
                if (entries) {
                    blockmodel.display = new_display
                }
            }
            for (let key in Project.unhandled_root_fields) {
                if (blockmodel[key] === undefined) blockmodel[key] = Project.unhandled_root_fields[key];
            }
            this.dispatchEvent('compile', {model: blockmodel, options});
            if (options.raw) {
                return blockmodel
            } else {
                return autoStringify(blockmodel)
            }
        },
        parse(model, path, add) {},
    })
}

var duplicateNames = {}
var nameMap = {}
var allBones = []

function getNewName(name) {
    if (Object.hasOwn(duplicateNames, name)) {
        duplicateNames[name] += 1
        return name + duplicateNames[name].toString()
    }
    duplicateNames[name] = 0
    return name
}

function getNewNamesRecursive(children) {
    children.forEach(element => {
        if (element.name != null) {
            nameMap[element.uuid] = getNewName(element.name)
        }
        if (element.children != null) {
            getNewNamesRecursive(element.children)
        }
    })
}
function findBones(children) {
    children.forEach(element => {
        if (element instanceof ArmatureBone) {
            allBones.push(element)
        }
        if (element.children != null) {
            findBones(element.children)
        }
    })
}

function goThroughChildren(array, parentOffset) {
    var parts = []
    array.forEach(element => {
        if (element instanceof Cube) {
            var part = {}
            part.type = "databank:cube"
            part.name = nameMap[element.uuid]
            part.rotation = [
                Math.degToRad(element.rotation[0]),
                Math.degToRad(element.rotation[1]),
                Math.degToRad(element.rotation[2])
            ]
            part.offset = [
                element.origin[0]-parentOffset[0],
                element.origin[1]-parentOffset[1],
                element.origin[2]-parentOffset[2]
            ]
            part.texOffset = [
                element.uv_offset[0],
                element.uv_offset[1]
            ]
            part.mirror = element.mirror_uv
            part.origin = [
                element.from[0] - element.origin[0],
                element.from[1] - element.origin[1],
                element.from[2] - element.origin[2]
            ]
            part.dimensions = [
                element.size(0, false),
                element.size(1, false),
                element.size(2, false)
            ]
            part.inflate = element.inflate
            parts.push(part)
        } else if (element instanceof Group) {
            var part = {}
            part.type = "databank:group"
            part.name = nameMap[element.uuid]
            part.rotation = [
                Math.degToRad(element.rotation[0]),
                Math.degToRad(element.rotation[1]),
                Math.degToRad(element.rotation[2])
            ]
            part.offset = [
                element.origin[0]-parentOffset[0],
                element.origin[1]-parentOffset[1],
                element.origin[2]-parentOffset[2]
            ]
            if (element.children != null) {
                part.children = goThroughChildren(element.children, [ element.origin[0], element.origin[1], element.origin[2] ])
            }
            parts.push(part)
        } else if (element instanceof Mesh) {
            var part = {}
            part.type = "databank:mesh"
            part.name = nameMap[element.uuid]
            part.rotation = [
                    Math.degToRad(element.rotation[0]),
                  Math.degToRad(element.rotation[1]),
                  Math.degToRad(element.rotation[2])
            ]
            part.offset = [
                element.origin[0]-parentOffset[0],
                element.origin[1]-parentOffset[1],
                element.origin[2]-parentOffset[2]
            ]
            var vertices = {}
            for (var k in element.vertices) {
                var current = element.vertices[k]
                var vertice = {}
                vertice.x = current[0]
                vertice.y = current[1]
                vertice.z = current[2]
                vertice.weights = {}
                var bones = []
                var key = element.uuid.substring(0, 6) + ':' + k
                allBones.forEach(bone => {
                    var weight = bone.vertex_weights[key] ?? bone.vertex_weights[k] ?? 0
                    if (weight > 0) {
                        vertice.weights[nameMap[bone.uuid]] = weight
                        bones.push(bone)
                    }
                })
                vertices[k] = vertice
            }
            part.vertices = vertices
            var faces = []
            for (var k in element.faces) {
                var v = element.faces[k]
                var face = []
                v.getSortedVertices().forEach((j) => {
                    var vertice = {}
                    if (j in element.vertices) {
                        vertice.id = j
                        if (j in v.uv) {
                            var uv = v.uv[j]
                            vertice.u = uv[0]
                            vertice.v = uv[1]
                        }
                    }
                    face.push(vertice)
                })
                faces.push(face)
            }
            part.faces = faces
            parts.push(part)
        } else if (element instanceof Armature) {
            var part = {}
            part.type = "databank:armature"
            part.name = nameMap[element.uuid]
            part.offset = [
                element.origin[0]-parentOffset[0],
                element.origin[1]-parentOffset[1],
                element.origin[2]-parentOffset[2]
            ]
            if (element.children != null) {
                part.children = goThroughChildren(element.children, [ element.origin[0], element.origin[1], element.origin[2] ])
            }
            parts.push(part)
        } else if (element instanceof ArmatureBone) {
            var part = {}
            part.type = "databank:bone"
            part.name = nameMap[element.uuid]
            part.offset = [
                element.origin[0],
                element.origin[1],
                element.origin[2]
            ]
            part.rotation = [
                Math.degToRad(element.rotation[0]),
                  Math.degToRad(element.rotation[1]),
                  Math.degToRad(element.rotation[2])
            ]
            part.dimensions = [
                element.size(0, false),
                  element.size(1, false),
                  element.size(2, false)
            ]
            if (element.children != null) {
                part.children = goThroughChildren(element.children, [ element.origin[0], element.origin[1], element.origin[2] ])
            }
            parts.push(part)
        }
    });
    return parts
}
let modelFormat, codec, export_to_databank_model, export_display
Plugin.register('databank_blockbench', {
    title: 'Databank Utils',
    author: 'Cmdpro',
    icon: 'icon',
    description: 'Useful utilities for the Databank mod',
    version: '1.0.0',
    variant: 'both',
    onload() {
        console.log("Loaded Databank Utils Plugin")
        codec = makeCodec()
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
            select_texture_for_particles: true,
            meshes: true,
            armature_rig: true,
        })
        codec.format = modelFormat
        export_display = new Action({
            id: 'export_blockmodel',
            icon: 'icon-format_block',
            category: 'file',
            condition: () => Format.id === 'databank',
            click: function () {
                codec.export();
            }
        })
        export_to_databank_model = new Action('export_to_databank_model', {
            name: 'Export to Databank Model',
            description: 'Exports to a Databank Model json',
            icon: "archive",
            category: "file",
            condition: () => Format.id === 'databank',
            click: function() {
                var model = {}

                duplicateNames = {}
                nameMap = {}
                allBones = []

                var rootGroups = []
                Project.groups.forEach(element => {
                    if (element.parent == 'root') {
                        rootGroups.push(element)
                    }
                })
                getNewNamesRecursive(rootGroups)
                findBones(rootGroups)

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
                                    var xMult = 1;
                                    var yMult = 1;
                                    var zMult = 1;
                                    addKeyframe(keyframe.time, keyframe.calc('x')*xMult, keyframe.calc('y')*yMult, keyframe.calc('z')*zMult, keyframe.interpolation);
                                    if (keyframe.data_points[1]) {
                                        addKeyframe(kf.time+0.001, keyframe.calc('x', 1)*xMult, keyframe.calc('y', 1)*yMult, keyframe.calc('z', 1)*zMult, keyframe.interpolation);
                                    } else if (keyframe.interpolation == 'step' && currentKeyframes[i+1]) {
                                        let next = currentKeyframes[i+1];
                                        addKeyframe(next.time-0.001, keyframe.calc('x')*xMult, keyframe.calc('y')*yMult, keyframe.calc('z')*zMult, 'linear');
                                    }

                                })
                                if (keyframes.length > 0) {
                                    animParts.push({
                                        bone: nameMap[animator.element.uuid],
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

                var parts = goThroughChildren(rootGroups, [0, 0, 0])
                model.parts = parts

                model.textureSize = [
                    Project.texture_width,
                    Project.texture_height
                ];

                model.version = 1

                var databankModelString = JSON.stringify(model, null, 2)
                Blockbench.export({
                    type: "Databank Model",
                    extensions: ['json'],
                    content: databankModelString
                });
            }
        })
        MenuBar.addAction(export_to_databank_model, 'file.export');
        MenuBar.addAction(export_display, 'file.export');
    },
    onunload() {
        export_to_databank_model.delete()
        export_display.delete()
        modelFormat.delete()
    }
});
