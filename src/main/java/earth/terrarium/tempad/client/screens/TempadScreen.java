package earth.terrarium.tempad.client.screens;

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.tempad.api.apps.TempadApp;
import earth.terrarium.tempad.api.apps.TempadAppApi;
import earth.terrarium.tempad.api.options.FuelOption;
import earth.terrarium.tempad.api.options.FuelOptionsApi;
import earth.terrarium.tempad.api.power.PowerSettings;
import earth.terrarium.tempad.api.power.PowerSettingsApi;
import earth.terrarium.tempad.client.components.ModSprites;
import earth.terrarium.tempad.client.config.TempadClientConfig;
import earth.terrarium.tempad.client.screens.base.BackgroundScreen;
import earth.terrarium.tempad.common.utils.LookupLocation;
import earth.terrarium.tempad.common.utils.TeleportUtils;
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
    private final LookupLocation lookup;

    protected int localLeft;
    protected int localTop;
    private final ItemStack tempadItem;
    private final FuelOption option;
    private final PowerSettings attachment;

    protected TempadScreen(ResourceLocation sprite, ResourceLocation appId, LookupLocation search) {
        super(249, 138, ModSprites.TEMPAD_SCREEN);
        this.appScreen = sprite;
        this.appId = appId;
        this.lookup = search;
        this.tempadItem = TeleportUtils.findTempad(minecraft.player, lookup);
        this.option = FuelOptionsApi.API.findItemOption(getTempadItem());
        this.attachment = PowerSettingsApi.API.get(getTempadItem());
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
            if (appButton != null && !appButton.appId.equals(this.appId)) appButton.app.openOnClient(minecraft.player, lookup);
        }));

        apps.updateEntries(TempadAppApi.API.getApps().entrySet().stream().map(entry -> new AppButton(entry.getValue(), entry.getKey())).sorted(Comparator.comparingInt(value -> value.app.priority())).toList());
    }

    public ItemStack getTempadItem() {
        return tempadItem;
    }

    public LookupLocation getLookup() {
        return lookup;
    }

    public FuelOption getOption() {
        return option;
    }

    public PowerSettings getAttachment() {
        return attachment;
    }

    public class FuelBar implements Renderable {
        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            if (minecraft == null) return;
            int barHeight;
            if(option.isDurabilityBarVisible(tempadItem, attachment)) {
                barHeight = (int) (option.getPercentage(tempadItem, attachment) * 54);
            } else {
                barHeight = option.canTimedoorOpen(tempadItem, attachment, minecraft.player) ? 54 : 0;
            }

            guiGraphics.blitSprite(ModSprites.BAR, 6, 54, 0, 0, left + 234, top + 42 + 54 - barHeight, 6, barHeight);

            if (mouseX >= left + 234 && mouseX <= left + 240 && mouseY >= top + 42 && mouseY <= top + 96) {
                List<Component> tooltip = new ArrayList<>();
                option.addToolTip(tempadItem, attachment, minecraft.level, tooltip, TooltipFlag.NORMAL);
                setTooltipForNextRenderPass(tooltip.stream().map(Component::getVisualOrderText).toList());
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
