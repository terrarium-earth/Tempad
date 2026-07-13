package earth.terrarium.tempad.client

import com.mojang.serialization.MapCodec
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.registries.color
import net.minecraft.client.color.item.ItemTintSource
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

class TempadColorItemTintSource : ItemTintSource {
    override fun calculate(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?): Int {
        return stack.color?.value ?: Tempad.ORANGE.value
    }

    override fun type(): MapCodec<out ItemTintSource> = CODEC

    companion object {
        val CODEC: MapCodec<TempadColorItemTintSource> = MapCodec.unit(TempadColorItemTintSource())
    }
}
