package com.fubangkun.srpgc.client;

import com.fubangkun.srpgc.Reference;
import com.fubangkun.srpgc.dimension.WorldProviderXenosis;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Side.CLIENT)
public final class XenosisSkyHandler {

    private static final IRenderHandler NO_CLOUDS = new IRenderHandler() {
        @Override
        public void render(float partialTicks, WorldClient world, Minecraft mc) {
        }
    };

    private XenosisSkyHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        WorldClient world = Minecraft.getMinecraft().world;
        if (world == null || !(world.provider instanceof WorldProviderXenosis)) {
            return;
        }
        if (world.provider.getSkyRenderer() == null) {
            world.provider.setSkyRenderer(new XenosisSkyProvider((IGalacticraftWorldProvider) world.provider));
        }
        if (world.provider.getCloudRenderer() == null) {
            world.provider.setCloudRenderer(NO_CLOUDS);
        }
    }
}
