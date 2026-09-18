package com.botaeng.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackImportStrategy;
import appeng.api.stacks.AEKeyType;

import com.botaeng.me.ManaKeyType;
import com.botaeng.me.strategy.ManaExportStrategy;
import com.botaeng.me.strategy.ManaImportStrategy;
import com.botaeng.me.strategy.ManaStorageAdapter;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
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
    }

    public void postInit(FMLPostInitializationEvent event) {
    }

}
