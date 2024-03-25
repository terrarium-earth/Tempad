package me.codexadrian.tempad.common.utils;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import earth.terrarium.baubly.common.BaubleUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record LookupLocation(String id, int slot) {
    public static final ByteCodec<LookupLocation> CODEC = ObjectByteCodec.create(
        ByteCodec.STRING_COMPONENT.fieldOf(LookupLocation::id),
        ByteCodec.INT.fieldOf(LookupLocation::slot),
        LookupLocation::new
    );

    public ItemStack findItem(Player player) {
        if (id.equals("main")) {
            return player.getInventory().getItem(slot);
        }
        return BaubleUtils.getBaubleContainer(player, id).getItem(slot);
    }
}