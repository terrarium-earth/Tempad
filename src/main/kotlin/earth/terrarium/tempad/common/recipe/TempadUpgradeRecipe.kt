package earth.terrarium.tempad.common.recipe

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.bytecodecs.StreamCodecByteCodec
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.ModRecipes
import earth.terrarium.tempad.common.registries.installedUpgrades
import earth.terrarium.tempad.common.utils.stack
import io.netty.buffer.ByteBuf
import net.minecraft.core.HolderLookup
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.item.crafting.SmithingRecipe
import net.minecraft.world.item.crafting.SmithingRecipeInput
import net.minecraft.world.level.Level
import net.neoforged.neoforge.items.ItemStackHandler

data class TempadUpgradeRecipe(val upgrade: Ingredient, val downloadTime: Int, val output: ResourceLocation): Recipe<UpgradeRecipeInput> {
    companion object Serializer: RecipeSerializer<TempadUpgradeRecipe> {
        val codec: MapCodec<TempadUpgradeRecipe> = RecordCodecBuilder.mapCodec {
            it.group(
                Ingredient.CODEC.fieldOf("item").forGetter(TempadUpgradeRecipe::upgrade),
                Codec.INT.fieldOf("download_time").forGetter(TempadUpgradeRecipe::downloadTime),
                ResourceLocation.CODEC.fieldOf("upgrade").forGetter(TempadUpgradeRecipe::output)
            ).apply(it, ::TempadUpgradeRecipe)
        }

        val byteCodec: StreamCodec<RegistryFriendlyByteBuf, TempadUpgradeRecipe> = StreamCodecByteCodec.toRegistry(ObjectByteCodec.create(
            ExtraByteCodecs.INGREDIENT.fieldOf(TempadUpgradeRecipe::upgrade),
            ByteCodec.INT.fieldOf(TempadUpgradeRecipe::downloadTime),
            ExtraByteCodecs.RESOURCE_LOCATION.fieldOf(TempadUpgradeRecipe::output),
            ::TempadUpgradeRecipe
        ))

        override fun codec(): MapCodec<TempadUpgradeRecipe> = codec
        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, TempadUpgradeRecipe> = byteCodec
    }

    override fun matches(input: UpgradeRecipeInput, level: Level): Boolean {
        return upgrade.test(input.upgrade)
    }

    override fun assemble(input: UpgradeRecipeInput, p_346030_: HolderLookup.Provider): ItemStack {
        val result = input.upgradable.copy()
        result.installedUpgrades += output
        return result
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean = false

    override fun getResultItem(registries: HolderLookup.Provider): ItemStack {
        return ModItems.tempad.stack {
            installedUpgrades += output
        }
    }

    override fun getSerializer(): RecipeSerializer<*> = Serializer

    override fun getType(): RecipeType<*> = ModRecipes.upgradeRecipe
}

data class UpgradeRecipeInput(val upgradable: ItemStack, val upgrade: ItemStack): RecipeInput {
    override fun getItem(index: Int): ItemStack = when (index) {
        0 -> upgradable
        1 -> upgrade
        else -> ItemStack.EMPTY
    }

    override fun size(): Int = 2
}