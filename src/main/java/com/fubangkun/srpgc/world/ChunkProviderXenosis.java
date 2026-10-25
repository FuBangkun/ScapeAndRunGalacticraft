package com.fubangkun.srpgc.world;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyCore;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteNodeCore;
import com.fubangkun.srpgc.config.ConfigManagerSRPGC;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.ChunkProviderSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class ChunkProviderXenosis extends ChunkProviderSpace {

    private static final int STARTER_NODE_CHUNK_X = 75;
    private static final int STARTER_NODE_CHUNK_Z = 75;
    private static final int STARTER_COLONY_CHUNK_X = 200;
    private static final int STARTER_COLONY_CHUNK_Z = 200;
    private static final int STRUCTURE_OFFSET = 8;
    private static final long NODE_CELL_SALT = 0x9E3779B97F4A7C15L;
    private static final long COLONY_CELL_SALT = 0xC2B2AE3D27D4EB4FL;

    private final BiomeDecoratorXenosis biomeDecorator = new BiomeDecoratorXenosis();

    public ChunkProviderXenosis(World world, long seed, boolean mapFeaturesEnabled) {
        super(world, seed, mapFeaturesEnabled);
    }

    private static boolean isWithinDistance(int x, int z, int targetX, int targetZ, int distance) {
        long dx = (long) x - targetX;
        long dz = (long) z - targetZ;
        return dx * dx + dz * dz <= (long) distance * distance;
    }

    private static BlockPos findStructureFloor(World world, int x, int z) {
        BlockPos surface = world.getHeight(new BlockPos(x, 0, z));
        BlockPos floor = ParasiteEventEntity.getFloorBuilding(world, surface, 100);
        return floor != null ? floor : ParasiteEventEntity.getFloor(world, surface, 100);
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
        return new BlockMetaPair(SRPBlocks.ParasiteRubble, (byte) 0);
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
        World world = this.world;
        if (world.isRemote || world.provider.getDimension() != ConfigManagerSRPGC.dimensionId
                || !ConfigManagerSRPGC.seedStructures) {
            return;
        }

        if (ConfigManagerSRPGC.starterNode) {
            if (chunkX == STARTER_NODE_CHUNK_X && chunkZ == STARTER_NODE_CHUNK_Z) {
                this.seedNode(world, chunkX, chunkZ, chunkX * 16 + STRUCTURE_OFFSET, chunkZ * 16 + STRUCTURE_OFFSET);
            }
            if (chunkX == STARTER_COLONY_CHUNK_X && chunkZ == STARTER_COLONY_CHUNK_Z) {
                this.seedColony(world, chunkX, chunkZ, chunkX * 16 + STRUCTURE_OFFSET, chunkZ * 16 + STRUCTURE_OFFSET);
            }
        }

        this.trySparseNode(world, chunkX, chunkZ);
        this.trySparseColony(world, chunkX, chunkZ);
    }

    private void trySparseNode(World world, int chunkX, int chunkZ) {
        int cellSize = ConfigManagerSRPGC.nodeCellChunks;
        int cellX = Math.floorDiv(chunkX, cellSize);
        int cellZ = Math.floorDiv(chunkZ, cellSize);
        Random selector = new Random(world.getSeed()
                ^ (long) cellX * 341873128712L
                ^ (long) cellZ * 132897987541L
                ^ NODE_CELL_SALT);
        int candidateX = cellX * cellSize + selector.nextInt(cellSize);
        int candidateZ = cellZ * cellSize + selector.nextInt(cellSize);
        if (chunkX != candidateX || chunkZ != candidateZ) {
            return;
        }

        int x = chunkX * 16 + STRUCTURE_OFFSET + selector.nextInt(16 - STRUCTURE_OFFSET);
        int z = chunkZ * 16 + STRUCTURE_OFFSET + selector.nextInt(16 - STRUCTURE_OFFSET);
        if (ConfigManagerSRPGC.starterNode && isWithinDistance(x, z,
                STARTER_NODE_CHUNK_X * 16 + STRUCTURE_OFFSET,
                STARTER_NODE_CHUNK_Z * 16 + STRUCTURE_OFFSET,
                SRPConfigWorld.minimumDistanceBetweenNodes)) {
            return;
        }
        this.seedNode(world, chunkX, chunkZ, x, z);
    }

    private void trySparseColony(World world, int chunkX, int chunkZ) {
        int cellSize = ConfigManagerSRPGC.colonyCellChunks;
        int cellX = Math.floorDiv(chunkX, cellSize);
        int cellZ = Math.floorDiv(chunkZ, cellSize);
        Random selector = new Random(world.getSeed()
                ^ (long) cellX * 341873128712L
                ^ (long) cellZ * 132897987541L
                ^ COLONY_CELL_SALT);
        int candidateX = cellX * cellSize + selector.nextInt(cellSize);
        int candidateZ = cellZ * cellSize + selector.nextInt(cellSize);
        if (chunkX != candidateX || chunkZ != candidateZ) {
            return;
        }

        int x = chunkX * 16 + STRUCTURE_OFFSET + selector.nextInt(16 - STRUCTURE_OFFSET);
        int z = chunkZ * 16 + STRUCTURE_OFFSET + selector.nextInt(16 - STRUCTURE_OFFSET);
        if (ConfigManagerSRPGC.starterNode && isWithinDistance(x, z,
                STARTER_COLONY_CHUNK_X * 16 + STRUCTURE_OFFSET,
                STARTER_COLONY_CHUNK_Z * 16 + STRUCTURE_OFFSET,
                SRPConfigWorld.minimumDistanceBetweenColonies)) {
            return;
        }
        this.seedColony(world, chunkX, chunkZ, x, z);
    }

    private void seedNode(World world, int chunkX, int chunkZ, int x, int z) {
        SRPWorldData data = SRPWorldData.get(world);
        if (data.getNodes("x").size() >= ConfigManagerSRPGC.maxNodes) {
            ConfigManagerSRPGC.debug("Skipping SRP node at chunk {},{}: configured limit {} reached.",
                    chunkX, chunkZ, ConfigManagerSRPGC.maxNodes);
            return;
        }

        BlockPos pos = findStructureFloor(world, x, z);
        if (pos == null) {
            ConfigManagerSRPGC.debug("Skipping SRP node at chunk {},{}: no floor found.", chunkX, chunkZ);
            return;
        }

        int result = data.setNode(pos.getX(), pos.getY(), pos.getZ());
        if (result == 1) {
            new WorldGenParasiteNodeCore(false, 1).generate(world, this.rand, pos);
            ConfigManagerSRPGC.debug("Seeded SRP node at {},{},{} (chunk {},{}).",
                    pos.getX(), pos.getY(), pos.getZ(), chunkX, chunkZ);
        } else if (result != 10) {
            ConfigManagerSRPGC.debug("Skipped SRP node at {},{},{}: setNode returned {}.",
                    pos.getX(), pos.getY(), pos.getZ(), result);
        }
    }

    private void seedColony(World world, int chunkX, int chunkZ, int x, int z) {
        SRPWorldData data = SRPWorldData.get(world);
        if (data.getColonies("x").size() >= ConfigManagerSRPGC.maxColonies) {
            ConfigManagerSRPGC.debug("Skipping SRP colony at chunk {},{}: configured limit {} reached.",
                    chunkX, chunkZ, ConfigManagerSRPGC.maxColonies);
            return;
        }

        BlockPos pos = findStructureFloor(world, x, z);
        if (pos == null) {
            ConfigManagerSRPGC.debug("Skipping SRP colony at chunk {},{}: no floor found.", chunkX, chunkZ);
            return;
        }

        int result = data.setColony(pos.getX(), pos.getY(), pos.getZ());
        if (result == 1) {
            new WorldGenParasiteColonyCore(false).generate(world, this.rand, pos);
            ConfigManagerSRPGC.debug("Seeded SRP colony at {},{},{} (chunk {},{}).",
                    pos.getX(), pos.getY(), pos.getZ(), chunkX, chunkZ);
        } else if (result != 10) {
            ConfigManagerSRPGC.debug("Skipped SRP colony at {},{},{}: setColony returned {}.",
                    pos.getX(), pos.getY(), pos.getZ(), result);
        }
    }

    @Override
    public void recreateStructures(@Nonnull Chunk chunk, int x, int z) {
    }
}
