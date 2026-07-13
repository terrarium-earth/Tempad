package earth.terrarium.tempad.client.model

import com.mojang.serialization.MapCodec
import earth.terrarium.tempad.api.capabilities.chronons
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.transfer.access.ItemAccess

object ChrononChargeProperty : RangeSelectItemModelProperty {
    val MAP_CODEC: MapCodec<ChrononChargeProperty> = MapCodec.unit(ChrononChargeProperty)

    override fun get(stack: ItemStack, level: ClientLevel?, entity: ItemOwner?, seed: Int): Float {
        val handler = ItemAccess.forStack(stack).chronons!!
        return if (handler.capacityAsLong == 0L) 0f else handler.amountAsLong / handler.capacityAsLong.toFloat()
    }

    override fun type(): MapCodec<out RangeSelectItemModelProperty> = MAP_CODEC
}