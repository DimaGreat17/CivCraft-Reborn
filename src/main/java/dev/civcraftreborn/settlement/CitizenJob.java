package dev.civcraftreborn.settlement;

/**
 * Starter actual-work roles. BUILD requires a live construction order, all
 * other roles require a fully registered, continuously verified building.
 */
public enum CitizenJob {
    UNEMPLOYED, BUILDER, FARMER, STOREKEEPER, CRAFTER;

    public boolean supports(FunctionalBuildingTemplates.Type type) {
        return switch (this) {
            case FARMER -> type == FunctionalBuildingTemplates.Type.FARM;
            case STOREKEEPER -> type == FunctionalBuildingTemplates.Type.STORAGE;
            case CRAFTER -> type == FunctionalBuildingTemplates.Type.WORKSHOP;
            case BUILDER, UNEMPLOYED -> false;
        };
    }
}
