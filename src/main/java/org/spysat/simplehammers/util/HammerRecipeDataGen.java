package org.spysat.simplehammers.util;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.NotNull;
import org.spysat.simplehammers.config.Config.HammerRecipe;

import java.util.concurrent.CompletableFuture;

public class HammerRecipeDataGen implements DataGeneratorEntrypoint {

    private static class HammerablesTagGenerator extends BlockTagProvider {
        public HammerablesTagGenerator(FabricDataOutput output, CompletableFuture<WrapperLookup> completableFuture) {
            super(output, completableFuture);
        }

        public static final TagKey<Block> HAMMERABLES = TagKey.of(RegistryKeys.BLOCK, new Identifier("simplehammers:mineable_with_hammer")); //Custom block tag

        private @NotNull ArrayList<String> parseRecipeList(String path){
            Gson gson = new Gson();

            // Read the JSON content and parse it into an array of HammerRecipe objects
            HammerRecipe[] recipeArray = gson.fromJson(path, HammerRecipe[].class);

            Set<String> uniqueOutputs = new HashSet<>();
            ArrayList<String> uniqueBlockIDs = new ArrayList<>();

            // Iterate through the parsed array of recipes, removing duplicate entries
            for (HammerRecipe recipe : recipeArray) {
                String output = recipe.getOutput();
                if (!uniqueOutputs.contains(output)) {
                    uniqueOutputs.add(output);
                    uniqueBlockIDs.add(output);
                }
            }
            return uniqueBlockIDs;
        }

        @Override
        protected void configure(WrapperLookup arg) {
            ArrayList<String> mineableBlockList = parseRecipeList("simplehammers.json");

            for (String s : mineableBlockList) {
                getOrCreateTagBuilder(HAMMERABLES).add(Registries.BLOCK.get(new Identifier(s)));
            }
        }
        //TODO: This throws an error where it expects BEGIN_ARRAY but gets STRING when I call this and also parseRecipeList. I need to make sure that parseRecipeList is an ArrayList with all the unique Strings of recipeList in it
    }

    @Override
    public void onInitializeDataGenerator(@NotNull FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(HammerablesTagGenerator::new);
    }
}