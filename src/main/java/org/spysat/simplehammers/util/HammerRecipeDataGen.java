package org.spysat.simplehammers.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

        private @NotNull ArrayList<String> parseRecipeList(String path) {
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();

            // Read the JSON content from the file
            try {
                Reader reader = Files.newBufferedReader(Paths.get(path));
                JsonObject obj = parser.parse(reader).getAsJsonObject();

                // Extract the "recipeList" array
                JsonArray recipeListJson = obj.getAsJsonArray("recipeList");

                // Parse the "recipeList" array into an array of HammerRecipe objects
                Type recipeArrayType = new TypeToken<HammerRecipe[]>(){}.getType();
                HammerRecipe[] recipeArray = gson.fromJson(recipeListJson, recipeArrayType);

                ArrayList<String> uniqueBlockIDs = getStrings(recipeArray);

                return uniqueBlockIDs;
            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception
                return null;
            }
        }


        private @NotNull static ArrayList<String> getStrings(HammerRecipe @NotNull [] recipeArray) {
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
            ArrayList<String> mineableBlockList = parseRecipeList("../../run/config/simplehammers.json");

            for (String s : mineableBlockList) {
                getOrCreateTagBuilder(HAMMERABLES).add(Registries.BLOCK.get(new Identifier(s)));
            }
        }
    }

    @Override
    public void onInitializeDataGenerator(@NotNull FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(HammerablesTagGenerator::new);
    }
}