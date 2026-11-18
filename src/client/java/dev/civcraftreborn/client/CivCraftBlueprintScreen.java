package dev.civcraftreborn.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * The blueprint screen enables real, world-space 3D preview. Building is always
 * manual: missing blocks are indicated by cyan hologram points; completed
 * blocks turn green / disappear once a plan is finished. It never places items.
 */
public final class CivCraftBlueprintScreen extends Screen {
    private final Screen parent;
    private final Button[] planButtons = new Button[CampBlueprintProjection.Plan.values().length];

    public CivCraftBlueprintScreen(Screen parent) {
        super(Component.literal("CivCraft — 3D-схема построек"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = width / 2 - 155;
        int y = Math.max(28, height / 2 - 112);

        int index = 0;
        for (CampBlueprintProjection.Plan plan : CampBlueprintProjection.Plan.values()) {
            final int row = index;
            planButtons[row] = addRenderableWidget(Button.builder(
                    Component.literal(label(plan)),
                    b -> {
                        CampBlueprintProjection.show(plan);
                        Minecraft.getInstance().gui.setScreen(null);
                    }).bounds(x, y + row * 24, 310, 20).build());
            index++;
        }

        addRenderableWidget(Button.builder(
                Component.literal("Скрыть 3D-проекцию"),
                b -> {
                    CampBlueprintProjection.hide();
                    Minecraft.getInstance().gui.setScreen(null);
                }).bounds(x, y + 125, 310, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Сверить с сервером"),
                b -> {
                    Minecraft minecraft = Minecraft.getInstance();
                    if (minecraft.getConnection() != null) {
                        minecraft.getConnection().sendCommand("civcraft camp plan");
                        minecraft.gui.setScreen(null);
                    }
                }).bounds(x, y + 149, 310, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Назад к управлению"),
                b -> onClose()).bounds(x, y + 173, 310, 20).build());
    }

    private String label(CampBlueprintProjection.Plan plan) {
        int done = CampBlueprintProjection.completed(plan);
        int total = CampBlueprintProjection.steps(plan).size();
        return plan.title() + ": " + done + " / " + total + " — показать в мире";
    }

    @Override
    public void tick() {
        super.tick();
        int i = 0;
        for (CampBlueprintProjection.Plan plan : CampBlueprintProjection.Plan.values()) {
            if (planButtons[i] != null) {
                planButtons[i].setMessage(Component.literal(label(plan)));
            }
            i++;
        }
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().gui.setScreen(parent);
    }
}
