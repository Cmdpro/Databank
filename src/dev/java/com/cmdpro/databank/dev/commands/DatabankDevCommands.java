package com.cmdpro.databank.dev.commands;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.DatabankUtils;
import com.cmdpro.databank.dev.DatabankDev;
import com.cmdpro.databank.dialogue.DialogueTree;
import com.cmdpro.databank.dialogue.DialogueTreeManager;
import com.cmdpro.databank.instanceddimension.InstancedDimension;
import com.cmdpro.databank.instanceddimension.InstancedDimensionManager;
import com.cmdpro.databank.megastructures.Megastructure;
import com.cmdpro.databank.megastructures.MegastructureManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.List;

@EventBusSubscriber(modid = DatabankDev.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class DatabankDevCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal(DatabankDev.MOD_ID)
                .requires(source -> source.hasPermission(4))
                .then(Commands.literal("dimension")
                        .then(Commands.argument("value", StringArgumentType.string())
                                .executes((command) -> {
                                    return runDimension(command);
                                })
                        )
                )
        );
    }
    private static int runDimension(CommandContext<CommandSourceStack> command) throws CommandSyntaxException {
        String value = command.getArgument("value", String.class);
        ServerLevel dimension = null;
        if (value.equals("1")) {
            InstancedDimension.Instance instance = InstancedDimensionManager.instanceddimensions.get(DatabankDev.locate("saved_testing")).create();
            dimension = instance.getOrCreateDimension(command.getSource().getServer());
        }
        if (value.equals("2")) {
            InstancedDimension.Instance instance = InstancedDimensionManager.instanceddimensions.get(DatabankDev.locate("unsaved_testing")).create();
            dimension = instance.getOrCreateDimension(command.getSource().getServer());
        }

        if (value.equals("3")) {
            InstancedDimension.Instance instance = InstancedDimensionManager.instanceddimensions.get(DatabankDev.locate("unsaved_delete_testing")).create();
            dimension = instance.getOrCreateDimension(command.getSource().getServer());
        }
        ServerPlayer player = command.getSource().getPlayer();
        Vec3 pos = player.position();
        Vec2 look = player.getRotationVector();
        player.teleportTo(dimension, pos.x, pos.y, pos.z, look.x, look.y);
        command.getSource().sendSuccess(Component::empty, true);
        return Command.SINGLE_SUCCESS;
    }
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }
}
