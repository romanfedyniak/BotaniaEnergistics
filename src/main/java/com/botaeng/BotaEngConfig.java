package com.botaeng;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Config(modid = BotaEng.MODID)
@Mod.EventBusSubscriber(modid = BotaEng.MODID)
public final class BotaEngConfig {

    @Config.Comment({
            "Mana items that a terminal or a conversion monitor neither fills nor empties, and that a portable mana",
            "cell neither charges nor draws from: items whose worth is the mana put into them, such as a tool that",
            "levels up. Written as modid:item, or modid:item@meta for one damage value."})
    public static String[] manaItemBlacklist = {
            "botania:terrapick",
            "botania:manamirror",
            "extrabotany:mastermanaring"
    };

    /** Parsed from {@link #manaItemBlacklist}; null until first asked, and again after the config changes. */
    private static Map<Item, Set<Integer>> blacklist;

    private BotaEngConfig() {
    }

    /**
     * @return whether mana may not be moved in or out of this item other than the way Botania itself does it
     */
    public static boolean isBlacklisted(final ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (blacklist == null) {
            blacklist = parse(manaItemBlacklist);
        }
        final Set<Integer> metas = blacklist.get(stack.getItem());
        return metas != null && (metas.isEmpty() || metas.contains(stack.getMetadata()));
    }

    /** An empty set of damage values stands for every one of them. */
    private static Map<Item, Set<Integer>> parse(final String[] entries) {
        final Map<Item, Set<Integer>> parsed = new HashMap<>();
        for (final String entry : entries) {
            final String[] parts = entry.trim().split("@", 2);
            // A mod that is not installed leaves its line doing nothing.
            final Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(parts[0]));
            if (item == null) {
                continue;
            }
            final Set<Integer> metas = parsed.computeIfAbsent(item, i -> new HashSet<>());
            if (parts.length == 2) {
                try {
                    metas.add(Integer.parseInt(parts[1].trim()));
                } catch (final NumberFormatException e) {
                    BotaEng.log.warn("Ignoring the damage value in mana item blacklist entry '{}'", entry);
                }
            } else {
                // A bare id covers every damage value, even when another line names one.
                metas.add(-1);
            }
        }
        parsed.values().forEach(metas -> {
            if (metas.remove(-1)) {
                metas.clear();
            }
        });
        return parsed;
    }

    @SubscribeEvent
    public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
        if (BotaEng.MODID.equals(event.getModID())) {
            ConfigManager.sync(BotaEng.MODID, Config.Type.INSTANCE);
            blacklist = null;
        }
    }
}
