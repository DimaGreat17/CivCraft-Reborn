package dev.civcraftreborn.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Catalog reflects stage authorized by the server for the clicked physical
 * Camp Core. A fresh camp sees BASIC only; promoted settlement sees all plans.
 */
public final class CivCraftBlueprintScreen extends Screen {
    private static final int PER_PAGE = 4;
    private final Screen parent;
    private int page;
    private int lastStage = -999;
    private final List<PlanButton> visibleButtons = new ArrayList<>();

    private record PlanButton(CampBlueprintProjection.Plan plan, Button button) {}

    public CivCraftBlueprintScreen(Screen parent) {
        super(Component.literal("CivCraft — каталог построек"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        visibleButtons.clear();
        lastStage = CampBlueprintProjection.serverStage();
        List<CampBlueprintProjection.Plan> plans = availablePlans();
        int pageCount = Math.max(1, (plans.size() + PER_PAGE - 1) / PER_PAGE);
        page = Math.min(page, pageCount-1);
        int x = width/2-155;
        int y = Math.max(22, height/2-105);

        if (lastStage == -2) {
            Button loading = addRenderableWidget(Button.builder(
                    Component.literal("Ожидание ответа сервера о стадии лагеря"),
                    b -> {}).bounds(x, y, 310, 20).build());
            loading.active = false;
        } else {
            for (int i=page*PER_PAGE; i<Math.min((page+1)*PER_PAGE, plans.size()); i++) {
                CampBlueprintProjection.Plan plan = plans.get(i);
                int row = i % PER_PAGE;
                Button button = addRenderableWidget(Button.builder(Component.literal(label(plan)),
                        b -> {
                            if (CampBlueprintProjection.show(plan)) {
                                Minecraft.getInstance().gui.setScreen(
                                        new CivCraftPlacementScreen(this));
                            }
                        }).bounds(x, y+row*24, 310, 20).build());
                visibleButtons.add(new PlanButton(plan, button));
            }
        }

        Button previous = addRenderableWidget(Button.builder(Component.literal("◀ Ранее"),
                b -> { page--; rebuildWidgets(); })
                .bounds(x,y+104,94,20).build());
        previous.active = page > 0;

        String tier = switch (lastStage) {
            case 1 -> "Поселение";
            case 0 -> "Лагерь";
            case -1 -> "Сердце";
            default -> "Проверка";
        };
        addRenderableWidget(Button.builder(Component.literal(
                tier + " " + (page+1)+"/"+pageCount),
                b -> {}).bounds(x+99,y+104,112,20).build()).active=false;

        Button next = addRenderableWidget(Button.builder(Component.literal("Далее ▶"),
                b -> { page++; rebuildWidgets(); })
                .bounds(x+216,y+104,94,20).build());
        next.active = page < pageCount-1;

        addRenderableWidget(Button.builder(Component.literal("Скрыть 3D-схему"),
                b -> {
                    CampBlueprintProjection.hide();
                    Minecraft.getInstance().gui.setScreen(null);
                }).bounds(x,y+130,310,20).build());

        addRenderableWidget(Button.builder(Component.literal("Проверить реальные постройки"),
                b -> {
                    Minecraft client = Minecraft.getInstance();
                    if (client.getConnection() != null) {
                        client.getConnection().sendCommand("civcraft camp plan");
                        client.gui.setScreen(null);
                    }
                }).bounds(x,y+154,310,20).build());

        addRenderableWidget(Button.builder(Component.literal("Назад к лагерю"),
                b -> onClose()).bounds(x,y+178,310,20).build());
    }

    private List<CampBlueprintProjection.Plan> availablePlans() {
        return Arrays.stream(CampBlueprintProjection.Plan.values())
                .filter(CampBlueprintProjection::unlocked).toList();
    }

    private String label(CampBlueprintProjection.Plan plan) {
        return plan.title()+" — "+
                CampBlueprintProjection.completed(plan)+"/"+
                CampBlueprintProjection.steps(plan).size()+" блоков";
    }

    @Override
    public void tick() {
        super.tick();
        if (lastStage != CampBlueprintProjection.serverStage()) {
            page=0;
            rebuildWidgets();
            return;
        }
        for (PlanButton entry : visibleButtons) {
            entry.button().setMessage(Component.literal(label(entry.plan())));
        }
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().gui.setScreen(parent);
    }
}
