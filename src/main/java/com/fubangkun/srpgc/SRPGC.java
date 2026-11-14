package com.fubangkun.srpgc;

import com.fubangkun.srpgc.compat.SRPCompat;
import com.fubangkun.srpgc.config.ConfigManagerSRPGC;
import com.fubangkun.srpgc.planet.PlanetXenosis;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "required-after:galacticraftcore;required-after:srparasites")
public class SRPGC {
    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigManagerSRPGC.load(event);
        SRPCompat.injectConfigs();
        PlanetXenosis.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        PlanetXenosis.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PlanetXenosis.postInit();
    }
}
