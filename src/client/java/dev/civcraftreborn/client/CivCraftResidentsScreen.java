package dev.civcraftreborn.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Compact resident and profession UI. No browser popups or offscreen long
 * button column: 4 commands per page and a fixed footer.
 * All actions are validated and executed by the server.
 */
public final class CivCraftResidentsScreen extends Screen {
    private final Screen parent;
    private int page;

    public CivCraftResidentsScreen(Screen parent) {
        super(Component.literal("CivCraft — жители и профессии"));
        this.parent=parent;
    }

    @Override
    protected void init() {
        int x=width/2-150;
        int y=Math.max(8,height/2-98);
        addRenderableWidget(Button.builder(Component.literal(
                page==0?"Жители и базовые профессии":"Рабочие развитого поселения"),
                b->{}).bounds(x,y,300,20).build()).active=false;

        if(page==0) {
            action(x,y+24,"Принять существующего жителя",
                    "civcraft camp recruit");
            action(x,y+47,"Пригласить переселенца (12 хлеба)",
                    "civcraft camp invite");
            action(x,y+70,"Назначить строителем",
                    "civcraft camp job assign BUILDER");
            action(x,y+93,"Назначить фермером",
                    "civcraft camp job assign FARMER");
        } else {
            action(x,y+24,"Назначить мастером",
                    "civcraft camp job assign CRAFTSMAN");
            action(x,y+47,"Назначить кладовщиком",
                    "civcraft camp job assign STOREKEEPER");
            action(x,y+70,"Назначить дозорным (башня)",
                    "civcraft camp job assign WATCHMAN");
            action(x,y+93,"Назначить водоносом (колодец)",
                    "civcraft camp job assign WATER_CARRIER");
        }

        addRenderableWidget(Button.builder(Component.literal("◀ Предыдущие"),
                b->{page=0;rebuildWidgets();}).bounds(x,y+118,145,20).build())
                .active=page==1;
        addRenderableWidget(Button.builder(Component.literal("Следующие ▶"),
                b->{page=1;rebuildWidgets();}).bounds(x+155,y+118,145,20).build())
                .active=page==0;

        action(x,y+143,"Список жителей и назначений",
                "civcraft camp job list");
        addRenderableWidget(Button.builder(Component.literal("Вернуться к лагерю"),
                b->onClose()).bounds(x,y+167,300,20).build());
    }

    private void action(int x,int y,String text,String command) {
        addRenderableWidget(Button.builder(Component.literal(text),
                b->issue(command)).bounds(x,y,300,20).build());
    }

    private void issue(String command) {
        Minecraft game=Minecraft.getInstance();
        if(game.getConnection()!=null)game.getConnection().sendCommand(command);
        game.gui.setScreen(null);
    }

    @Override
    public void onClose(){
        Minecraft.getInstance().gui.setScreen(parent);
    }
}
