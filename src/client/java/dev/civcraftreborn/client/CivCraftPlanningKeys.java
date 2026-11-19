package dev.civcraftreborn.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

/**
 * All planning actions use vanilla persistent Minecraft key mappings.
 * The screen reads mapping.matches(event), never hardcoded event codes,
 * so Settings -> Controls -> Key Binds works immediately and after restart.
 */
public final class CivCraftPlanningKeys {
    private static KeyMapping panUp,panDown,panLeft,panRight;
    private static KeyMapping viewLeft,viewRight,blueprintRotate;
    private static KeyMapping raise,lower,zoomIn,zoomOut;
    private static KeyMapping dragBlueprint,dragCamera;
    private CivCraftPlanningKeys(){}

    private static KeyMapping key(String name,int defaultKey,KeyMapping.Category category) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.civcraft_reborn."+name,InputConstants.Type.KEYBOARD,defaultKey,category));
    }

    private static KeyMapping mouse(String name,int defaultButton,KeyMapping.Category category) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.civcraft_reborn."+name,InputConstants.Type.MOUSE,defaultButton,category));
    }

    public static void register(KeyMapping.Category category) {
        panUp=key("planning_north",InputConstants.KEY_UP,category);
        panDown=key("planning_south",InputConstants.KEY_DOWN,category);
        panLeft=key("planning_west",InputConstants.KEY_LEFT,category);
        panRight=key("planning_east",InputConstants.KEY_RIGHT,category);
        viewLeft=key("planning_rotate_left",InputConstants.KEY_Z,category);
        viewRight=key("planning_rotate_right",InputConstants.KEY_X,category);
        blueprintRotate=key("planning_blueprint_rotate",InputConstants.KEY_R,category);
        raise=key("planning_raise",InputConstants.KEY_PAGEUP,category);
        lower=key("planning_lower",InputConstants.KEY_PAGEDOWN,category);
        zoomIn=key("planning_zoom_in",61,category); // SDL equals / '+' position
        zoomOut=key("planning_zoom_out",InputConstants.KEY_MINUS,category);
        dragBlueprint=mouse("planning_drag_blueprint",1,category);
        dragCamera=mouse("planning_drag_camera",3,category);
    }

    public static boolean panUp(KeyEvent e){return panUp.matches(e);}
    public static boolean panDown(KeyEvent e){return panDown.matches(e);}
    public static boolean panLeft(KeyEvent e){return panLeft.matches(e);}
    public static boolean panRight(KeyEvent e){return panRight.matches(e);}
    public static boolean viewLeft(KeyEvent e){return viewLeft.matches(e);}
    public static boolean viewRight(KeyEvent e){return viewRight.matches(e);}
    public static boolean blueprintRotate(KeyEvent e){return blueprintRotate.matches(e);}
    public static boolean raise(KeyEvent e){return raise.matches(e);}
    public static boolean lower(KeyEvent e){return lower.matches(e);}
    public static boolean zoomIn(KeyEvent e){return zoomIn.matches(e);}
    public static boolean zoomOut(KeyEvent e){return zoomOut.matches(e);}
    public static boolean dragBlueprint(MouseButtonEvent e){return dragBlueprint.matchesMouse(e);}
    public static boolean dragCamera(MouseButtonEvent e){return dragCamera.matchesMouse(e);}
    public static String boundRotate(){return blueprintRotate.getTranslatedKeyMessage().getString();}
    public static String boundRaise(){return raise.getTranslatedKeyMessage().getString();}
    public static String boundLower(){return lower.getTranslatedKeyMessage().getString();}
}
