package com.fubangkun.srpgc.world;

import com.dhanantry.scapeandrunparasites.world.biome.BiomeInfestedPlain;
import com.fubangkun.srpgc.Reference;
import com.fubangkun.srpgc.SRPGC;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BiomeXenosis extends BiomeInfestedPlain {

    public static final String REGISTRY_NAME = Reference.MOD_ID + ":parasite_planet";
    public static BiomeXenosis INSTANCE;

    public BiomeXenosis() {
        this.setRegistryName(REGISTRY_NAME);
        this.mobListClear();
    }

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    public static final class RegistrationHandler {

        private RegistrationHandler() {
        }

        @SubscribeEvent
        public static void onRegisterBiomes(RegistryEvent.Register<Biome> event) {
            INSTANCE = new BiomeXenosis();
            event.getRegistry().register(INSTANCE);
            SRPGC.LOGGER.info("Registered biome {}.", REGISTRY_NAME);
        }
    }
}
