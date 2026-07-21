package earth.terrarium.tempad.common.utils

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.SlotAccess
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.capabilities.*
import net.neoforged.neoforge.transfer.ResourceHandler
import net.neoforged.neoforge.transfer.ResourceHandlerUtil
import net.neoforged.neoforge.transfer.access.ItemAccess
import net.neoforged.neoforge.transfer.energy.EnergyHandler
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil
import net.neoforged.neoforge.transfer.fluid.FluidResource
import net.neoforged.neoforge.transfer.item.ItemResource
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper
import net.neoforged.neoforge.transfer.resource.Resource
import net.neoforged.neoforge.transfer.resource.ResourceStack
import net.neoforged.neoforge.transfer.transaction.Transaction
import net.neoforged.neoforge.transfer.transaction.TransactionContext
import java.util.function.Predicate

class BlockCapabilityHelper<T : Any, C>(val event: RegisterCapabilitiesEvent, val capability: BlockCapability<T, C>) {
    operator fun set(vararg blocks: Block, provider: IBlockCapabilityProvider<T, C>) {
        event.registerBlock(capability, provider, *blocks)
    }

    operator fun set(vararg blocks: BlockEntityType<*>, provider: ICapabilityProvider<BlockEntity, C, T>) {
        blocks.forEach { event.registerBlockEntity(capability, it, provider) }
    }
}

fun <T : Any, C> RegisterCapabilitiesEvent.register(capability: BlockCapability<T, C>) = BlockCapabilityHelper(this, capability)

class ItemCapabilityHelper<T : Any, C>(val event: RegisterCapabilitiesEvent, val capability: ItemCapability<T, C>) {
    operator fun set(vararg items: Item, provider: ICapabilityProvider<ItemStack, C, T>) {
        items.forEach { event.registerItem(capability, provider, it) }
    }
}

fun <T : Any, C> RegisterCapabilitiesEvent.register(capability: ItemCapability<T, C>) = ItemCapabilityHelper(this, capability)

class EntityCapabilityHelper<T : Any, C>(val event: RegisterCapabilitiesEvent, val capability: EntityCapability<T, C>) {
    operator fun set(vararg entities: EntityType<*>, provider: ICapabilityProvider<Entity, C, T>) {
        entities.forEach { event.registerEntity(capability, it, provider) }
    }
}

fun <T : Any, C> RegisterCapabilitiesEvent.register(capability: EntityCapability<T, C>) = EntityCapabilityHelper(this, capability)

fun EnergyHandler.testExtract(amount: Int): Boolean {
    Transaction.openRoot().use {
        val extracted = this.extract(amount, it)
        if (extracted != amount) {
            return false
        }
    }
    return true
}

fun EnergyHandler.testInsert(amount: Int): Boolean {
    Transaction.openRoot().use {
        val inserted = this.insert(amount, it)
        if (inserted != amount) {
            return false
        }
    }
    return true
}

fun ResourceHandler<ItemResource>.access(slot: Int): ItemAccess = ItemAccess.forHandlerIndex(this, slot)

val ItemStack.access: ItemAccess
    get() = ItemAccess.forStack(this)

val Slot.access: ItemAccess
    get() = ItemAccess.forHandlerIndex(VanillaContainerWrapper.of(this.container), this.index)

class SlotAccessItemAccess(val slotAccess: SlotAccess): ItemAccess {
    override fun getResource(): ItemResource = ItemResource.of(slotAccess.get())

    override fun getAmount(): Int = slotAccess.get().count

    override fun insert(
        resource: ItemResource,
        amount: Int,
        transaction: TransactionContext,
    ): Int {
        if (resource.matches(slotAccess.get())) {
            val stack = slotAccess.get()
            val insertAmount = (stack.maxStackSize - stack.count).coerceAtMost(amount)
            if (insertAmount > 0) {
                val newStack = stack.copy()
                newStack.count += insertAmount
                slotAccess.set(newStack)
                return insertAmount
            }
        } else if (slotAccess.get().isEmpty) {
            val insertAmount = amount.coerceAtMost(resource.maxStackSize)
            val newStack = resource.toStack(insertAmount)
            slotAccess.set(newStack)
            return insertAmount
        }
        return 0
    }

    override fun extract(
        resource: ItemResource,
        amount: Int,
        transaction: TransactionContext,
    ): Int {
        val stack = slotAccess.get()
        if (resource.matches(stack)) {
            val extractAmount = stack.count.coerceAtMost(amount)
            if (extractAmount > 0) {
                val newStack = stack.copy()
                newStack.count -= extractAmount
                slotAccess.set(newStack)
                return extractAmount
            }
        }
        return 0
    }
}

val SlotAccess.access: ItemAccess
    get() = SlotAccessItemAccess(this)

context(ctx: TransactionContext)
fun EnergyHandler.insert(amount: Int): Int = this.insert(amount, ctx)

context(ctx: TransactionContext)
fun EnergyHandler.extract(amount: Int): Int = this.extract(amount, ctx)

context(ctx: TransactionContext)
fun <T: Resource> ResourceHandler<T>.insert(index: Int, resource: T, amount: Int): Int = this.insert(index, resource, amount, ctx)

context(ctx: TransactionContext)
fun <T: Resource> ResourceHandler<T>.insert(resource: T, amount: Int): Int = this.insert(resource, amount, ctx)

context(ctx: TransactionContext)
fun <T: Resource> ResourceHandler<T>.insertStacking(resource: T, amount: Int): Int = ResourceHandlerUtil.insertStacking(this, resource, amount, ctx)

context(ctx: TransactionContext)
fun <T: Resource> ResourceHandler<T>.extract(index: Int, resource: T, amount: Int): Int = this.extract(index, resource, amount, ctx)

context(ctx: TransactionContext)
fun <T: Resource> ResourceHandler<T>.extractFirst(index: Int, filter: Predicate<T>, amount: Int): ResourceStack<T>? = ResourceHandlerUtil.extractFirst(this, filter, amount, ctx)

context(ctx: TransactionContext)
fun <T: Resource> ResourceHandler<T>.extract(resource: T, amount: Int): Int = this.extract(resource, amount, ctx)

fun transfer(block: context(Transaction) () -> Unit) {
    Transaction.openRoot().use {
        block(it)
    }
}

context(ctx: Transaction)
fun subTransfer(block: context(Transaction) () -> Unit) {
    Transaction.open(ctx).use {
        block(it)
    }
}

context(ctx: Transaction)
fun commit() {
    ctx.commit()
}

context(ctx: TransactionContext)
fun EnergyHandler.move(to: EnergyHandler, amount: Int): Int = EnergyHandlerUtil.move(this, to, amount, ctx)

context(ctx: TransactionContext)
fun <T: Resource> ResourceHandler<T>.move(to: ResourceHandler<T>, filter: Predicate<T>, amount: Int): Int = ResourceHandlerUtil.move(this, to, filter, amount, ctx)

context(ctx: TransactionContext)
fun <T: Resource> ResourceHandler<T>.moveStacking(to: ResourceHandler<T>, filter: Predicate<T>, amount: Int): Int = ResourceHandlerUtil.moveStacking(this, to, filter, amount, ctx)

context(ctx: TransactionContext)
fun <T: Resource> ResourceHandler<T>.moveFirst(to: ResourceHandler<T>, filter: Predicate<T>, amount: Int): ResourceStack<T>? = ResourceHandlerUtil.moveFirst(this, to, filter, amount, ctx)

context(ctx: TransactionContext)
fun <T: Resource> ResourceHandler<T>.moveFirstStacking(to: ResourceHandler<T>, filter: Predicate<T>, amount: Int):  ResourceStack<T>? = ResourceHandlerUtil.moveFirstStacking(this, to, filter, amount, ctx)

context(ctx: TransactionContext)
fun ItemAccess.insert(resource: ItemResource, amount: Int): Int = this.insert(resource, amount, ctx)

context(ctx: TransactionContext)
fun ItemAccess.extract(resource: ItemResource, amount: Int): Int = this.extract(resource, amount, ctx)

context(ctx: TransactionContext)
fun ItemAccess.exchange(resource: ItemResource, amount: Int): Int = this.exchange(resource, amount, ctx)

fun <T: Resource> ResourceHandler<T>.indexOf(resource: T): Int = ResourceHandlerUtil.indexOf(this, resource)

operator fun <T: Resource> ResourceHandler<T>.contains(resource: T): Boolean = ResourceHandlerUtil.contains(this, resource)

val ResourceHandler<*>.isFull: Boolean get() = ResourceHandlerUtil.isFull(this)

val ResourceHandler<*>.isEmpty: Boolean get() = ResourceHandlerUtil.isEmpty(this)

fun ResourceHandler<*>.isEmpty(index: Int) = this.getResource(index).isEmpty || this.getAmountAsInt(index) <= 0

fun ResourceHandler<ItemResource>.stack(index: Int) = getResource(index).toStack(getAmountAsInt(index))

fun ResourceHandler<FluidResource>.stack(index: Int) = getResource(index).toStack(getAmountAsInt(index))

fun <T: Resource> ResourceHandler<T>.isValid(resource: T): Boolean = ResourceHandlerUtil.isValid(this, resource)

val EnergyHandler.isFull: Boolean get() = EnergyHandlerUtil.isFull(this)

val EnergyHandler.isEmpty: Boolean get() = this.amountAsLong <= 0

val EnergyHandler.hasRoom: Boolean get() = this.amountAsLong < this.capacityAsLong

val ItemAccess.items: ResourceHandler<ItemResource>? get() = getCapability(Capabilities.Item.ITEM)
val ItemAccess.fluids: ResourceHandler<FluidResource>? get() = getCapability(Capabilities.Fluid.ITEM)
val ItemAccess.energy: EnergyHandler? get() = getCapability(Capabilities.Energy.ITEM)