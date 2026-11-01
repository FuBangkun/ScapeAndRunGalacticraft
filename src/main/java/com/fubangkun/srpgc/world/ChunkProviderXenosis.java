package com.fubangkun.srpgc.world;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import java.util.Collections;
import java.util.List;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.ChunkProviderSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;

import javax.annotation.Nonnull;

@SuppressWarnings("deprecation")
public class ChunkProviderXenosis extends ChunkProviderSpace {

    private final BiomeDecoratorXenosis biomeDecorator = new BiomeDecoratorXenosis();

    public ChunkProviderXenosis(World world, long seed, boolean mapFeaturesEnabled) {
        super(world, seed, mapFeaturesEnabled);
    }

    @Override
    protected BiomeDecoratorSpace getBiomeGenerator() {
        return this.biomeDecorator;
    }

    @Override
    protected Biome[] getBiomesForGeneration() {
        return new Biome[]{BiomeXenosis.INSTANCE};
    }

    @Override
    protected int getSeaLevel() {
        return 64;
    }

    @Override
    protected List<MapGenBaseMeta> getWorldGenerators() {
        return Collections.emptyList();
    }

    @Override
    protected BlockMetaPair getGrassBlock() {
        return new BlockMetaPair(SRPBlocks.ParasiteStain, (byte) 0);
    }

    @Override
    protected BlockMetaPair getDirtBlock() {
        return new BlockMetaPair(SRPBlocks.ParasiteStain, (byte) 0);
    }

    @Override
    protected BlockMetaPair getStoneBlock() {
        return new BlockMetaPair(SRPBlocks.ParasiteRubble, (byte) 2);
    }

    @Override
    public double getHeightModifier() {
        return 16.0D;
    }

    @Override
    public double getSmallFeatureHeightModifier() {
        return 26.0D;
    }

    @Override
    public double getMountainHeightModifier() {
        return 100.0D;
    }

    @Override
    public double getValleyHeightModifier() {
        return 52.0D;
    }

    @Override
    public int getCraterProbability() {
        return 900;
    }

    @Override
    public void onChunkProvide(int chunkX, int chunkZ, ChunkPrimer primer) {
    }

    @Override
    public void onPopulate(int chunkX, int chunkZ) {
    }

    @Override
    public void recreateStructures(@Nonnull Chunk chunk, int x, int z) {
    }
}
