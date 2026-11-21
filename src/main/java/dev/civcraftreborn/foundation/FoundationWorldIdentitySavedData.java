package dev.civcraftreborn.foundation;

import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import dev.civcraftreborn.CivCraftReborn;

/**
 * Immutable, per-world identity. Minecraft remains the authority for the actual
 * world-generation seed. We record that seed once and compare it after reload.
 * A clone of the *same saved world* retains the same UUID and seed by design.
 */
public final class FoundationWorldIdentitySavedData extends SavedData {
    private static final Codec<FoundationWorldIdentitySavedData> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("world_id").forGetter(FoundationWorldIdentitySavedData::worldId),
                    Codec.LONG.fieldOf("seed").forGetter(FoundationWorldIdentitySavedData::seed)
            ).apply(instance, FoundationWorldIdentitySavedData::loaded));

    private static final SavedDataType<FoundationWorldIdentitySavedData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(CivCraftReborn.MOD_ID, "world_identity"),
            FoundationWorldIdentitySavedData::fresh,
            CODEC,
            null
    );

    private final String worldId;
    private long seed;
    private boolean initialized;

    private FoundationWorldIdentitySavedData(String worldId, long seed, boolean initialized) {
        this.worldId = UUID.fromString(worldId).toString();
        this.seed = seed;
        this.initialized = initialized;
    }

    private static FoundationWorldIdentitySavedData fresh() {
        var data = new FoundationWorldIdentitySavedData(UUID.randomUUID().toString(), 0L, false);
        data.setDirty();
        return data;
    }

    private static FoundationWorldIdentitySavedData loaded(String worldId, long seed) {
        return new FoundationWorldIdentitySavedData(worldId, seed, true);
    }

    public String worldId() {
        return worldId;
    }

    public long seed() {
        return seed;
    }

    public static FoundationWorldIdentitySavedData get(MinecraftServer server) {
        FoundationWorldIdentitySavedData identity = server.overworld().getDataStorage().computeIfAbsent(TYPE);
        long actualSeed = server.overworld().getSeed();
        if (!identity.initialized) {
            identity.seed = actualSeed;
            identity.initialized = true;
            identity.setDirty();
        } else if (identity.seed != actualSeed) {
            throw new IllegalStateException(
                    "CivCraft world identity seed mismatch for world " + identity.worldId
                    + "; refusing to use a stale or unrelated identity record");
        }
        return identity;
    }
}
