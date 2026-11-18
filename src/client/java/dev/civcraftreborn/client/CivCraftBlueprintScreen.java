package dev.civcraftreborn.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Paged catalog of optional physical starter buildings. Each item shows real
 * missing/placed blocks using the native translucent world-space renderer.
 */
public final class CivCraftBlueprintScreen extends Screen {
    private static final int PER_PAGE = 4;
    private final Screen parent;
    private int page;
    private final List<PlanButton> visiblePlanButtons = new ArrayList<>();

    private record PlanButton(CampBlueprintProjection.Plan plan, Button button) {}

    public CivCraftBlueprintScreen(Screen parent) {
        super(Component.literal("CivCraft — каталог схем построек"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        visiblePlanButtons.clear();
        int x = width / 2 - 155;
        int y = Math.max(25, height / 2 - 102);
        CampBlueprintProjection.Plan[] plans = CampBlueprintProjection.Plan.values();
        int pageCount = (plans.length + PER_PAGE - 1) / PER_PAGE;

        for (int i = page * PER_PAGE; i < Math.min((page+1)*PER_PAGE, plans.length); i++) {
            CampBlueprintProjection.Plan plan = plans[i];
            int row = i % PER_PAGE;
            Button button = addRenderableWidget(Button.builder(
                    Component.literal(label(plan)),
                    b -> {
                        CampBlueprintProjection.show(plan);
                        Minecraft.getInstance().gui.setScreen(null);
                    }).bounds(x, y + row * 24, 310, 20).build());
            visiblePlanButtons.add(new PlanButton(plan, button));
        }

        Button previous = addRenderableWidget(Button.builder(Component.literal("◀ Назад"),
                b -> { page--; rebuildWidgets(); })
                .bounds(x, y+103, 95, 20).build());
        previous.active = page > 0;

        addRenderableWidget(Button.builder(Component.literal(
                "Страница " + (page+1) + "/" + pageCount),
                b -> {}).bounds(x+99, y+103, 112, 20).build()).active = false;

        Button next = addRenderableWidget(Button.builder(Component.literal("Вперёд ▶"),
                b -> { page++; rebuildWidgets(); })
                .bounds(x+215, y+103, 95, 20).build());
        next.active = page < pageCount-1;

        addRenderableWidget(Button.builder(Component.literal("Скрыть проекцию"),
                b -> {
                    CampBlueprintProjection.hide();
                    Minecraft.getInstance().gui.setScreen(null);
                }).bounds(x, y+128, 310, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Проверить постройки на сервере"),
                b -> {
                    Minecraft minecraft = Minecraft.getInstance();
                    if (minecraft.getConnection() != null) {
                        minecraft.getConnection().sendCommand("civcraft camp plan");
                        minecraft.gui.setScreen(null);
                    }
                }).bounds(x, y+152, 310, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Вернуться к лагерю"),
                b -> onClose()).bounds(x, y+176, 310, 20).build());
    }

    private String label(CampBlueprintProjection.Plan plan) {
        return plan.title() + " — " + CampBlueprintProjection.completed(plan)
                + "/" + CampBlueprintProjection.steps(plan).size() + " блоков";
    }

    @Override
    public void tick() {
        super.tick();
        for (PlanButton pair : visiblePlanButtons) {
            pair.button().setMessage(Component.literal(label(pair.plan())));
        }
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().gui.setScreen(parent);
    }
}
