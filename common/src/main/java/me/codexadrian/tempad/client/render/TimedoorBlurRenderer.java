package me.codexadrian.tempad.client.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import me.codexadrian.tempad.BlurReloader;
import me.codexadrian.tempad.entity.TimedoorEntity;
import me.codexadrian.tempad.platform.Services;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.Comparator;
import java.util.stream.StreamSupport;

public class TimedoorBlurRenderer {

    public static void renderBlur(float deltaTime, PoseStack poseStack, Camera camera) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget renderTexture = minecraft.getMainRenderTarget();
        RenderTarget blurRenderTarget = Services.SHADERS.getBlurReloader().getRenderTarget();
        if (blurRenderTarget == null) return;

        //blurRenderTarget.clear(false);
        clear(blurRenderTarget);
        blurRenderTarget.copyDepthFrom(renderTexture);
        renderTexture.bindWrite(false);

        Vec3 position = camera.getPosition();
        double cameraX = position.x();
        double cameraY = position.y();
        double cameraZ = position.z();

        AbstractUniform inSize = Services.SHADERS.getTimedoorShader().safeGetUniform("InSize");
        AbstractUniform viewMatUniform = Services.SHADERS.getTimedoorShader().safeGetUniform("ViewMat");
        PoseStack viewMat = new PoseStack();
        viewMat.mulPose(Vector3f.XP.rotationDegrees(camera.getXRot()));
        viewMat.mulPose(Vector3f.YP.rotationDegrees(camera.getYRot() + 180.0F));

        viewMatUniform.set(viewMat.last().pose());
        inSize.set((float) renderTexture.width, (float) renderTexture.height);

        assert minecraft.level != null;
        blurRenderTarget.bindWrite(false);
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();

        StreamSupport.stream(minecraft.level.entitiesForRendering().spliterator(), false).filter(TimedoorEntity.class::isInstance).sorted(Comparator.comparingDouble(value -> -value.distanceToSqr(minecraft.cameraEntity))).forEach(entity -> {
            double entityX = Mth.lerp(deltaTime, entity.xOld, entity.getX());
            double entityY = Mth.lerp(deltaTime, entity.yOld, entity.getY());
            double entityZ = Mth.lerp(deltaTime, entity.zOld, entity.getZ());
            float entityYaw = Mth.lerp(deltaTime, entity.yRotO, entity.getYRot());

            minecraft.getEntityRenderDispatcher().render(entity, entityX - cameraX, entityY - cameraY, entityZ - cameraZ, entityYaw, deltaTime, poseStack, bufferSource, minecraft.getEntityRenderDispatcher().getPackedLightCoords(entity, deltaTime));
        });

        bufferSource.endLastBatch();

        Services.SHADERS.getBlurReloader().getTimedoorBlur().process(deltaTime);
        renderTexture.bindWrite(false);
    }

    public static void clear(RenderTarget renderTarget) {
        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, renderTarget.frameBufferId);
        GlStateManager._clear(GL11.GL_COLOR_BUFFER_BIT, false);
        GlStateManager._clearColor(0, 0, 0, 0);
        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }
}
