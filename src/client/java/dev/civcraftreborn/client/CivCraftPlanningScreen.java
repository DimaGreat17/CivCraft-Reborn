package dev.civcraftreborn.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.SlabBlock;
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
    private Button blockHint;

    public CivCraftPlanningScreen() {
        super(Component.literal("CivCraft — градостроительство"));
    }

    @Override public boolean isPauseScreen() { return false; }

    @Override
    protected void init() {
        blockHint=addRenderableWidget(Button.builder(
                Component.literal("Наведите курсор на чертёж: покажу нужный блок"),
                b -> {}).bounds(Math.max(5,width/2-230),height-28,
                        Math.min(460,width-10),20).build());
        blockHint.active=false;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics,int mouseX,int mouseY,float partialTick) {
        // Keep the actual world visible: do not dim, blur or draw a menu panel.
        if(blockHint!=null)blockHint.setMessage(Component.literal(hoveredHint(mouseX,mouseY)));
    }

    /**
     * Approximate vanilla perspective projection of the expected block
     * centres. Only read-only client data is used: never interact with blocks
     * or send server mutations from a hover.
     */
    private String hoveredHint(int mouseX,int mouseY) {
        var plan=CampBlueprintProjection.selected();
        Minecraft client=Minecraft.getInstance();
        if(plan==null||client.level==null)
            return "Нет активного чертежа";
        double yaw=Math.toRadians(CivCraftPlanningCamera.yaw());
        double forwardX=-Math.sin(yaw),forwardZ=Math.cos(yaw);
        double rightX=-Math.cos(yaw),rightZ=-Math.sin(yaw);
        double d=CivCraftPlanningCamera.distance();
        double cameraX=CivCraftPlanningCamera.x()-Math.sin(yaw)*d*0.72;
        double cameraY=CivCraftPlanningCamera.y()+d*0.85;
        double cameraZ=CivCraftPlanningCamera.z()-Math.cos(yaw)*d*0.72;
        double pitch=Math.toRadians(55.0);
        double sinPitch=Math.sin(pitch),cosPitch=Math.cos(pitch);
        double f=height/(2*Math.tan(Math.toRadians(70.0)/2));
        double best=35*35;
        CampBlueprintProjection.Step nearest=null;
        BlockPos nearestPos=null;
        for(var step:CampBlueprintProjection.steps(plan)) {
            if(CampBlueprintProjection.matches(plan,step))continue;
            BlockPos pos=CampBlueprintProjection.worldPosition(plan,step);
            if(pos==null||!client.level.hasChunkAt(pos))continue;
            double x=pos.getX()+0.5-cameraX;
            double y=pos.getY()+0.5-cameraY;
            double z=pos.getZ()+0.5-cameraZ;
            double horizontal=x*forwardX+z*forwardZ;
            double depth=horizontal*cosPitch-y*sinPitch;
            if(depth<=0.5)continue;
            double screenRight=x*rightX+z*rightZ;
            double screenUp=y*cosPitch-horizontal*sinPitch;
            double px=width*0.5+f*screenRight/depth;
            double py=height*0.5-f*screenUp/depth;
            double distanceSquared=(mouseX-px)*(mouseX-px)+(mouseY-py)*(mouseY-py);
            if(distanceSquared<best) {
                best=distanceSquared;
                nearest=step;
                nearestPos=pos;
            }
        }
        if(nearest==null)
            return "ЛКМ: переместить · R: повернуть · PgUp/PgDn: высота";
        var state=CampBlueprintProjection.expectedState(plan,nearest);
        String extra="";
        if(state.hasProperty(StairBlock.FACING))
            extra=" · направление: "+state.getValue(StairBlock.FACING);
        if(state.hasProperty(DoorBlock.HALF))
            extra=" · часть двери: "+state.getValue(DoorBlock.HALF);
        if(state.hasProperty(SlabBlock.TYPE))
            extra=" · плита: "+state.getValue(SlabBlock.TYPE);
        var actual=client.level.getBlockState(nearestPos);
        return (actual.isAir()?"Нужен: ":"Убрать препятствие → ")
                +state.getBlock().getName().getString()+extra
                +" · Y="+nearestPos.getY();
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
        else if(key==InputConstants.KEY_R)rotateBlueprint();
        else if(key==InputConstants.KEY_PAGEUP)moveVertical(1);
        else if(key==InputConstants.KEY_PAGEDOWN)moveVertical(-1);
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

    private void moveVertical(int deltaY) {
        sendPosition();
        dragRemainderX=dragRemainderZ=0;
        if(!CampBlueprintProjection.moveVertical(deltaY))return;
        if(CampBlueprintProjection.selected()==CampBlueprintProjection.Plan.HEART) {
            Minecraft client=Minecraft.getInstance();
            if(client.getConnection()!=null)
                client.getConnection().sendCommand("civcraft camp heart height "
                        +CampBlueprintProjection.offsetY());
        }
    }

    private void rotateBlueprint() {
        if(CampBlueprintProjection.selected()==null)return;
        // Finish any pending drag before rotating, so server commands
        // are applied in their original placement order.
        sendPosition();
        dragRemainderX=dragRemainderZ=0;
        if(!CampBlueprintProjection.rotate(1))return;
        if(CampBlueprintProjection.selected()==CampBlueprintProjection.Plan.HEART) {
            Minecraft client=Minecraft.getInstance();
            if(client.getConnection()!=null)
                client.getConnection().sendCommand("civcraft camp heart rotate "
                        +CampBlueprintProjection.rotationDegrees()/90);
        }
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
