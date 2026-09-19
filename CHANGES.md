# Changelog

All notable Botania Energistics changes are grouped by the version in which they first appeared.

## Important compatibility notice

- Botania Energistics runs only with AE2 Unofficial Deconstructed, not with standard AE2 or AE2 Unofficial
  Extended Life.
- Back up the world before installing or updating the mod.

## Unreleased

- **The mod loads next to AE2UD and Botania r1.10-364.** It adds nothing yet.
- **Mana is a kind of content an ME network holds.** It is registered with AE2UD the way fluids are, so the
  terminals list it, the key-type picker offers it and it is drawn as the surface of a mana pool. A byte of a
  storage cell holds 1000 mana, which makes a 1k cell about one pool, and a machine operation moves 1000.
  Amounts are counted one mana at a time; a tooltip also says how many pools that is. The idea of mana as a
  key type is [Applied Botanics](https://github.com/ramidzkh/Applied-Botanics)'.
- **The buses and storage buses of AE2UD move mana.** An export bus, or an interface pushing a pattern, fills
  any Botania block that takes mana - a pool, a spreader, a runic altar, a terrestrial agglomeration plate. An
  import bus draws only from a pool, the one block Botania lets mana out of, and a creative pool gives without
  end, as it does to a spreader. A storage bus mounts a pool as storage both ways and any other such block as
  a place mana can only be put. Botania does not say how much a block took, so the change in its mana is
  measured and the network gives exactly that; a mana distributor, which keeps nothing and passes mana on, is
  left alone because what it took cannot be measured. An interface slot holds a pool's worth.
- **Mana storage components, 1k to 16384k, are made on a runic altar.** Mana cells will be built around these
  and not around AE2UD's own components, so the materials of Botania matter to them. Each tier takes three of
  the tier below, an AE2 processor and the material of its stage of Botania: manasteel and mana powder, mana
  pearls, mana diamonds, elementium and pixie dust, dragonstone, terrasteel, Gaia spirit and Gaia spirit
  ingots, for 10,000 to 1,000,000 mana. The tiers from 256k follow AE2UD's switch for its own large cells.
- **Mana storage cells, 1k to 16384k.** A mana cell is a mana component in a mana cell housing, made of mana
  glass, mana powder and manasteel; it is crafted whole, or from a housing and a component, and a cell taken
  apart gives both back. It holds one type - there is only one mana - and 1000 mana a byte, so a 1k cell holds
  1,016,000 and a 16384k one about 16.6 billion. Bytes per type and idle drain are those of AE2UD's fluid cells
  of the same size. It takes an inverter, a sticky and a void card; equal distribution has nothing to divide.
- **A creative mana cell.** It hands out mana without end and swallows whatever is put in, like an AE2UD
  creative cell given mana, but as an item of its own with no recipe, so a pack can make it a reward.
