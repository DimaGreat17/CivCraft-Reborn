package dev.civcraftreborn.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/** Factory for the single CivCraft entry point on Minecraft's World tab. */
public final class CivCraftWorldTabIntegration {
    private CivCraftWorldTabIntegration() {}

    public static Button createButton() {
        return Button.builder(Component.literal("Мир CivCraft..."), button -> {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.gui.setScreen(new CivCraftWorldOptionsScreen(minecraft.gui.screen()));
        }).size(210, 20).build();
    }
}
