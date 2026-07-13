package earth.terrarium.tempad.client.model

import com.mojang.serialization.MapCodec
import earth.terrarium.tempad.common.registries.ModComponents
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

object WalletFullProperty : ConditionalItemModelProperty {
    val MAP_CODEC: MapCodec<WalletFullProperty> = MapCodec.unit(WalletFullProperty)

    override fun get(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int, context: ItemDisplayContext): Boolean {
        val contents = stack.get(ModComponents.walletContents) ?: return false
        return contents.slots >= 18 && (0 until 18).none { contents.getStackInSlot(it).isEmpty }
    }

    override fun type(): MapCodec<out ConditionalItemModelProperty> = MAP_CODEC
}