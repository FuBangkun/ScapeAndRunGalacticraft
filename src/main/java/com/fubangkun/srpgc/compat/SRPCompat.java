package com.fubangkun.srpgc.compat;

import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.fubangkun.srpgc.Reference;
import com.fubangkun.srpgc.SRPGC;
import com.fubangkun.srpgc.config.ConfigManagerSRPGC;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public final class SRPCompat {

    public static final String BIOME_ID = Reference.MOD_ID + ":parasite_planet";

    private static SRPSaveData lastCheckedData;

    private SRPCompat() {
    }

    public static void injectConfigs() {
        if (!ConfigManagerSRPGC.injectSRPConfig) {
            SRPGC.LOGGER.info("SRP configuration injection is disabled.");
            return;
        }

        int dimId = ConfigManagerSRPGC.dimensionId;
        try {
            injectEvolutionDimension(dimId);
        } catch (Exception e) {
            logFailure("evolution dimension entry", e);
        }
        try {
            injectNodeDimension(dimId);
        } catch (Exception e) {
            logFailure("node dimension whitelist", e);
        }
        try {
            injectColonyDimension(dimId);
        } catch (Exception e) {
            logFailure("colony dimension whitelist", e);
        }
        try {
            injectBiomeBlacklist();
        } catch (Exception e) {
            logFailure("biome blacklist entry", e);
        }

        if (!SRPConfigSystems.useEvolution) {
            SRPGC.LOGGER.warn("SRP evolution is disabled, so startPhase will not affect Xenosis spawns.");
        }
    }

    private static void injectEvolutionDimension(int dimId) {
        String[] current = SRPConfigSystems.evolutionDimStart;
        if (current == null) {
            current = new String[0];
        }

        for (String value : current) {
            if (value == null) {
                continue;
            }
            String[] parts = value.split(";", 2);
            if (parts.length > 0 && parts[0].trim().equals(Integer.toString(dimId))) {
                SRPGC.LOGGER.info("SRP evolution list already contains dimension {} as \"{}\"; leaving it unchanged.", dimId, value);
                return;
            }
        }

        String entry = dimId + ";" + ConfigManagerSRPGC.startPhase + ";0";
        String[] updated = Arrays.copyOf(current, current.length + 1);
        updated[current.length] = entry;
        SRPConfigSystems.evolutionDimStart = updated;
        SRPGC.LOGGER.info("Injected SRP evolution entry \"{}\"; list is now {}.", entry, Arrays.toString(updated));
    }

    private static void injectNodeDimension(int dimId) {
        int[] current = SRPConfigWorld.blackListedDimensionsNodes;
        if (current == null) {
            current = new int[0];
        }

        for (int value : current) {
            if (value == dimId) {
                SRPGC.LOGGER.info("SRP node dimension whitelist already contains {}.", dimId);
                return;
            }
        }

        int[] updated = Arrays.copyOf(current, current.length + 1);
        updated[current.length] = dimId;
        SRPConfigWorld.blackListedDimensionsNodes = updated;
        SRPGC.LOGGER.info("Injected dimension {} into SRP node whitelist; list is now {}.", dimId, Arrays.toString(updated));
    }

    private static void injectColonyDimension(int dimId) {
        int[] current = SRPConfigWorld.blackListedDimensionsColonies;
        if (current == null) {
            current = new int[0];
        }

        for (int value : current) {
            if (value == dimId) {
                SRPGC.LOGGER.info("SRP colony dimension whitelist already contains {}.", dimId);
                return;
            }
        }

        int[] updated = Arrays.copyOf(current, current.length + 1);
        updated[current.length] = dimId;
        SRPConfigWorld.blackListedDimensionsColonies = updated;
        SRPGC.LOGGER.info("Injected dimension {} into SRP colony whitelist; list is now {}.", dimId, Arrays.toString(updated));
    }

    private static void injectBiomeBlacklist() {
        if (SRPConfigWorld.biomeBlackListInverted) {
            SRPGC.LOGGER.warn("SRP's biome blacklist is inverted, so {} cannot be added without making it the only convertible biome. Skipping injection.",
                    BIOME_ID);
            return;
        }

        String[] current = SRPConfigWorld.biomeBlackList;
        if (current == null) {
            current = new String[0];
        }

        for (String value : current) {
            if (BIOME_ID.equals(value)) {
                SRPGC.LOGGER.info("SRP biome blacklist already contains {}.", BIOME_ID);
                return;
            }
        }

        String[] updated = Arrays.copyOf(current, current.length + 1);
        updated[current.length] = BIOME_ID;
        SRPConfigWorld.biomeBlackList = updated;
        SRPGC.LOGGER.info("Injected {} into SRP biome blacklist; list is now {}.", BIOME_ID, Arrays.toString(updated));
    }

    private static void logFailure(String target, Exception exception) {
        SRPGC.LOGGER.error("Failed to inject the SRP {}; Xenosis may not behave as designed.", target, exception);
    }

    private static void ensureDimensionEntry(World world) {
        if (!ConfigManagerSRPGC.injectSRPConfig) {
            return;
        }

        try {
            SRPSaveData data = SRPSaveData.get(world);
            if (data == lastCheckedData) {
                return;
            }

            int dimId = ConfigManagerSRPGC.dimensionId;
            try {
                if (!hasDimensionEntry(data, dimId)) {
                    addDimension(dimId);
                    data.setEvolutionPhase(dimId, (byte) ConfigManagerSRPGC.startPhase, true, world, true);
                }
            } catch (ReflectiveOperationException e) {
                SRPGC.LOGGER.warn("Could not inspect SRP's dimension list; falling back to the phase-0 check.", e);
                addDimension(dimId);
                if (data.getEvolutionPhase(dimId) == 0 && ConfigManagerSRPGC.startPhase != 0) {
                    data.setEvolutionPhase(dimId, (byte) ConfigManagerSRPGC.startPhase, true, world, true);
                }
            }

            lastCheckedData = data;
            SRPGC.LOGGER.info("Xenosis SRP evolution phase is {} (configured start phase: {}).",
                    data.getEvolutionPhase(dimId), ConfigManagerSRPGC.startPhase);
            checkBiomeBlacklist();
        } catch (Exception e) {
            SRPGC.LOGGER.error("Failed to add Xenosis to existing SRP save data; its evolution phase may stay at 0.", e);
        }
    }

    private static boolean hasDimensionEntry(SRPSaveData data, int dimId) throws ReflectiveOperationException {
        Field field = SRPSaveData.class.getDeclaredField("dimEPid");
        field.setAccessible(true);
        List<?> ids = (List<?>) field.get(data);
        return ids != null && ids.contains(dimId);
    }

    private static void addDimension(int dimId) throws ReflectiveOperationException {
        Method method = SRPSaveData.class.getDeclaredMethod("addDim", int.class);
        method.setAccessible(true);
        method.invoke(null, dimId);
    }

    private static void checkBiomeBlacklist() {
        if (SRPConfigWorld.biomeBlackListInverted) {
            SRPGC.LOGGER.warn("SRP's biome blacklist is inverted; {} is not protected from biome spread.", BIOME_ID);
            return;
        }

        String[] blacklist = SRPConfigWorld.biomeBlackList;
        if (blacklist != null) {
            for (String entry : blacklist) {
                if (BIOME_ID.equals(entry)) {
                    return;
                }
            }
        }
        SRPGC.LOGGER.warn("SRP's biome blacklist no longer contains {}; node spread may replace the Xenosis biome.", BIOME_ID);
    }

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    public static final class EventHandler {

        private EventHandler() {
        }

        @SubscribeEvent
        public static void onWorldLoad(WorldEvent.Load event) {
            World world = event.getWorld();
            if (world.isRemote || world.provider.getDimension() != ConfigManagerSRPGC.dimensionId) {
                return;
            }
            ensureDimensionEntry(world);
        }
    }
}
