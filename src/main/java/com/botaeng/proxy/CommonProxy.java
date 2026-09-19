package com.botaeng.proxy;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import appeng.api.AEApi;
import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackImportStrategy;
import appeng.api.stacks.AEKeyType;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.CardTraits;
import appeng.api.upgrades.IUpgradeRegistry;

import com.botaeng.BotaEngItems;
import com.botaeng.BotaEngRecipes;
import com.botaeng.me.CreativeManaCell;
import com.botaeng.me.ManaKeyType;
import com.botaeng.me.strategy.ManaExportStrategy;
import com.botaeng.me.strategy.ManaImportStrategy;
import com.botaeng.me.strategy.ManaStorageAdapter;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        BotaEngItems.init();
    }

    @SubscribeEvent
    public void onRegisterItems(final RegistryEvent.Register<Item> event) {
        BotaEngItems.ITEMS.values().forEach(event.getRegistry()::register);
    }

    /**
     * Mana joins AE2UD's key types, which is what makes every terminal, bus and cell able to carry it.
     */
    @SubscribeEvent
    public void onRegisterKeyTypes(final RegistryEvent.Register<AEKeyType> event) {
        event.getRegistry().register(ManaKeyType.INSTANCE);
    }

    public void init(FMLInitializationEvent event) {
        // With these three, the buses, storage buses and interfaces AE2UD already ships carry mana.
        StackImportStrategy.register(ManaKeyType.INSTANCE, ManaImportStrategy::create);
        StackExportStrategy.register(ManaKeyType.INSTANCE, ManaExportStrategy::create);
        ExternalStorageStrategy.register(ManaKeyType.INSTANCE, ManaStorageAdapter.Strategy::new);

        // An interface slot holds a pool's worth.
        GenericSlotCapacities.register(ManaKeyType.INSTANCE, ManaKeyType.POOL);
        StorageCells.addCellHandler(new CreativeManaCell.Handler());

        BotaEngRecipes.init();
    }

    public void postInit(FMLPostInitializationEvent event) {
        // The cards AE2UD gives its fluid cells, less equal distribution: there is only one kind of mana.
        final IUpgradeRegistry upgrades = AEApi.instance().registries().upgrades();
        for (final Item cell : BotaEngItems.CELLS.values()) {
            final ItemStack stack = new ItemStack(cell);
            upgrades.addTraitSupport(CardTraits.INVERTER, stack, 1);
            upgrades.addTraitSupport(CardTraits.STICKY, stack, 1);
            upgrades.addTraitSupport(CardTraits.VOID, stack, 1);
        }
        // A portable one keeps the energy cards and charge rate of AE2UD's portable cells.
        for (final Item cell : BotaEngItems.PORTABLE_CELLS.values()) {
            final ItemStack stack = new ItemStack(cell);
            upgrades.addTraitSupport(CardTraits.ENERGY, stack, 2);
            upgrades.addTraitSupport(CardTraits.VOID, stack, 1);
            AEApi.instance().registries().charger().addChargeRate(cell, 800d);
        }
    }

}
