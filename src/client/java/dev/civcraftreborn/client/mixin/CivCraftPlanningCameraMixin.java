package dev.civcraftreborn.client.mixin;

import dev.civcraftreborn.client.CivCraftPlanningCamera;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Overrides only the local render camera after vanilla entity alignment.
 * No player/entity position, reach or server data are changed.
 * The original camera returns on the next vanilla frame after F7 toggle-off.
 */
@Mixin(Camera.class)
public abstract class CivCraftPlanningCameraMixin {
    @Shadow protected abstract void setPosition(double x,double y,double z);
    @Shadow protected abstract void setRotation(float yRot,float xRot);

    @Inject(method="alignWithEntity",at=@At("TAIL"))
    private void civcraft$obliquePlanningView(float tickProgress,CallbackInfo ci){
        if(!CivCraftPlanningCamera.active())return;
        float yaw=CivCraftPlanningCamera.yaw();
        double radians=Math.toRadians(yaw);
        double horizontal=CivCraftPlanningCamera.distance()*0.72;
        double dx=-Math.sin(radians)*horizontal;
        double dz=Math.cos(radians)*horizontal;
        setPosition(CivCraftPlanningCamera.x()-dx,
                CivCraftPlanningCamera.y()+CivCraftPlanningCamera.distance()*0.85,
                CivCraftPlanningCamera.z()-dz);
        setRotation(yaw,55.0f);
    }
}
