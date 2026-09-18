package com.botaeng;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import appeng.api.AEApi;

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

    public static final Map<String, Item> ITEMS = new LinkedHashMap<>();
    public static final Map<ManaTier, Item> COMPONENTS = new EnumMap<>(ManaTier.class);

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
    }

    private static Item item(final String name, final Item item) {
        item.setRegistryName(BotaEng.id(name));
        item.setTranslationKey(BotaEng.MODID + "." + name);
        item.setCreativeTab(TAB);
        ITEMS.put(name, item);
        return item;
    }
}
