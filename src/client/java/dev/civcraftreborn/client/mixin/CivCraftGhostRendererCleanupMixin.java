package dev.civcraftreborn.client.mixin;

import dev.civcraftreborn.client.CivCraftGhostBlockRenderer;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Release transient GPU allocations when Minecraft's game renderer closes. */
@Mixin(GameRenderer.class)
public abstract class CivCraftGhostRendererCleanupMixin {
    @Inject(method = "close", at = @At("RETURN"))
    private void civcraft$closeGhostBuffer(CallbackInfo callback) {
        CivCraftGhostBlockRenderer.close();
    }
}
