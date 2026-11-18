package dev.civcraftreborn.settlement;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Verifies persistent one-time construction grants cannot contain duplicates. */
final class StarterGrantStateTest {
    @Test
    void duplicateReceiptsAreRejected() {
        String id = UUID.randomUUID().toString();
        assertThrows(IllegalArgumentException.class, () ->
                new StarterGrantState(1, List.of(id, id)));
    }

    @Test
    void unsupportedSaveSchemaAndInvalidPlayerIdAreRejected() {
        String id = UUID.randomUUID().toString();
        assertThrows(IllegalArgumentException.class, () ->
                new StarterGrantState(99, List.of(id)));
        assertThrows(IllegalArgumentException.class, () ->
                new StarterGrantState(1, List.of("not-a-uuid")));
    }

    @Test
    void validReceiptSurvivesStateCopy() {
        String id = UUID.randomUUID().toString();
        StarterGrantState state = new StarterGrantState(1, List.of(id));
        assertEquals(List.of(id), state.claimedPlayers());
        assertThrows(UnsupportedOperationException.class,
                () -> state.claimedPlayers().add(UUID.randomUUID().toString()));
    }
}
