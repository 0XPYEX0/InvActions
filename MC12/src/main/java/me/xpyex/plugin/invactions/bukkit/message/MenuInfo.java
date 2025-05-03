package me.xpyex.plugin.invactions.bukkit.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class MenuInfo {
    private String name = null;
    private List<String> lore = new ArrayList<>();

    public static MenuInfo of(String name, String... lore) {
        return of(name, Arrays.asList(lore));
    }
}
