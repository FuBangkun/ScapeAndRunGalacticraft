package com.fubangkun.srpgc.dimension;

import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import micdoodle8.mods.galacticraft.core.entities.EntityLander;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.Random;

public class TeleportTypeXenosis implements ITeleportType {

    @Override
    public boolean useParachute() {
        return ConfigManagerCore.disableLander;
    }

    @Override
    public Vector3 getPlayerSpawnLocation(WorldServer world, EntityPlayerMP player) {
        if (player == null) {
            return null;
        }

        GCPlayerStats stats = GCPlayerStats.get(player);
        double x = stats.getCoordsTeleportedFromX();
        double z = stats.getCoordsTeleportedFromZ();
        int limit = ConfigManagerCore.otherPlanetWorldBorders - 2;
        if (limit > 20) {
            if (x > limit) {
                z *= limit / x;
                x = limit;
            } else if (x < -limit) {
                z *= -limit / x;
                x = -limit;
            }
            if (z > limit) {
                x *= limit / z;
                z = limit;
            } else if (z < -limit) {
                x *= -limit / z;
                z = -limit;
            }
        }
        return new Vector3(x, ConfigManagerCore.disableLander ? 250.0D : 900.0D, z);
    }

    @Override
    public Vector3 getEntitySpawnLocation(WorldServer world, Entity entity) {
        return new Vector3(entity.posX, ConfigManagerCore.disableLander ? 250.0D : 900.0D, entity.posZ);
    }

    @Override
    public Vector3 getParaChestSpawnLocation(WorldServer world, EntityPlayerMP player, Random rand) {
        if (ConfigManagerCore.disableLander) {
            double x = (rand.nextDouble() * 2.0D - 1.0D) * 4.0D;
            double z = (rand.nextDouble() * 2.0D - 1.0D) * 4.0D;
            return new Vector3(player.posX + x, 220.0D, player.posZ + z);
        }
        return null;
    }

    @Override
    public void onSpaceDimensionChanged(World newWorld, EntityPlayerMP player, boolean ridingAutoRocket) {
        if (ridingAutoRocket || player == null) {
            return;
        }

        GCPlayerStats stats = GCPlayerStats.get(player);
        if (stats.getTeleportCooldown() > 0 || ConfigManagerCore.disableLander) {
            return;
        }

        if (player.capabilities.isFlying) {
            player.capabilities.isFlying = false;
        }

        EntityLander lander = new EntityLander(player);
        lander.setPosition(player.posX, player.posY, player.posZ);

        if (!newWorld.isRemote) {
            boolean previous = CompatibilityManager.forceLoadChunks((WorldServer) newWorld);
            lander.forceSpawn = true;
            newWorld.spawnEntity(lander);
            lander.setWorld(newWorld);
            newWorld.updateEntityWithOptionalForce(lander, true);
            player.startRiding(lander);
            CompatibilityManager.forceLoadChunksEnd((WorldServer) newWorld, previous);
        }

        stats.setTeleportCooldown(10);
    }

    @Override
    public void setupAdventureSpawn(EntityPlayerMP player) {
    }
}
