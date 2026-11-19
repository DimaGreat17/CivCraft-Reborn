package dev.civcraftreborn.settlement;

/** Roles bind citizens to real buildings. No role provides infinite stock. */
public enum ResidentWorkRole {
    BUILDER,
    FARMER,
    CRAFTSMAN,
    STOREKEEPER,
    WATCHMAN,
    WATER_CARRIER;

    public boolean compatible(FunctionalBuildingTemplates.Type type) {
        return switch(this) {
            case BUILDER -> true;
            case FARMER -> type == FunctionalBuildingTemplates.Type.FARM;
            case CRAFTSMAN -> type == FunctionalBuildingTemplates.Type.WORKSHOP
                    || type == FunctionalBuildingTemplates.Type.SMITHY;
            case STOREKEEPER -> type == FunctionalBuildingTemplates.Type.STORAGE
                    || type == FunctionalBuildingTemplates.Type.GRANARY;
            case WATCHMAN -> type == FunctionalBuildingTemplates.Type.WATCHTOWER;
            case WATER_CARRIER -> type == FunctionalBuildingTemplates.Type.WELL;
        };
    }
}
