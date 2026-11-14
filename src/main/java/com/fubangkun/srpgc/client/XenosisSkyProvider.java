package com.fubangkun.srpgc.client;

import com.fubangkun.srpgc.planet.PlanetXenosis;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class XenosisSkyProvider extends IRenderHandler {

    private static final ResourceLocation SUN_TEXTURE =
            new ResourceLocation("galacticraftcore", "textures/gui/planets/atmosphericsun.png");
    private static final float[][] STAR_TINTS = {
            {1.00F, 0.97F, 0.86F},
            {0.80F, 1.00F, 0.72F},
            {1.00F, 0.84F, 0.58F},
            {0.78F, 0.90F, 1.00F},
            {0.94F, 0.80F, 1.00F}
    };

    private final int starList;
    private final int skyList;
    private final int horizonList;
    private final float sunSize;
    private final float[] sunTint;
    private final List<Planet> companions = new ArrayList<>();

    public XenosisSkyProvider(IGalacticraftWorldProvider provider) {
        this.sunSize = 17.5F * provider.getSolarSize();

        SolarSystem system = PlanetXenosis.PLANET == null ? null : PlanetXenosis.PLANET.getParentSolarSystem();
        if (system == null) {
            system = GalacticraftCore.solarSystemSol;
        }
        this.sunTint = tintFor(system.getUnlocalizedName());
        for (Planet planet : GalaxyRegistry.getPlanetsForSolarSystem(system)) {
            if (planet == PlanetXenosis.PLANET || planet.getBodyIcon() == null) {
                continue;
            }
            this.companions.add(planet);
            if (this.companions.size() >= 4) {
                break;
            }
        }

        int lists = GLAllocation.generateDisplayLists(3);
        this.starList = lists;
        this.skyList = lists + 1;
        this.horizonList = lists + 2;

        GL11.glPushMatrix();
        GL11.glNewList(this.starList, 4864);
        renderStars();
        GL11.glEndList();
        GL11.glPopMatrix();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GL11.glNewList(this.skyList, 4864);
        for (int x = -384; x <= 384; x += 64) {
            for (int z = -384; z <= 384; z += 64) {
                buffer.begin(7, DefaultVertexFormats.POSITION);
                buffer.pos(x, 16.0D, z).endVertex();
                buffer.pos(x + 64, 16.0D, z).endVertex();
                buffer.pos(x + 64, 16.0D, z + 64).endVertex();
                buffer.pos(x, 16.0D, z + 64).endVertex();
                tessellator.draw();
            }
        }
        GL11.glEndList();

        GL11.glNewList(this.horizonList, 4864);
        buffer.begin(7, DefaultVertexFormats.POSITION);
        for (int x = -384; x <= 384; x += 64) {
            for (int z = -384; z <= 384; z += 64) {
                buffer.pos(x + 64, -16.0D, z).endVertex();
                buffer.pos(x, -16.0D, z).endVertex();
                buffer.pos(x, -16.0D, z + 64).endVertex();
                buffer.pos(x + 64, -16.0D, z + 64).endVertex();
            }
        }
        tessellator.draw();
        GL11.glEndList();
    }

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        GL11.glDisable(3553);
        GlStateManager.disableRescaleNormal();
        Vec3d skyColor = world.getSkyColor(mc.getRenderViewEntity(), partialTicks);
        float red = (float) skyColor.x;
        float green = (float) skyColor.y;
        float blue = (float) skyColor.z;
        if (mc.gameSettings.anaglyph) {
            float r = (red * 30.0F + green * 59.0F + blue * 11.0F) / 100.0F;
            float g = (red * 30.0F + green * 70.0F) / 100.0F;
            float b = (red * 30.0F + blue * 70.0F) / 100.0F;
            red = r;
            green = g;
            blue = b;
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GL11.glDepthMask(false);
        GL11.glEnable(2912);
        GL11.glColor3f(red, green, blue);
        GL11.glCallList(this.skyList);
        GL11.glDisable(2912);
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        RenderHelper.disableStandardItemLighting();

        float starBrightness = world.getStarBrightness(partialTicks);
        if (starBrightness > 0.0F) {
            GL11.glPushMatrix();
            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-19.0F, 0.0F, 1.0F, 0.0F);
            GL11.glColor4f(starBrightness, starBrightness, starBrightness, starBrightness);
            GL11.glCallList(this.starList);
            GL11.glPopMatrix();
        }

        GL11.glPushMatrix();
        GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
        GL11.glDisable(3553);

        // sun disc backing so the star reads against the dark sky
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
        float size = this.sunSize / 3.5F;
        buffer.begin(7, DefaultVertexFormats.POSITION);
        buffer.pos(-size, 99.9D, -size).endVertex();
        buffer.pos(size, 99.9D, -size).endVertex();
        buffer.pos(size, 99.9D, size).endVertex();
        buffer.pos(-size, 99.9D, size).endVertex();
        tessellator.draw();

        GL11.glEnable(3553);
        GL11.glColor4f(this.sunTint[0], this.sunTint[1], this.sunTint[2], 1.0F);
        size = this.sunSize;
        mc.renderEngine.bindTexture(SUN_TEXTURE);
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-size, 100.0D, -size).tex(0.0D, 0.0D).endVertex();
        buffer.pos(size, 100.0D, -size).tex(1.0D, 0.0D).endVertex();
        buffer.pos(size, 100.0D, size).tex(1.0D, 1.0D).endVertex();
        buffer.pos(-size, 100.0D, size).tex(0.0D, 1.0D).endVertex();
        tessellator.draw();

        // the other planets of the same system hang in the sky like Earth does over Mars
        for (int i = 0; i < this.companions.size(); i++) {
            Planet planet = this.companions.get(i);
            float planetSize = 0.3F + 0.45F * Math.min(1.5F, planet.getRelativeSize());
            GL11.glPushMatrix();
            GL11.glRotatef(planet.getPhaseShift() * 360.0F + i * 23.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(0.6F, 0.6F, 0.6F);
            GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(200.0F, 1.0F, 0.0F, 0.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.renderEngine.bindTexture(planet.getBodyIcon());
            buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(-planetSize, -100.0D, planetSize).tex(0.0D, 1.0D).endVertex();
            buffer.pos(planetSize, -100.0D, planetSize).tex(1.0D, 1.0D).endVertex();
            buffer.pos(planetSize, -100.0D, -planetSize).tex(1.0D, 0.0D).endVertex();
            buffer.pos(-planetSize, -100.0D, -planetSize).tex(0.0D, 0.0D).endVertex();
            tessellator.draw();
            GL11.glPopMatrix();
        }

        GL11.glDisable(3553);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();

        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glEnable(2912);

        double horizon = mc.player.getPosition().getY() - world.getHorizon();
        if (horizon < 0.0D) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 12.0F, 0.0F);
            GL11.glCallList(this.horizonList);
            GL11.glPopMatrix();

            GL11.glDisable(3553);
            GL11.glColor3f(0.0F, 0.0F, 0.0F);
            float top = -((float) (horizon + 65.0D));
            buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            for (int side = 0; side < 2; side++) {
                float sx = side == 0 ? -1.0F : 1.0F;
                buffer.pos(-1.0F, top, sx).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
                buffer.pos(1.0F, top, sx).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
                buffer.pos(1.0F, -1.0F, sx).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
                buffer.pos(-1.0F, -1.0F, sx).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
                buffer.pos(sx, -1.0F, -1.0F).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
                buffer.pos(sx, -1.0F, 1.0F).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
                buffer.pos(sx, top, 1.0F).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
                buffer.pos(sx, top, -1.0F).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
            }
            tessellator.draw();

            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, -((float) (horizon - 16.0D)), 0.0F);
            GL11.glCallList(this.horizonList);
            GL11.glPopMatrix();
        }

        GL11.glEnable(3553);
        GlStateManager.enableRescaleNormal();
        GL11.glEnable(2903);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDepthMask(true);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3042);
    }

    private static float[] tintFor(String systemName) {
        if ("sol".equalsIgnoreCase(systemName)) {
            return STAR_TINTS[0];
        }
        return STAR_TINTS[Math.floorMod(systemName.hashCode(), STAR_TINTS.length)];
    }

    private static void renderStars() {
        Random random = new Random(10842L);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION);
        int count = ConfigManagerCore.moreStars ? 20000 : 6000;

        for (int i = 0; i < count; i++) {
            double x = random.nextFloat() * 2.0F - 1.0F;
            double y = random.nextFloat() * 2.0F - 1.0F;
            double z = random.nextFloat() * 2.0F - 1.0F;
            double size = 0.15F + random.nextFloat() * 0.1F;
            double length = x * x + y * y + z * z;
            if (length >= 1.0D || length <= 0.01D) {
                continue;
            }
            length = 1.0D / Math.sqrt(length);
            x *= length;
            y *= length;
            z *= length;
            double scale = ConfigManagerCore.moreStars ? random.nextDouble() * 150.0D + 130.0D : 100.0D;
            double px = x * scale;
            double py = y * scale;
            double pz = z * scale;
            double yaw = Math.atan2(x, z);
            double sinYaw = Math.sin(yaw);
            double cosYaw = Math.cos(yaw);
            double pitch = Math.atan2(Math.sqrt(x * x + z * z), y);
            double sinPitch = Math.sin(pitch);
            double cosPitch = Math.cos(pitch);
            double roll = random.nextDouble() * Math.PI * 2.0D;
            double sinRoll = Math.sin(roll);
            double cosRoll = Math.cos(roll);

            for (int vertex = 0; vertex < 4; vertex++) {
                double offsetX = (double) ((vertex & 2) - 1) * size;
                double offsetY = (double) ((vertex + 1 & 2) - 1) * size;
                double rx = offsetX * cosRoll - offsetY * sinRoll;
                double ry = offsetY * cosRoll + offsetX * sinRoll;
                double tx = rx * sinPitch;
                double ty = -rx * cosPitch;
                double dx = ty * sinYaw - ry * cosYaw;
                double dz = ry * sinYaw + ty * cosYaw;
                buffer.pos(px + dx, py + tx, pz + dz).endVertex();
            }
        }
        tessellator.draw();
    }
}
