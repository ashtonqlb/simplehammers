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

        // Carmen code
        do {
            recipe = ConfigProvider.CONFIG.getRecipeAtIndex(i);
            if (recipe != null) {
                hammeringMap.put(Registries.BLOCK.get(new Identifier(recipe.getInput())), Registries.BLOCK.get(new Identifier(recipe.getOutput())));
            }
            i++;
        } while (recipe != null);

        //HammerRecipe -> <String, String> -> <Block, Block>

        //While getRecipeAtIndex returns a value
            //For each element in Recipe, convert from String to Block
                //Put inputBlock and outputBlock (each element) into HammeringMap as a key, value pair
            //Add 1 to i

        return hammeringMap;
    }

    public AbstractMap.SimpleEntry<Block, Block> getRecipeFromMap (HashMap<Block, Block> map){
        return null;

        //Return a single key pair from HammeringMap.
    }
}

//TODO - Generate the Hammering Map from the Config's recipeList. The HammeringMap is a HashMap of type <Block, Block> which is used in the BlockHarvestMixin