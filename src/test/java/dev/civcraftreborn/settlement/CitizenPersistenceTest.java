package dev.civcraftreborn.settlement;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

final class CitizenPersistenceTest {
    @Test
    void citizenTracksExistingMinecraftEntityWithoutGeneratingAPerson() {
        UUID entity = UUID.randomUUID();
        CampRecord camp = CampRules.found(UUID.randomUUID(), "minecraft:overworld",
                0, 64, 0, "Original Camp", 30);
        CitizenRecord citizen = CitizenRecord.recruit(entity, camp, 4, 65, 6, 40);
        assertEquals(entity.toString(), citizen.entityUuid());
        assertEquals(camp.id(), citizen.campId());
        assertEquals(40, citizen.joinedGameTime());
        assertNotEquals(entity.toString(), citizen.id());
    }

    @Test
    void savedCitizensRejectDuplicateWorldEntityOrCitizenIdentity() {
        CampRecord camp = CampRules.found(UUID.randomUUID(), "minecraft:overworld",
                0, 64, 0, "Camp", 0);
        CitizenRecord one = CitizenRecord.recruit(UUID.randomUUID(), camp, 4, 65, 6, 20);
        assertThrows(IllegalArgumentException.class,
                () -> new CitizenRegistryState(1, List.of(one, one)));
        CitizenRecord clone = new CitizenRecord(UUID.randomUUID().toString(), one.entityUuid(),
                camp.id(), 5, 65, 7, 20);
        assertThrows(IllegalArgumentException.class,
                () -> new CitizenRegistryState(1, List.of(one, clone)));
    }
}
