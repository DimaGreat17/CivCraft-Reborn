package dev.civcraftreborn.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import dev.civcraftreborn.settlement.FunctionalBuildingTemplates;

/**
 * Lightweight blueprint details only. All blueprint movement and rotation
 * is exclusively performed in the oblique F7 planning camera.
 */
public final class CivCraftPlacementScreen extends Screen {
    private final Screen parent;

    public CivCraftPlacementScreen(Screen parent) {
        super(Component.literal("CivCraft — проект строительства"));
        this.parent=parent;
    }

    @Override
    protected void init() {
        int x=width/2-155;
        int y=Math.max(22,height/2-78);
        var plan=CampBlueprintProjection.selected();
        if(plan==null || !CampBlueprintProjection.unlocked(plan)) {
            Minecraft.getInstance().gui.setScreen(parent);
            return;
        }

        addRenderableWidget(Button.builder(Component.literal(plan.title()),
                b -> {}).bounds(x,y,310,20).build()).active=false;
        addRenderableWidget(Button.builder(Component.literal(
                "Перенос и поворот — только в камере F7"),
                b -> {}).bounds(x,y+25,310,20).build()).active=false;

        addRenderableWidget(Button.builder(Component.literal("Открыть камеру градостроительства"),
                b -> CivCraftPlanningCamera.activate(Minecraft.getInstance()))
                .bounds(x,y+50,310,20).build());

        addRenderableWidget(Button.builder(Component.literal("Материалы и остаток"),
                b -> Minecraft.getInstance().gui.setScreen(new CivCraftMaterialsScreen(this)))
                .bounds(x,y+75,310,20).build());

        if(plan==CampBlueprintProjection.Plan.HEART) {
            Button confirm=addRenderableWidget(Button.builder(
                    Component.literal(CampBlueprintProjection.heartConfirmed()
                            ? "Площадка подтверждена"
                            : "Подтвердить площадку лагеря"),
                    b -> sendCommand("civcraft camp heart confirm"))
                    .bounds(x,y+100,310,20).build());
            confirm.active=!CampBlueprintProjection.heartConfirmed();
        } else {
            boolean functional;
            try {FunctionalBuildingTemplates.Type.valueOf(plan.name());functional=true;}
            catch(IllegalArgumentException e) {functional=false;}
            Button order=addRenderableWidget(Button.builder(
                    Component.literal("Поручить строительство NPC"),
                    b -> sendBuildingCommand(false)).bounds(x,y+100,310,20).build());
            Button register=addRenderableWidget(Button.builder(
                    Component.literal("Принять готовое здание"),
                    b -> sendBuildingCommand(true)).bounds(x,y+125,310,20).build());
            order.active=functional;
            register.active=functional;
        }

        addRenderableWidget(Button.builder(Component.literal("Назад к каталогу"),
                b -> onClose()).bounds(x,y+155,310,20).build());
    }

    private void sendCommand(String command) {
        Minecraft client=Minecraft.getInstance();
        if(client.getConnection()!=null)
            client.getConnection().sendCommand(command);
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

    @Override
    public void onClose(){
        Minecraft.getInstance().gui.setScreen(parent);
    }
}
