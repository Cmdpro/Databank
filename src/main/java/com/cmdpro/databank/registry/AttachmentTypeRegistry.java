package com.cmdpro.databank.registry;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.dialogue.DialogueInstance;
import com.cmdpro.databank.dialogue.DialogueTree;
import com.cmdpro.databank.instanceddimension.InstancedDimension;
import com.cmdpro.databank.instanceddimension.PlayerLastLocationData;
import com.cmdpro.databank.misc.VersionChangeHelper;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class AttachmentTypeRegistry {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES,
            Databank.MOD_ID);
    public static final Supplier<AttachmentType<Optional<BlockEntity>>> BINDING_BLOCK =
            register("binding_block", () -> AttachmentType.builder(() -> Optional.ofNullable((BlockEntity)null)).build());

    public static final Supplier<AttachmentType<HashMap<String, String>>> MOD_VERSIONS =
            register("mod_versions", () -> AttachmentType.builder(() -> new HashMap<String, String>()).serialize(Codec.unboundedMap(Codec.STRING, Codec.STRING).xmap(HashMap::new, HashMap::new)).copyOnDeath().build());

    public static final Supplier<AttachmentType<Optional<DialogueInstance>>> CURRENT_DIALOGUE =
            register("current_dialogue", () -> AttachmentType.builder(() -> Optional.ofNullable((DialogueInstance)null)).build());

    public static final Supplier<AttachmentType<ArrayList<InstancedDimension.Instance>>> INSTANCED_DIMENSIONS =
            register("instanced_dimensions", () -> AttachmentType.builder(() -> new ArrayList<InstancedDimension.Instance>()).serialize(InstancedDimension.Instance.CODEC.listOf().xmap(ArrayList::new, ArrayList::new)).build());

    public static final Supplier<AttachmentType<ArrayList<InstancedDimension.Instance>>> TEMP_INSTANCED_DIMENSIONS =
            register("temp_instanced_dimensions", () -> AttachmentType.builder(() -> new ArrayList<InstancedDimension.Instance>()).serialize(InstancedDimension.Instance.CODEC.listOf().xmap(ArrayList::new, ArrayList::new)).build());

    public static final Supplier<AttachmentType<Optional<PlayerLastLocationData>>> LAST_LOCATION_DATA =
            register("last_location_data", () -> AttachmentType.builder(() -> Optional.ofNullable((PlayerLastLocationData)null)).serialize(PlayerLastLocationData.CODEC.xmap(Optional::of, Optional::get), Optional::isPresent).build());

    private static <T extends AttachmentType<?>> Supplier<T> register(final String name, final Supplier<T> attachment) {
        return ATTACHMENT_TYPES.register(name, attachment);
    }
}