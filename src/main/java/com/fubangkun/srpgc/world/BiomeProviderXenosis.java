package com.fubangkun.srpgc.world;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeProviderSpace;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class BiomeProviderXenosis extends BiomeProviderSpace {

    public BiomeProviderXenosis(World world) {
    }

    @Override
    public Biome getBiome() {
        return BiomeXenosis.INSTANCE;
    }
}
