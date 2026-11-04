package com.fubangkun.srpgc.dimension;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.fubangkun.srpgc.config.ConfigManagerSRPGC;
import com.fubangkun.srpgc.planet.PlanetXenosis;
import com.fubangkun.srpgc.world.BiomeProviderXenosis;
import com.fubangkun.srpgc.world.ChunkProviderXenosis;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.ISolarLevel;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public class WorldProviderXenosis extends WorldProviderSpace implements ISolarLevel {

    private double solarMultiplier = -1.0D;

    @Override
    public CelestialBody getCelestialBody() {
        return PlanetXenosis.PLANET;
    }

    @Override
    @Nonnull
    public DimensionType getDimensionType() {
        return XenosisDimensions.getType();
    }

    @Override
    public Class<? extends IChunkGenerator> getChunkProviderClass() {
        return ChunkProviderXenosis.class;
    }

    @Override
    public Class<? extends BiomeProvider> getBiomeProviderClass() {
        return BiomeProviderXenosis.class;
    }

    @Override
    public Vector3 getFogColor() {
        return new Vector3(0.14F, 0.02F, 0.03F);
    }

    @Override
    public Vector3 getSkyColor() {
        return new Vector3(0.20F, 0.04F, 0.06F);
    }

    @Override
    public boolean hasSunset() {
        return false;
    }

    @Override
    public long getDayLength() {
        return 24000L;
    }

    @Override
    public double getHorizon() {
        return 63.0D;
    }

    @Override
    public int getAverageGroundLevel() {
        return 64;
    }

    @Override
    public boolean canCoordinateBeSpawn(int x, int z) {
        return true;
    }

    @Override
    public float getGravity() {
        // GC subtracts this from 0.08; zero keeps Overworld gravity.
        return 0.0F;
    }

    @Override
    public float getArrowGravity() {
        return 0.05F;
    }

    @Override
    public double getFuelUsageMultiplier() {
        return 1.0D;
    }

    @Override
    public boolean canSpaceshipTierPass(int tier) {
        return tier >= ConfigManagerSRPGC.tier;
    }

    @Override
    public float getFallDamageModifier() {
        return 1.0F;
    }

    @Override
    public int getDungeonSpacing() {
        return 0;
    }

    @Override
    public ResourceLocation getDungeonChestType() {
        return LootTableList.CHESTS_SIMPLE_DUNGEON;
    }

    @Override
    public List<Block> getSurfaceBlocks() {
        List<Block> blocks = new LinkedList<>();
        blocks.add(SRPBlocks.ParasiteStain);
        blocks.add(SRPBlocks.ParasiteRubble);
        blocks.add(SRPBlocks.ParasiteRubbleDense);
        blocks.add(SRPBlocks.HarlequinnGrass);
        blocks.add(SRPBlocks.HarleskinnBlock);
        return blocks;
    }

    @Override
    public double getSolarEnergyMultiplier() {
        if (this.solarMultiplier < 0.0D) {
            double solarSize = this.getSolarSize();
            this.solarMultiplier = solarSize * solarSize * solarSize;
        }
        return this.solarMultiplier;
    }
}
