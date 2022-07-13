package me.codexadrian.tempad.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.codexadrian.tempad.Constants;
import me.codexadrian.tempad.client.widgets.TextButton;
import me.codexadrian.tempad.client.widgets.TimedoorSprite;
import me.codexadrian.tempad.network.messages.DeleteLocationPacket;
import me.codexadrian.tempad.network.messages.SummonTimedoorPacket;
import me.codexadrian.tempad.platform.Services;
import me.codexadrian.tempad.data.LocationData;
import me.codexadrian.tempad.data.TempadComponent;
import me.codexadrian.tempad.tempad.TempadItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RunProgramScreen extends Screen {
    private static final ResourceLocation GRID = new ResourceLocation(Constants.MODID, "textures/widget/tempad_grid.png");
    private final int color;

    private static final int WIDTH = 480;
    private static final int HEIGHT = 256;
    private int mouseMovement;
    private int listSize;
    private final InteractionHand hand;
    private boolean interfaceNeedsReload = false;
    private boolean listNeedsReload = false;
    private final TimedoorSprite timedoorSprite;
    private List<LocationData> allLocations;
    private List<TextButton> displayedLocations;
    private final List<Button> displayedInterfaceButtons = new ArrayList<>();
    private final List<Button> upNextButtons = new ArrayList<>();

    public RunProgramScreen(int color, InteractionHand hand) {
        super(Component.nullToEmpty(""));
        this.color = color;
        this.hand = hand;
        //displayedLocation = new TextButton(0, 0, 12, Component.nullToEmpty(""), color, true, (but)->{});
        //teleportButton = new TextButton(0, 0, 12, Component.nullToEmpty(""), color, true, (but)->{});
        //deleteLocationButton = new TextButton(0,0, 12, Component.nullToEmpty(""), color, true, (but)->{});
        timedoorSprite = new TimedoorSprite(0, 0, color, 16 * 9);
        allLocations = new ArrayList<>();
        displayedLocations = new ArrayList<>();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int wiggleRoom = listSize > 12 ? listSize - 12 : 0;
        mouseMovement = Math.min(wiggleRoom, Math.max(mouseMovement + (int) (delta * 10), 0));
        if(listSize > 12) listNeedsReload = true;
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    protected void init() {
        super.init();
        int offset = 3;
        timedoorSprite.changePosition((width - WIDTH) / 2 + 16 * 3, (height - HEIGHT) / 2 + offset + 16 * 2);
        ItemStack stack = minecraft.player.getItemInHand(this.hand);
        if(stack.hasTag()) {
            allLocations = new ArrayList<>(TempadComponent.fromStack(stack).getLocations());
            listSize = allLocations.size();
            List<LocationData> shownLocationData = new ArrayList<>();
            if(listSize > 12) {
                for(int i = 0; i < 12; i++) {
                    shownLocationData.add(allLocations.get(i));
                }
            } else {
                shownLocationData = allLocations;
            }

            int x = (width - WIDTH) / 2 + offset + 16 * 15;
            int y = (height - HEIGHT) / 2 + offset + 16 * 2;
            Collections.sort(shownLocationData);
            for(LocationData data : shownLocationData) {
                var locationButton = new TextButton(x, y, 12, Component.literal(data.getName()), color, (button) -> locationButtonOnPress(data));
                this.displayedLocations.add(locationButton);
                addRenderableWidget(locationButton);
                y+=16;
            }
        }

        TextButton addLocation = new TextButton((width - WIDTH) / 2 + offset + 16 * 15, (height - HEIGHT) / 2 + offset + 16 * 14, 12, Component.translatable("gui." + Constants.MODID + ".new_location"), color, (button)->{
          minecraft.setScreen(new NewLocationScreen(color, hand));
        });

        addRenderableWidget(addLocation);
    }

    private void locationButtonOnPress(LocationData data) {
        String locationName = data.getBlockPos().toShortString();
        TextButton displayedLocation = new TextButton((width - WIDTH) / 2 + 16 * 8 - (int)(font.width(locationName) * .75) - 8, (height - HEIGHT) / 2 + 3 + 16 * 11,12, Component.literal(locationName), color, (button1) -> {});
        var teleportText = Component.translatable("gui." + Constants.MODID + ".teleport");
        Instant timeUntilUsable = null;
        ItemStack itemInHand = minecraft.player.getItemInHand(hand);
        if(itemInHand.hasTag() && itemInHand.getTag().contains(Constants.TIMER_NBT)) timeUntilUsable = Instant.ofEpochSecond(itemInHand.getTag().getLong(Constants.TIMER_NBT));
        TextButton teleportButton = new TextButton((width - WIDTH) / 2 + 16 * 8 - (int)(font.width(teleportText) * .75) - 8, (height - HEIGHT) / 2 + 3 + 16 * 12,12, teleportText, color, (button2) -> teleportAction(data), timeUntilUsable);
        var deleteText = Component.translatable("gui." + Constants.MODID + ".delete");
        TextButton deleteLocationButton = new TextButton((width - WIDTH) / 2 + 16 * 8 - (int)(font.width(deleteText) * .75) - 8, (height - HEIGHT) / 2 + 3 + 16 * 13,12, deleteText, color, (button2) ->{
            Minecraft.getInstance().setScreen(null);
            Services.NETWORK.sendToServer(new DeleteLocationPacket(data.getId(), hand));
        });
        upNextButtons.add(displayedLocation);
        upNextButtons.add(teleportButton);
        upNextButtons.add(deleteLocationButton);
        this.interfaceNeedsReload = true;
    }

    private void teleportAction(LocationData data) {
        if(minecraft == null || minecraft.player == null) return;
        ItemStack itemInHand = minecraft.player.getItemInHand(hand);
        if(itemInHand.hasTag() && itemInHand.getItem() instanceof TempadItem tempadItem) {
            if(tempadItem.getOption().canTimedoorOpen(minecraft.player, itemInHand)) {
                Minecraft.getInstance().setScreen(null);
                Services.NETWORK.sendToServer(new SummonTimedoorPacket(data.getLevelKey().location(), data.getBlockPos(), hand, color));
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(this.interfaceNeedsReload) {
            displayedInterfaceButtons.forEach(this::removeWidget);
            displayedInterfaceButtons.clear();
            displayedInterfaceButtons.addAll(upNextButtons);
            upNextButtons.clear();
            displayedInterfaceButtons.forEach(this::addRenderableWidget);
            removeWidget(timedoorSprite);
            addRenderableWidget(timedoorSprite);
            this.interfaceNeedsReload = false;
        }
        int x = (width - WIDTH) / 2 + 3 + 16 * 15;
        int y = (height - HEIGHT) / 2 + 3 + 16 * 2;
        if(this.listNeedsReload) {

            for(TextButton button : displayedLocations) {
                removeWidget(button);
            }
            displayedLocations = new ArrayList<>();
            int offset = Math.min(mouseMovement, listSize - 12);
            for(int i = offset; i < 12 + offset; i++) {
                LocationData data = allLocations.get(i);
                displayedLocations.add(new TextButton(x, y, 12, Component.literal(data.getName()), color, (button -> locationButtonOnPress(data))));
                y+=16;
            }
            displayedLocations.forEach(this::addRenderableWidget);
            this.listNeedsReload = false;
        }
    }

    private void renderOutline(PoseStack poseStack) {
        int lineWidth = 4;
        fill(poseStack, (width - WIDTH - lineWidth) / 2, (height - HEIGHT - lineWidth) / 2, (width + WIDTH + lineWidth) / 2, (height + HEIGHT + lineWidth) / 2, color | 0xFF000000);
    }

    private void renderGridBackground(PoseStack poseStack, float red, float green, float blue) {
        RenderSystem.setShaderTexture(0, GRID);
        RenderSystem.setShaderColor(red * 0.5f, green * 0.5f, blue * 0.5f, 1f);
        blit(poseStack, (width - WIDTH) / 2, (height - HEIGHT) / 2, WIDTH, HEIGHT, 0, 0, WIDTH, HEIGHT, 16, 16);
    }

    @Override
    public void renderBackground(PoseStack poseStack, int offset) {
        super.renderBackground(poseStack, offset);
        float red = (color >> 16 & 0xFF) / 255f;
        float green = (color >> 8 & 0xFF) / 255f;
        float blue = (color & 0xFF) / 255f;
        renderOutline(poseStack);
        renderGridBackground(poseStack, red, green, blue);
        renderHeaders(poseStack);
    }

    private void renderHeaders(PoseStack matrices) {
        Font font = minecraft.font;
        int cornerX = (width - WIDTH) / 2 + 3;
        int cornerY = (height - HEIGHT) / 2 + 3;
        int x = cornerX + 16 * 15;
        int y = cornerY + 16;
        matrices.pushPose();
        matrices.translate(x * (-0.5), y * (-0.5), 0);
        matrices.scale(1.5F, 1.5F, 0);
        drawString(matrices, font, Component.translatable("gui." + Constants.MODID + ".select_location"), x, y, color);
        matrices.popPose();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
