package com.seedxray;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Map;

/**
 * Renders colored box outlines around predicted ore positions using
 * Forge's RenderLevelStageEvent. Uses LINES vertex format (correct for 1.20.4).
 */
public class XrayRenderer {

    public static void render(RenderLevelStageEvent event) {
        if (OrePredictor.predictedOres.isEmpty()) return;

        Camera camera = event.getCamera();
        Vec3 camPos = camera.getPosition();

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

        Matrix4f posMat  = poseStack.last().pose();
        Matrix3f normMat = poseStack.last().normal();

        RenderSystem.setShader(GameRenderer::getLineProgram);
        RenderSystem.lineWidth(2.5f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buf = tesselator.getBuilder();
        buf.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);

        for (Map.Entry<BlockPos, Block> entry : OrePredictor.predictedOres.entrySet()) {
            float[] c = color(entry.getValue());
            drawBox(buf, posMat, normMat, entry.getKey(), c[0], c[1], c[2], 0.9f);
        }

        tesselator.end();

        poseStack.popPose();

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.lineWidth(1.0f);
    }

    private static void drawBox(BufferBuilder buf, Matrix4f pos, Matrix3f norm,
                                BlockPos bp, float r, float g, float b, float a) {
        float x0 = bp.getX(), y0 = bp.getY(), z0 = bp.getZ();
        float x1 = x0+1, y1 = y0+1, z1 = z0+1;
        line(buf,pos,norm, x0,y0,z0, x1,y0,z0, r,g,b,a);
        line(buf,pos,norm, x1,y0,z0, x1,y0,z1, r,g,b,a);
        line(buf,pos,norm, x1,y0,z1, x0,y0,z1, r,g,b,a);
        line(buf,pos,norm, x0,y0,z1, x0,y0,z0, r,g,b,a);
        line(buf,pos,norm, x0,y1,z0, x1,y1,z0, r,g,b,a);
        line(buf,pos,norm, x1,y1,z0, x1,y1,z1, r,g,b,a);
        line(buf,pos,norm, x1,y1,z1, x0,y1,z1, r,g,b,a);
        line(buf,pos,norm, x0,y1,z1, x0,y1,z0, r,g,b,a);
        line(buf,pos,norm, x0,y0,z0, x0,y1,z0, r,g,b,a);
        line(buf,pos,norm, x1,y0,z0, x1,y1,z0, r,g,b,a);
        line(buf,pos,norm, x1,y0,z1, x1,y1,z1, r,g,b,a);
        line(buf,pos,norm, x0,y0,z1, x0,y1,z1, r,g,b,a);
    }

    private static void line(BufferBuilder buf, Matrix4f pos, Matrix3f norm,
                             float x0, float y0, float z0,
                             float x1, float y1, float z1,
                             float r, float g, float b, float a) {
        float nx=x1-x0, ny=y1-y0, nz=z1-z0;
        float len=(float)Math.sqrt(nx*nx+ny*ny+nz*nz);
        if(len==0) return;
        nx/=len; ny/=len; nz/=len;
        buf.vertex(pos,x0,y0,z0).color(r,g,b,a).normal(norm,nx,ny,nz).endVertex();
        buf.vertex(pos,x1,y1,z1).color(r,g,b,a).normal(norm,nx,ny,nz).endVertex();
    }

    private static float[] color(Block b) {
        if (b==Blocks.DIAMOND_ORE||b==Blocks.DEEPSLATE_DIAMOND_ORE) return new float[]{0f,0.9f,1f};
        if (b==Blocks.ANCIENT_DEBRIS)                                return new float[]{0.8f,0.35f,0.05f};
        if (b==Blocks.GOLD_ORE||b==Blocks.DEEPSLATE_GOLD_ORE)       return new float[]{1f,0.85f,0f};
        if (b==Blocks.IRON_ORE||b==Blocks.DEEPSLATE_IRON_ORE)       return new float[]{0.75f,0.55f,0.35f};
        if (b==Blocks.EMERALD_ORE||b==Blocks.DEEPSLATE_EMERALD_ORE) return new float[]{0f,1f,0.3f};
        if (b==Blocks.LAPIS_ORE||b==Blocks.DEEPSLATE_LAPIS_ORE)     return new float[]{0.1f,0.2f,0.9f};
        if (b==Blocks.REDSTONE_ORE||b==Blocks.DEEPSLATE_REDSTONE_ORE) return new float[]{1f,0.05f,0.05f};
        if (b==Blocks.COPPER_ORE||b==Blocks.DEEPSLATE_COPPER_ORE)   return new float[]{0.8f,0.4f,0.15f};
        if (b==Blocks.COAL_ORE||b==Blocks.DEEPSLATE_COAL_ORE)       return new float[]{0.45f,0.45f,0.45f};
        return new float[]{1f,1f,1f};
    }
}
