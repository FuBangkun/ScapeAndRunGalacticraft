package com.fubangkun.srpgc.world;

import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteShrouded;
import com.fubangkun.srpgc.Reference;

public class BiomeXenosis extends BiomeParasiteShrouded {

    public static final String REGISTRY_NAME = Reference.MOD_ID + ":parasite_planet";

    public BiomeXenosis() {
        super();
        this.setRegistryName(REGISTRY_NAME);
    }
}
