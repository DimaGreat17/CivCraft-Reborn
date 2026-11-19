package dev.civcraftreborn.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Separate management surface for the player's camp. All mutations are
 * performed through existing server-authoritative commands; client buttons
 * never directly edit settlement records, resources, or citizen state.
 */
public final class CivCraftManagementScreen extends Screen {
    private final Screen parent;
    private boolean civilizationTab;

    public CivCraftManagementScreen(Screen parent) {
        super(Component.literal("CivCraft — управление"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int left = width / 2 - 140;
        int top = Math.max(30, height / 2 - 100);

        addRenderableWidget(Button.builder(Component.literal("Лагерь"),
                b -> { civilizationTab = false; rebuildWidgets(); })
                .bounds(left, top, 138, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Цивилизация"),
                b -> { civilizationTab = true; rebuildWidgets(); })
                .bounds(left + 142, top, 138, 20).build());

        if (!civilizationTab) {
            addRenderableWidget(Button.builder(Component.literal("Основать лагерь"),
                    b -> command("civcraft camp found"))
                    .bounds(left, top + 32, 138, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Проверка лагеря"),
                    b -> command("civcraft camp inspect"))
                    .bounds(left + 142, top + 32, 138, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Схема строительства"),
                    b -> Minecraft.getInstance().gui.setScreen(new CivCraftBlueprintScreen(this)))
                    .bounds(left, top + 58, 280, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Принять жителя"),
                    b -> command("civcraft camp recruit"))
                    .bounds(left, top + 84, 138, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Повысить поселение"),
                    b -> command("civcraft camp promote"))
                    .bounds(left + 142, top + 84, 138, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Жители и профессии"),
                    b -> Minecraft.getInstance().gui.setScreen(new CivCraftResidentsScreen(this)))
                    .bounds(left, top + 110, 280, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Рабочие здания / стройка"),
                    b -> command("civcraft camp building list"))
                    .bounds(left, top + 136, 138, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Заказы на строительство"),
                    b -> command("civcraft camp construction list"))
                    .bounds(left + 142, top + 136, 138, 20).build());
        } else {
            Button future = addRenderableWidget(Button.builder(
                    Component.literal("Цивилизации — в следующем этапе"),
                    b -> {}).bounds(left, top + 55, 280, 20).build());
            future.active = false;
        }
        addRenderableWidget(Button.builder(Component.literal("Закрыть"),
                b -> onClose()).bounds(left, top + 168, 280, 20).build());
    }

    private void command(String command) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() != null) {
            minecraft.getConnection().sendCommand(command);
            minecraft.gui.setScreen(parent);
        }
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().gui.setScreen(parent);
    }
}
