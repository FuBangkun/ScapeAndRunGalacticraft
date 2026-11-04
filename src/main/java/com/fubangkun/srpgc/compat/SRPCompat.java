package com.fubangkun.srpgc.compat;

import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.fubangkun.srpgc.Reference;
import com.fubangkun.srpgc.SRPGC;
import com.fubangkun.srpgc.config.ConfigManagerSRPGC;
import com.fubangkun.srpgc.world.BiomeXenosis;
import com.fubangkun.srpgc.world.BiomeXenosisHarlequin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SRPCompat {

    public static final String[] BIOME_IDS = {BiomeXenosis.REGISTRY_NAME, BiomeXenosisHarlequin.REGISTRY_NAME};

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
            SRPGC.LOGGER.warn("SRP's biome blacklist is inverted, so {} cannot be added without making them the only convertible biomes. Skipping injection.",
                    Arrays.toString(BIOME_IDS));
            return;
        }

        String[] current = SRPConfigWorld.biomeBlackList;
        if (current == null) {
            current = new String[0];
        }

        List<String> missing = new ArrayList<>();
        for (String biomeId : BIOME_IDS) {
            if (excludes(current, biomeId)) {
                missing.add(biomeId);
            }
        }

        if (missing.isEmpty()) {
            SRPGC.LOGGER.info("SRP biome blacklist already contains {}.", Arrays.toString(BIOME_IDS));
            return;
        }

        String[] updated = Arrays.copyOf(current, current.length + missing.size());
        for (int i = 0; i < missing.size(); i++) {
            updated[current.length + i] = missing.get(i);
        }
        SRPConfigWorld.biomeBlackList = updated;
        SRPGC.LOGGER.info("Injected {} into SRP biome blacklist; list is now {}.", missing, Arrays.toString(updated));
    }

    private static boolean excludes(String[] values, String value) {
        for (String entry : values) {
            if (value.equals(entry)) {
                return false;
            }
        }
        return true;
    }

    private static void logFailure(String target, Exception exception) {
        SRPGC.LOGGER.error("Failed to inject the SRP {}; Xenosis may not behave as designed.", target, exception);
    }

    private static void ensureDimensionEntry(World world) {
        if (!ConfigManagerSRPGC.injectSRPConfig || world.isRemote) {
            return;
        }

        try {
            int dimId = ConfigManagerSRPGC.dimensionId;
            SRPSaveData data = SRPSaveData.get(world, dimId);
            if (data == null) {
                SRPGC.LOGGER.warn("SRP save data is unavailable; skipping Xenosis SRP setup.");
                return;
            }
            if (data == lastCheckedData) {
                return;
            }

            try {
                if (!hasDimensionEntry(data, dimId)) {
                    addDimension(data, dimId);
                    data.setEvolutionPhase(dimId, (byte) ConfigManagerSRPGC.startPhase, true, world);
                }
            } catch (ReflectiveOperationException e) {
                SRPGC.LOGGER.warn("Could not inspect SRP's dimension list; falling back to the phase-0 check.", e);
                addDimension(data, dimId);
                if (data.getEvolutionPhase(dimId) == 0 && ConfigManagerSRPGC.startPhase != 0) {
                    data.setEvolutionPhase(dimId, (byte) ConfigManagerSRPGC.startPhase, true, world);
                }
            }

            lastCheckedData = data;
            SRPGC.LOGGER.info("Xenosis SRP evolution phase is {} (configured start phase: {}).",
                    data.getEvolutionPhase(dimId), ConfigManagerSRPGC.startPhase);
            checkBiomeBlacklist();
        } catch (Exception e) {
            SRPGC.LOGGER.error("Failed to prepare Xenosis SRP save data.", e);
        }
    }

    private static void ensureOriginSeed(EntityPlayer player) {
        if (!ConfigManagerSRPGC.seedOrigin) {
            return;
        }
        if (player == null || player.world == null || player.world.isRemote) {
            return;
        }
        if (!SRPConfigWorld.originActivated) {
            SRPGC.LOGGER.warn("SRP's origin system is disabled; Xenosis will not spawn parasites.");
            return;
        }

        World world = player.world;
        SRPWorldData data = SRPWorldData.get(world);
        if (data == null) {
            return;
        }
        ConfigManagerSRPGC.debug("Xenosis origin seed check: dimension {}, existing origins {}.",
                world.provider.getDimension(), data.getorigins("x").size());
        if (!data.getorigins("x").isEmpty()) {
            return;
        }

        int x = MathHelper.floor(player.posX);
        int z = MathHelper.floor(player.posZ);
        BlockPos pos = world.getHeight(new BlockPos(x, 0, z));
        int result = ParasiteEventWorld.placeOriginInWorld(world, pos, SRPConfigWorld.originHealth,
                ConfigManagerSRPGC.originRadius);
        if (result == 1 || result == 2) {
            SRPGC.LOGGER.info("Seeded Xenosis EIV origin near ({}, {}, {}) with radius {} (result {}).",
                    pos.getX(), pos.getY(), pos.getZ(), ConfigManagerSRPGC.originRadius, result);
        } else {
            SRPGC.LOGGER.warn("Could not seed Xenosis EIV origin near ({}, {}, {}): result {}.",
                    pos.getX(), pos.getY(), pos.getZ(), result);
        }
    }

    private static boolean hasDimensionEntry(SRPSaveData data, int dimId) throws ReflectiveOperationException {
        Field field = SRPSaveData.class.getDeclaredField("dimEPid");
        field.setAccessible(true);
        List<?> ids = (List<?>) field.get(data);
        return ids != null && ids.contains(dimId);
    }

    private static void addDimension(SRPSaveData data, int dimId) throws ReflectiveOperationException {
        Method method = SRPSaveData.class.getDeclaredMethod("addDim", int.class);
        method.setAccessible(true);
        method.invoke(data, dimId);
    }

    private static void checkBiomeBlacklist() {
        if (SRPConfigWorld.biomeBlackListInverted) {
            SRPGC.LOGGER.warn("SRP's biome blacklist is inverted; {} are not protected from biome spread.",
                    Arrays.toString(BIOME_IDS));
            return;
        }

        String[] blacklist = SRPConfigWorld.biomeBlackList;
        for (String biomeId : BIOME_IDS) {
            if (blacklist == null || excludes(blacklist, biomeId)) {
                SRPGC.LOGGER.warn("SRP's biome blacklist no longer contains {}; node spread may replace that Xenosis biome.", biomeId);
            }
        }
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

        @SubscribeEvent
        public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
            if (event.player.world.isRemote || event.toDim != ConfigManagerSRPGC.dimensionId) {
                return;
            }
            ensureDimensionEntry(event.player.world);
            ensureOriginSeed(event.player);
        }

        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.player.world.isRemote || event.player.dimension != ConfigManagerSRPGC.dimensionId) {
                return;
            }
            ensureDimensionEntry(event.player.world);
            ensureOriginSeed(event.player);
        }
    }
}
