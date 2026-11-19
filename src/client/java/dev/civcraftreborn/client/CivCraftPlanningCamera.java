package dev.civcraftreborn.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

/**
 * Client-only oblique view over the current blueprint.
 * A transparent Screen owns gameplay input while active, so planning never
 * moves the actual player or sends interaction actions to the server.
 */
public final class CivCraftPlanningCamera {
    private static boolean active;
    private static String world;
    private static double targetX, targetY, targetZ;
    private static double distance = 28;
    private static float yaw, wantedYaw;
    private static KeyMapping toggle;

    private CivCraftPlanningCamera() {}

    public static void register(KeyMapping.Category category) {
        toggle = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.civcraft_reborn.planning_camera", InputConstants.Type.KEYBOARD,
                InputConstants.KEY_F7,category));
        CivCraftPlanningKeys.register(category);
        ClientTickEvents.END_CLIENT_TICK.register(CivCraftPlanningCamera::tick);
    }

    public static boolean active() { return active; }
    public static boolean matchesToggle(net.minecraft.client.input.KeyEvent event) {
        return toggle.matches(event);
    }
    public static double x() { return targetX; }
    public static double y() { return targetY; }
    public static double z() { return targetZ; }
    public static double distance() { return distance; }
    public static float yaw() { return yaw; }

    public static boolean activate(Minecraft client) {
        if(client.player == null || client.level == null
                || CampBlueprintProjection.selected() == null
                || !CampBlueprintProjection.unlocked(CampBlueprintProjection.selected()))
            return false;
        BlockPos origin=CampBlueprintProjection.placedOrigin();
        if(origin==null)return false;
        if(CampBlueprintProjection.selected()==CampBlueprintProjection.Plan.HEART) {
            var def=dev.civcraftreborn.settlement.CampHeartTemplate.definition(
                    CampBlueprintProjection.heartBlueprintId());
            targetX=origin.getX()+def.width()/2.0;
            targetY=origin.getY()+def.height()/2.0;
            targetZ=origin.getZ()+def.depth()/2.0;
        } else {
            targetX=origin.getX()+3.5;
            targetY=origin.getY()+2;
            targetZ=origin.getZ()+3.5;
        }
        distance=28;
        yaw=0;
        wantedYaw=0;
        world=client.level.dimension().identifier().toString();
        active=true;
        client.gui.setScreen(new CivCraftPlanningScreen());
        return true;
    }

    public static void deactivate() {
        active=false;
        world=null;
    }

    /** Rotate by small steps and ease toward the target in the render camera. */
    public static void rotate(float delta) {
        wantedYaw+=delta;
    }

    public static void zoom(double delta) {
        distance=Math.max(10,Math.min(80,distance+delta));
    }

    /** Screen up means away into the picture, screen right means visually right. */
    public static void pan(double screenRight,double screenUp) {
        double radians=Math.toRadians(yaw);
        double forwardX=-Math.sin(radians), forwardZ=Math.cos(radians);
        double rightX=-Math.cos(radians), rightZ=-Math.sin(radians);
        targetX += forwardX*screenUp + rightX*screenRight;
        targetZ += forwardZ*screenUp + rightZ*screenRight;
        Minecraft client=Minecraft.getInstance();
        if(client.player!=null) {
            BlockPos player=client.player.blockPosition();
            targetX=Math.max(player.getX()-110,Math.min(player.getX()+110,targetX));
            targetZ=Math.max(player.getZ()-110,Math.min(player.getZ()+110,targetZ));
        }
    }

    /** Screen-pixel delta projected onto the ground plane; no actual blocks are moved. */
    public static double[] dragToWorld(double pixelsRight,double pixelsDown) {
        double blocksPerPixel=Math.max(0.02,distance/580.0);
        double radians=Math.toRadians(yaw);
        double forwardX=-Math.sin(radians), forwardZ=Math.cos(radians);
        double rightX=-Math.cos(radians), rightZ=-Math.sin(radians);
        double right=pixelsRight*blocksPerPixel;
        double up=-pixelsDown*blocksPerPixel;
        return new double[]{rightX*right+forwardX*up,rightZ*right+forwardZ*up};
    }

    private static void tick(Minecraft client) {
        if(client.level==null||client.player==null) {
            if(active)deactivate();
            return;
        }
        if(active&&!client.level.dimension().identifier().toString().equals(world)) {
            deactivate();
            if(client.gui.screen() instanceof CivCraftPlanningScreen)
                client.gui.setScreen(null);
            return;
        }
        if(!active) {
            while(toggle.consumeClick()) {
                if(client.gui.screen()==null)activate(client);
            }
            return;
        }
        // If another screen replaces our overlay, restore vanilla camera
        // instead of silently enabling movement while view is detached.
        if(!(client.gui.screen() instanceof CivCraftPlanningScreen)) {
            deactivate();
            return;
        }
        float delta=(wantedYaw-yaw)%360f;
        if(delta>180f)delta-=360f;
        if(delta< -180f)delta+=360f;
        if(Math.abs(delta)>0.02f)yaw+=delta*0.24f;
        else yaw=wantedYaw;
    }
}
