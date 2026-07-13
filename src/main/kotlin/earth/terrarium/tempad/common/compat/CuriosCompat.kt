package earth.terrarium.tempad.common.compat

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import earth.terrarium.tempad.Tempad.Companion.level
import earth.terrarium.tempad.api.Priority
import earth.terrarium.tempad.api.PriorityId
import earth.terrarium.tempad.api.access.ItemAccessAddress
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.api.access.ItemAccessRegistry
import earth.terrarium.tempad.api.access.ItemAccessAddressType
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.transfer.access.ItemAccess
import net.neoforged.neoforge.transfer.item.ItemResource
import net.neoforged.neoforge.transfer.item.PlayerInventoryWrapper
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal
import net.neoforged.neoforge.transfer.transaction.TransactionContext
import top.theillusivec4.curios.api.CuriosApi
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler

@Suppress("removal")
class CuriosItemAccess(val player: Player, val data: CuriosSlotInfo) : SnapshotJournal<CompoundTag>(), ItemAccess {
    val curios: IDynamicStackHandler = CuriosApi.getCuriosInventory(player).orElseThrow().curios[data.identifier]!!.stacks
    val inventory: PlayerInventoryWrapper = PlayerInventoryWrapper.of(player)

    override fun getResource(): ItemResource = ItemResource.of(curios.getStackInSlot(data.index))

    override fun getAmount(): Int = curios.getStackInSlot(data.index).count

    override fun insert(
        resource: ItemResource,
        amount: Int,
        transaction: TransactionContext,
    ): Int {
        updateSnapshots(transaction)
        val leftover = curios.insertItem(data.index, resource.toStack(amount), false).count
        if(leftover > 0) inventory.placeItemBackInInventory(resource, leftover, transaction)
        return amount
    }

    override fun extract(
        resource: ItemResource,
        amount: Int,
        transaction: TransactionContext,
    ): Int {
        updateSnapshots(transaction)
        if (!resource.matches(curios.getStackInSlot(data.index))) return 0
        return curios.extractItem(data.index, amount, false).count
    }

    override fun exchange(newResource: ItemResource, amount: Int, transaction: TransactionContext?): Int {
        val oldStack = curios.getStackInSlot(data.index)
        if (newResource.item == oldStack.item && oldStack.count == amount) {
            transaction?.let { updateSnapshots(it) }
            curios.setStackInSlot(data.index, newResource.toStack(amount))
            return amount
        } else {
            return super.exchange(newResource, amount, transaction)
        }
    }

    override fun createSnapshot(): CompoundTag = curios.serializeNBT(level?.registryAccess())

    override fun revertToSnapshot(snapshot: CompoundTag) {
        curios.deserializeNBT(level?.registryAccess(), snapshot)
    }

    companion object {
        val id = "curios".tempadId
        val type = ItemAccessAddressType(id, CuriosSlotInfo.codec)
    }

    class CuriosSlotInfo(val identifier: String, val index: Int) {
        companion object {
            val codec: ByteCodec<CuriosSlotInfo> = ObjectByteCodec.create(
                ByteCodec.STRING.fieldOf(CuriosSlotInfo::identifier),
                ByteCodec.INT.fieldOf(CuriosSlotInfo::index),
                ::CuriosSlotInfo
            )
        }
    }
}

fun initCuriosCompat() {
    ItemAccessRegistry.register(CuriosItemAccess.type, ::CuriosItemAccess)
    ItemAccessRegistry.registerLocator(PriorityId(CuriosItemAccess.id, Priority.HIGH)) { player, filter ->
        val inventory = CuriosApi.getCuriosInventory(player).orElseThrow().curios
        for ((identifier, handler) in inventory) {
            for (index in 0 until handler.stacks.slots) {
                val stack = handler.stacks.getStackInSlot(index)
                if (filter(stack)) {
                    return@registerLocator ItemAccessAddress(CuriosItemAccess.type, CuriosItemAccess.CuriosSlotInfo(identifier, index))
                }
            }
        }
        return@registerLocator null
    }
}