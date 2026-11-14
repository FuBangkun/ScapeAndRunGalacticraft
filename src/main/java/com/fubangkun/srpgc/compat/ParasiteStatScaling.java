package com.fubangkun.srpgc.compat;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.fubangkun.srpgc.Reference;
import com.fubangkun.srpgc.config.ConfigManagerSRPGC;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public final class ParasiteStatScaling {

    private static final String SCALED_TAG = "srpgc_stat_scaled";
    private static final String MODIFIER_NAME = "srpgc_dimension_scaling";
    private static final UUID HEALTH_ID = UUID.fromString("2f9b1a2e-9d4c-4c63-9a55-6a0c1f2d3a01");
    private static final UUID DAMAGE_ID = UUID.fromString("7b1c3d4e-5f60-4712-8a9b-0c1d2e3f4a02");
    private static final UUID ARMOR_ID = UUID.fromString("1a2b3c4d-5e6f-4081-92a3-0b1c2d3e4f03");

    private ParasiteStatScaling() {
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote || !(event.getEntity() instanceof EntityParasiteBase)) {
            return;
        }

        EntityParasiteBase parasite = (EntityParasiteBase) event.getEntity();
        float multiplier = multiplierFor(parasite.dimension);
        if (multiplier == 1.0F) {
            return;
        }

        NBTTagCompound data = parasite.getEntityData();
        if (data.getBoolean(SCALED_TAG)) {
            return;
        }
        data.setBoolean(SCALED_TAG, true);

        apply(parasite, SharedMonsterAttributes.MAX_HEALTH, HEALTH_ID, multiplier);
        apply(parasite, SharedMonsterAttributes.ATTACK_DAMAGE, DAMAGE_ID, multiplier);
        apply(parasite, SharedMonsterAttributes.ARMOR, ARMOR_ID, multiplier);
        parasite.setHealth(parasite.getMaxHealth());

        ConfigManagerSRPGC.debug("Scaled {} in dimension {} by {}.",
                parasite.getClass().getSimpleName(), parasite.dimension, multiplier);
    }

    private static void apply(EntityParasiteBase parasite, IAttribute attribute, UUID id, float multiplier) {
        IAttributeInstance instance = parasite.getEntityAttribute(attribute);
        if (instance.getModifier(id) != null) {
            return;
        }
        instance.applyModifier(new AttributeModifier(id, MODIFIER_NAME, multiplier - 1.0D, 2));
    }

    private static float multiplierFor(int dimension) {
        if (dimension == ConfigManagerSRPGC.dimensionId) {
            return ConfigManagerSRPGC.xenosisMobStatMultiplier;
        }
        if (dimension == 0) {
            return ConfigManagerSRPGC.overworldMobStatMultiplier;
        }
        return 1.0F;
    }
}
