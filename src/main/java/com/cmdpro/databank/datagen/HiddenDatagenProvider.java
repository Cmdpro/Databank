package com.cmdpro.databank.datagen;

import com.cmdpro.databank.hidden.Hidden;
import com.cmdpro.databank.hidden.HiddenCondition;
import com.cmdpro.databank.hidden.HiddenSerializer;
import com.cmdpro.databank.hidden.HiddenTypeInstance;
import com.cmdpro.databank.hidden.conditions.*;
import com.cmdpro.databank.hidden.types.BlockHiddenType;
import com.cmdpro.databank.hidden.types.ItemHiddenType;
import com.mojang.serialization.Codec;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.JsonCodecProvider;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class HiddenDatagenProvider extends JsonCodecProvider<Hidden> {
    public HiddenDatagenProvider(PackOutput output, CompletableFuture lookupProvider, String modId, ExistingFileHelper existingFileHelper) {
        super(output, PackOutput.Target.DATA_PACK, "databank/hidden", PackType.SERVER_DATA, HiddenSerializer.ORIGINAL_CODEC, lookupProvider, modId, existingFileHelper);
    }
    @Override
    public String getName() {
        return "Hidden";
    }
    public void createHidden(ResourceLocation id, HiddenTypeInstance<?> instance, HiddenCondition condition) {
        unconditional(id, new Hidden(instance, condition));
    }
    public BlockHiddenType.BlockHiddenTypeInstance createBlockInstance(Block original, Block hiddenAs) {
        return new BlockHiddenType.BlockHiddenTypeInstance(
                original,
                hiddenAs,
                Optional.empty(),
                new ActualPlayerCondition(),
                true,
                Optional.empty(),
                new ArrayList<>()
        );
    }
    public BlockHiddenType.BlockHiddenTypeInstance setNameOverride(BlockHiddenType.BlockHiddenTypeInstance instance, Component nameOverride) {
        instance.nameOverride = nameOverride != null ? Optional.of(nameOverride) : Optional.empty();
        return instance;
    }
    public BlockHiddenType.BlockHiddenTypeInstance setOriginalLootCondition(BlockHiddenType.BlockHiddenTypeInstance instance, HiddenCondition condition) {
        if (condition != null) {
            instance.dropOriginalLootCondition = condition;
            instance.shouldOverwriteLootIfHidden = true;
        } else {
            instance.shouldOverwriteLootIfHidden = false;
        }
        return instance;
    }
    public BlockHiddenType.BlockHiddenTypeInstance setShouldApplyPredicate(BlockHiddenType.BlockHiddenTypeInstance instance, StatePropertiesPredicate predicate) {
        instance.shouldApplyPredicate = predicate != null ? Optional.of(predicate) : Optional.empty();
        return instance;
    }
    public BlockHiddenType.BlockHiddenTypeInstance addOverride(BlockHiddenType.BlockHiddenTypeInstance instance, BlockHiddenType.BlockHiddenOverride override) {
        instance.overrides.add(override);
        return instance;
    }
    public BlockHiddenType.BlockHiddenOverride createOverride(StatePropertiesPredicate predicate, Block hiddenAs) {
        return new BlockHiddenType.BlockHiddenOverride(
                predicate,
                hiddenAs,
                Optional.empty(),
                new ActualPlayerCondition(),
                true
        );
    }
    public BlockHiddenType.BlockHiddenOverride setNameOverride(BlockHiddenType.BlockHiddenOverride instance, Component nameOverride) {
        instance.nameOverride = nameOverride != null ? Optional.of(nameOverride) : Optional.empty();
        return instance;
    }
    public BlockHiddenType.BlockHiddenOverride setOriginalLootCondition(BlockHiddenType.BlockHiddenOverride instance, HiddenCondition condition) {
        if (condition != null) {
            instance.dropOriginalLootCondition = condition;
            instance.shouldOverwriteLootIfHidden = true;
        } else {
            instance.shouldOverwriteLootIfHidden = false;
        }
        return instance;
    }
    public ItemHiddenType.ItemHiddenTypeInstance createItemInstance(Item original, Item hiddenAs) {
        return new ItemHiddenType.ItemHiddenTypeInstance(
                original,
                hiddenAs,
                Optional.empty()
        );
    }
    public ItemHiddenType.ItemHiddenTypeInstance setNameOverride(ItemHiddenType.ItemHiddenTypeInstance instance, Component nameOverride) {
        instance.nameOverride = nameOverride != null ? Optional.of(nameOverride) : Optional.empty();
        return instance;
    }
    public AdvancementCondition createAdvancementCondition(ResourceKey<Advancement> advancement) {
        return new AdvancementCondition(advancement);
    }
    public NotCondition createNotCondition(HiddenCondition condition) {
        return new NotCondition(condition);
    }
    public AndCondition createAndCondition(HiddenCondition conditionA, HiddenCondition conditionB) {
        return new AndCondition(conditionA, conditionB);
    }
    public OrCondition createOrCondition(HiddenCondition conditionA, HiddenCondition conditionB) {
        return new OrCondition(conditionA, conditionB);
    }
    public AlwaysTrueCondition createAlwaysTrueCondition() {
        return new AlwaysTrueCondition();
    }
    public ActualPlayerCondition createActualPlayerCondition() {
        return new ActualPlayerCondition();
    }
}
