package dev.civcraftreborn.settlement;

import dev.civcraftreborn.CivCraftReborn;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * A physical, mineable central camp block. The item is obtained through
 * vanilla crafting, so survival creation consumes real Minecraft materials.
 */
public final class CampBlocks {
    private static final Identifier CORE_IDENTIFIER = Identifier.fromNamespaceAndPath(
            CivCraftReborn.MOD_ID, "camp_core");
    private static final BlockItemId CORE_ID = BlockItemId.create(CORE_IDENTIFIER, CORE_IDENTIFIER);

    public static final Block CAMP_CORE = registerCore();

    private CampBlocks() {}

    private static Block registerCore() {
        Block block = Registry.register(BuiltInRegistries.BLOCK, CORE_ID.block(),
                new Block(BlockBehaviour.Properties.of()
                        .setId(CORE_ID.block()).strength(3.0f).sound(SoundType.WOOD)));
        Registry.register(BuiltInRegistries.ITEM, CORE_ID.item(),
                new BlockItem(block, new Item.Properties()
                        .useBlockDescriptionPrefix().setId(CORE_ID.item())));
        return block;
    }

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
                .register(tab -> tab.accept(CAMP_CORE.asItem()));
    }
}
