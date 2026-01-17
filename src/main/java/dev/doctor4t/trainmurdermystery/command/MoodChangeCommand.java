package dev.doctor4t.trainmurdermystery.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import dev.doctor4t.trainmurdermystery.TMM;
import dev.doctor4t.trainmurdermystery.cca.PlayerMoodComponent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.Collection;

public class MoodChangeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("tmm:setMood")
                        .requires(source -> source.hasPermission(2))
                        .then(
                                Commands.argument("mood", FloatArgumentType.floatArg(0.0f, 1.0f))
                                        .executes(context -> execute(context.getSource(), ImmutableList.of(context.getSource().getEntityOrException()), FloatArgumentType.getFloat(context, "mood")))
                                        .then(
                                                Commands.argument("targets", EntityArgument.entities())
                                                        .executes(context -> execute(context.getSource(), EntityArgument.getEntities(context, "targets"), FloatArgumentType.getFloat(context, "mood")))
                                        )
                        )
        );
    }

    private static int execute(CommandSourceStack source, Collection<? extends Entity> targets, float mood) {
        return TMM.executeSupporterCommand(source,
                () -> {
                    for (Entity target : targets) {
                        PlayerMoodComponent moodComponent = PlayerMoodComponent.KEY.get(target);
                        moodComponent.setMood(mood);
                    }
                    
                    if (targets.size() == 1) {
                        Entity target = targets.iterator().next();
                        source.sendSuccess(
                            () -> Component.translatable("commands.tmm.setmood", target.getName().getString(), String.format("%.2f", mood))
                                .withStyle(style -> style.withColor(0x00FF00)),
                            true
                        );
                    } else {
                        source.sendSuccess(
                            () -> Component.translatable("commands.tmm.setmood.multiple", targets.size(), String.format("%.2f", mood))
                                .withStyle(style -> style.withColor(0x00FF00)),
                            true
                        );
                    }
                }
        );
    }
}