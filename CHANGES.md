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
- **An ME Mana Buffer holds a recipe's items and mana for whatever comes next.** Botania's machines take their
  items only when they fall in, so an interface cannot fill a runic altar, a pool or a brewery; the buffer
  gives it somewhere to put the whole recipe instead - 27 slots, as in a chest, enough for a runic altar's
  sixteen inputs and its livingrock, and 10,000,000 mana, ten pools, set by `manaBufferCapacity` in the config.
  It does nothing with them itself: what takes them out and where it takes them is up to the player. Its items
  are an inventory like a chest's from every side, and to Botania it is a mana pool, so a spreader beside it
  draws its mana and shoots it on, and a burst can fill it; the network's buses and storage buses read it as a
  pool too. Right-click opens it like a chest, with the mana shown as a bar. Broken, it drops its items and
  loses its mana, as a pool does. It is crafted from a mana pool, a formation core and seven livingrock.
- **Botania's tools and armour draw mana from an ME network through a wireless terminal.** A player carrying a
  linked wireless terminal in range of an access point spends the network's mana once the tablets, rings and
  portable mana cells they carry are empty - the network is offered to Botania after everything in the
  inventory, though a ring worn as a bauble is still counted after it, since Botania looks at baubles last. It
  only ever takes: nothing a player carries charges into the network. It goes by the rules of opening the
  terminal - linked, charged, in range, and the player allowed to take things out of that network - and each
  network is counted once however many terminals reach it. The terminal's battery pays the network for moving
  the mana, and half an AE more each time mana is drawn. Items on the mana item blacklist are not charged from
  it. Every wireless terminal has a switch for it in its settings drawer, on to begin with, and `wirelessMana`
  in the config turns the whole thing off for a server. Botania's client never counts the network, so the
  mana bar Botania draws for a held tool does not include it.
- **The Lexica Botania has a category for the mod.** Energistics, after Botania's own categories, holds seven
  entries: mana in the network, the storage cells and their components, portable cells, the Fluix Mana Pool, the
  ME Mana Buffer, the burst P2P tunnel and wireless mana, with the recipes of each. The Fluix Mana Pool entry
  waits for elven knowledge, like Botania's own entries made of elementium. The creative mana cell has no entry.
  A Fluix Mana Pool or an ME Mana Buffer opens its entry when a lexicon is used on it, and an item of the mod
  looks itself up in the lexicon as Botania's do.
