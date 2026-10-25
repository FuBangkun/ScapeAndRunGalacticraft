package com.fubangkun.srpgc;

import com.fubangkun.srpgc.compat.SRPCompat;
import com.fubangkun.srpgc.config.ConfigManagerSRPGC;
import com.fubangkun.srpgc.planet.PlanetXenosis;
import com.fubangkun.srpgc.proxy.IProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "required-after:galacticraftcore;required-after:srparasites")
public class SRPGC {

    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_NAME);

    @SidedProxy(modId = Reference.MOD_ID, clientSide = "com.fubangkun.srpgc.proxy.ClientProxy", serverSide = "com.fubangkun.srpgc.proxy.CommonProxy")
    public static IProxy proxy;

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
