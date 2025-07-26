package com.cmdpro.databank.commands;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.DatabankUtils;
import com.cmdpro.databank.megastructures.Megastructure;
import com.cmdpro.databank.megastructures.MegastructureManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.SetBlockCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.List;

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
                .then(Commands.literal("recheck_advancements")
                        .then(Commands.argument("target", EntityArgument.players())
                                .executes((command) -> {
                                    return recheckAdvancements(command);
                                })
                        )
                )
        );
    }
    private static int recheckAdvancements(CommandContext<CommandSourceStack> command) throws CommandSyntaxException {
        List<ServerPlayer> players = command.getArgument("target", EntitySelector.class).findPlayers(command.getSource());
        for (ServerPlayer i : players) {
            DatabankUtils.recheckAdvancements(i);
        }
        command.getSource().sendSuccess(() -> {
            return Component.translatable(players.size() == 1 ? "commands.databank.recheck_advancements" : "commands.databank.recheck_advancements.plural", players.size());
        }, true);
        return Command.SINGLE_SUCCESS;
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
