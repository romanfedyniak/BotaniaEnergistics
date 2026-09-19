package com.botaeng.lexicon;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.IAddonEntry;
import vazkii.botania.api.lexicon.ITwoNamedPage;
import vazkii.botania.api.lexicon.LexiconCategory;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconPage;

/**
 * An entry named as Botania names its own, under this mod's keys: botaeng.entry.name, botaeng.tagline.name and
 * botaeng.page.name0 onwards.
 */
public class BotaEngLexiconEntry extends LexiconEntry implements IAddonEntry {

    public BotaEngLexiconEntry(final String name, final LexiconCategory category) {
        super(name, category);
        BotaniaAPI.addEntry(this, category);
    }

    @Override
    public LexiconEntry setLexiconPages(final LexiconPage... pages) {
        final String prefix = "botaeng.page." + this.unlocalizedName;
        for (final LexiconPage page : pages) {
            page.unlocalizedName = prefix + page.unlocalizedName;
            if (page instanceof ITwoNamedPage twoNamed) {
                twoNamed.setSecondUnlocalizedName(prefix + twoNamed.getSecondUnlocalizedName());
            }
        }
        return super.setLexiconPages(pages);
    }

    @Override
    public String getUnlocalizedName() {
        return "botaeng.entry." + this.unlocalizedName;
    }

    @Override
    public String getTagline() {
        return "botaeng.tagline." + this.unlocalizedName;
    }

    @Override
    public String getSubtitle() {
        return "[Botania Energistics]";
    }
}
