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
import net.minecraft.core.HolderLookup
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.Level

data class TempadUpgradeRecipe(val upgrade: Ingredient, val downloadTime: Int, val output: Identifier): Recipe<UpgradeRecipeInput> {
    companion object {
        val codec: MapCodec<TempadUpgradeRecipe> = RecordCodecBuilder.mapCodec {
            it.group(
                Ingredient.CODEC.fieldOf("item").forGetter(TempadUpgradeRecipe::upgrade),
                Codec.INT.fieldOf("download_time").forGetter(TempadUpgradeRecipe::downloadTime),
                Identifier.CODEC.fieldOf("upgrade").forGetter(TempadUpgradeRecipe::output)
            ).apply(it, ::TempadUpgradeRecipe)
        }

        val byteCodec: StreamCodec<RegistryFriendlyByteBuf, TempadUpgradeRecipe> = StreamCodecByteCodec.toRegistry(ObjectByteCodec.create(
            ExtraByteCodecs.INGREDIENT.fieldOf(TempadUpgradeRecipe::upgrade),
            ByteCodec.INT.fieldOf(TempadUpgradeRecipe::downloadTime),
            ExtraByteCodecs.IDENTIFIER.fieldOf(TempadUpgradeRecipe::output),
            ::TempadUpgradeRecipe
        ))

        val serializer = RecipeSerializer(codec, byteCodec)
    }

    override fun matches(input: UpgradeRecipeInput, level: Level): Boolean {
        return upgrade.test(input.upgrade)
    }

    override fun assemble(input: UpgradeRecipeInput): ItemStack {
        val result = input.upgradable.copy()
        result.installedUpgrades += output
        return result
    }

    override fun showNotification(): Boolean = false

    override fun group(): String = ""

    override fun getSerializer(): RecipeSerializer<out Recipe<UpgradeRecipeInput>> = Companion.serializer

    override fun getType(): RecipeType<out Recipe<UpgradeRecipeInput>> = ModRecipes.upgradeRecipe

    override fun placementInfo(): PlacementInfo = PlacementInfo.NOT_PLACEABLE

    override fun recipeBookCategory(): RecipeBookCategory = ModRecipes.upgradeRecipeBook
}

data class UpgradeRecipeInput(val upgradable: ItemStack, val upgrade: ItemStack): RecipeInput {
    override fun getItem(index: Int): ItemStack = when (index) {
        0 -> upgradable
        1 -> upgrade
        else -> ItemStack.EMPTY
    }

    override fun size(): Int = 2
}