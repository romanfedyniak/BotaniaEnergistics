package com.botaeng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import vazkii.botania.api.BotaniaAPI;

import appeng.api.AEApi;
import appeng.api.definitions.IMaterials;

/**
 * Recipes made on Botania's machines; everything a crafting table makes is in JSON.
 */
public final class BotaEngRecipes {

    private BotaEngRecipes() {
    }

    public static void init() {
        final IMaterials materials = AEApi.instance().definitions().materials();
        Item below = null;
        for (final Map.Entry<ManaTier, Item> entry : BotaEngItems.COMPONENTS.entrySet()) {
            final ManaTier tier = entry.getKey();
            final Optional<ItemStack> processor = tier.processor.apply(materials).maybeStack(1);
            if (!processor.isPresent()) {
                // A pack that turned the processor off has no way to make this tier.
                below = entry.getValue();
                continue;
            }
            final List<Object> inputs = new ArrayList<>();
            if (below != null) {
                for (int i = 0; i < 3; i++) {
                    inputs.add(new ItemStack(below));
                }
            }
            inputs.add(processor.get());
            Collections.addAll(inputs, tier.materials);
            BotaniaAPI.registerRuneAltarRecipe(new ItemStack(entry.getValue()), tier.altarMana, inputs.toArray());
            below = entry.getValue();
        }
    }
}
