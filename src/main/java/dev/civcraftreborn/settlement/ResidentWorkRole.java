package dev.civcraftreborn.settlement;

/** Roles bind citizens to real buildings. No role provides infinite stock. */
public enum ResidentWorkRole {
    BUILDER,
    FARMER,
    CRAFTSMAN,
    STOREKEEPER;

    public boolean compatible(FunctionalBuildingTemplates.Type type) {
        return switch(this) {
            case BUILDER -> true;
            case FARMER -> type == FunctionalBuildingTemplates.Type.FARM;
            case CRAFTSMAN -> type == FunctionalBuildingTemplates.Type.WORKSHOP;
            case STOREKEEPER -> type == FunctionalBuildingTemplates.Type.STORAGE;
        };
    }
}
