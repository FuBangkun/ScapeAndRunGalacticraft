package com.fubangkun.srpgc.world;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.ChunkProviderSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public class ChunkProviderXenosis extends ChunkProviderSpace {

    private static final IBlockState HARLEQUIN_SURFACE = SRPBlocks.HarlequinnGrass.getDefaultState();
    private static final IBlockState HARLEQUIN_SUBSURFACE = SRPBlocks.HarleskinnBlock.getDefaultState();

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
        return new Biome[]{XenosisBiomes.BASE, XenosisBiomes.HARLEQUIN};
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
        long seed = this.world.getSeed();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (!XenosisBiomes.isHarlequin(seed, chunkX * 16 + x, chunkZ * 16 + z)) {
                    continue;
                }
                this.replaceSurface(primer, x, z);
            }
        }
    }

    private void replaceSurface(ChunkPrimer primer, int x, int z) {
        boolean surfacePlaced = false;
        for (int y = 254; y >= 0; y--) {
            Block block = primer.getBlockState(x, y, z).getBlock();
            if (block == Blocks.AIR) {
                if (surfacePlaced) {
                    return;
                }
                continue;
            }
            if (block != SRPBlocks.ParasiteStain) {
                return;
            }
            primer.setBlockState(x, y, z, surfacePlaced ? HARLEQUIN_SUBSURFACE : HARLEQUIN_SURFACE);
            surfacePlaced = true;
        }
    }

    @Override
    public void onPopulate(int chunkX, int chunkZ) {
    }

    @Override
    public void recreateStructures(@Nonnull Chunk chunk, int x, int z) {
    }
}
