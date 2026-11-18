package dev.civcraftreborn.client;

import dev.civcraftreborn.foundation.FoundationDefaults;
import dev.civcraftreborn.foundation.SessionMode;
import dev.civcraftreborn.foundation.WorldPreset;
import dev.civcraftreborn.foundation.WorldSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Dedicated CivCraft world-creation configuration. Nothing is injected over
 * the Create World Game/More tabs; returning restores the previous World tab.
 */
public final class CivCraftWorldOptionsScreen extends Screen {
    private final Screen parent;
    private WorldSettings selected;

    public CivCraftWorldOptionsScreen(Screen parent) {
        super(Component.literal("Настройки мира CivCraft"));
        this.parent = parent;
        this.selected = FoundationDefaults.load();
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 125;
        int y = Math.max(55, this.height / 2 - 65);

        final Button[] optionButtons = new Button[2];
        addRenderableWidget(Button.builder(
                Component.literal("Мир CivCraft: " + (selected.civcraftWorld() ? "Вкл" : "Выкл")),
                button -> {
                    selected = new WorldSettings(WorldSettings.CURRENT_SCHEMA,
                            selected.preset(), selected.mode(), !selected.civcraftWorld());
                    save();
                    optionButtons[0].active = selected.civcraftWorld();
                    optionButtons[1].active = selected.civcraftWorld();
                    button.setMessage(Component.literal(
                            "Мир CivCraft: " + (selected.civcraftWorld() ? "Вкл" : "Выкл")));
                }).bounds(x, y, 250, 20).build());

        Button size = addRenderableWidget(Button.builder(
                Component.literal(sizeLabel()), button -> {
                    WorldPreset[] all = WorldPreset.values();
                    WorldPreset next = all[(selected.preset().ordinal() + 1) % all.length];
                    selected = new WorldSettings(WorldSettings.CURRENT_SCHEMA,
                            next, selected.mode(), selected.civcraftWorld());
                    save();
                    button.setMessage(Component.literal(sizeLabel()));
                }).bounds(x, y + 26, 250, 20).build());

        Button mode = addRenderableWidget(Button.builder(
                Component.literal(modeLabel()), button -> {
                    SessionMode next = selected.mode() == SessionMode.ENDLESS_SANDBOX
                            ? SessionMode.FINITE_VICTORY : SessionMode.ENDLESS_SANDBOX;
                    selected = new WorldSettings(WorldSettings.CURRENT_SCHEMA,
                            selected.preset(), next, selected.civcraftWorld());
                    save();
                    button.setMessage(Component.literal(modeLabel()));
                }).bounds(x, y + 52, 250, 20).build());

        // These controls only apply when CivCraft mode is enabled.
        optionButtons[0] = size;
        optionButtons[1] = mode;
        size.active = selected.civcraftWorld();
        mode.active = selected.civcraftWorld();

        addRenderableWidget(Button.builder(Component.literal("Готово"), button -> onClose())
                .bounds(x, y + 90, 250, 20).build());
    }

    private String sizeLabel() {
        return "Размер карты: " + selected.preset().sideBlocks()
                + " × " + selected.preset().sideBlocks();
    }

    private String modeLabel() {
        return selected.mode() == SessionMode.FINITE_VICTORY
                ? "Конечная (победа пока недоступна)" : "Режим: Свободная песочница";
    }

    private void save() {
        FoundationDefaults.saveWorldCreationChoice(selected);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().gui.setScreen(parent);
    }
}
