package earth.terrarium.tempad.client

import net.minecraft.client.model.HumanoidModel
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions

object RudimentaryTempadClient: IClientItemExtensions {
    override fun getArmPose(
        entityLiving: LivingEntity,
        hand: InteractionHand,
        itemStack: ItemStack,
    ): HumanoidModel.ArmPose? {
        return super.getArmPose(entityLiving, hand, itemStack)
    }
}