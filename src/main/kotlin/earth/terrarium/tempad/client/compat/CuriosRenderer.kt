package earth.terrarium.tempad.client.compat

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import earth.terrarium.tempad.common.registries.ModItems
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.player.PlayerRenderer
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import top.theillusivec4.curios.api.SlotContext
import top.theillusivec4.curios.api.client.CuriosRendererRegistry
import top.theillusivec4.curios.api.client.ICurioRenderer

fun initCuriosCompat() {
    CuriosRendererRegistry.register(ModItems.tempad) { CuriosRenderer }
    CuriosRendererRegistry.register(ModItems.cardWallet) { CuriosRenderer }
    CuriosRendererRegistry.register(ModItems.chronometer) { CuriosRenderer }
    CuriosRendererRegistry.register(ModItems.chrononGenerator) { CuriosRenderer }
    CuriosRendererRegistry.register(ModItems.chrononBattery) { CuriosRenderer }
    CuriosRendererRegistry.register(ModItems.chrononCell) { CuriosRenderer }
    CuriosRendererRegistry.register(ModItems.creativeChronometer) { CuriosRenderer }
    CuriosRendererRegistry.register(ModItems.screeningDevice) { CuriosRenderer }
    CuriosRendererRegistry.register(ModItems.locationBroadcaster) { CuriosRenderer }
}

object CuriosRenderer: ICurioRenderer {
    override fun <T : LivingEntity, M : EntityModel<T>> render(
        stack: ItemStack,
        slotContext: SlotContext,
        matrixStack: PoseStack,
        renderLayerParent: RenderLayerParent<T, M>,
        renderTypeBuffer: MultiBufferSource,
        light: Int,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float,
    ) {
        if (renderLayerParent is PlayerRenderer) {
            matrixStack.pushPose()
            if (slotContext.identifier != "bracelet") {
                ICurioRenderer.translateIfSneaking(matrixStack, slotContext.entity)
                ICurioRenderer.rotateIfSneaking(matrixStack, slotContext.entity)
                matrixStack.scale(0.35f, 0.35f, 0.35f)
            }
            when (slotContext.identifier) {
                "belt" -> {
                    val offset = if(!slotContext.entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty) {
                        0.2
                    } else if(!slotContext.entity.getItemBySlot(EquipmentSlot.LEGS).isEmpty) {
                        0.1
                    } else 0.0
                    matrixStack.translate(-0.3, 1.4, -0.4 - offset)
                    matrixStack.mulPose(Axis.ZP.rotationDegrees(180f))
                }
                "charm" -> {
                    val zOffset = if(!slotContext.entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty) {
                        0.35
                    } else if(!slotContext.entity.getItemBySlot(EquipmentSlot.LEGS).isEmpty) {
                        0.15
                    } else 0.0
                    matrixStack.scale(0.5f, 0.5f, 0.5f)
                    matrixStack.translate(1 - slotContext.index * 1.0, 2.8, 0.75 + zOffset)
                    matrixStack.mulPose(Axis.ZP.rotationDegrees(180f))
                }
                "bracelet" -> {
                    val render = Minecraft.getInstance().entityRenderDispatcher.getRenderer<LivingEntity?>(slotContext.entity)

                    if (render is LivingEntityRenderer<*, *>) {
                        val model = render.getModel()


                        if (model is HumanoidModel<*>) {
                            if(slotContext.entity.mainArm == HumanoidArm.RIGHT) {
                                model.leftArm.translateAndRotate(matrixStack)
                                matrixStack.mulPose(Axis.XP.rotationDegrees(90f))
                                matrixStack.mulPose(Axis.YP.rotationDegrees(90f))
                                matrixStack.scale(0.4f, 0.4f, 0.4f)
                                matrixStack.translate(1.0, 0.0, 0.35)
                            } else {
                                model.rightArm.translateAndRotate(matrixStack)
                                model.rightArm.yScale
                                matrixStack.mulPose(Axis.XP.rotationDegrees(-90f))
                                matrixStack.mulPose(Axis.YP.rotationDegrees(-90f))
                                matrixStack.scale(0.4f, 0.4f, 0.4f)
                                matrixStack.translate(1.0, 0.0, 0.35 )
                            }
                        }
                    }
                }
            }

            Minecraft.getInstance().itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, 0, matrixStack, renderTypeBuffer, slotContext.entity.level(), 0)
            matrixStack.popPose()
        }
    }
}