package com.botaeng.integration;

import net.minecraft.item.ItemStack;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

import com.botaeng.BotaEngItems;

@JEIPlugin
public final class BotaEngHeiPlugin implements IModPlugin {

    @Override
    public void register(final IModRegistry registry) {
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(
                new ItemStack(BotaEngItems.NETWORK_MANA));
    }
}
