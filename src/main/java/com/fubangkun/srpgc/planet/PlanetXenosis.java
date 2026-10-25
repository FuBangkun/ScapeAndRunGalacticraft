package com.fubangkun.srpgc.planet;

import com.fubangkun.srpgc.Reference;
import com.fubangkun.srpgc.SRPGC;
import com.fubangkun.srpgc.config.ConfigManagerSRPGC;
import com.fubangkun.srpgc.dimension.TeleportTypeXenosis;
import com.fubangkun.srpgc.dimension.WorldProviderXenosis;
import com.fubangkun.srpgc.dimension.XenosisDimensions;
import com.fubangkun.srpgc.world.BiomeXenosis;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.world.AtmosphereInfo;
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;

public final class PlanetXenosis {

    public static final String NAME = "xenosis";
    public static Planet PLANET;

    private PlanetXenosis() {
    }

    public static void preInit() {
        Planet planet = new Planet(NAME);
        planet.setParentSolarSystem(GalacticraftCore.solarSystemSol);
        planet.setRingColorRGB(0.35F, 0.05F, 0.08F);
        planet.setPhaseShift(0.42F);
        planet.setRelativeSize(0.48F);
        planet.setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(1.7F, 1.7F));
        planet.setRelativeOrbitTime(2.6F);
        PLANET = planet;
    }

    public static void init() {
        if (PLANET == null) {
            return;
        }

        BiomeXenosis biome = BiomeXenosis.INSTANCE;
        if (biome == null) {
            SRPGC.LOGGER.error("Biome {} was not registered; Xenosis will not be registered.", BiomeXenosis.REGISTRY_NAME);
            return;
        }

        int biomeId = Biome.getIdForBiome(biome);
        if (biomeId < 0 || biomeId > 255) {
            SRPGC.LOGGER.error("Biome {} has invalid numeric id {}; Xenosis will not be registered.", BiomeXenosis.REGISTRY_NAME, biomeId);
            return;
        }

        PLANET.setBiomeInfo(biome);
        PLANET.setDimensionInfo(ConfigManagerSRPGC.dimensionId, WorldProviderXenosis.class);
        PLANET.setTierRequired(ConfigManagerSRPGC.tier);
        // SRP life originated on Earth, so Xenosis is habitable instead of an environmental hazard.
        PLANET.setAtmosphere(new AtmosphereInfo(true, false, false, 0.0F, 1.0F, 1.0F));
        PLANET.atmosphereComponent(EnumAtmosphericGas.NITROGEN);
        PLANET.atmosphereComponent(EnumAtmosphericGas.OXYGEN);
        PLANET.atmosphereComponent(EnumAtmosphericGas.ARGON);
        PLANET.setBodyIcon(new ResourceLocation(Reference.MOD_ID, "textures/gui/celestialbodies/xenosis.png"));

        GalaxyRegistry.register(PLANET);
        GalacticraftRegistry.registerTeleportType(WorldProviderXenosis.class, new TeleportTypeXenosis());
        GalacticraftRegistry.registerRocketGui(WorldProviderXenosis.class,
                new ResourceLocation(Reference.MOD_ID, "textures/gui/xenosis_rocket_gui.png"));

        SRPGC.LOGGER.info("Registered planet {}: dimension={}, biome={} (numeric id {}), tier={}, startPhase={}",
                NAME, ConfigManagerSRPGC.dimensionId, BiomeXenosis.REGISTRY_NAME, biomeId, ConfigManagerSRPGC.tier,
                ConfigManagerSRPGC.startPhase);
    }

    public static void postInit() {
        DimensionType type = XenosisDimensions.findType();
        XenosisDimensions.setType(type);

        if (type == null) {
            if (PLANET != null) {
                PLANET.setUnreachable();
            }
            SRPGC.LOGGER.error("Galacticraft did not register a dimension type for Xenosis (id {}); the planet is unreachable.",
                    ConfigManagerSRPGC.dimensionId);
        } else {
            SRPGC.LOGGER.info("Xenosis dimension type ready: {} (id {}).", type.getName(), type.getId());
        }
    }
}
