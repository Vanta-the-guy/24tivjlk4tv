package com.seedxray;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SeedXrayMod.MOD_ID)
public class SeedXrayMod {

    public static final String MOD_ID = "seed_xray";

    public SeedXrayMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(SeedXrayClientEvents::onRegisterKeyMappings);

        MinecraftForge.EVENT_BUS.register(SeedXrayClientEvents.class);
    }
}
