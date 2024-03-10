package me.codexadrian.tempad.client.components;

import com.mojang.math.Axis;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import me.codexadrian.tempad.client.screens.map.ClaimMapRenderer;
import me.codexadrian.tempad.client.screens.map.ClaimMapTopologyAlgorithm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;

import java.util.concurrent.CompletableFuture;

public class MapWidget implements Renderable {
    public static final ResourceLocation MAP_ICONS = new ResourceLocation("textures/map/map_icons.png");
    ClaimMapRenderer mapRenderer;
    int viewDistance;
    int x;
    int y;
    int size;

    public MapWidget(int x, int y, int size, int viewDistance) {
        this.viewDistance = viewDistance;
        this.x = x;
        this.y = y;
        this.size = size;
        this.refreshMap();
    }

    public void renderLoading(GuiGraphics graphics) {
        Font font = Minecraft.getInstance().font;
        graphics.drawCenteredString(font, "Loading...", (int) (x + size / 2f), (int) (y + size / 2f), 0xFFFFFF);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        if (this.mapRenderer == null) {
            this.renderLoading(graphics);
        } else {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            this.mapRenderer.render(graphics, this.x, this.y, this.size);
            try (var pose = new CloseablePoseStack(graphics)) {
                pose.translate(0, 0, 2);
                this.renderPlayerAvatar(player, graphics);
            }
        }
    }

    public void refreshMap() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        ChunkPos chunkPos = player.chunkPosition();
        int scale = getScaledRenderDistance();
        int minX = chunkPos.getMinBlockX() - scale;
        int minZ = chunkPos.getMinBlockZ() - scale;
        int maxX = chunkPos.getMaxBlockX() + scale + 1;
        int maxZ = chunkPos.getMaxBlockZ() + scale + 1;

        if (scale / 8 > 12) {
            // If the render distance is greater than 12 chunks, run asynchronously to avoid stuttering.
            CompletableFuture.supplyAsync(() -> ClaimMapTopologyAlgorithm.setColors(minX, minZ, maxX, maxZ, player.clientLevel, player)).thenAcceptAsync(colors ->
                this.mapRenderer = new ClaimMapRenderer(colors, scale * 2 + 16), Minecraft.getInstance());
        } else {
            int[][] colors = ClaimMapTopologyAlgorithm.setColors(minX, minZ, maxX, maxZ, player.clientLevel, player);
            this.mapRenderer = new ClaimMapRenderer(colors, scale * 2 + 16);
        }
    }

    public int getScaledRenderDistance() {
        int scale = this.viewDistance * 8;
        scale -= scale % 16;
        return scale;
    }

    private void renderPlayerAvatar(LocalPlayer player, GuiGraphics graphics) {
        float left = (this.size) / 2f;
        float top = (this.size) / 2f;

        double playerX = player.getX();
        double playerZ = player.getZ();
        double x = (playerX % 16) + (playerX >= 0 ? -8 : 8);
        double y = (playerZ % 16) + (playerZ >= 0 ? -8 : 8);

        float scale = this.size / (getScaledRenderDistance() * 2f + 16);

        x *= scale;
        y *= scale;
        try (var pose = new CloseablePoseStack(graphics)) {
            pose.translate(this.x + left + x, this.y + top + y, 0);
            pose.mulPose(Axis.ZP.rotationDegrees(player.getYRot()));
            pose.translate(-4, -4, 0);
            graphics.blit(MAP_ICONS, 0, 0, 40, 0, 8, 8, 128, 128);
        }
    }
}
