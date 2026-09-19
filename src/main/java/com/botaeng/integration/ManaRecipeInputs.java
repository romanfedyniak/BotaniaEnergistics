package com.botaeng.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.brew.IBrewContainer;
import vazkii.botania.api.recipe.RecipeBrew;
import vazkii.botania.api.recipe.RecipeManaInfusion;
import vazkii.botania.api.recipe.RecipeRuneAltar;

import appeng.api.integrations.hei.ExtraInputProviders;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;

import com.botaeng.me.ManaKey;

/**
 * The mana a runic altar, a mana pool or a botanical brewery takes, which Botania's recipe screens draw as a bar
 * and do not list, put into a processing pattern with the rest of the recipe. Each recipe is found again in
 * Botania's own lists by what the screen shows.
 */
public final class ManaRecipeInputs {

    private ManaRecipeInputs() {
    }

    public static void register() {
        ExtraInputProviders.register("botania.runicAltar", ManaRecipeInputs::runicAltar);
        ExtraInputProviders.register("botania.manaPool", ManaRecipeInputs::manaPool);
        ExtraInputProviders.register("botania.brewery", ManaRecipeInputs::brewery);
    }

    private static List<GenericStack> runicAltar(final List<GenericStack> inputs, final List<GenericStack> outputs) {
        final List<ItemStack> in = items(inputs);
        final List<ItemStack> out = items(outputs);
        for (final RecipeRuneAltar recipe : BotaniaAPI.runeAltarRecipes) {
            if (containsEqual(out, recipe.getOutput()) && allFound(recipe.getInputs(), in)) {
                return mana(recipe.getManaUsage());
            }
        }
        return Collections.emptyList();
    }

    private static List<GenericStack> manaPool(final List<GenericStack> inputs, final List<GenericStack> outputs) {
        final List<ItemStack> in = items(inputs);
        final List<ItemStack> out = items(outputs);
        for (final RecipeManaInfusion recipe : BotaniaAPI.manaInfusionRecipes) {
            if (containsEqual(out, recipe.getOutput())
                    && allFound(Collections.singletonList(recipe.getInput()), in)
                    && catalystShown(recipe.getCatalyst(), in)) {
                return mana(recipe.getManaToConsume());
            }
        }
        return Collections.emptyList();
    }

    /** A brew costs what the container it is brewed into says. */
    private static List<GenericStack> brewery(final List<GenericStack> inputs, final List<GenericStack> outputs) {
        final List<ItemStack> in = items(inputs);
        final List<ItemStack> out = items(outputs);
        for (final ItemStack container : in) {
            if (!(container.getItem() instanceof IBrewContainer)) {
                continue;
            }
            for (final RecipeBrew recipe : BotaniaAPI.brewRecipes) {
                if (containsEqual(out, recipe.getOutput(container)) && allFound(recipe.getInputs(), in)) {
                    return mana(((IBrewContainer) container.getItem()).getManaCost(recipe.getBrew(), container));
                }
            }
        }
        return Collections.emptyList();
    }

    private static List<GenericStack> mana(final int amount) {
        return amount > 0 ? Collections.singletonList(new GenericStack(ManaKey.INSTANCE, amount))
                : Collections.emptyList();
    }

    private static List<ItemStack> items(final List<GenericStack> stacks) {
        final List<ItemStack> items = new ArrayList<>();
        for (final GenericStack stack : stacks) {
            if (stack.what() instanceof AEItemKey) {
                items.add(((AEItemKey) stack.what()).toStack());
            }
        }
        return items;
    }

    private static boolean containsEqual(final List<ItemStack> stacks, final ItemStack wanted) {
        for (final ItemStack stack : stacks) {
            if (ItemStack.areItemsEqual(stack, wanted) && ItemStack.areItemStackTagsEqual(stack, wanted)) {
                return true;
            }
        }
        return false;
    }

    /** Every input of the recipe, an item or an ore name, matched to a different one of those shown. */
    private static boolean allFound(final List<Object> recipeInputs, final List<ItemStack> shown) {
        final List<ItemStack> left = new ArrayList<>(shown);
        for (final Object input : recipeInputs) {
            if (!left.removeIf(new FirstMatch(input))) {
                return false;
            }
        }
        return true;
    }

    private static boolean catalystShown(final IBlockState catalyst, final List<ItemStack> shown) {
        if (catalyst == null) {
            return true;
        }
        final Block block = catalyst.getBlock();
        return allFound(Collections.singletonList(new ItemStack(block, 1, block.getMetaFromState(catalyst))), shown);
    }

    /** Takes out only the first stack that matches, so two equal inputs need two equal stacks. */
    private static final class FirstMatch implements Predicate<ItemStack> {

        private final Object input;
        private boolean taken;

        private FirstMatch(final Object input) {
            this.input = input;
        }

        @Override
        public boolean test(final ItemStack stack) {
            if (this.taken || !matches(this.input, stack)) {
                return false;
            }
            this.taken = true;
            return true;
        }

        private static boolean matches(final Object input, final ItemStack stack) {
            if (input instanceof ItemStack) {
                return OreDictionary.itemMatches((ItemStack) input, stack, false);
            }
            if (input instanceof String) {
                for (final ItemStack ore : OreDictionary.getOres((String) input, false)) {
                    if (OreDictionary.itemMatches(ore, stack, false)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
