package earth.terrarium.tempad.client

import net.minecraft.client.model.HumanoidModel
import net.minecraft.util.Mth
import net.minecraft.world.entity.HumanoidArm
import net.neoforged.fml.common.asm.enumextension.EnumProxy
import net.neoforged.neoforge.client.IArmPoseTransformer

object ModEnums {
    @JvmField
    val rudimentaryTempadPos: EnumProxy<HumanoidModel.ArmPose> = EnumProxy(HumanoidModel.ArmPose::class.java, true, true, IArmPoseTransformer {model, entity, arm ->
        (Mth.clamp(model.head.xRot, -1.2f, 1.2f) - 1f).let {
            model.rightArm.xRot = it
            model.leftArm.xRot = it
            when(arm) {
                HumanoidArm.RIGHT -> {
                    model.leftArm.xRot -= 0.55f
                    model.leftArm.yRot += 0.5f
                }
                HumanoidArm.LEFT -> {
                    model.rightArm.xRot -= 0.55f
                    model.rightArm.yRot -= 0.5f
                }
            }
        }
    })
}