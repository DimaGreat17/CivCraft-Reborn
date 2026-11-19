package dev.civcraftreborn.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import dev.civcraftreborn.settlement.HeartSiteRequest;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Separate management surface for the player's camp. All mutations are
 * performed through existing server-authoritative commands; client buttons
 * never directly edit settlement records, resources, or citizen state.
 */
public final class CivCraftManagementScreen extends Screen {
    private final Screen parent;
    private boolean civilizationTab;
    private Button foundingButton;
    private Button progressLabel;
    private final Button[] overviewLabels=new Button[4];
    private int progressRefreshTicks;

    public CivCraftManagementScreen(Screen parent) {
        super(Component.literal("CivCraft — управление"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        progressRefreshTicks=0;
        foundingButton=null;
        progressLabel=null;
        java.util.Arrays.fill(overviewLabels,null);
        requestProgress();
        int left = width / 2 - 140;
        int top = Math.max(30, height / 2 - 100);

        addRenderableWidget(Button.builder(Component.literal("Лагерь"),
                b -> { civilizationTab = false; rebuildWidgets(); })
                .bounds(left, top, 138, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Цивилизация"),
                b -> { civilizationTab = true; rebuildWidgets(); })
                .bounds(left + 142, top, 138, 20).build());

        if (!civilizationTab) {
            foundingButton=addRenderableWidget(Button.builder(Component.literal("Основать лагерь"),
                    b -> command("civcraft camp found"))
                    .bounds(left, top + 32, 138, 20).build());
            foundingButton.active=CampBlueprintProjection.heartReadyToFound();
            addRenderableWidget(Button.builder(Component.literal("Проверить постройку"),
                    b -> command("civcraft camp heart inspect"))
                    .bounds(left + 142, top + 32, 138, 20).build());
            progressLabel=addRenderableWidget(Button.builder(
                    Component.literal(CampBlueprintProjection.heartProgressLabel()),
                    b -> {}).bounds(left,top+58,280,20).build());
            progressLabel.active=false;
            addRenderableWidget(Button.builder(Component.literal("Схема строительства"),
                    b -> Minecraft.getInstance().gui.setScreen(new CivCraftBlueprintScreen(this)))
                    .bounds(left, top + 82, 280, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Принять жителя"),
                    b -> command("civcraft camp recruit"))
                    .bounds(left, top + 108, 138, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Повысить поселение"),
                    b -> command("civcraft camp promote"))
                    .bounds(left + 142, top + 108, 138, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Жители и профессии"),
                    b -> Minecraft.getInstance().gui.setScreen(new CivCraftResidentsScreen(this)))
                    .bounds(left, top + 134, 280, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Рабочие здания / стройка"),
                    b -> command("civcraft camp building list"))
                    .bounds(left, top + 160, 138, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Заказы на строительство"),
                    b -> command("civcraft camp construction list"))
                    .bounds(left + 142, top + 160, 138, 20).build());
        } else {
            // For now this is an honest, functional settlement overview.
            // Independent AI civilizations and inter-state diplomacy remain P2.
            String[] lines=overviewLines();
            for(int i=0;i<overviewLabels.length;i++) {
                overviewLabels[i]=addRenderableWidget(Button.builder(
                        Component.literal(lines[i]),b -> {})
                        .bounds(left,top+36+i*29,280,23).build());
                overviewLabels[i].active=false;
            }
            addRenderableWidget(Button.builder(Component.literal(
                    "Проверить условия следующего уровня"),
                    b -> command("civcraft camp inspect"))
                    .bounds(left,top+162,280,20).build());
        }
        addRenderableWidget(Button.builder(Component.literal("Закрыть"),
                b -> onClose()).bounds(left, top + 192, 280, 20).build());
    }

    private void requestProgress() {
        var client=Minecraft.getInstance();
        if(client.getConnection()!=null && client.level!=null)
            ClientPlayNetworking.send(new HeartSiteRequest());
    }

    @Override
    public void tick() {
        super.tick();
        // This is a read-only server query, never a client-computed completion.
        int refreshInterval=CivCraftOverviewState.latest().exists()?100:40;
        if(++progressRefreshTicks>=refreshInterval) {
            progressRefreshTicks=0;
            requestProgress();
        }
        if(foundingButton!=null)
            foundingButton.active=CampBlueprintProjection.heartReadyToFound();
        if(progressLabel!=null)
            progressLabel.setMessage(Component.literal(
                    CampBlueprintProjection.heartProgressLabel()));
        String[] lines=overviewLines();
        for(int i=0;i<overviewLabels.length;i++)
            if(overviewLabels[i]!=null)
                overviewLabels[i].setMessage(Component.literal(lines[i]));
    }

    private String[] overviewLines() {
        return new String[]{
                "Статус: "+CivCraftOverviewState.stageLabel(),
                CivCraftOverviewState.resourceLabel(),
                CivCraftOverviewState.buildingLabel(),
                CivCraftOverviewState.readinessLabel()
        };
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
