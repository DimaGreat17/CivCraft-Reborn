package dev.civcraftreborn.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

/**
 * Client-only city planning camera: a raised oblique view over the current
 * construction site. It neither teleports the player nor alters server rules.
 *
 * F7 toggles, arrow keys pan, plus/minus zoom, Z/X turn the view.
 * Minecraft's normal camera is restored automatically when disabled.
 */
public final class CivCraftPlanningCamera {
    private static boolean active;
    private static String world;
    private static double targetX, targetY, targetZ;
    private static double distance=28;
    private static int quarterTurns;
    private static KeyMapping toggle, north, south, east, west, zoomIn, zoomOut, turnLeft, turnRight;

    private CivCraftPlanningCamera(){}

    private static KeyMapping key(String id,int code,KeyMapping.Category category){
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.civcraft_reborn."+id,InputConstants.Type.KEYBOARD,code,category));
    }

    public static void register(KeyMapping.Category category){
        toggle=key("planning_camera",InputConstants.KEY_F7,category);
        north=key("planning_north",InputConstants.KEY_UP,category);
        south=key("planning_south",InputConstants.KEY_DOWN,category);
        west=key("planning_west",InputConstants.KEY_LEFT,category);
        east=key("planning_east",InputConstants.KEY_RIGHT,category);
        zoomIn=key("planning_zoom_in",org.lwjgl.glfw.GLFW.GLFW_KEY_EQUAL,category);
        zoomOut=key("planning_zoom_out",InputConstants.KEY_MINUS,category);
        turnLeft=key("planning_rotate_left",InputConstants.KEY_Z,category);
        turnRight=key("planning_rotate_right",InputConstants.KEY_X,category);
        ClientTickEvents.END_CLIENT_TICK.register(CivCraftPlanningCamera::tick);
    }

    public static boolean active(){return active;}

    public static boolean activate(Minecraft client){
        if(client.level==null||client.player==null)return false;
        if(CampBlueprintProjection.selected()==null||
                !CampBlueprintProjection.unlocked(CampBlueprintProjection.selected()))
            return false;
        BlockPos origin=CampBlueprintProjection.placedOrigin();
        if(origin==null)return false;
        targetX=origin.getX()+3.5;
        targetY=origin.getY()+2;
        targetZ=origin.getZ()+3.5;
        quarterTurns=0;
        distance=28;
        world=client.level.dimension().identifier().toString();
        active=true;
        return true;
    }

    public static void deactivate(){active=false;world=null;}

    public static double x(){return targetX;}
    public static double y(){return targetY;}
    public static double z(){return targetZ;}
    public static double distance(){return distance;}
    public static int quarterTurns(){return quarterTurns;}

    private static void tick(Minecraft client){
        if(client.player==null||client.level==null){
            deactivate();return;
        }
        if(active&&!client.level.dimension().identifier().toString().equals(world)){
            deactivate();return;
        }
        while(toggle.consumeClick()){
            if(active)deactivate();
            else if(client.gui.screen()==null)activate(client);
        }
        if(!active||client.gui.screen()!=null)return;
        double speed=Math.max(0.5,distance/32.0);
        if(north.isDown())targetZ-=speed;
        if(south.isDown())targetZ+=speed;
        if(west.isDown())targetX-=speed;
        if(east.isDown())targetX+=speed;
        if(zoomIn.isDown())distance=Math.max(10,distance-0.6);
        if(zoomOut.isDown())distance=Math.min(80,distance+0.6);
        while(turnLeft.consumeClick())quarterTurns=Math.floorMod(quarterTurns-1,4);
        while(turnRight.consumeClick())quarterTurns=Math.floorMod(quarterTurns+1,4);
        // Do not let the camera drift arbitrarily far beyond the area loaded
        // by the player; no remote chunk loading is ever initiated here.
        var p=client.player.blockPosition();
        targetX=Math.max(p.getX()-110,Math.min(p.getX()+110,targetX));
        targetZ=Math.max(p.getZ()-110,Math.min(p.getZ()+110,targetZ));
    }
}
