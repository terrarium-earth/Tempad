package earth.terrarium.tempad.common.config;

import com.teamresourceful.resourcefulconfig.api.annotations.Comment;
import com.teamresourceful.resourcefulconfig.api.annotations.Config;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType;
import earth.terrarium.tempad.client.screen.Sorting;

@Config("tempad-client")
public class ClientConfig {

    @ConfigEntry(id = "sorting_mode", type = EntryType.STRING)
    @Comment("The sorting mode for the Tempad screen. Options: DIMENSION, ALPHABETICAL, TYPE")
    public static String sortingMode = Sorting.DIMENSION.name();
}
