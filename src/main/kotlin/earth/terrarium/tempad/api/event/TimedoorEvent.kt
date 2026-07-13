package earth.terrarium.tempad.api.event

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.common.entity.TimedoorEntity
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.bus.api.ICancellableEvent
import net.neoforged.neoforge.event.entity.EntityEvent
import net.neoforged.neoforge.transfer.access.ItemAccess
import java.util.*

open class TimedoorEvent(timedoor: TimedoorEntity) : EntityEvent(timedoor) {
    override fun getEntity(): TimedoorEntity {
        return super.getEntity() as TimedoorEntity
    }

    open class Open(timedoor: TimedoorEntity, val provider: Identifier?, val locationId: UUID?, val opener: GameProfile) :
        TimedoorEvent(timedoor), ICancellableEvent {
        var errorMessage: Component? = null

        /**
         * If you cancel the event, you should provide a reason why.
         */
        fun fail(message: Component) {
            isCanceled = true
            errorMessage = message
        }
    }

    class OpenWithItem(
        timedoor: TimedoorEntity, opener: GameProfile, val tempadCtx: ItemAccess,
        provider: Identifier?, locationId: UUID?,
    ) : Open(timedoor, provider, locationId, opener)

    class OpenWithBlock(
        timedoor: TimedoorEntity, opener: GameProfile, val block: BlockEntity,
        provider: Identifier?, locationId: UUID?,
    ) : Open(timedoor, provider, locationId, opener)

    class Close(timedoor: TimedoorEntity) : TimedoorEvent(timedoor)
    class Enter(timedoor: TimedoorEntity, val teleportee: Entity) : TimedoorEvent(timedoor), ICancellableEvent
    class Exit(timedoor: TimedoorEntity, val teleportee: Entity) : TimedoorEvent(timedoor)
}