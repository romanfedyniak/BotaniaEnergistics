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
- **Portable mana cells, 1k to 16384k, that Botania takes for a mana tablet.** Each is AE2UD's portable cell
  holding mana, made from a chest, a mana component, an energy cell and a mana cell housing, and there for
  every size AE2UD has a portable fluid cell. Carried, its mana is spent by Botania's tools and armour and
  passed to other mana items, and a pool fills or empties it, all without AE energy, as a tablet costs none.
  It holds one type and half what a cell of its size does, so the 1k one is almost exactly a tablet. Botania
  counts mana in an int, so it sees at most about two billion of the 16384k one's eight; the rest shows as it
  is spent. It takes energy and void cards. Portable mana cells are [Applied
  Botanics](https://github.com/ramidzkh/Applied-Botanics)'.
- **Mana items fill and empty against a terminal and a conversion monitor, like a bucket.** A mana tablet, a
  mana ring or a portable mana cell clicked on the mana row, or on a conversion monitor showing mana, takes or
  gives as much as it holds room for; a creative tablet gives without end. Botania does not say how much an
  item took, so the change in its mana is measured. An item Botania marks as never giving mana away is not
  emptied. Items whose worth is the mana put into them are left alone, listed in the config as
  `manaItemBlacklist`: the Terra Shatterer, which levels up as it fills, the Mana Mirror, whose mana is a pool
  anywhere in the world, and ExtraBotany's Master Mana Ring, which holds two billion. A portable mana cell
  neither charges nor draws from them either, or it would charge one as fast as a terminal.
- **The Fluix Mana Pool is a mana pool whose mana is the network's.** It keeps none of its own: what it shows
  is the mana on the ME network it is joined to, and whatever Botania puts in or takes out goes to and from
  that network, costing the network the energy a bus would pay. So a spreader beside it draws on the network,
  functional flowers bound to it run on the network, sparks can fill the network through
  it, items thrown in are infused or charged from the network, and a pool under an elven gateway pylon pays the
  portal from it. It needs a channel; offline it reads empty and full. Botania counts a pool's mana in an int,
  so it shows at most about two billion. The buses and storage buses leave it alone, since they would only move
  the network's mana around in a circle. It is crafted from a mana pool, an engineering processor, two fluix
  glass cables, two elementium ingots, two pixie dust and a fluix crystal. The Fluix Mana Pool is [Botania
  Applie](https://github.com/NNYYOONNIIOO/Botania_Applie)'s.
- **Moving a runic altar, mana pool or brewery recipe from HEI into a pattern brings its mana along.** Botania
  draws the mana a recipe takes as a bar and not as an ingredient, so it was left out; now it goes into the
  processing pattern after the items, as much as the recipe uses - for a brew, as much as the container it is
  brewed into costs. The recipe is looked up in Botania's own lists by what it makes and what it is made of.
- **A P2P tunnel carries mana bursts.** A burst that strikes the face of an input comes out of the face of an
  output whole - with its lens, colour, mana and speed - and flies on in the direction that face looks. With
  several outputs the bursts take turns rather than being split, so a lens does nothing more often than it
  would have. The burst still answers to the spreader that fired it, so the spreader waits for it as for any
  other. A burst that strikes the tunnel anywhere but its face, or any other part of the cable, goes out as
  against a wall. A spreader aims at a tunnel like at a pool, and fires only while an output is online. A
  tunnel is attuned with any Botania lens or any mana spreader. Since Botania only asks blocks, every AE2 cable
  now counts to Botania as a block that takes mana and is full unless a burst tunnel on it has somewhere to
  send the burst - which also keeps a bore lens from breaking cables. The mana P2P tunnel that carries bursts
  is [Applied Botanics](https://github.com/ramidzkh/Applied-Botanics)'.
