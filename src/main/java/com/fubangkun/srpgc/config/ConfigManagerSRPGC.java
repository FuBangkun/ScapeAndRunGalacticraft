package com.fubangkun.srpgc.config;

import com.fubangkun.srpgc.SRPGC;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public final class ConfigManagerSRPGC {

    private static final String CATEGORY_GENERAL = "general";
    private static final String CATEGORY_BIOMES = "biomes";
    private static final String CATEGORY_ORIGIN = "origin";
    private static final String CATEGORY_DEBUG = "debug";
    private static final int DEFAULT_DIMENSION_ID = -40;

    public static int dimensionId = DEFAULT_DIMENSION_ID;
    public static int tier = 3;
    public static int startPhase = 4;
    public static boolean injectSRPConfig = true;
    public static int harlequinPercent = 35;
    public static int harlequinRegionChunks = 10;
    public static boolean seedOrigin = true;
    public static int originRadius = 200;
    public static boolean debugLogging = false;

    private ConfigManagerSRPGC() {
    }

    public static void load(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        dimensionId = validateDimensionId(config.getInt("dimensionId", CATEGORY_GENERAL, DEFAULT_DIMENSION_ID, -1000000, -2,
                "Dimension id of Xenosis. Must be negative and must not collide with another mod."));
        tier = clamp(config.getInt("tier", CATEGORY_GENERAL, 3, 1, 3,
                "Rocket tier required to reach Xenosis."), 1, 3);
        startPhase = clamp(config.getInt("startPhase", CATEGORY_GENERAL, 4, 0, 10,
                "SRP evolution phase used by the Xenosis spawn table."), 0, 10);
        injectSRPConfig = config.getBoolean("injectSRPConfig", CATEGORY_GENERAL, true,
                "Inject the dimension and biome entries Xenosis needs into SRP's in-memory configuration.");
        harlequinPercent = clamp(config.getInt("harlequinPercent", CATEGORY_BIOMES, 35, 0, 100,
                "Approximate share of the Xenosis surface covered by the Harlequin parasite biome variant. Set 0 for a single biome planet."), 0, 100);
        harlequinRegionChunks = clamp(config.getInt("harlequinRegionChunks", CATEGORY_BIOMES, 10, 1, 64,
                "Side length in chunks of one Harlequin region cell. Larger cells make fewer, bigger patches."), 1, 64);
        seedOrigin = config.getBoolean("seedOrigin", CATEGORY_ORIGIN, true,
                "Seed one SRP emerging infestation vector (origin) at the Xenosis world spawn.");
        originRadius = clamp(config.getInt("originRadius", CATEGORY_ORIGIN, 200, 1, 100000,
                "Initial radius of the seeded Xenosis origin."), 1, 100000);
        debugLogging = config.getBoolean("debugLogging", CATEGORY_DEBUG, false,
                "Log SRP injection and origin seeding details.");

        config.save();

        SRPGC.LOGGER.info("Loaded configuration: dimensionId={}, tier={}, startPhase={}, injectSRPConfig={}, harlequinPercent={}, seedOrigin={}, originRadius={}",
                dimensionId, tier, startPhase, injectSRPConfig, harlequinPercent, seedOrigin, originRadius);
    }

    public static void debug(String message, Object... args) {
        if (debugLogging) {
            SRPGC.LOGGER.info(message, args);
        }
    }

    private static int validateDimensionId(int configuredId) {
        if (configuredId == -1 || configuredId >= 0) {
            SRPGC.LOGGER.error("Invalid Xenosis dimension id {} (must be below -1), using {}", configuredId, DEFAULT_DIMENSION_ID);
            return DEFAULT_DIMENSION_ID;
        }
        if (configuredId >= -31 && configuredId <= -26) {
            SRPGC.LOGGER.error("Xenosis dimension id {} collides with Galacticraft's reserved range -31..-26, using {}",
                    configuredId, DEFAULT_DIMENSION_ID);
            return DEFAULT_DIMENSION_ID;
        }
        return configuredId;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
