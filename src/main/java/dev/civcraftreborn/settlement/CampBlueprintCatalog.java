package dev.civcraftreborn.settlement;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * Server/client-independent, immutable blueprint definition read from a
 * packaged Minecraft structure conversion. The very same states are passed
 * to construction validation, projection, materials and completion checks.
 *
 * A definition is looked up by explicit VERSIONED ID. It never loads arbitrary
 * files supplied over the network or silently changes a saved world.
 */
public final class CampBlueprintCatalog {
    public record Cell(int x,int y,int z,BlockState state) {}
    public record Definition(String id,int width,int height,int depth,List<Cell> cells) {}

    private CampBlueprintCatalog() {}

    /** Explicit pack index; resources from worlds or remote clients are never executed. */
    public static List<String> availableBundled() {
        String path="/data/civcraft_reborn/blueprints/index.json";
        try(InputStream stream=CampBlueprintCatalog.class.getResourceAsStream(path)) {
            if(stream==null)return List.of("camp_heart_v1");
            JsonObject json=JsonParser.parseReader(new InputStreamReader(
                    stream,StandardCharsets.UTF_8)).getAsJsonObject();
            if(!"civcraft_reborn:blueprint-index/v1".equals(
                    json.get("schema").getAsString()))
                throw new IllegalStateException("Invalid blueprint index schema");
            List<String> ids=new ArrayList<>();
            for(JsonElement value:json.getAsJsonArray("ids")) {
                String id=value.getAsString();
                if(!id.matches("[a-z0-9_/-]+")||id.contains("..")||ids.contains(id))
                    throw new IllegalStateException("Invalid or duplicate blueprint ID: "+id);
                ids.add(id);
            }
            if(!ids.contains("camp_heart_v1"))
                throw new IllegalStateException("Legacy blueprint may not be removed");
            return List.copyOf(ids);
        } catch(java.io.IOException e) {
            throw new IllegalStateException("Could not read blueprint index",e);
        }
    }

    public static Definition loadBundled(String id) {
        if(!id.matches("[a-z0-9_/-]+") || id.contains(".."))
            throw new IllegalArgumentException("Invalid blueprint ID: "+id);
        String path="/data/civcraft_reborn/blueprints/"+id+".json";
        try(InputStream stream=CampBlueprintCatalog.class.getResourceAsStream(path)) {
            if(stream==null)throw new IllegalStateException(
                    "Missing packaged blueprint "+path);
            JsonObject doc=JsonParser.parseReader(new InputStreamReader(
                    stream,StandardCharsets.UTF_8)).getAsJsonObject();
            if(!"civcraft_reborn:blueprint/v1".equals(doc.get("schema").getAsString())
                    || !id.equals(doc.get("id").getAsString()))
                throw new IllegalStateException("Blueprint ID/schema mismatch: "+id);
            JsonArray dims=doc.getAsJsonArray("size");
            if(dims.size()!=3)throw new IllegalStateException("Invalid blueprint dimensions");
            int width=dims.get(0).getAsInt();
            int height=dims.get(1).getAsInt();
            int depth=dims.get(2).getAsInt();
            if(width<1||width>96||height<1||height>96||depth<1||depth>96
                    ||(long)width*height*depth>100000)
                throw new IllegalStateException("Unacceptable blueprint size");
            JsonArray entries=doc.getAsJsonArray("cells");
            if(entries==null||entries.isEmpty()||entries.size()>100000)
                throw new IllegalStateException("Empty or oversized blueprint");
            List<Cell> cells=new ArrayList<>(entries.size());
            Set<String> seen=new HashSet<>();
            for(JsonElement element:entries) {
                JsonObject entry=element.getAsJsonObject();
                JsonArray pos=entry.getAsJsonArray("pos");
                if(pos==null||pos.size()!=3)
                    throw new IllegalStateException("Malformed blueprint position");
                int x=pos.get(0).getAsInt(),y=pos.get(1).getAsInt(),
                        z=pos.get(2).getAsInt();
                if(x<0||x>=width||y<0||y>=height||z<0||z>=depth
                        ||!seen.add(x+","+y+","+z))
                    throw new IllegalStateException("Duplicate/out of bounds blueprint cell");
                JsonObject stateEntry=entry.getAsJsonObject("state");
                String blockName=stateEntry.get("id").getAsString();
                Identifier key=Identifier.parse(blockName);
                Block block=BuiltInRegistries.BLOCK.getValue(key);
                if(block==null||!BuiltInRegistries.BLOCK.getKey(block).equals(key))
                    throw new IllegalStateException("Unknown blueprint block "+blockName);
                if(blockName.equals("minecraft:structure_block")
                        || blockName.equals("minecraft:jigsaw")
                        || blockName.equals("minecraft:structure_void"))
                    throw new IllegalStateException("Control block in user schematic");
                BlockState state=block.defaultBlockState();
                JsonObject properties=stateEntry.getAsJsonObject("properties");
                if(properties!=null)for(Map.Entry<String,JsonElement> prop:properties.entrySet()) {
                    Property<?> property=state.getBlock().getStateDefinition()
                            .getProperty(prop.getKey());
                    if(property==null)throw new IllegalStateException(
                            "Unknown property "+prop.getKey()+" on "+blockName);
                    state=withProperty(state,property,prop.getValue().getAsString());
                }
                cells.add(new Cell(x,y,z,state));
            }
            if(doc.has("required_cells")
                    && doc.get("required_cells").getAsInt()!=cells.size())
                throw new IllegalStateException("Blueprint cell count mismatch");
            return new Definition(id,width,height,depth,List.copyOf(cells));
        } catch(java.io.IOException e) {
            throw new IllegalStateException("Could not read blueprint "+id,e);
        }
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    private static BlockState withProperty(BlockState state,Property property,String value) {
        var parsed=property.getValue(value);
        if(parsed.isEmpty())throw new IllegalStateException(
                "Invalid value "+value+" for "+property.getName());
        return state.setValue(property,(Comparable)parsed.get());
    }
}
