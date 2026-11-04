package com.fubangkun.srpgc.world;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteBush;
import com.dhanantry.scapeandrunparasites.world.gen.feature.*;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class BiomeDecoratorXenosis extends BiomeDecoratorSpace {

    private static final int DECORATION_OFFSET = 8;

    private final WorldGenerator bigTree = new WorldGenParasiteTree(false);
    private final WorldGenerator thinTree = new WorldGenParasiteTreeThin(false);
    private final WorldGenerator tendrilBush = new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.TENDRIL, 4);
    private final WorldGenerator eyeBush = new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.EYE, 2);
    private final WorldGenerator popBush = new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.POP, 2);
    private final WorldGenerator parasiteBall = new WorldGenParasiteBall(false);
    private final WorldGenerator bigParasiteBall = new WorldGenParasiteBigBall(false);

    private World currentWorld;
    private boolean decorating;

    @Override
    public void decorate(World world, Random random, int posX, int posZ) {
        if (this.decorating) {
            return;
        }
        this.decorating = true;
        try {
            super.decorate(world, random, posX, posZ);
        } finally {
            this.decorating = false;
        }
    }

    @Override
    protected void decorate() {
        this.place(this.rand.nextInt(3) == 0 ? this.thinTree : this.bigTree, 1);
        this.place(this.tendrilBush, 12);
        this.place(this.eyeBush, 3);
        this.place(this.popBush, 2);
        this.place(this.parasiteBall, 2);
        if (this.rand.nextBoolean()) {
            this.place(this.bigParasiteBall, 1);
        }
    }

    private void place(WorldGenerator generator, int count) {
        World world = this.getCurrentWorld();
        for (int i = 0; i < count; i++) {
            // The vanilla +8 offset keeps decoration spill inside the already loaded +X/+Z neighbors.
            BlockPos pos = world.getHeight(new BlockPos(this.posX + DECORATION_OFFSET + this.rand.nextInt(16), 0,
                    this.posZ + DECORATION_OFFSET + this.rand.nextInt(16)));
            generator.generate(world, this.rand, pos);
        }
    }

    @Override
    protected World getCurrentWorld() {
        return this.currentWorld;
    }

    @Override
    protected void setCurrentWorld(World world) {
        this.currentWorld = world;
    }
}
