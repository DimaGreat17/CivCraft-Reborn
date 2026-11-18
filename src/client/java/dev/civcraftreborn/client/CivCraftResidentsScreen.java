package dev.civcraftreborn.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Small first version of resident management. Each action is a server command;
 * owner must stand near an already recruited resident to assign the role.
 */
public final class CivCraftResidentsScreen extends Screen {
    private final Screen parent;
    public CivCraftResidentsScreen(Screen parent) {
        super(Component.literal("CivCraft — жители и профессии"));
        this.parent=parent;
    }
    @Override
    protected void init() {
        int x=width/2-150;
        int y=Math.max(25,height/2-112);

        addRenderableWidget(Button.builder(
                Component.literal("Подойди к нанятому жителю (не далее 8 блоков)"),
                b->{}).bounds(x,y,300,20).build()).active=false;

        addRenderableWidget(Button.builder(Component.literal("Принять жителя в лагерь"),
                b->issue("civcraft camp recruit")).bounds(x,y+26,300,20).build());
        addRenderableWidget(Button.builder(Component.literal("Назначить строителем"),
                b->issue("civcraft camp job assign BUILDER")).bounds(x,y+52,300,20).build());
        addRenderableWidget(Button.builder(Component.literal("Назначить фермером"),
                b->issue("civcraft camp job assign FARMER")).bounds(x,y+78,300,20).build());
        addRenderableWidget(Button.builder(Component.literal("Назначить мастером"),
                b->issue("civcraft camp job assign CRAFTSMAN")).bounds(x,y+104,300,20).build());
        addRenderableWidget(Button.builder(Component.literal("Назначить кладовщиком"),
                b->issue("civcraft camp job assign STOREKEEPER")).bounds(x,y+130,300,20).build());
        addRenderableWidget(Button.builder(Component.literal("Список жителей и работ"),
                b->issue("civcraft camp job list")).bounds(x,y+156,300,20).build());
        addRenderableWidget(Button.builder(Component.literal("Назад к лагерю"),
                b->onClose()).bounds(x,y+182,300,20).build());
    }
    private void issue(String command) {
        Minecraft game=Minecraft.getInstance();
        if(game.getConnection()!=null)game.getConnection().sendCommand(command);
        game.gui.setScreen(null);
    }
    @Override
    public void onClose(){Minecraft.getInstance().gui.setScreen(parent);}
}
