package earth.terrarium.tempad.client

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.ItemPropertyFunction
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

fun interface BooleanItemPropertyFunction: ItemPropertyFunction {
    override fun call(pStack: ItemStack, pLevel: ClientLevel?, pEntity: LivingEntity?, pSeed: Int): Float {
        return if(invoke(pStack, pLevel, pEntity, pSeed)) 1f else 0f
    }

    fun invoke(pStack: ItemStack, pLevel: ClientLevel?, pEntity: LivingEntity?, pSeed: Int): Boolean
}