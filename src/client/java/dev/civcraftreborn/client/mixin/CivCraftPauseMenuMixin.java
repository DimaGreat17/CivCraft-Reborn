package dev.civcraftreborn.client.mixin;

import dev.civcraftreborn.client.CivCraftManagementScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Dedicated clickable CivCraft menu entry, independent of the physical core.
 * Available via Esc as well as the configurable G key.
 */
@Mixin(PauseScreen.class)
public abstract class CivCraftPauseMenuMixin extends Screen {
    protected CivCraftPauseMenuMixin() {
        super(Component.literal("CivCraft"));
    }

    @Inject(method="init",at=@At("TAIL"))
    private void civcraft$menuButton(CallbackInfo ci) {
        addRenderableWidget(Button.builder(Component.literal("CivCraft"),
                b->Minecraft.getInstance().gui.setScreen(
                        new CivCraftManagementScreen((Screen)(Object)this)))
                .bounds(Math.max(4,width-110),Math.max(4,height-28),104,20).build());
    }
}
