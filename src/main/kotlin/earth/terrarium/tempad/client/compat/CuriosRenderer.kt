package earth.terrarium.tempad.client.compat

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import earth.terrarium.tempad.common.mixin.PlayerModelAccessor
import earth.terrarium.tempad.common.registries.ModItems
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.player.AvatarRenderer
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import top.theillusivec4.curios.api.SlotContext
import top.theillusivec4.curios.api.client.ICurioRenderer

fun initCuriosCompat() {
    ICurioRenderer.register(ModItems.tempad) { CuriosRenderer }
    ICurioRenderer.register(ModItems.cardWallet) { CuriosRenderer }

    ICurioRenderer.register(ModItems.capacitorTimeSteel) { CuriosRenderer }
    ICurioRenderer.register(ModItems.capacitorIron) { CuriosRenderer }

    ICurioRenderer.register(ModItems.chrononGenTimeSteel) { CuriosRenderer }
    ICurioRenderer.register(ModItems.chrononGenIron) { CuriosRenderer }
    ICurioRenderer.register(ModItems.creativeChronometer) { CuriosRenderer }

    ICurioRenderer.register(ModItems.waymitterTimeSteel) { CuriosRenderer }
    ICurioRenderer.register(ModItems.waymitterIron) { CuriosRenderer }
}

object CuriosRenderer : ICurioRenderer {
    override fun <S : LivingEntityRenderState, M : EntityModel<in S>> render(
        stack: ItemStack,
        slotContext: SlotContext,
        matrixStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        packedLight: Int,
        renderState: S,
        renderLayerParent: RenderLayerParent<S, M>,
        context: EntityRendererProvider.Context,
        yRotation: Float,
        xRotation: Float
    ) {
        super.render(
            stack,
            slotContext,
            matrixStack,
            submitNodeCollector,
            packedLight,
            renderState,
            renderLayerParent,
            context,
            yRotation,
            xRotation
        )
        if (renderLayerParent is AvatarRenderer<*>) {
            val render = Minecraft.getInstance().entityRenderDispatcher.getRenderer(slotContext.entity) as? AvatarRenderer ?: return
            val model = render.getModel() as? HumanoidModel<*> ?: return
            matrixStack.pushPose()
            if (slotContext.identifier != "bracelet") {
                model.body.translateAndRotate(matrixStack)
                matrixStack.scale(0.35f, 0.35f, 0.35f)
            }
            when (slotContext.identifier) {
                "belt" -> {
                    if (slotContext.index != 0) return matrixStack.popPose()
                    val offset = if(!slotContext.entity.getItemBySlot(EquipmentSlot.LEGS).isEmpty) {
                        0.1
                    } else 0.0
                    matrixStack.translate(-0.75 - offset, 2.0, 0.0)
                    matrixStack.mulPose(Axis.ZP.rotationDegrees(180f))
                    matrixStack.mulPose(Axis.YP.rotationDegrees(90f))
                }
                "charm" -> {
                    if (slotContext.index > 3) return matrixStack.popPose()
                    val zOffset = if(!slotContext.entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty) {
                        0.35
                    } else if(!slotContext.entity.getItemBySlot(EquipmentSlot.LEGS).isEmpty) {
                        0.15
                    } else 0.0
                    matrixStack.scale(0.5f, 0.5f, 0.5f)
                    matrixStack.translate(1 - slotContext.index * 1.0, 3.65, 0.75 + zOffset)
                    matrixStack.mulPose(Axis.ZP.rotationDegrees(180f))
                }
                "bracelet" -> {
                    if (slotContext.index != 0) return matrixStack.popPose()
                    val armOffset = if ((model as PlayerModelAccessor).slim) 0.0 else 0.15
                    if(slotContext.entity.mainArm == HumanoidArm.RIGHT) {
                        model.leftArm.translateAndRotate(matrixStack)
                        matrixStack.mulPose(Axis.XP.rotationDegrees(90f))
                        matrixStack.mulPose(Axis.YP.rotationDegrees(90f))
                        matrixStack.scale(0.4f, 0.4f, 0.4f)
                        matrixStack.translate(1.0, 0.0, 0.35 + armOffset)
                    } else {
                        model.rightArm.translateAndRotate(matrixStack)
                        model.rightArm.yScale
                        matrixStack.mulPose(Axis.XP.rotationDegrees(-90f))
                        matrixStack.mulPose(Axis.YP.rotationDegrees(-90f))
                        matrixStack.scale(0.4f, 0.4f, 0.4f)
                        matrixStack.translate(1.0, 0.0, 0.35 + armOffset)
                    }
                }
            }

            val state = Display.ItemDisplay.ItemRenderState(stack, ItemDisplayContext.FIXED)

            // TODO: How the heck do you render an item now?
            // Minecraft.getInstance().itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, matrixStack, renderTypeBuffer, slotContext.entity.level(), 0)
            matrixStack.popPose()

        }
    }
}