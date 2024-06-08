package earth.terrarium.tempad.client.components;

import earth.terrarium.tempad.common.Tempad;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

public class ModSprites {

    public static final ResourceLocation TEMPAD_SCREEN = new ResourceLocation(Tempad.MODID, "screen/tempad");
    public static final ResourceLocation TELEPORT_SCREEN = new ResourceLocation(Tempad.MODID, "screen/teleport");
    public static final ResourceLocation NEW_LOCATION = new ResourceLocation(Tempad.MODID, "screen/new_location");
    public static final ResourceLocation PRINTER_SCREEN = new ResourceLocation(Tempad.MODID, "screen/printer");
    public static final ResourceLocation BAR = new ResourceLocation(Tempad.MODID, "screen/bar");
    public static final ResourceLocation MODAL = new ResourceLocation(Tempad.MODID, "modal/background");

    public static final WidgetSprites FAVORITE = new WidgetSprites(
        new ResourceLocation(Tempad.MODID, "screen/favorite/normal"),
        new ResourceLocation(Tempad.MODID, "screen/favorite/disabled"),
        new ResourceLocation(Tempad.MODID, "screen/favorite/hover")
    );

    public static final WidgetSprites UNFAVORITE = new WidgetSprites(
        new ResourceLocation(Tempad.MODID, "screen/unfavorite/normal"),
        new ResourceLocation(Tempad.MODID, "screen/unfavorite/disabled"),
        new ResourceLocation(Tempad.MODID, "screen/unfavorite/hover")
    );

    public static final WidgetSprites DOWNLOAD = new WidgetSprites(
        new ResourceLocation(Tempad.MODID, "screen/download/normal"),
        new ResourceLocation(Tempad.MODID, "screen/download/disabled"),
        new ResourceLocation(Tempad.MODID, "screen/download/hover")
    );

    public static final WidgetSprites DELETE = new WidgetSprites(
        new ResourceLocation(Tempad.MODID, "screen/delete/normal"),
        new ResourceLocation(Tempad.MODID, "screen/delete/disabled"),
        new ResourceLocation(Tempad.MODID, "screen/delete/hover")
    );

    public static final WidgetSprites TELEPORT = new WidgetSprites(
        new ResourceLocation(Tempad.MODID, "screen/teleport/normal"),
        new ResourceLocation(Tempad.MODID, "screen/teleport/disabled"),
        new ResourceLocation(Tempad.MODID, "screen/teleport/hover")
    );

    public static final WidgetSprites ADD = new WidgetSprites(
        new ResourceLocation(Tempad.MODID, "screen/add/normal"),
        new ResourceLocation(Tempad.MODID, "screen/add/disabled"),
        new ResourceLocation(Tempad.MODID, "screen/add/hover")
    );

    public static final WidgetSprites BACK = new WidgetSprites(
        new ResourceLocation(Tempad.MODID, "modal/back/normal"),
        new ResourceLocation(Tempad.MODID, "modal/back/disabled"),
        new ResourceLocation(Tempad.MODID, "modal/back/hover")
    );

    public static final WidgetSprites SAVE = new WidgetSprites(
        new ResourceLocation(Tempad.MODID, "modal/save/normal"),
        new ResourceLocation(Tempad.MODID, "modal/save/disabled"),
        new ResourceLocation(Tempad.MODID, "modal/save/hover")
    );
}
