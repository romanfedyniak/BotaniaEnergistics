package com.botaeng;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import appeng.api.AEApi;
import appeng.items.materials.MaterialType;

import com.botaeng.block.BlockFluixManaPool;
import com.botaeng.block.BlockManaBuffer;
import com.botaeng.item.ItemManaBurstP2P;
import com.botaeng.item.ItemManaStorageCell;
import com.botaeng.item.ItemPortableManaCell;

/**
 * Everything the mod registers, by registry name.
 */
public final class BotaEngItems {

    public static final CreativeTabs TAB = new CreativeTabs(BotaEng.MODID) {
        @Nonnull
        @Override
        public ItemStack createIcon() {
            return new ItemStack(COMPONENTS.get(ManaTier.T1K));
        }
    };

    public static final Map<String, Block> BLOCKS = new LinkedHashMap<>();
    public static final Map<String, Item> ITEMS = new LinkedHashMap<>();
    public static final Map<ManaTier, Item> COMPONENTS = new EnumMap<>(ManaTier.class);
    public static final Map<ManaTier, Item> CELLS = new EnumMap<>(ManaTier.class);
    public static final Map<ManaTier, Item> PORTABLE_CELLS = new EnumMap<>(ManaTier.class);
    public static Item MANA_CELL_HOUSING;
    public static Item CREATIVE_MANA_CELL;
    public static Block FLUIX_MANA_POOL;
    public static Block MANA_BUFFER;
    public static Item MANA_P2P;

    private BotaEngItems() {
    }

    public static void init() {
        final boolean highCapacity = AEApi.instance().definitions().items().cell256k().isEnabled();
        for (final ManaTier tier : ManaTier.values()) {
            if (tier.isHighCapacity() && !highCapacity) {
                continue;
            }
            COMPONENTS.put(tier, item("mana_component_" + tier.name, new Item()));
        }

        MANA_CELL_HOUSING = item("mana_cell_housing", new Item());
        for (final ManaTier tier : COMPONENTS.keySet()) {
            final MaterialType universal = MaterialType.valueOf("CELL" + tier.name.toUpperCase(Locale.ROOT) + "_PART");
            CELLS.put(tier, item("mana_cell_" + tier.name, new ItemManaStorageCell(tier, universal)));
        }
        for (final ManaTier tier : COMPONENTS.keySet()) {
            if (tier.portableFluidCell.apply(AEApi.instance().definitions().items()).isEnabled()) {
                PORTABLE_CELLS.put(tier, item("portable_mana_cell_" + tier.name, new ItemPortableManaCell(tier.kilobytes)));
            }
        }
        // No recipe: a pack that wants it as a reward gives it one.
        CREATIVE_MANA_CELL = item("creative_mana_cell", new Item().setMaxStackSize(1));

        MANA_P2P = item("mana_p2p", new ItemManaBurstP2P());

        FLUIX_MANA_POOL = block("fluix_mana_pool", new BlockFluixManaPool());
        MANA_BUFFER = block("mana_buffer", new BlockManaBuffer());
    }

    private static Block block(final String name, final Block block) {
        block.setRegistryName(BotaEng.id(name));
        block.setTranslationKey(BotaEng.MODID + "." + name);
        block.setCreativeTab(TAB);
        BLOCKS.put(name, block);
        ITEMS.put(name, new ItemBlock(block).setRegistryName(block.getRegistryName()));
        return block;
    }

    private static Item item(final String name, final Item item) {
        item.setRegistryName(BotaEng.id(name));
        item.setTranslationKey(BotaEng.MODID + "." + name);
        item.setCreativeTab(TAB);
        ITEMS.put(name, item);
        return item;
    }
}
