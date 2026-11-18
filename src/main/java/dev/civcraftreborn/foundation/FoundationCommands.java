package dev.civcraftreborn.foundation;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/** Read-only, server-authoritative diagnostics for a world created with the P0 mod. */
public final class FoundationCommands {
    private FoundationCommands() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(Commands.literal("civcraft")
                        .then(Commands.literal("worldinfo").executes(context -> {
                            var source = context.getSource();
                            WorldSettings settings = WorldSettingsSavedData.get(source.getServer()).settings();
                            var border = source.getServer().overworld().getWorldBorder();
                            String message = "CivCraft world: " + settings.preset().sideBlocks() + "x"
                                    + settings.preset().sideBlocks()
                                    + ", mode=" + settings.mode()
                                    + ", schema=" + settings.schemaVersion()
                                    + ", border-center=(" + border.getCenterX() + "," + border.getCenterZ() + ")"
                                    + ", border-size=" + border.getSize()
                                    + ", victory-evaluator=UNAVAILABLE";
                            source.sendSuccess(() -> Component.literal(message), false);
                            return 1;
                        }))));
    }
}
