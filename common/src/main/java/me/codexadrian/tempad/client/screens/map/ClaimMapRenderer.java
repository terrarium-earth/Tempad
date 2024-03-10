package me.codexadrian.tempad.client.screens.map;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import me.codexadrian.tempad.common.Tempad;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class ClaimMapRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Tempad.MODID, "claimmaptextures");

    public ClaimMapRenderer(int[][] colors, int scale) {
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        var dynamicTexture = new DynamicTexture(scale, scale, true);
        textureManager.register(TEXTURE, dynamicTexture);
        updateTexture(dynamicTexture, colors, scale);
    }

    private void updateTexture(DynamicTexture texture, int[][] colors, int scale) {
        NativeImage nativeImage = texture.getPixels();
        if (nativeImage == null) return;
        for (int i = 0; i < scale; ++i) {
            for (int j = 0; j < scale; ++j) {
                nativeImage.setPixelRGBA(i, j, colors[i][j]);
            }
        }

        texture.upload();
    }

    public void render(GuiGraphics graphics, int x, int y, int size) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        try (var pose = new CloseablePoseStack(graphics)) {
            pose.translate(x, y, 1.0);
            Matrix4f matrix4f = pose.last().pose();
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            builder.vertex(matrix4f, 0.0F, size, -0.01F).uv(0.0F, 1.0F).endVertex();
            builder.vertex(matrix4f, size, size, -0.01F).uv(1.0F, 1.0F).endVertex();
            builder.vertex(matrix4f, size, 0.0F, -0.01F).uv(1.0F, 0.0F).endVertex();
            builder.vertex(matrix4f, 0.0F, 0.0F, -0.01F).uv(0.0F, 0.0F).endVertex();
            BufferUploader.drawWithShader(builder.end());
        }
    }
}