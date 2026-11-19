package dev.civcraftreborn.client;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Material bill: needed in world = total - already built; still collect =
 * needed in world - matching inventory. Visual numbers are advisory only:
 * server-side stock, rights and construction remain authoritative.
 */
public final class CivCraftMaterialsScreen extends Screen {
    private static final int ROWS = 5;
    private final Screen parent;
    private int page;
    private Button[] rows;

    public CivCraftMaterialsScreen(Screen parent) {
        super(Component.literal("CivCraft — материалы постройки"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        CampBlueprintProjection.Plan plan = CampBlueprintProjection.selected();
        if (plan == null || !CampBlueprintProjection.unlocked(plan)) {
            Minecraft.getInstance().gui.setScreen(parent);
            return;
        }
        List<CampBlueprintProjection.MaterialLine> materials =
                CampBlueprintProjection.materials(plan);
        int totalPages = Math.max(1, (materials.size()+ROWS-1)/ROWS);
        page=Math.min(page,totalPages-1);
        rows = new Button[ROWS];
        int x=width/2-155;
        int y=Math.max(25,height/2-106);

        addRenderableWidget(Button.builder(Component.literal(
                plan.title()+" — необходимо / уже стоит / есть / добыть"),
                b -> {}).bounds(x,y,310,20).build()).active=false;

        for(int i=0;i<ROWS;i++) {
            int index=page*ROWS+i;
            if (index>=materials.size()) break;
            rows[i]=addRenderableWidget(Button.builder(
                    Component.literal(line(materials.get(index))),
                    b -> {}).bounds(x,y+25+i*22,310,20).build());
            rows[i].active=false;
        }

        Button previous = addRenderableWidget(Button.builder(Component.literal("◀"),
                b -> { page--;rebuildWidgets(); }).bounds(x,y+139,85,20).build());
        previous.active=page>0;

        addRenderableWidget(Button.builder(Component.literal(
                "Страница "+(page+1)+"/"+totalPages),
                b -> {}).bounds(x+90,y+139,130,20).build()).active=false;

        Button next=addRenderableWidget(Button.builder(Component.literal("▶"),
                b -> { page++; rebuildWidgets(); }).bounds(x+225,y+139,85,20).build());
        next.active=page<totalPages-1;

        addRenderableWidget(Button.builder(Component.literal("Назад к размещению"),
                b -> onClose()).bounds(x,y+165,310,20).build());
    }

    private String line(CampBlueprintProjection.MaterialLine line) {
        return line.title()+"  "+line.required()+"/"+line.alreadyBuilt()
                +"/"+line.inInventory()+"/"+line.stillNeeded();
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().gui.setScreen(parent);
    }
}
