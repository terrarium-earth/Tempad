package earth.terrarium.tempad.common.registries

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import earth.terrarium.tempad.api.Priority
import earth.terrarium.tempad.api.PriorityId
import earth.terrarium.tempad.api.access.ItemAccessAddress
import earth.terrarium.tempad.api.access.ItemAccessRegistry
import earth.terrarium.tempad.api.access.ItemAccessAddressType
import earth.terrarium.tempad.api.access.WorkstationItemAccess
import earth.terrarium.tempad.common.compat.initCuriosCompat
import earth.terrarium.tempad.tempadId
import net.minecraft.core.BlockPos
import net.neoforged.fml.ModList
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.transfer.access.ItemAccess

object ModItemAccess {
    val inventory: ItemAccessAddressType<Int> = ItemAccessAddressType("inventory".tempadId, ByteCodec.INT)
    val carriedMenu: ItemAccessAddressType<Unit> = ItemAccessAddressType("carried_menu".tempadId, ByteCodec.unit(Unit))
    val block: ItemAccessAddressType<BlockPos> = ItemAccessAddressType("block".tempadId, ExtraByteCodecs.BLOCK_POS)

    fun init() {
        ItemAccessRegistry.register(inventory, ItemAccess::forPlayerSlot)
        ItemAccessRegistry.register(carriedMenu) { player, _ -> ItemAccess.forPlayerCursor(player, player.containerMenu) }
        ItemAccessRegistry.register(block) { player, pos -> WorkstationItemAccess(player.level(), pos) }

        if (ModList.get().isLoaded("curios")) {
            initCuriosCompat()
        }

        ItemAccessRegistry.registerLocator(PriorityId("inventory".tempadId, Priority.NORMAL)) { player, filter ->
            for (index in 0 until player.inventory.containerSize) {
                val stack = player.inventory.getItem(index)
                if (filter(stack)) {
                    return@registerLocator ItemAccessAddress(inventory, index)
                }
            }
            return@registerLocator null
        }
    }
}