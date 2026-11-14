package com.fubangkun.srpgc.dimension;

import com.fubangkun.srpgc.Reference;
import com.fubangkun.srpgc.config.ConfigManagerSRPGC;
import com.fubangkun.srpgc.world.XenosisBiomes;
import micdoodle8.mods.galacticraft.core.util.OxygenUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public final class HarlequinHazard {

    private static final int INTERVAL_TICKS = 40;
    private static final int EFFECT_TICKS = 140;

    private HarlequinHazard() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) {
            return;
        }
        if (!ConfigManagerSRPGC.harlequinHazard) {
            return;
        }

        EntityPlayer player = event.player;
        if (player.dimension != ConfigManagerSRPGC.dimensionId || player.isCreative() || player.isSpectator()) {
            return;
        }
        if (player.ticksExisted % INTERVAL_TICKS != 0) {
            return;
        }
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }
        if (player.world.getBiome(player.getPosition()) != XenosisBiomes.HARLEQUIN) {
            return;
        }
        if (OxygenUtil.hasValidOxygenSetup((EntityPlayerMP) player)) {
            return;
        }

        player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, EFFECT_TICKS, 0, true, true));
        player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, EFFECT_TICKS, 0, true, true));
        player.addPotionEffect(new PotionEffect(MobEffects.POISON, EFFECT_TICKS / 2, 0, true, true));
    }
}
