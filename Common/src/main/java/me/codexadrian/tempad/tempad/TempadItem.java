package me.codexadrian.tempad.tempad;

import me.codexadrian.tempad.client.gui.TempadScreen;
import me.codexadrian.tempad.entity.TimedoorEntity;
import me.codexadrian.tempad.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class TempadItem extends Item {

    public TempadItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);
        if (level.isClientSide) openScreen(player, interactionHand);
        return InteractionResultHolder.success(stack);
    }

    public static void summonTimeDoor(LocationData locationData, Player player) {
        TimedoorEntity timedoor = new TimedoorEntity(Services.REGISTRY.getTimedoor(), player.level);
        var dir = player.getDirection();
        timedoor.setColor(Services.COLOR.getColor(player));
        timedoor.setLocation(locationData);
        timedoor.setOwner(player.getUUID());
        var position = player.blockPosition().relative(dir, 3);
        timedoor.setPos(position.getX(), position.getY(), position.getZ());
        timedoor.setYRot(dir.getOpposite().toYRot());
        player.level.addFreshEntity(timedoor);
    }

    private void openScreen(Player player, InteractionHand interactionHand) {
        //int color = ColorDataComponent.COLOR_DATA.get(player).getColor();
        int color = Services.COLOR.getColor(player);
        Minecraft.getInstance().setScreen(new TempadScreen(color, interactionHand));
    }
}
