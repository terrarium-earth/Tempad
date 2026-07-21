package earth.terrarium.tempad.client.model

import com.mojang.serialization.MapCodec
import earth.terrarium.tempad.common.menu.AbstractTempadMenu
import earth.terrarium.tempad.common.registries.ModItems
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

object TempadInUseProperty : ConditionalItemModelProperty {
    val CODEC: MapCodec<TempadInUseProperty> = MapCodec.unit(TempadInUseProperty)

    override fun type() = CODEC

    override fun get(
        stack: ItemStack,
        level: ClientLevel?,
        entity: LivingEntity?,
        seed: Int,
        context: ItemDisplayContext,
    ): Boolean {
        if (entity !is Player) return false
        val menu = entity.containerMenu
        if (menu !is AbstractTempadMenu<*>) return false
        return menu.ctx.resource.matches(stack)
    }
}