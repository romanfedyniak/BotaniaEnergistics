package com.botaeng;

import java.util.function.Function;

import appeng.api.definitions.IItemDefinition;
import appeng.api.definitions.IMaterials;

/**
 * The eight sizes of mana storage, from 1k to 16384k, and the Botania material each is made of. Components are
 * made on a runic altar from three of the tier below, an AE2 processor and the materials of that stage of
 * Botania, for mana that grows with the tier.
 */
public enum ManaTier {

    T1K("1k", 1, 10_000, IMaterials::logicProcessor, "ingotManasteel", "ingotManasteel", "powderMana", "powderMana"),
    T4K("4k", 4, 25_000, IMaterials::calcProcessor, "manaPearl", "manaPearl"),
    T16K("16k", 16, 50_000, IMaterials::calcProcessor, "manaDiamond", "manaDiamond"),
    T64K("64k", 64, 100_000, IMaterials::calcProcessor,
            "ingotElvenElementium", "ingotElvenElementium", "elvenPixieDust", "elvenPixieDust"),
    T256K("256k", 256, 200_000, IMaterials::engProcessor, "elvenDragonstone", "elvenDragonstone"),
    T1024K("1024k", 1024, 400_000, IMaterials::engProcessor, "ingotTerrasteel", "ingotTerrasteel"),
    T4096K("4096k", 4096, 700_000, IMaterials::engProcessor, "eternalLifeEssence", "eternalLifeEssence"),
    T16384K("16384k", 16384, 1_000_000, IMaterials::engProcessor, "gaiaIngot", "gaiaIngot");

    public final String name;
    public final int kilobytes;
    public final int altarMana;
    public final Function<IMaterials, IItemDefinition> processor;
    public final String[] materials;

    ManaTier(final String name, final int kilobytes, final int altarMana,
            final Function<IMaterials, IItemDefinition> processor, final String... materials) {
        this.name = name;
        this.kilobytes = kilobytes;
        this.altarMana = altarMana;
        this.processor = processor;
        this.materials = materials;
    }

    /** The larger tiers follow AE2UD's high capacity switch, as its own cells do. */
    public boolean isHighCapacity() {
        return this.kilobytes >= 256;
    }
}
