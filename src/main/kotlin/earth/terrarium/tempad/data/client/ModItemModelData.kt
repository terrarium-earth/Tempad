package earth.terrarium.tempad.data.client

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.common.registries.ModItems
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.client.model.generators.ModelFile
import net.neoforged.neoforge.common.data.ExistingFileHelper

class ModItemModelData(output: PackOutput, fileHelper: ExistingFileHelper) : ItemModelProvider(output, Tempad.MOD_ID, fileHelper) {
    override fun registerModels() {
        getBuilder("tempad").apply {
            parent(ModelFile.UncheckedModelFile("item/generated"))
            texture("layer0", "tempad:item/tempad/base")
            for (attached in arrayOf(0f, 1f)) {
                for (inUse in arrayOf(0f, 1f)) {
                    for ((index, charge) in arrayOf(0f, 0.33f, 0.66f, 1f).withIndex()) {
                        var path = "tempad:item/tempad/"
                        path += (if (attached == 1f) "attached" else "base")
                        path += "_"
                        path += (if (inUse == 1f) "in_use" else "idle")
                        path += "_$index"

                        override().apply {
                            predicate("attached".tempadId, attached)
                            predicate("in_use".tempadId, inUse)
                            predicate("charge".tempadId, charge)

                            model(getBuilder(path).apply {
                                parent(ModelFile.UncheckedModelFile("item/generated"))

                                var layer = 0
                                texture("layer0", "item/tempad/base${if (attached == 1f) "_with_twister" else ""}".tempadId)

                                if (inUse == 1f) {
                                    texture("layer" + ++layer, "item/tempad/screen_on".tempadId)
                                }

                                if (charge > 0) {
                                    texture("layer"+ ++layer, "item/tempad/charge_${index}")
                                }
                            })
                        }
                    }
                }
            }
        }

        basicItem(ModItems.chronometer).chargedLayered("chronometer")
        basicItem(ModItems.chrononGenerator).chargedLayered("chronon_generator")

        basicItem(ModItems.chrononCell).charged("chronon_cell")
        basicItem(ModItems.chrononBattery).charged("chronon_battery")

        basicItem(ModItems.locationBroadcaster).booleanProp("enabled")
        basicItem(ModItems.screeningDevice).booleanProp("enabled")

        basicItem(ModItems.locationCard).booleanProp("written")
        withExistingParent("tempad:item/timedoor_projector", "tempad:block/timedoor_projector_off")
            .override()
            .predicate("has_card".tempadId, 1f)
            .model(withExistingParent("tempad:item/timedoor_projector_with_card", "tempad:block/timedoor_projector_off_with_card"))

        basicItem(ModItems.timeTwister)
        basicItem(ModItems.newLocationUpgrade)
        basicItem(ModItems.playerTeleportUpgrade)
        basicItem(ModItems.timeSteel)
        basicItem(ModItems.creativeChronometer)
        basicItem(ModItems.cardWallet).booleanProp("full")
        simpleBlockItem(ModBlocks.timedoorMarker)
        simpleBlockItem(ModBlocks.chronomark)
        simpleBlockItem(ModBlocks.workstation)
        simpleBlockItem(ModBlocks.metronome)
    }

    fun ItemModelBuilder.booleanProp(propName: String): ItemModelBuilder {
        return this.override()
            .predicate(propName.tempadId, 1f)
            .model(getBuilder(this.location.toString() + "_$propName").apply {
                parent(ModelFile.UncheckedModelFile("item/generated"))
                texture("layer0", ResourceLocation.fromNamespaceAndPath(this.location.namespace, this.location.path))
            })
            .end()
    }

    fun ItemModelBuilder.charged(name: String) {
        for ((index, charge) in arrayOf(0f, 0.33f, 0.66f, 1f).withIndex()) {
            override().apply {
                predicate("charge".tempadId, charge)

                model(getBuilder("tempad:${name}_${index}").apply {
                    parent(ModelFile.UncheckedModelFile("item/generated"))

                    texture("layer0", "tempad:item/$name/charge_${index}")
                })
            }
        }
    }

    fun ItemModelBuilder.chargedLayered(name: String) {
        for ((index, charge) in arrayOf(0f, 0.33f, 0.66f, 1f).withIndex()) {
            override().apply {
                predicate("charge".tempadId, charge)

                model(getBuilder("tempad:${name}_${index}").apply {
                    parent(ModelFile.UncheckedModelFile("item/generated"))

                    texture("layer0", "tempad:item/$name")
                    if (index > 0) texture("layer1", "tempad:item/$name/charge_${index}")
                })
            }
        }
    }
}