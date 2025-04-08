package earth.terrarium.tempad.data.client

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.ModItems
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.data.LanguageProvider

class ModLangData(output: PackOutput) : LanguageProvider(output, Tempad.MOD_ID, "en_us") {
    override fun addTranslations() {
        for (entry in ModItems.registry.entries) {
            try {
                add(entry.get(), entry.id.formatted)
            } catch (_: Exception) {}
        }

        for (entry in ModBlocks.blocks.entries) {
            try {
                add(entry.get(), entry.id.formatted)
            } catch (_: Exception) {}
        }

        ModItems.locationCard.apply {
            addSub("creator_toolip", "Created by %s")
            addSub("shift_toolip", "Hold [%s] for more info")
            addSub("add_location", "Right click to set location")
            addSub("added_location", "Added location: %s")
            addSub("dynamic_error", "You may not add player, spatial anchor, or other dynamic locations to this device")
            addSub("set_location", "Set location: %s")
            addSub("redeem", "%s: [%s] To Use")
            addSub("created_by", "Created by: %s")
            addSub("id", "ID: %s")
        }

        ModItems.cardWallet.apply {
            addSub("prefix", "Contains:")
            addSub("empty", "No cards")
        }

        ModBlocks.timedoorMarker.apply {
            addSub("owner_mismatch.place", "Error: User != Anchor Owner. Return to owner or Reset anchor")
            addSub("owner_mismatch.use", "Error: User != Anchor Owner. Only owner may edit options")
        }

        addRoot("app.tempad.teleport", "Teleport") {
            it.addSub("pin", "Pin")
            it.addSub("unpin", "Unpin")
            it.addSub("teleport", "Teleport")
            it.addSub("delete", "Delete")
            it.addSub("no_selection", "No location selected. Click on a location view details")
        }

        addRoot("app.tempad.settings", "Settings") {
            it.addSub("default_macro", "Default Macro")
            it.addSub("default_macro.subtitle", "Default macro to use when shift-clicking Tempad or pressing [F] key")
            it.addSub("default_app", "Default App")
            it.addSub("default_app.subtitle", "Default app to open when right-clicking Tempad")
            it.addSub("organization_method", "Organization")
            it.addSub("organization_method.subtitle", "Default way to organize shown locations on the Tempad")
        }

        addRoot("app.tempad.portal_setup", "Portal Setup") {
            it.addSub("offset", "Offset: (-/+)")
            it.addSub("left_right", "L/R: ")
            it.addSub("left_right.desc", "Positive places portal to your left, negative to your right \n\nAccepts decimal (e.g 1.3)")
            it.addSub("up_down", "U/D: ")
            it.addSub("up_down.desc", "Positive places portal higher, negative lower \n\nAccepts decimal (e.g 1.3)")
            it.addSub("front_back", "F/B: ")
            it.addSub("front_back.desc", "Positive places portal in front of you, negative behind you \n\nAccepts decimal (e.g 1.3)")
            it.addSub("angle", "Angle: ")
            it.addSub("upright", "Is upright: ")
        }

        addRoot("app.tempad.new_location", "New Location") {
            it.addSub("name", "Name:")
            it.addSub("color", "Color:")
        }

        addRoot("app.tempad.timeline", "Travel Timeline") {
            it.addSub("tracking", "Tracking")
            it.addSub("teleport", "Teleport")
        }
    }

    val ResourceLocation.formatted get() = path.split('_').joinToString(" ") { it.replaceFirstChar { it.uppercaseChar() } }

    fun Block.addSub(key: String, value: String) {
        add("$descriptionId.$key", value)
    }

    fun Item.addSub(key: String, value: String) {
        add("$descriptionId.$key", value)
    }

    fun String.addSub(key: String, value: String) {
        add("$this.$key", value)
    }

    fun addRoot(key: String, value: String, entries: (String) -> Unit = {}): String {
        add(key, value)
        entries.invoke(key)
        return key
    }
}