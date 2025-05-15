package com.cmdpro.databank.commands;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.megastructures.Megastructure;
import com.cmdpro.databank.megastructures.MegastructureManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.SetBlockCommand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = Databank.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class DatabankCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal(Databank.MOD_ID)
                .requires(source -> source.hasPermission(4))
                .then(Commands.literal("spawn_megastructure")
                        .then(Commands.argument("megastructure", ResourceLocationArgument.id())
                                .suggests((stack, builder) -> {
                                    return SharedSuggestionProvider.suggest(MegastructureManager.megastructures.keySet().stream().map(ResourceLocation::toString), builder);
                                })
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                    .executes((command) -> {
                                        return spawnMegastructure(command);
                                    })
                                )
                        )
                )
        );
    }
    private static int spawnMegastructure(CommandContext<CommandSourceStack> command) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(command, "pos");
        ResourceLocation id = command.getArgument("megastructure", ResourceLocation.class);
        Megastructure megastructure = MegastructureManager.megastructures.get(id);
        megastructure.placeIntoWorld(command.getSource().getLevel(), pos);
        command.getSource().sendSuccess(() -> {
            return Component.translatable("commands.databank.megastructure", id.toString(), pos.toShortString());
        }, true);
        return Command.SINGLE_SUCCESS;
    }
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }
}
