package dev.civcraftreborn.foundation;

import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import dev.civcraftreborn.CivCraftReborn;

/**
 * Stored in the Overworld's level data. Not a process-global config.
 * The codec rejects unknown schema versions rather than silently resetting a world.
 */
public final class WorldSettingsSavedData extends SavedData {
    private static final SavedDataType<WorldSettingsSavedData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(CivCraftReborn.MOD_ID, "world_settings"),
            WorldSettingsSavedData::fresh,
            WorldSettings.CODEC.xmap(WorldSettingsSavedData::loaded, WorldSettingsSavedData::settings),
            null
    );

    private final WorldSettings settings;

    private WorldSettingsSavedData(WorldSettings settings) {
        this.settings = settings;
    }

    private static WorldSettingsSavedData fresh() {
        WorldSettingsSavedData data = new WorldSettingsSavedData(FoundationDefaults.load());
        data.setDirty();
        return data;
    }

    private static WorldSettingsSavedData loaded(WorldSettings settings) {
        return new WorldSettingsSavedData(settings);
    }

    public WorldSettings settings() {
        return settings;
    }

    public static WorldSettingsSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }
}
