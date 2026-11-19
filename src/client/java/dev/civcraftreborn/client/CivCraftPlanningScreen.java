package dev.civcraftreborn.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/**
 * World-transparent planning overlay. As a real Minecraft Screen it captures
 * movement/attack/use input instead of allowing detached-camera player control.
 * Drag left = move the unconfirmed blueprint, drag right = pan the camera.
 * The server remains authoritative; only one final move request is sent after
 * a drag, and rejected moves are reconciled by server placement snapshots.
 */
public final class CivCraftPlanningScreen extends Screen {
    private double dragRemainderX,dragRemainderZ;
    private boolean blueprintMoved;

    public CivCraftPlanningScreen() {
        super(Component.literal("CivCraft — градостроительство"));
    }

    @Override public boolean isPauseScreen() { return false; }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics,int mouseX,int mouseY,float partialTick) {
        // Keep the actual world visible: do not dim, blur or draw a menu panel.
    }

    private void exit() {
        CivCraftPlanningCamera.deactivate();
        Minecraft.getInstance().gui.setScreen(null);
    }

    private void sendPosition() {
        if(!blueprintMoved)return;
        blueprintMoved=false;
        if(CampBlueprintProjection.selected()!=CampBlueprintProjection.Plan.HEART)return;
        Minecraft client=Minecraft.getInstance();
        if(client.getConnection()!=null)
            client.getConnection().sendCommand("civcraft camp heart move "
                    +CampBlueprintProjection.offsetX()+" "
                    +CampBlueprintProjection.offsetZ());
    }

    private void shiftBlueprint(double dx,double dz) {
        if(CampBlueprintProjection.selected()==null
                || (CampBlueprintProjection.selected()==CampBlueprintProjection.Plan.HEART
                    && CampBlueprintProjection.heartConfirmed()))return;
        dragRemainderX+=dx;
        dragRemainderZ+=dz;
        int blocksX=dragRemainderX>0?(int)Math.floor(dragRemainderX):(int)Math.ceil(dragRemainderX);
        int blocksZ=dragRemainderZ>0?(int)Math.floor(dragRemainderZ):(int)Math.ceil(dragRemainderZ);
        if(blocksX==0&&blocksZ==0)return;
        if(CampBlueprintProjection.move(blocksX,blocksZ)) {
            dragRemainderX-=blocksX;
            dragRemainderZ-=blocksZ;
            blueprintMoved=true;
        } else {
            dragRemainderX=0;
            dragRemainderZ=0;
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int key=event.key();
        if(key==InputConstants.KEY_ESCAPE||key==InputConstants.KEY_F7) {
            sendPosition();
            exit();
            return true;
        }
        double pan=Math.max(1.0,CivCraftPlanningCamera.distance()/15.0);
        if(key==InputConstants.KEY_UP)CivCraftPlanningCamera.pan(0,pan);
        else if(key==InputConstants.KEY_DOWN)CivCraftPlanningCamera.pan(0,-pan);
        else if(key==InputConstants.KEY_RIGHT)CivCraftPlanningCamera.pan(pan,0);
        else if(key==InputConstants.KEY_LEFT)CivCraftPlanningCamera.pan(-pan,0);
        else if(key==InputConstants.KEY_Z)CivCraftPlanningCamera.rotate(-15f);
        else if(key==InputConstants.KEY_X)CivCraftPlanningCamera.rotate(15f);
        else if(key==InputConstants.KEY_MINUS)CivCraftPlanningCamera.zoom(2);
        else if(key==61)CivCraftPlanningCamera.zoom(-2); // SDL '=' scan code
        else if(key==InputConstants.KEY_G) {
            sendPosition();
            exit();
            Minecraft.getInstance().gui.setScreen(new CivCraftManagementScreen(null));
        }
        // Explicitly swallow W/A/S/D, jump, attack/use hotkeys, etc. while
        // the planner overlay is active. The player stays put.
        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event,boolean doubleClick) {
        // Consume mouse presses in the overlay instead of mining/placing
        // Minecraft blocks with the detached construction camera.
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event,double dragX,double dragY) {
        if(event.button()==1) { // SDL3 left button
            double[] delta=CivCraftPlanningCamera.dragToWorld(dragX,dragY);
            shiftBlueprint(delta[0],delta[1]);
        } else if(event.button()==3) { // SDL3 right button
            double blocksPerPixel=Math.max(0.02,CivCraftPlanningCamera.distance()/580.0);
            CivCraftPlanningCamera.pan(-dragX*blocksPerPixel,dragY*blocksPerPixel);
        }
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if(event.button()==1) {
            sendPosition();
            dragRemainderX=dragRemainderZ=0;
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX,double mouseY,double scrollX,double scrollY) {
        CivCraftPlanningCamera.zoom(-scrollY*2);
        return true;
    }

    @Override
    public void onClose() {
        sendPosition();
        exit();
    }
}
