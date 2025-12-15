package com.cmdpro.databank.integration.darkmodeeverywhere;

import java.util.List;

public class DarkModeBlacklist {
    private List<String> packagesToBlock;

    public static DarkModeBlacklist blacklist = new DarkModeBlacklist();

    public DarkModeBlacklist() {
        packagesToBlock = List.of(
                "com.cmdpro.databank",
                "com.cmdpro.datanessence",
                "com.cmdpro.runology",
                "EsetKalenko.Enderstar",
                "earth.terrarium.pastel"
        );
    }

    public List<String> getContents() {
        return packagesToBlock;
    }

    public void addPackageToBlacklist(String pakij) {
        packagesToBlock.add(pakij);
    }
}
