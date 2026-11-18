package dev.civcraftreborn.settlement;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** Server-authoritative per-world receipt ledger for one-time CivCraft starter materials. */
public record StarterGrantState(int schema, List<String> claimedPlayers) {
    public static final int CURRENT_SCHEMA = 1;

    public static final Codec<StarterGrantState> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("schema").forGetter(StarterGrantState::schema),
                    Codec.STRING.listOf().fieldOf("claimed_players").forGetter(StarterGrantState::claimedPlayers)
            ).apply(instance, StarterGrantState::new));

    public StarterGrantState {
        if (schema != CURRENT_SCHEMA) throw new IllegalArgumentException("Unknown starter-grant save schema");
        for (String id : claimedPlayers) UUID.fromString(id);
        Set<String> unique = new HashSet<>(claimedPlayers);
        if (unique.size() != claimedPlayers.size())
            throw new IllegalArgumentException("Duplicate starter grant receipts");
        claimedPlayers = List.copyOf(claimedPlayers);
    }
}
