package org.spysat.simplehammers.util;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spysat.simplehammers.config.Config;
import org.spysat.simplehammers.config.ConfigProvider;

import java.util.AbstractMap;
import java.util.HashMap;

public class HammerRecipeSerialization {
    public HashMap<Block, Block> getHammeringMap(){
        HashMap<Block, Block> hammeringMap = new HashMap<>();
        Config.HammerRecipe recipe;
        int i = 0;

        do {
            recipe = ConfigProvider.CONFIG.getRecipeAtIndex(i);
            if (recipe != null) {
                hammeringMap.put(Registries.BLOCK.get(new Identifier(recipe.getInput())), Registries.BLOCK.get(new Identifier(recipe.getOutput())));
            }
            i++;
        } while (recipe != null);

        return hammeringMap;
    }
}