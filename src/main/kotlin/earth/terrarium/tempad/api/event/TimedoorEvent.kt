package earth.terrarium.tempad.api.event

import earth.terrarium.tempad.api.fuel.ItemContext
import earth.terrarium.tempad.common.entity.TimedoorEntity
import net.minecraft.world.entity.Entity
import net.neoforged.bus.api.ICancellableEvent
import net.neoforged.neoforge.event.entity.EntityEvent

open class TimedoorEvent(timedoor: TimedoorEntity): EntityEvent(timedoor) {
    override fun getEntity(): TimedoorEntity {
        return super.getEntity() as TimedoorEntity
    }

    class Open(timedoor: TimedoorEntity, val tempadCtx: ItemContext): TimedoorEvent(timedoor), ICancellableEvent
    class Close(timedoor: TimedoorEntity): TimedoorEvent(timedoor)
    class Enter(timedoor: TimedoorEntity, val teleportee: Entity): TimedoorEvent(timedoor), ICancellableEvent
    class Exit(timedoor: TimedoorEntity, val teleportee: Entity): TimedoorEvent(timedoor)
}