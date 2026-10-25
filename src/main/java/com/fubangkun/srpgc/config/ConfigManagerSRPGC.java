package com.fubangkun.srpgc.config;

import com.fubangkun.srpgc.SRPGC;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public final class ConfigManagerSRPGC {

    private static final String CATEGORY_GENERAL = "general";
    private static final String CATEGORY_STRUCTURES = "structures";
    private static final String CATEGORY_DEBUG = "debug";
    private static final int DEFAULT_DIMENSION_ID = -40;

    public static int dimensionId = DEFAULT_DIMENSION_ID;
    public static int tier = 3;
    public static int startPhase = 4;
    public static boolean injectSRPConfig = true;
    public static boolean seedStructures = true;
    public static int maxNodes = 6;
    public static int maxColonies = 3;
    public static boolean starterNode = true;
    public static int nodeCellChunks = 800;
    public static int colonyCellChunks = 160;
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
                "SRP evolution phase Xenosis starts at."), 0, 10);
        injectSRPConfig = config.getBoolean("injectSRPConfig", CATEGORY_GENERAL, true,
                "Inject the dimension and biome entries Xenosis needs into SRP's in-memory configuration.");
        seedStructures = config.getBoolean("seedStructures", CATEGORY_STRUCTURES, true,
                "Seed SRP nodes and colonies while Xenosis chunks are populated.");
        maxNodes = clamp(config.getInt("maxNodes", CATEGORY_STRUCTURES, 6, 1, 20,
                "Maximum number of seeded SRP nodes in Xenosis."), 1, 20);
        maxColonies = clamp(config.getInt("maxColonies", CATEGORY_STRUCTURES, 3, 1, 20,
                "Maximum number of seeded SRP colonies in Xenosis."), 1, 20);
        starterNode = config.getBoolean("starterNode", CATEGORY_STRUCTURES, true,
                "Guarantee one SRP node and one colony near the Xenosis world spawn.");
        nodeCellChunks = clamp(config.getInt("nodeCellChunks", CATEGORY_STRUCTURES, 800, 1, 100000,
                "Side length in chunks of one node placement cell."), 1, 100000);
        colonyCellChunks = clamp(config.getInt("colonyCellChunks", CATEGORY_STRUCTURES, 160, 1, 100000,
                "Side length in chunks of one colony placement cell."), 1, 100000);
        debugLogging = config.getBoolean("debugLogging", CATEGORY_DEBUG, false,
                "Log SRP injection and structure seeding details.");

        config.save();

        SRPGC.LOGGER.info("Loaded configuration: dimensionId={}, tier={}, startPhase={}, injectSRPConfig={}, seedStructures={}",
                dimensionId, tier, startPhase, injectSRPConfig, seedStructures);
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
