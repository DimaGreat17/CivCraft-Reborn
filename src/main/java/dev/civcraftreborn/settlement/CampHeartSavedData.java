package dev.civcraftreborn.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import dev.civcraftreborn.CivCraftReborn;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/** The authoritative per-world list of manually positioned player camp hearts. */
public final class CampHeartSavedData extends SavedData {
    private static final SavedDataType<CampHeartSavedData> TYPE=new SavedDataType<>(
            Identifier.fromNamespaceAndPath(CivCraftReborn.MOD_ID,"heart_sites"),
            CampHeartSavedData::fresh,
            CampHeartSiteState.CODEC.xmap(CampHeartSavedData::restore,CampHeartSavedData::snapshot),null);
    private final List<CampHeartSite> sites=new ArrayList<>();

    private CampHeartSavedData(List<CampHeartSite> records){sites.addAll(records);}
    private static CampHeartSavedData fresh(){
        CampHeartSavedData s=new CampHeartSavedData(List.of());s.setDirty();return s;
    }
    private static CampHeartSavedData restore(CampHeartSiteState state){
        return new CampHeartSavedData(state.sites());
    }
    private CampHeartSiteState snapshot(){return new CampHeartSiteState(1,sites);}

    public static CampHeartSavedData get(MinecraftServer server){
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }
    public Optional<CampHeartSite> byCore(String dimension,BlockPos core){
        return sites.stream().filter(s->s.dimension().equals(dimension)&&s.core().equals(core)).findFirst();
    }
    public List<CampHeartSite> ownedBy(UUID owner,String dimension){
        return sites.stream().filter(s->s.owner().equals(owner.toString())
                &&s.dimension().equals(dimension)).toList();
    }
    public Optional<CampHeartSite> nearby(UUID owner,String dimension,BlockPos player,int maxDistance){
        return ownedBy(owner,dimension).stream()
                .filter(s->s.core().distSqr(player)<=(long)maxDistance*maxDistance)
                .min((a,b)->Double.compare(a.core().distSqr(player),b.core().distSqr(player)));
    }
    public void add(CampHeartSite site){
        if(byCore(site.dimension(),site.core()).isPresent()||
                sites.stream().anyMatch(s->s.id().equals(site.id())))
            throw new IllegalStateException("Camp Heart site already exists");
        sites.add(site);setDirty();
    }
    public void replace(CampHeartSite oldSite,CampHeartSite newSite){
        if(!oldSite.id().equals(newSite.id()))throw new IllegalArgumentException("Cannot change site ID");
        int index=sites.indexOf(oldSite);
        if(index<0)throw new IllegalStateException("Outdated Heart site");
        sites.set(index,newSite);setDirty();
    }
    public void remove(String dimension,BlockPos core){
        if(sites.removeIf(s->s.dimension().equals(dimension)&&s.core().equals(core)))setDirty();
    }
}
