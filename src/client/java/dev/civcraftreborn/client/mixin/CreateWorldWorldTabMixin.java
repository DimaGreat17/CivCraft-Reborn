package dev.civcraftreborn.client.mixin;

import dev.civcraftreborn.client.CivCraftWorldTabIntegration;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Append CivCraft entry to the existing World tab layout. TabManager owns the
 * widget lifecycle, preventing the duplicated floating buttons previously
 * registered on ScreenEvents.AFTER_INIT.
 */
@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$WorldTab")
public abstract class CreateWorldWorldTabMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void civcraft$appendWorldOptions(CallbackInfo ci) {
        ((GridLayoutTab) (Object) this).getLayout()
                .addChild(CivCraftWorldTabIntegration.createButton(), 12, 0, 1, 2);
    }
}
