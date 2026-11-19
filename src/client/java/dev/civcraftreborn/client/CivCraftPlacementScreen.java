package dev.civcraftreborn.client;

import dev.civcraftreborn.settlement.CampBlueprintCatalog;
import dev.civcraftreborn.settlement.FunctionalBuildingTemplates;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Building details and server-validated blueprint variant selection.
 * Placement, rotation and vertical shift happen exclusively in the F7 camera.
 */
public final class CivCraftPlacementScreen extends Screen {
    private final Screen parent;
    private Button variantButton;

    public CivCraftPlacementScreen(Screen parent) {
        super(Component.literal("CivCraft — проект строительства"));
        this.parent=parent;
    }

    @Override protected void init() {
        int x=width/2-155;
        int y=Math.max(22,height/2-90);
        var plan=CampBlueprintProjection.selected();
        if(plan==null || !CampBlueprintProjection.unlocked(plan)) {
            Minecraft.getInstance().gui.setScreen(parent);
            return;
        }
        addRenderableWidget(Button.builder(Component.literal(plan.title()),
                b -> {}).bounds(x,y,310,20).build()).active=false;
        addRenderableWidget(Button.builder(Component.literal(
                "Перемещение и поворот — только в камере F7"),
                b -> {}).bounds(x,y+25,310,20).build()).active=false;

        int next=y+50;
        if(plan==CampBlueprintProjection.Plan.HEART) {
            List<String> versions=CampBlueprintCatalog.availableBundled();
            variantButton=addRenderableWidget(Button.builder(
                    Component.literal("Вариант: "+CampBlueprintProjection.heartBlueprintId()),
                    b -> {
                        if(CampBlueprintProjection.heartConfirmed())return;
                        int current=versions.indexOf(CampBlueprintProjection.heartBlueprintId());
                        String selected=versions.get((current+1+versions.size())%versions.size());
                        var client=Minecraft.getInstance();
                        if(client.getConnection()!=null)
                            client.getConnection().sendCommand(
                                    "civcraft camp heart variant "+selected);
                    }).bounds(x,next,310,20).build());
            variantButton.active=!CampBlueprintProjection.heartConfirmed()
                    && versions.size()>1;
            next+=25;
        }
        final int cameraY=next;
        addRenderableWidget(Button.builder(Component.literal("Камера градостроительства (F7)"),
                b -> CivCraftPlanningCamera.activate(Minecraft.getInstance()))
                .bounds(x,cameraY,310,20).build());
        next+=25;
        addRenderableWidget(Button.builder(Component.literal("Материалы и остаток"),
                b -> Minecraft.getInstance().gui.setScreen(new CivCraftMaterialsScreen(this)))
                .bounds(x,next,310,20).build());
        next+=25;

        if(plan==CampBlueprintProjection.Plan.HEART) {
            Button confirm=addRenderableWidget(Button.builder(
                    Component.literal(CampBlueprintProjection.heartConfirmed()
                            ? "Площадка подтверждена" : "Подтвердить площадку лагеря"),
                    b -> sendCommand("civcraft camp heart confirm"))
                    .bounds(x,next,310,20).build());
            confirm.active=!CampBlueprintProjection.heartConfirmed();
        } else {
            boolean functional;
            try {FunctionalBuildingTemplates.Type.valueOf(plan.name());functional=true;}
            catch(IllegalArgumentException e) {functional=false;}
            Button order=addRenderableWidget(Button.builder(
                    Component.literal("Поручить строительство NPC"),
                    b -> sendBuildingCommand(false)).bounds(x,next,310,20).build());
            next+=25;
            Button register=addRenderableWidget(Button.builder(
                    Component.literal("Принять готовое здание"),
                    b -> sendBuildingCommand(true)).bounds(x,next,310,20).build());
            order.active=functional;
            register.active=functional;
        }
        addRenderableWidget(Button.builder(Component.literal("Назад к каталогу"),
                b -> onClose()).bounds(x,next+35,310,20).build());
    }

    @Override public void tick() {
        super.tick();
        if(variantButton!=null) {
            variantButton.setMessage(Component.literal(
                    "Вариант: "+CampBlueprintProjection.heartBlueprintId()));
            variantButton.active=!CampBlueprintProjection.heartConfirmed()
                    && CampBlueprintCatalog.availableBundled().size()>1;
        }
    }

    private void sendCommand(String command) {
        Minecraft client=Minecraft.getInstance();
        if(client.getConnection()!=null)client.getConnection().sendCommand(command);
        client.gui.setScreen(null);
    }

    private void sendBuildingCommand(boolean register) {
        var plan=CampBlueprintProjection.selected();
        var origin=CampBlueprintProjection.placedOrigin();
        if(plan==null||origin==null)return;
        int turns=CampBlueprintProjection.rotationDegrees()/90;
        String command="civcraft camp "+
                (register?"building register":"construction order")+
                " "+plan.name()+" "+turns+" "+origin.getX()+
                " "+origin.getY()+" "+origin.getZ();
        sendCommand(command);
    }

    @Override public void onClose() {
        Minecraft.getInstance().gui.setScreen(parent);
    }
}
