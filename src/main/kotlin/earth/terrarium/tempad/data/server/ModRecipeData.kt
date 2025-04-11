package earth.terrarium.tempad.data.server

import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.tempadId
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.conditions.ModLoadedCondition
import net.neoforged.neoforge.common.conditions.NotCondition
import java.util.concurrent.CompletableFuture

class ModRecipeData(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider>) :
    RecipeProvider(output, registries) {

    // Modded items, from ModItems.name
    val timeSteel = 'T'
    val cell = 'C'
    val battery = 'B'
    val locationBroadcaster = 'L'
    val card = 'p'

    // Vanilla items, from Items.NAME
    val copper = 'c'
    val glass = 'g'
    val tintedGlass = 'G'
    val redstoneLamp = 'R'
    val redstone = 'r'
    val leather = 'l'
    val quartz = 'Q'
    val compass = 'D'
    val button = 'b'
    val amethyst = 'A'
    val emerald = 'e'
    val enderPearl = 'E'
    val ironBlock = 'I'
    val iron = 'i'
    val gold = 'a'
    val blackDye = 'd'
    val paper = 'P'
    val enderChest = 'h'
    val driedKelp = 'k'
    val clock = 'K'
    val netheriteScrap = 's'

    val air = ' '

    // modded
    fun ShapedRecipeBuilder.timeSteel() = define(timeSteel, ModItems.timeSteel)

    fun ShapedRecipeBuilder.cell() = define(cell, ModItems.chrononCell)

    fun ShapedRecipeBuilder.battery() = define(battery, ModItems.chrononBattery)

    fun ShapedRecipeBuilder.card() = define(card, ModItems.locationCard)

    fun ShapedRecipeBuilder.locationBroadcaster() = define(locationBroadcaster, ModItems.locationBroadcaster)

    //vanilla
    fun ShapedRecipeBuilder.copper() = define(copper, Tags.Items.INGOTS_COPPER)

    fun ShapedRecipeBuilder.glass() = define(glass, Tags.Items.GLASS_BLOCKS_COLORLESS)

    fun ShapedRecipeBuilder.tintedGlass() = define(tintedGlass, Tags.Items.GLASS_BLOCKS_TINTED)

    fun ShapedRecipeBuilder.redstoneLamp() = define(redstoneLamp, Items.REDSTONE_LAMP)

    fun ShapedRecipeBuilder.redstone() = define(redstone, Tags.Items.DUSTS_REDSTONE)

    fun ShapedRecipeBuilder.leather() = define(leather, Tags.Items.LEATHERS)

    fun ShapedRecipeBuilder.quartz() = define(quartz, Tags.Items.GEMS_QUARTZ)

    fun ShapedRecipeBuilder.compass() = define(compass, Items.COMPASS)

    fun ShapedRecipeBuilder.button() = define(button, Items.STONE_BUTTON)

    fun ShapedRecipeBuilder.amethyst() = define(amethyst, Tags.Items.GEMS_AMETHYST)

    fun ShapedRecipeBuilder.emerald() = define(emerald, Tags.Items.GEMS_EMERALD)

    fun ShapedRecipeBuilder.enderPearl() = define(enderPearl, Tags.Items.ENDER_PEARLS)

    fun ShapedRecipeBuilder.ironBlock() = define(ironBlock, Tags.Items.STORAGE_BLOCKS_IRON)

    fun ShapedRecipeBuilder.iron() = define(iron, Tags.Items.INGOTS_IRON)

    fun ShapedRecipeBuilder.gold() = define(gold, Tags.Items.INGOTS_GOLD)

    fun ShapedRecipeBuilder.paper() = define(paper, Items.PAPER)

    fun ShapedRecipeBuilder.blackDye() = define(blackDye, Tags.Items.DYES_BLACK)

    fun ShapedRecipeBuilder.enderChest() = define(enderChest, Items.ENDER_CHEST)

    fun ShapedRecipeBuilder.driedKelp() = define(driedKelp, Items.DRIED_KELP)

    fun ShapedRecipeBuilder.clock() = define(clock, Items.CLOCK)

    fun ShapedRecipeBuilder.netheriteScrap() = define(netheriteScrap, Items.NETHERITE_SCRAP)

    fun ShapedRecipeBuilder.pattern(vararg char: Char): ShapedRecipeBuilder {
        pattern(char.joinToString(""))
        return this
    }

    fun ShapedRecipeBuilder.unlockedBy(item: Item): ShapedRecipeBuilder {
        unlockedBy(getHasName(item), has(item))
        return this
    }


    fun RecipeOutput.shaped(item: Item, count: Int = 1, name: String? = null, contents: ShapedRecipeBuilder.() -> Unit) {
        val recipe = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item, count)
        contents(recipe)
        if(name == null) recipe.save(this) else recipe.save(this, name)
    }

    fun RecipeOutput.shapeless(item: Item, count: Int = 1, name: String? = null, contents: ShapelessRecipeBuilder.() -> Unit) {
        val recipe = ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item, count)
        contents(recipe)
        if(name == null) recipe.save(this) else recipe.save(this, name)
    }

    fun RecipeOutput.clean(item: Item) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item)
            .requires(item)
            .unlockedBy(getHasName(item), has(item))
            .save(this, (BuiltInRegistries.ITEM.getKey(item).path + "_clean").tempadId)
    }

    override fun buildRecipes(recipes: RecipeOutput) {
        recipes.shaped(ModItems.tempad) {
            timeSteel()
            tintedGlass()
            enderPearl()
            quartz()
            redstoneLamp()
            battery()
            unlockedBy(ModItems.timeSteel)
            pattern(tintedGlass, tintedGlass, tintedGlass)
            pattern(quartz, enderPearl, redstoneLamp)
            pattern(timeSteel, battery, timeSteel)
        }

        recipes.shaped(ModItems.timedoorProjector) {
            copper()
            glass()
            cell()
            enderPearl()
            ironBlock()
            unlockedBy(Items.ENDER_PEARL)
            pattern(copper, copper, air)
            pattern(glass, enderPearl, cell)
            pattern(ironBlock, ironBlock, ironBlock)
        }

        recipes.shaped(ModItems.timedoorMarker) {
            iron()
            ironBlock()
            glass()
            enderPearl()
            unlockedBy(Items.ENDER_PEARL)
            pattern(iron, glass, iron)
            pattern(iron, enderPearl, iron)
            pattern(iron, ironBlock, iron)
        }

        recipes.shaped(ModItems.locationBroadcaster) {
            glass()
            iron()
            emerald()
            redstone()
            compass()
            unlockedBy(Items.COMPASS)
            pattern(air, glass, air)
            pattern(emerald, compass, redstone)
            pattern(air, iron, air)
        }

        recipes.shaped(ModItems.timeTwister) {
            timeSteel()
            enderPearl()
            tintedGlass()
            battery()
            unlockedBy(ModItems.timeSteel)
            pattern(timeSteel, tintedGlass, air)
            pattern(battery, enderPearl, tintedGlass)
            pattern(timeSteel, tintedGlass, air)
        }

        recipes.shaped(ModItems.workstation) {
            tintedGlass()
            copper()
            quartz()
            timeSteel()
            unlockedBy(ModItems.timeSteel)
            pattern(tintedGlass, tintedGlass, air)
            pattern(copper, copper, copper)
            pattern(timeSteel, quartz, timeSteel)
        }

        recipes.shaped(ModItems.chronomark) {
            iron()
            timeSteel()
            tintedGlass()
            ironBlock()
            enderPearl()
            unlockedBy(ModItems.timeSteel)
            pattern(iron, tintedGlass, iron)
            pattern(timeSteel, enderPearl, timeSteel)
            pattern(iron, ironBlock, iron)
        }

        recipes.shaped(ModItems.chrononCell) {
            iron()
            copper()
            amethyst()
            redstone()
            unlockedBy(Items.AMETHYST_SHARD)
            pattern(iron, copper, iron)
            pattern(redstone, amethyst, redstone)
            pattern(iron, copper, iron)
        }

        recipes.shaped(ModItems.chrononBattery) {
            iron()
            timeSteel()
            amethyst()
            cell()
            unlockedBy(ModItems.timeSteel)
            pattern(iron, timeSteel, iron)
            pattern(cell, amethyst, cell)
            pattern(iron, timeSteel, iron)
        }

        recipes.shaped(ModItems.chronometer) {
            tintedGlass()
            timeSteel()
            clock()
            battery()
            unlockedBy(ModItems.timeSteel)
            pattern(air, tintedGlass, air)
            pattern(timeSteel, clock, timeSteel)
            pattern(air, battery, air)
        }

        recipes.shaped(ModItems.chrononGenerator) {
            glass()
            iron()
            clock()
            cell()
            unlockedBy(Items.AMETHYST_SHARD)
            pattern(air, glass, air)
            pattern(iron, clock, iron)
            pattern(air, cell, air)
        }

        recipes.shaped(ModItems.screeningDevice) {
            tintedGlass()
            timeSteel()
            emerald()
            redstone()
            compass()
            unlockedBy(ModItems.timeSteel)
            pattern(air, tintedGlass, air)
            pattern(emerald, compass, redstone)
            pattern(air, timeSteel, air)
        }

        recipes.shaped(ModItems.metronome) {
            timeSteel()
            enderChest()
            battery()
            unlockedBy(ModItems.timeSteel)
            pattern(timeSteel, enderChest, timeSteel)
            pattern(timeSteel, battery, timeSteel)
            pattern(timeSteel, battery, timeSteel)
        }

        recipes.shaped(ModItems.cardWallet) {
            copper()
            leather()
            card()
            unlockedBy(Items.COPPER_INGOT)
            pattern(copper, air, copper)
            pattern(leather, card, leather)
            pattern(copper, copper, copper)
        }

        recipes.shaped(ModItems.playerTeleportUpgrade) {
            driedKelp()
            enderPearl()
            locationBroadcaster()
            unlockedBy(Items.DRIED_KELP)
            pattern(driedKelp, driedKelp, driedKelp)
            pattern(enderPearl, locationBroadcaster, enderPearl)
            pattern(driedKelp, driedKelp, driedKelp)
        }

        recipes.clean(ModItems.locationCard)
        recipes.clean(ModItems.timedoorMarker)
        recipes.clean(ModItems.chronomark)

        recipes.shapeless(ModItems.locationCard, 4, "tempad:card_cheap") {
            requires(Tags.Items.INGOTS_IRON)
            requires(Tags.Items.DYES)
            unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
        }

        recipes.shapeless(ModItems.locationCard, 16, "tempad:card_expensive") {
            requires(ModItems.timeSteel)
            requires(Tags.Items.DYES)
            unlockedBy(getHasName(ModItems.timeSteel), has(ModItems.timeSteel))
        }

        recipes.withConditions(NotCondition(ModLoadedCondition("create"))).shapeless(ModItems.timeSteel, 2, "tempad:time_steel_shapeless") {
            requires(Items.IRON_INGOT)
            requires(Items.NETHERITE_SCRAP)
            requires(Items.NETHERITE_SCRAP)
            requires(Items.IRON_INGOT)
            unlockedBy(getHasName(Items.NETHERITE_SCRAP), has(Items.NETHERITE_SCRAP))
        }

        recipes.withConditions(ModLoadedCondition("create")).shaped(ModItems.timeSteel, 2, "tempad:time_steel_shaped") {
            iron()
            netheriteScrap()
            unlockedBy(getHasName(Items.NETHERITE_SCRAP), has(Items.NETHERITE_SCRAP))
            pattern(netheriteScrap, iron)
            pattern(iron, netheriteScrap)
        }
    }
}