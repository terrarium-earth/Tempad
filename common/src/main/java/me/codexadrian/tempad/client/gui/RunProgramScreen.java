package me.codexadrian.tempad.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import me.codexadrian.tempad.client.widgets.TextButton;
import me.codexadrian.tempad.client.widgets.TextEntry;
import me.codexadrian.tempad.client.widgets.TimedoorSprite;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.network.NetworkHandler;
import me.codexadrian.tempad.common.network.messages.DeleteLocationPacket;
import me.codexadrian.tempad.common.network.messages.SummonTimedoorPacket;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.data.TempadComponent;
import me.codexadrian.tempad.common.tempad.TempadItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RunProgramScreen extends Screen {
    private static final ResourceLocation GRID = new ResourceLocation(Tempad.MODID, "textures/widget/tempad_grid.png");
    private final int color;

    private static final int WIDTH = 256;
    private static final int HEIGHT = 160;
    private int mouseMovement;
    private int listSize;
    private final InteractionHand hand;
    private boolean interfaceNeedsReload = false;
    private final TimedoorSprite timedoorSprite;
    private List<LocationData> allLocations;
    private final List<Button> displayedInterfaceButtons = new ArrayList<>();
    private final List<Button> upNextButtons = new ArrayList<>();

    public RunProgramScreen(int color, InteractionHand hand) {
        super(Component.nullToEmpty(""));
        this.color = color;
        this.hand = hand;
        timedoorSprite = new TimedoorSprite(0, 0, color, 96);
        allLocations = new ArrayList<>();
    }

    @Override
    protected void init() {
        super.init();
        int offset = 3;
        timedoorSprite.changePosition((width - WIDTH) / 2 + 24, (height - HEIGHT) / 2 + offset + 16);
        ItemStack stack = minecraft.player.getItemInHand(this.hand);
        if(stack.hasTag()) {
            allLocations = new ArrayList<>(TempadComponent.fromStack(stack).getLocations());
            int x = (width - WIDTH) / 2 + offset + 16 * 9;
            int y = (height - HEIGHT) / 2 + offset + 16 * 2;
            var list = addRenderableWidget(new SelectionList<TextEntry>(x, y, 16 * 6, 16 * 6 - 6, 10, textEntry -> { if(textEntry != null) locationButtonOnPress(textEntry.data); }));
            list.updateEntries(allLocations.stream().map(TextEntry::new).toList());
        }

        TextButton addLocation = new TextButton((width - WIDTH) / 2 + offset + 16 * 9, (height - HEIGHT) / 2 + offset + 16 * 8 + 3, Component.translatable("gui." + Tempad.MODID + ".new_location"), 0xFFFFFFFF, (button)->{
          minecraft.setScreen(new NewLocationScreen(color, hand));
        });

        addRenderableWidget(addLocation);
    }

    private void locationButtonOnPress(LocationData data) {
        String locationName = data.getBlockPos().toShortString();
        TextButton displayedLocation = new TextButton((width - WIDTH) / 2 + 72 - minecraft.font.width(locationName) / 2, (height - HEIGHT) / 2 + 3 + 16 * 7, Component.literal(locationName), color, (button1) -> {});

        var teleportText = Component.translatable("gui." + Tempad.MODID + ".teleport");
        TempadItem itemInHand = (TempadItem) minecraft.player.getItemInHand(hand).getItem();
        TextButton teleportButton = new TextButton((width - WIDTH) / 2 + 72 - minecraft.font.width(teleportText) / 2, (height - HEIGHT) / 2 + 3 + 16 * 7 + 10, teleportText, color, (button2) -> teleportAction(data), () -> itemInHand.getOption().canTimedoorOpen(minecraft.player, minecraft.player.getItemInHand(hand)));

        var deleteText = Component.translatable("gui." + Tempad.MODID + ".delete");
        TextButton deleteLocationButton = new TextButton((width - WIDTH) / 2 + 72 - minecraft.font.width(deleteText) / 2, (height - HEIGHT) / 2 + 3 + 16 * 7 + 20, deleteText, color, (button2) ->{
            Minecraft.getInstance().setScreen(null);
            NetworkHandler.CHANNEL.sendToServer(new DeleteLocationPacket(data.getId(), hand));
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
                NetworkHandler.CHANNEL.sendToServer(new SummonTimedoorPacket(data.getLevelKey().location(), data.getBlockPos(), hand, color));
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
    }

    private void renderOutline(@NotNull GuiGraphics graphics) {
        int lineWidth = 4;
        graphics.fill((width - WIDTH - lineWidth) / 2, (height - HEIGHT - lineWidth) / 2, (width + WIDTH + lineWidth) / 2, (height + HEIGHT + lineWidth) / 2, color | 0xFF000000);
    }

    private void renderGridBackground(@NotNull GuiGraphics graphics, float red, float green, float blue) {
        RenderSystem.setShaderTexture(0, GRID);
        RenderSystem.setShaderColor(red * 0.5f, green * 0.5f, blue * 0.5f, 1f);
        graphics.blit(GRID,(width - WIDTH) / 2, (height - HEIGHT) / 2, WIDTH, HEIGHT, 0, 0, WIDTH, HEIGHT, 16, 16);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics) {
        super.renderBackground(graphics);
        float red = (color >> 16 & 0xFF) / 255f;
        float green = (color >> 8 & 0xFF) / 255f;
        float blue = (color & 0xFF) / 255f;
        renderOutline(graphics);
        renderGridBackground(graphics, red, green, blue);
        RenderSystem.setShaderColor(red, green, blue, 1f);
        graphics.fill((width - WIDTH) / 2 + 16 * 9, (height - HEIGHT) / 2 + 16 * 2, (width - WIDTH) / 2 + 16 * 15, (height - HEIGHT) / 2 + 16 * 8, 0xFFFFFFFF);
        graphics.fill((width - WIDTH) / 2 + 16 * 9 + 2, (height - HEIGHT) / 2 + 16 * 2 + 2, (width - WIDTH) / 2 + 16 * 15 - 2, (height - HEIGHT) / 2 + 16 * 8 - 2, 0xFF131313);
        renderHeaders(graphics);
    }

    private void renderHeaders(@NotNull GuiGraphics graphics) {
        Font font = minecraft.font;
        int cornerX = (width - WIDTH) / 2 + 3;
        int cornerY = (height - HEIGHT) / 2 + 5;
        int x = cornerX + 16 * 9;
        int y = cornerY + 16;
        try (var pose = new CloseablePoseStack(graphics)) {
            graphics.drawString(font, Component.translatable("gui." + Tempad.MODID + ".select_location"), x, y, 0xFFFFFFFF);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
