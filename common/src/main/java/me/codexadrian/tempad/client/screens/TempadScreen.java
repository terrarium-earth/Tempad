package me.codexadrian.tempad.client.screens;

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import me.codexadrian.tempad.api.apps.TempadApp;
import me.codexadrian.tempad.api.apps.TempadAppApi;
import me.codexadrian.tempad.api.options.FuelOption;
import me.codexadrian.tempad.client.components.ModSprites;
import me.codexadrian.tempad.client.config.TempadClientConfig;
import me.codexadrian.tempad.client.screens.base.BackgroundScreen;
import me.codexadrian.tempad.common.items.TempadItem;
import me.codexadrian.tempad.common.utils.TeleportUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TempadScreen extends BackgroundScreen {
    private final ResourceLocation appScreen;
    private final ResourceLocation appId;
    protected int localLeft;
    protected int localTop;

    protected TempadScreen(ResourceLocation sprite, ResourceLocation appId) {
        super(249, 138, ModSprites.TEMPAD_SCREEN);
        this.appScreen = sprite;
        this.appId = appId;
    }

    @Override
    protected void init() {
        super.init();
        this.localLeft = this.left + 10;
        this.localTop = this.top + 10;

        addRenderableOnly((graphics, mouseX, mouseY, partialTick) -> {
            graphics.blitSprite(appScreen, localLeft, localTop, 198, 118);
            graphics.drawString(font, Component.translatable(appId.toLanguageKey("app")), localLeft + 6, localTop + 7, TempadClientConfig.color);
        });

        addRenderableOnly(new FuelBar());

        var apps = addRenderableWidget(new SelectionList<AppButton>(left + 210, top + 13, 12, 114, 12, (appButton) -> {
            if (appButton != null && !appButton.appId.equals(this.appId)) appButton.app.open();
        }));

        apps.updateEntries(TempadAppApi.API.getApps().entrySet().stream().map(entry -> new AppButton(entry.getValue(), entry.getKey())).sorted(Comparator.comparingInt(value -> value.app.priority())).toList());
    }

    public class FuelBar implements Renderable {
        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            if (minecraft == null) return;

            ItemStack tempad = TeleportUtils.findTempad(minecraft.player);
            if (tempad.getItem() instanceof TempadItem item) {
                FuelOption option = item.getOption();
                int barHeight;
                if(option.isDurabilityBarVisible(tempad)) {
                    barHeight = (int) (option.getPercentage(tempad) * 54);
                } else {
                    barHeight = option.canTimedoorOpen(minecraft.player, tempad) ? 54 : 0;
                }

                guiGraphics.blitSprite(ModSprites.BAR, 6, 54, 0, 0, left + 234, top + 42 + 54 - barHeight, 6, barHeight);

                if (mouseX >= left + 234 && mouseX <= left + 240 && mouseY >= top + 42 && mouseY <= top + 96) {
                    List<Component> tooltip = new ArrayList<>();
                    option.addToolTip(tempad, minecraft.level, tooltip, TooltipFlag.NORMAL);
                    setTooltipForNextRenderPass(tooltip.stream().map(Component::getVisualOrderText).toList());
                }
            }
        }
    }

    public class AppButton extends ListEntry {
        private final TempadApp app;
        private final ResourceLocation appId;

        public AppButton(TempadApp app, ResourceLocation appId) {
            this.app = app;
            this.appId = appId;
        }

        @Override
        protected void render(@NotNull GuiGraphics graphics, @NotNull ScissorBoxStack stack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
            ResourceLocation resourceLocation = this.app.getWidgetSprites().get(true, hovered);
            graphics.blitSprite(resourceLocation, left, top, width, height);
            if (hovered) {
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(Component.translatable(appId.toLanguageKey("app")));
                setTooltipForNextRenderPass(tooltip.stream().map(Component::getVisualOrderText).toList());
            }
        }

        @Override
        public void setFocused(boolean focused) {
            // do nothing
        }

        @Override
        public boolean isFocused() {
            return false;
        }
    }
}
