package com.botaeng.lexicon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.internal.IInternalMethodHandler;
import vazkii.botania.api.lexicon.LexiconCategory;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconPage;
import vazkii.botania.api.lexicon.LexiconRecipeMappings;

import appeng.api.AEApi;

import com.botaeng.BotaEng;
import com.botaeng.BotaEngItems;
import com.botaeng.BotaEngRecipes;
import com.botaeng.ManaTier;

/**
 * The mod's own category in the Lexica Botania.
 */
public final class BotaEngLexicon {

    public static LexiconCategory category;
    public static LexiconEntry intro;
    public static LexiconEntry cells;
    public static LexiconEntry portableCells;
    public static LexiconEntry fluixPool;
    public static LexiconEntry buffer;
    public static LexiconEntry burstTunnel;
    public static LexiconEntry wirelessMana;

    private BotaEngLexicon() {
    }

    /** After the recipes, which the pages look up. */
    public static void init() {
        final IInternalMethodHandler pages = BotaniaAPI.internalHandler;
        // Between Botania's own categories and its miscellany.
        category = new LexiconCategory("botaeng.category.energistics")
                .setIcon(BotaEng.id("textures/gui/lexicon_category.png"))
                .setPriority(1);
        BotaniaAPI.addCategory(category);

        intro = new BotaEngLexiconEntry("intro", category).setPriority();
        intro.setLexiconPages(pages.textPage("0"), pages.textPage("1"), pages.textPage("2"), pages.textPage("3"));
        intro.setIcon(new ItemStack(BotaEngItems.CELLS.get(ManaTier.T1K)));

        cells = new BotaEngLexiconEntry("cells", category);
        final List<LexiconPage> cellPages = new ArrayList<>();
        cellPages.add(pages.textPage("0"));
        if (!BotaEngRecipes.COMPONENTS.isEmpty()) {
            cellPages.add(pages.runeRecipesPage("1", BotaEngRecipes.COMPONENTS));
        }
        cellPages.add(pages.textPage("2"));
        addCrafting(cellPages, "3", recipes("cells/mana_cell_housing"));
        addCrafting(cellPages, "4", tierRecipes("cells/mana_cell_%s_housing", BotaEngItems.CELLS.keySet()));
        addCrafting(cellPages, "5", tierRecipes("cells/mana_cell_%s", BotaEngItems.CELLS.keySet()));
        cells.setLexiconPages(cellPages.toArray(new LexiconPage[0]));

        portableCells = new BotaEngLexiconEntry("portableCells", category);
        final List<LexiconPage> portablePages = new ArrayList<>();
        portablePages.add(pages.textPage("0"));
        portablePages.add(pages.textPage("1"));
        addCrafting(portablePages, "2",
                tierRecipes("cells/portable_mana_cell_%s", BotaEngItems.PORTABLE_CELLS.keySet()));
        portableCells.setLexiconPages(portablePages.toArray(new LexiconPage[0]));

        // Elementium and pixie dust go into it, so it waits for Alfheim like Botania's elven entries.
        fluixPool = new BotaEngLexiconEntry("fluixPool", category).setKnowledgeType(BotaniaAPI.elvenKnowledge);
        final List<LexiconPage> poolPages = new ArrayList<>();
        poolPages.add(pages.textPage("0"));
        poolPages.add(pages.textPage("1"));
        addCrafting(poolPages, "2", recipes("blocks/fluix_mana_pool"));
        fluixPool.setLexiconPages(poolPages.toArray(new LexiconPage[0]));

        buffer = new BotaEngLexiconEntry("buffer", category);
        final List<LexiconPage> bufferPages = new ArrayList<>();
        bufferPages.add(pages.textPage("0"));
        addCrafting(bufferPages, "1", recipes("blocks/mana_buffer"));
        buffer.setLexiconPages(bufferPages.toArray(new LexiconPage[0]));

        burstTunnel = new BotaEngLexiconEntry("burstTunnel", category);
        burstTunnel.setLexiconPages(pages.textPage("0"), pages.textPage("1"));
        final ItemStack tunnel = new ItemStack(BotaEngItems.MANA_P2P);
        burstTunnel.setIcon(tunnel);
        // No recipe page maps it: it is attuned, not crafted.
        LexiconRecipeMappings.map(tunnel, burstTunnel, 0);

        wirelessMana = new BotaEngLexiconEntry("wirelessMana", category);
        wirelessMana.setLexiconPages(pages.textPage("0"));
        AEApi.instance().definitions().items().wirelessTerminal().maybeStack(1).ifPresent(wirelessMana::setIcon);
    }

    /** A page cycling through the recipes, or none when a pack has removed them all. */
    private static void addCrafting(final List<LexiconPage> pages, final String name,
            final List<ResourceLocation> recipes) {
        if (!recipes.isEmpty()) {
            pages.add(BotaniaAPI.internalHandler.craftingRecipesPage(name, recipes));
        }
    }

    /** Each tier's recipe of this pattern that is loaded. */
    private static List<ResourceLocation> tierRecipes(final String pattern, final Collection<ManaTier> tiers) {
        return recipes(tiers.stream().map(tier -> String.format(pattern, tier.name)).toArray(String[]::new));
    }

    /** Those of these JSON recipes that are loaded; a condition may have left some out. */
    private static List<ResourceLocation> recipes(final String... paths) {
        final List<ResourceLocation> found = new ArrayList<>();
        for (final String path : paths) {
            final ResourceLocation recipe = BotaEng.id(path);
            if (ForgeRegistries.RECIPES.containsKey(recipe)) {
                found.add(recipe);
            }
        }
        return found;
    }
}
