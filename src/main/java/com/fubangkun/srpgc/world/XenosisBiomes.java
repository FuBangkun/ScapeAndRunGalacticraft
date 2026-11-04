package com.fubangkun.srpgc.world;

import com.fubangkun.srpgc.Reference;
import com.fubangkun.srpgc.SRPGC;
import com.fubangkun.srpgc.config.ConfigManagerSRPGC;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class XenosisBiomes {

    public static BiomeXenosis BASE;
    public static BiomeXenosisHarlequin HARLEQUIN;

    private XenosisBiomes() {
    }

    public static Biome biomeAt(long worldSeed, int blockX, int blockZ) {
        return isHarlequin(worldSeed, blockX, blockZ) ? HARLEQUIN : BASE;
    }

    public static boolean isHarlequin(long worldSeed, int blockX, int blockZ) {
        if (HARLEQUIN == null) {
            return false;
        }
        if (ConfigManagerSRPGC.harlequinPercent <= 0) {
            return false;
        }
        if (ConfigManagerSRPGC.harlequinPercent >= 100) {
            return true;
        }

        int cell = ConfigManagerSRPGC.harlequinRegionChunks * 16;
        int cellX = Math.floorDiv(blockX, cell);
        int cellZ = Math.floorDiv(blockZ, cell);
        double tx = smooth(Math.floorMod(blockX, cell) / (double) cell);
        double tz = smooth(Math.floorMod(blockZ, cell) / (double) cell);
        double blend = corner(worldSeed, cellX, cellZ) * (1.0D - tx) * (1.0D - tz)
                + corner(worldSeed, cellX + 1, cellZ) * tx * (1.0D - tz)
                + corner(worldSeed, cellX, cellZ + 1) * (1.0D - tx) * tz
                + corner(worldSeed, cellX + 1, cellZ + 1) * tx * tz;
        return blend >= 0.5D;
    }

    private static double smooth(double t) {
        return t * t * (3.0D - 2.0D * t);
    }

    private static double corner(long worldSeed, int cellX, int cellZ) {
        return hash(worldSeed, cellX, cellZ) * 100.0D < ConfigManagerSRPGC.harlequinPercent ? 1.0D : 0.0D;
    }

    private static double hash(long seed, int cellX, int cellZ) {
        long mixed = seed + (long) cellX * 0x9E3779B97F4A7C15L + (long) cellZ * 0xC2B2AE3D27D4EB4FL;
        mixed ^= mixed >>> 30;
        mixed *= 0xBF58476D1CE4E5B9L;
        mixed ^= mixed >>> 27;
        mixed *= 0x94D049BB133111EBL;
        mixed ^= mixed >>> 31;
        return (double) (mixed >>> 11) / 9007199254740992.0D;
    }

    public static void setUpBiomes() {
        if (BASE != null) {
            BASE.setBlocks();
            BASE.mobListClear();
        }
        if (HARLEQUIN != null) {
            HARLEQUIN.setBlocks();
            HARLEQUIN.mobListClear();
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    public static final class RegistrationHandler {

        private RegistrationHandler() {
        }

        @SubscribeEvent
        public static void onRegisterBiomes(RegistryEvent.Register<Biome> event) {
            BASE = new BiomeXenosis();
            HARLEQUIN = new BiomeXenosisHarlequin();
            event.getRegistry().register(BASE);
            event.getRegistry().register(HARLEQUIN);
            SRPGC.LOGGER.info("Registered biomes {} and {}.", BiomeXenosis.REGISTRY_NAME,
                    BiomeXenosisHarlequin.REGISTRY_NAME);
        }
    }
}
