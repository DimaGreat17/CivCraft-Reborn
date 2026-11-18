package dev.civcraftreborn.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

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
                b -> { CampBlueprintProjection.move(0,-1); refresh(); })
                .bounds(x+105,y+50,100,20).build());
        Button west = addRenderableWidget(Button.builder(Component.literal("Запад -X"),
                b -> { CampBlueprintProjection.move(-1,0); refresh(); })
                .bounds(x,y+75,100,20).build());
        Button south = addRenderableWidget(Button.builder(Component.literal("Юг +Z"),
                b -> { CampBlueprintProjection.move(0,1); refresh(); })
                .bounds(x+105,y+75,100,20).build());
        Button east = addRenderableWidget(Button.builder(Component.literal("Восток +X"),
                b -> { CampBlueprintProjection.move(1,0); refresh(); })
                .bounds(x+210,y+75,100,20).build());

        Button rotateLeft = addRenderableWidget(Button.builder(Component.literal("↶ 90°"),
                b -> { CampBlueprintProjection.rotate(-1); refresh(); })
                .bounds(x,y+100,153,20).build());
        Button rotateRight = addRenderableWidget(Button.builder(Component.literal("90° ↷"),
                b -> { CampBlueprintProjection.rotate(1); refresh(); })
                .bounds(x+157,y+100,153,20).build());

        boolean movable = plan != CampBlueprintProjection.Plan.HEART;
        for (Button button : new Button[]{north,west,south,east,rotateLeft,rotateRight}) {
            button.active = movable;
        }

        addRenderableWidget(Button.builder(Component.literal("Материалы и остаток"),
                b -> Minecraft.getInstance().gui.setScreen(new CivCraftMaterialsScreen(this)))
                .bounds(x,y+125,310,20).build());

        addRenderableWidget(Button.builder(Component.literal("Показать проекцию в мире"),
                b -> Minecraft.getInstance().gui.setScreen(null))
                .bounds(x,y+150,310,20).build());

        addRenderableWidget(Button.builder(Component.literal("Назад к каталогу"),
                b -> onClose()).bounds(x,y+175,310,20).build());
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
