package xyz.eclipseisoffline.customtimecycle;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.Component;
import xyz.eclipseisoffline.customtimecycle.TimeManager.DayPartTimeRate;

public class CustomTimeCycle implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                Commands.literal("timecycle")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("status")
                                .executes(context -> {
                                    TimeManager timeManager = TimeManager.getInstance(context.getSource().getLevel());
                                    if (timeManager.isNormalTimeRate()) {
                                        context.getSource().sendSuccess(() -> Component.literal("Using normal time durations"), false);
                                        return 0;
                                    }

                                    boolean usingDayCycle = timeManager.isDay();
                                    DayPartTimeRate timeRate = timeManager.getTimeRate();

                                    context.getSource().sendSuccess(() -> Component.literal("Using " + (usingDayCycle ? "day time tick rate" : "night time tick rate") + " (duration=" + timeRate.getDuration() + ")"), false);
                                    context.getSource().sendSuccess(() -> Component.literal("Incrementing " + timeRate.getIncrement() + " time ticks every " + timeRate.getIncrementModulus() + " server ticks"), false);
                                    return 0;
                                })
                        )
                        .then(Commands.literal("set")
                                .then(Commands.argument("dayduration", TimeArgument.time(1))
                                        .then(Commands.argument("nightduration", TimeArgument.time(1))
                                                .executes(context -> {
                                                    TimeManager timeManager = TimeManager.getInstance(context.getSource().getLevel());

                                                    long dayRate = IntegerArgumentType.getInteger(context, "dayduration");
                                                    long nightRate = IntegerArgumentType.getInteger(context, "nightduration");
                                                    timeManager.setTimeRate(dayRate, nightRate);
                                                    context.getSource().sendSuccess(() -> Component.literal("Set day duration to " + dayRate + " server ticks and night duration to " + nightRate + " server ticks"), true);
                                                    return 0;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("reset")
                                .executes(context -> {
                                    TimeManager.getInstance(context.getSource().getLevel()).resetTimeRate();
                                    context.getSource().sendSuccess(() -> Component.literal("Reset day and night durations"), true);
                                    return 0;
                                })
                        )
        ));
    }
}
