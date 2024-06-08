package earth.terrarium.tempad.common.items;

import earth.terrarium.tempad.api.locations.LocationApi;
import earth.terrarium.tempad.common.data.LocationData;
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
                tooltipComponents.add(Component.literal(location.name()).withStyle(ChatFormatting.GRAY));
                tooltipComponents.add(Component.translatable("gui.tempad.x", location.blockPos().getX()).withStyle(ChatFormatting.GRAY));
                tooltipComponents.add(Component.translatable("gui.tempad.y", location.blockPos().getY()).withStyle(ChatFormatting.GRAY));
                tooltipComponents.add(Component.translatable("gui.tempad.z", location.blockPos().getZ()).withStyle(ChatFormatting.GRAY));
                tooltipComponents.add(Component.translatable("gui.tempad.dimension", Component.translatable(location.levelKey().location().toLanguageKey("dimension"))).withStyle(ChatFormatting.GRAY));

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
                LocationApi.API.add(level, player.getUUID(), location);
                player.getItemInHand(usedHand).shrink(1);
                player.displayClientMessage(Component.translatable("item.tempad.location_card.added_location", Component.literal(location.name()).withStyle(ChatFormatting.GOLD)), true);
            } else {
                ItemStack cardStack = player.getItemInHand(usedHand);
                var newLocation = new LocationData(cardStack.getHoverName().getString(), level.dimension(), player.blockPosition());
                ItemStack stack = new ItemStack(this);
                setLocation(stack, newLocation, player.getDisplayName().getString());
                if (cardStack.getCount() > 1) {
                    cardStack.shrink(1);
                    player.getInventory().placeItemBackInInventory(stack);
                } else {
                    player.setItemInHand(usedHand, stack);
                }
                player.displayClientMessage(Component.translatable("item.tempad.location_card.set_location", Component.literal(newLocation.name()).withStyle(ChatFormatting.GOLD)), true);
            }
            return InteractionResultHolder.success(player.getItemInHand(usedHand));
        } else {
            return InteractionResultHolder.pass(player.getItemInHand(usedHand));
        }
    }
}
