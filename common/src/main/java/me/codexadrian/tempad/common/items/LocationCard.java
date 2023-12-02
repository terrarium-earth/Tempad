package me.codexadrian.tempad.common.items;

import com.teamresourceful.resourcefullib.common.utils.CommonUtils;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.data.TempadLocationHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class LocationCard extends Item {
    public LocationCard(Properties properties) {
        super(properties);
    }

    public static void setLocation(ItemStack stack, LocationData locationData, String creator) {
        stack.getOrCreateTag().put("Location", locationData.toTag());
        stack.getOrCreateTag().putString("Creator", creator);
    }

    @Nullable
    public static LocationData getLocation(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains("Location")) return null;
        return LocationData.fromTag(stack.getOrCreateTag().getCompound("Location"));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);

        var location = getLocation(stack);

        if (location != null) {
            tooltipComponents.add(Component.translatable("item.tempad.location_card.shift_toolip", Component.literal("SHIFT").withStyle(Screen.hasShiftDown() ? ChatFormatting.WHITE : ChatFormatting.GOLD)).withStyle(ChatFormatting.GRAY));

            if (Screen.hasShiftDown()) {
                tooltipComponents.add(Component.literal(location.getName()).withStyle(ChatFormatting.GRAY));
                tooltipComponents.add(Component.translatable("gui.tempad.x", location.getBlockPos().getX()).withStyle(ChatFormatting.GRAY));
                tooltipComponents.add(Component.translatable("gui.tempad.y", location.getBlockPos().getY()).withStyle(ChatFormatting.GRAY));
                tooltipComponents.add(Component.translatable("gui.tempad.z", location.getBlockPos().getZ()).withStyle(ChatFormatting.GRAY));
                tooltipComponents.add(Component.translatable("gui.tempad.dimension", Component.translatable(location.getLevelKey().location().toLanguageKey("dimension"))).withStyle(ChatFormatting.GRAY));

                if (stack.hasTag() && stack.getTag().contains("Creator")) {
                    tooltipComponents.add(Component.translatable("item.tempad.location_card.creator_toolip", Component.translatable(stack.getTag().getString("Creator")).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY));
                }
            }
        } else {
            tooltipComponents.add(Component.translatable("item.tempad.location_card.add_location").withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(Component.translatable("item.tempad.location_card.set_name").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide) {
            var location = getLocation(player.getItemInHand(usedHand));
            if (location != null) {
                TempadLocationHandler.addLocation(level, player.getUUID(), location);
                player.getItemInHand(usedHand).shrink(1);
                player.displayClientMessage(Component.translatable("item.tempad.location_card.added_location", Component.literal(location.getName()).withStyle(ChatFormatting.GOLD)), true);
            } else {
                var newLocation = new LocationData(player.getItemInHand(usedHand).getHoverName().getString(), level.dimension(), player.blockPosition(), CommonUtils.generate(id -> !TempadLocationHandler.containsLocation(level, player.getUUID(), id), UUID::randomUUID));
                setLocation(player.getItemInHand(usedHand), newLocation, player.getDisplayName().getString());
                player.displayClientMessage(Component.translatable("item.tempad.location_card.set_location", Component.literal(newLocation.getName()).withStyle(ChatFormatting.GOLD)), true);
            }
            return InteractionResultHolder.success(player.getItemInHand(usedHand));
        } else {
            return InteractionResultHolder.pass(player.getItemInHand(usedHand));
        }
    }
}
