package com.fubangkun.srpgc.world;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeProviderSpace;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nonnull;

public class BiomeProviderXenosis extends BiomeProviderSpace {

    private final World world;

    public BiomeProviderXenosis(World world) {
        this.world = world;
    }

    @Override
    public Biome getBiome() {
        return XenosisBiomes.BASE;
    }

    @Override
    @Nonnull
    public Biome getBiome(BlockPos pos, Biome defaultBiome) {
        return XenosisBiomes.biomeAt(this.world.getSeed(), pos.getX(), pos.getZ());
    }

    @Override
    @Nonnull
    public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height) {
        return this.fill(biomes, x, z, width, height);
    }

    @Override
    @Nonnull
    public Biome[] getBiomes(Biome[] listToReuse, int x, int z, int width, int depth) {
        return this.fill(listToReuse, x, z, width, depth);
    }

    @Override
    @Nonnull
    public Biome[] getBiomes(Biome[] listToReuse, int x, int z, int width, int length, boolean cacheFlag) {
        return this.fill(listToReuse, x, z, width, length);
    }

    private Biome[] fill(Biome[] biomes, int x, int z, int width, int length) {
        if (biomes == null || biomes.length < width * length) {
            biomes = new Biome[width * length];
        }

        long seed = this.world.getSeed();
        for (int row = 0; row < length; row++) {
            for (int column = 0; column < width; column++) {
                biomes[row * width + column] = XenosisBiomes.biomeAt(seed, x + column, z + row);
            }
        }
        return biomes;
    }
}
