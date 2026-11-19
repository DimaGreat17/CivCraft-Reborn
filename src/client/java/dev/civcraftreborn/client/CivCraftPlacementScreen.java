package dev.civcraftreborn.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import dev.civcraftreborn.settlement.FunctionalBuildingTemplates;

/**
 * Move/rotate a ghost plan without changing physical Minecraft blocks.
 * Shift is bounded inside the camp radius; heart stays attached to its core.
 */
public final class CivCraftPlacementScreen extends Screen {
    private final Screen parent;
    private Button positionInfo;

    public CivCraftPlacementScreen(Screen parent) {
        super(Component.literal("CivCraft — размещение схемы"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = width/2-155;
        int y = Math.max(22, height/2-110);
        CampBlueprintProjection.Plan plan = CampBlueprintProjection.selected();
        if (plan == null || !CampBlueprintProjection.unlocked(plan)) {
            Minecraft.getInstance().gui.setScreen(parent);
            return;
        }

        addRenderableWidget(Button.builder(Component.literal(plan.title()),
                b -> {}).bounds(x,y,310,20).build()).active=false;

        positionInfo = addRenderableWidget(Button.builder(Component.literal(positionLabel()),
                b -> {}).bounds(x,y+25,310,20).build());
        positionInfo.active=false;

        Button north = addRenderableWidget(Button.builder(Component.literal("Север -Z"),
                b -> shift(0,-1))
                .bounds(x+105,y+50,100,20).build());
        Button west = addRenderableWidget(Button.builder(Component.literal("Запад -X"),
                b -> shift(-1,0))
                .bounds(x,y+75,100,20).build());
        Button south = addRenderableWidget(Button.builder(Component.literal("Юг +Z"),
                b -> shift(0,1))
                .bounds(x+105,y+75,100,20).build());
        Button east = addRenderableWidget(Button.builder(Component.literal("Восток +X"),
                b -> shift(1,0))
                .bounds(x+210,y+75,100,20).build());

        Button rotateLeft = addRenderableWidget(Button.builder(Component.literal("↶ 90°"),
                b -> turn(-1))
                .bounds(x,y+100,153,20).build());
        Button rotateRight = addRenderableWidget(Button.builder(Component.literal("90° ↷"),
                b -> turn(1))
                .bounds(x+157,y+100,153,20).build());

        boolean movable = plan != CampBlueprintProjection.Plan.HEART
                || !CampBlueprintProjection.heartConfirmed();
        for (Button button : new Button[]{north,west,south,east,rotateLeft,rotateRight}) {
            button.active = movable;
        }

        addRenderableWidget(Button.builder(Component.literal("Материалы и остаток"),
                b -> Minecraft.getInstance().gui.setScreen(new CivCraftMaterialsScreen(this)))
                .bounds(x,y+125,310,20).build());

        addRenderableWidget(Button.builder(Component.literal("Показать проекцию в мире"),
                b -> Minecraft.getInstance().gui.setScreen(null))
                .bounds(x,y+150,310,20).build());
        if(plan == CampBlueprintProjection.Plan.HEART) {
            Button confirm = addRenderableWidget(Button.builder(
                    Component.literal(CampBlueprintProjection.heartConfirmed()
                            ? "Площадка подтверждена"
                            : "Подтвердить площадку лагеря"),
                    b -> heartCommand("civcraft camp heart confirm"))
                    .bounds(x,y+176,310,20).build());
            confirm.active = !CampBlueprintProjection.heartConfirmed();
        }

        Button order = addRenderableWidget(Button.builder(
                Component.literal("Поручить строительство NPC"),
                b -> sendBuildingCommand(false))
                .bounds(x,y+176,310,20).build());
        Button register = addRenderableWidget(Button.builder(
                Component.literal("Принять готовое здание"),
                b -> sendBuildingCommand(true))
                .bounds(x,y+201,310,20).build());

        boolean functional = plan != CampBlueprintProjection.Plan.HEART;
        try { FunctionalBuildingTemplates.Type.valueOf(plan.name()); }
        catch(IllegalArgumentException e) { functional=false; }
        order.active=functional;
        register.active=functional;

        addRenderableWidget(Button.builder(Component.literal("Назад к каталогу"),
                b -> onClose()).bounds(x,y+226,310,20).build());
    }

    private void heartCommand(String command) {
        Minecraft game=Minecraft.getInstance();
        if(game.getConnection()!=null)game.getConnection().sendCommand(command);
    }

    private void shift(int x,int z) {
        if(CampBlueprintProjection.move(x,z)) {
            if(CampBlueprintProjection.selected()==CampBlueprintProjection.Plan.HEART) {
                heartCommand("civcraft camp heart move "+
                        CampBlueprintProjection.offsetX()+" "+
                        CampBlueprintProjection.offsetZ());
            }
            refresh();
        }
    }

    private void turn(int turns) {
        if(CampBlueprintProjection.rotate(turns)) {
            if(CampBlueprintProjection.selected()==CampBlueprintProjection.Plan.HEART)
                heartCommand("civcraft camp heart rotate "+
                        CampBlueprintProjection.rotationDegrees()/90);
            refresh();
        }
    }

    @Override
    public void tick() {
        super.tick();
        refresh();
    }

    private void sendBuildingCommand(boolean register) {
        CampBlueprintProjection.Plan plan=CampBlueprintProjection.selected();
        var origin=CampBlueprintProjection.placedOrigin();
        Minecraft game=Minecraft.getInstance();
        if(plan==null||origin==null||game.getConnection()==null)return;
        int quarterTurns=CampBlueprintProjection.rotationDegrees()/90;
        String action=register?"building register":"construction order";
        game.getConnection().sendCommand("civcraft camp "+action+" "+
                plan.name()+" "+quarterTurns+" "+origin.getX()+" "+
                origin.getY()+" "+origin.getZ());
        game.gui.setScreen(null); // Let owner read real server result.
    }

    private String positionLabel() {
        return "Сдвиг: X="+CampBlueprintProjection.offsetX()
                +" Z="+CampBlueprintProjection.offsetZ()
                +" | Поворот: "+CampBlueprintProjection.rotationDegrees()+"°";
    }

    private void refresh() {
        if (positionInfo != null)
            positionInfo.setMessage(Component.literal(positionLabel()));
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().gui.setScreen(parent);
    }
}
