package me.codexadrian.tempad.client.gui.blockmanager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import me.codexadrian.tempad.Constants;
import me.codexadrian.tempad.TempadClient;
import me.codexadrian.tempad.blocks.TempadBlockEntity;
import me.codexadrian.tempad.blocks.TempadBlockMenu;
import me.codexadrian.tempad.client.widgets.TextButton;
import me.codexadrian.tempad.data.LocationData;
import me.codexadrian.tempad.data.TempadComponent;
import me.codexadrian.tempad.network.messages.SummonBlockTimedoorPacket;
import me.codexadrian.tempad.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TempadBlockScreen extends AbstractContainerScreen<TempadBlockMenu> {
    private static final ResourceLocation OVERLAY = new ResourceLocation(Constants.MODID, "textures/gui/tempad_overlay.png");
    private static final ResourceLocation BASE_SCREEN = new ResourceLocation(Constants.MODID, "textures/gui/tempad_base.png");

    private SelectionList list;
    private ItemStack tempad = ItemStack.EMPTY;
    private LocationData data;

    public TempadBlockScreen(TempadBlockMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
        this.imageWidth = 240;
        this.imageHeight = 237;
    }

    @Override
    protected void init() {
        super.init();
        this.list = addRenderableWidget(new SelectionList(leftPos + 114, topPos + 24, 105, 68, font.lineHeight + 2, this::handleInput));
        addRenderableWidget(new TextButton(leftPos + 42, topPos + 22, 12, Component.translatable("gui.tempad.teleport"), TempadClient.getClientConfig().getColor(), this::handleTeleportButton));
    }

    @Override
    protected void containerTick() {
        ItemStack item = this.getMenu().container.getItem(0);
        if (tempad != item) {
            if (item.isEmpty()) {
                tempad = ItemStack.EMPTY;
                list.updateEntries(List.of());
            } else {
                TempadComponent component = TempadComponent.fromStack(item);
                this.list.updateEntries(component.getLocations().stream().map(LocationEntry::new).toList());
                this.tempad = item;
            }
        }
    }

    public void handleTeleportButton(Button button) {
        if (this.data != null) {
            Minecraft.getInstance().setScreen(null);
            Services.NETWORK.sendToServer(new SummonBlockTimedoorPacket(this.getMenu().pos, Direction.NORTH, Direction.UP, data.getLevelKey().location(), data.getBlockPos(), TempadClient.getClientConfig().getColor()));
        }
    }

    public void handleInput(ListEntry entry) {
        if (entry instanceof LocationEntry locationEntry) {
            this.data = locationEntry.getData();
        }
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int i, int j, float f) {
        this.renderBackground(poseStack);
        super.render(poseStack, i, j, f);
        renderTooltip(poseStack, i, j);
    }

    @Override
    protected void renderBg(@NotNull PoseStack poseStack, float f, int i, int j) {
        RenderSystem.setShaderTexture(0, BASE_SCREEN);
        RenderSystem.setShaderColor(1, 1, 1, 1f);
        blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        RenderSystem.setShaderTexture(0, OVERLAY);
        int color = TempadClient.getClientConfig().getColor();
        RenderSystem.setShaderColor(FastColor.ARGB32.red(color) / 255F, FastColor.ARGB32.green(color) / 255F, FastColor.ARGB32.blue(color) / 255F, 1f);
        blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(@NotNull PoseStack poseStack, int i, int j) {
        this.font.draw(poseStack, this.title, (float) this.titleLabelX, (float) this.titleLabelY, TempadClient.getClientConfig().getColor());
    }
}
