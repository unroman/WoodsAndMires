package juuxel.woodsandmires.data.builtin;

import juuxel.woodsandmires.biome.WamBiomeKeys;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.OverworldBiomeCreator;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.VegetationPlacedFeatures;

import java.util.function.Consumer;

public final class WamBiomes {
    public static final RegistryCollector<RegistryEntry<Biome>> BIOMES = new RegistryCollector<>();

    private WamBiomes() {
    }

    public static void register() {
        register(WamBiomeKeys.PINE_FOREST, pineForest());
        register(WamBiomeKeys.SNOWY_PINE_FOREST, snowyPineForest());
        register(WamBiomeKeys.OLD_GROWTH_PINE_FOREST, oldGrowthPineForest());
        register(WamBiomeKeys.LUSH_PINE_FOREST, lushPineForest());
        register(WamBiomeKeys.PINE_MIRE, pineMire());
        register(WamBiomeKeys.FELL, fell());
        register(WamBiomeKeys.SNOWY_FELL, snowyFell());
        register(WamBiomeKeys.PINY_GROVE, pinyGrove());
    }

    private static void register(RegistryKey<Biome> key, Biome biome) {
        BIOMES.add(BuiltinRegistries.add(BuiltinRegistries.BIOME, key, biome));
    }

    private static int getSkyColor(float temperature) {
        return OverworldBiomeCreator.getSkyColor(temperature);
    }

    private static Biome pineForest(Biome.Category category, Biome.Precipitation precipitation, float temperature,
                                    Consumer<GenerationSettings.Builder> earlyGenerationSettingsConfigurator,
                                    Consumer<GenerationSettings.Builder> generationSettingsConfigurator) {
        GenerationSettings generationSettings = generationSettings(builder -> {
            OverworldBiomeCreator.addBasicFeatures(builder);
            earlyGenerationSettingsConfigurator.accept(builder);
            DefaultBiomeFeatures.addForestFlowers(builder);
            DefaultBiomeFeatures.addLargeFerns(builder);
            DefaultBiomeFeatures.addDefaultOres(builder);
            DefaultBiomeFeatures.addDefaultDisks(builder);
            builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, WamPlacedFeatures.FALLEN_PINE);

            // Stone boulders
            builder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, WamPlacedFeatures.PINE_FOREST_BOULDER);

            generationSettingsConfigurator.accept(builder);

            if (precipitation != Biome.Precipitation.SNOW) {
                DefaultBiomeFeatures.addDefaultFlowers(builder);
                builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, WamPlacedFeatures.PINE_FOREST_HEATHER_PATCH);
            }
            DefaultBiomeFeatures.addForestGrass(builder);
            DefaultBiomeFeatures.addDefaultMushrooms(builder);
            DefaultBiomeFeatures.addDefaultVegetation(builder);
            DefaultBiomeFeatures.addSweetBerryBushes(builder);
        });

        SpawnSettings spawnSettings = spawnSettings(builder -> {
            DefaultBiomeFeatures.addFarmAnimals(builder);
            DefaultBiomeFeatures.addBatsAndMonsters(builder);

            builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.WOLF, 5, 4, 4));
            builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.FOX, 4, 2, 4));
        });

        return new Biome.Builder()
            .category(category)
            .effects(
                new BiomeEffects.Builder()
                    .waterColor(OverworldBiomeCreator.DEFAULT_WATER_COLOR)
                    .waterFogColor(OverworldBiomeCreator.DEFAULT_WATER_FOG_COLOR)
                    .fogColor(OverworldBiomeCreator.DEFAULT_FOG_COLOR)
                    .foliageColor(0x43C44F)
                    .skyColor(getSkyColor(temperature))
                    .moodSound(BiomeMoodSound.CAVE)
                    .build()
            )
            .precipitation(precipitation)
            .downfall(0.6f)
            .temperature(temperature)
            .generationSettings(generationSettings)
            .spawnSettings(spawnSettings)
            .build();
    }

    private static Biome pineForest() {
        // noinspection CodeBlock2Expr
        return pineForest(Biome.Category.FOREST, Biome.Precipitation.RAIN, 0.6f, builder -> {}, builder -> {
            builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, WamPlacedFeatures.FOREST_PINE);
        });
    }

    private static Biome snowyPineForest() {
        // noinspection CodeBlock2Expr
        return pineForest(Biome.Category.FOREST, Biome.Precipitation.SNOW, 0f, builder -> {}, builder -> {
            builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, WamPlacedFeatures.SNOWY_PINE_FOREST_TREES);
        });
    }

    private static Biome oldGrowthPineForest() {
        return pineForest(Biome.Category.FOREST, Biome.Precipitation.RAIN, 0.4f, builder -> {}, builder -> {
            builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, WamPlacedFeatures.OLD_GROWTH_PINE_FOREST_TREES);
        });
    }

    private static Biome lushPineForest() {
        return pineForest(Biome.Category.FOREST, Biome.Precipitation.RAIN, 0.6f, builder -> {
            DefaultBiomeFeatures.addSavannaTallGrass(builder);
        }, builder -> {
            builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, WamPlacedFeatures.LUSH_PINE_FOREST_TREES);
            builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, WamPlacedFeatures.LUSH_PINE_FOREST_FLOWERS);
            DefaultBiomeFeatures.addExtraDefaultFlowers(builder);
        });
    }

    private static Biome pineMire() {
        GenerationSettings generationSettings = generationSettings(builder -> {
            OverworldBiomeCreator.addBasicFeatures(builder);
            builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, WamPlacedFeatures.MIRE_PINE_SHRUB);
            builder.feature(GenerationStep.Feature.LAKES, WamPlacedFeatures.MIRE_PONDS);
            builder.feature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, WamPlacedFeatures.MIRE_MEADOW);
            builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, WamPlacedFeatures.MIRE_FLOWERS);
            builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_WATERLILY);
            builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, WamPlacedFeatures.MIRE_PINE_SNAG);
        });

        SpawnSettings spawnSettings = spawnSettings(builder -> {
            DefaultBiomeFeatures.addFarmAnimals(builder);
            DefaultBiomeFeatures.addBatsAndMonsters(builder);
        });

        return new Biome.Builder()
            .category(Biome.Category.SWAMP)
            .effects(
                new BiomeEffects.Builder()
                    .waterColor(0x7B6D1B)
                    .waterFogColor(OverworldBiomeCreator.DEFAULT_WATER_FOG_COLOR)
                    .fogColor(OverworldBiomeCreator.DEFAULT_FOG_COLOR)
                    .moodSound(BiomeMoodSound.CAVE)
                    .foliageColor(0xBFA243)
                    .grassColor(0xADA24C)
                    .skyColor(getSkyColor(0.6f))
                    .build()
            )
            .precipitation(Biome.Precipitation.RAIN)
            .downfall(0.9f)
            .temperature(0.6f)
            .generationSettings(generationSettings)
            .spawnSettings(spawnSettings)
            .build();
    }

    private static Biome fell(Biome.Precipitation precipitation, float temperature, Consumer<GenerationSettings.Builder> generationSettingsConfigurator) {
        SpawnSettings spawnSettings = spawnSettings(builder -> {
            DefaultBiomeFeatures.addBatsAndMonsters(builder);

            builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.WOLF, 5, 4, 4));
            builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.FOX, 4, 2, 4));
        });
        GenerationSettings generationSettings = generationSettings(builder -> {
            OverworldBiomeCreator.addBasicFeatures(builder);
            DefaultBiomeFeatures.addDefaultOres(builder);
            DefaultBiomeFeatures.addDefaultDisks(builder);
            generationSettingsConfigurator.accept(builder);
        });

        return new Biome.Builder()
            .category(Biome.Category.EXTREME_HILLS)
            .effects(
                new BiomeEffects.Builder()
                    .waterColor(OverworldBiomeCreator.DEFAULT_WATER_COLOR)
                    .waterFogColor(OverworldBiomeCreator.DEFAULT_WATER_FOG_COLOR)
                    .fogColor(OverworldBiomeCreator.DEFAULT_FOG_COLOR)
                    .skyColor(getSkyColor(temperature))
                    .moodSound(BiomeMoodSound.CAVE)
                    .build()
            )
            .precipitation(precipitation)
            .downfall(0.7f)
            .temperature(temperature)
            .generationSettings(generationSettings)
            .spawnSettings(spawnSettings)
            .build();
    }

    private static Biome fell() {
        return fell(Biome.Precipitation.RAIN, 0.25f, builder -> {
            DefaultBiomeFeatures.addDefaultFlowers(builder);
            builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, WamPlacedFeatures.FELL_HEATHER_PATCH);
            DefaultBiomeFeatures.addForestGrass(builder);
            DefaultBiomeFeatures.addDefaultMushrooms(builder);
            DefaultBiomeFeatures.addDefaultVegetation(builder);

            builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, WamPlacedFeatures.FELL_LICHEN);
            builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, WamPlacedFeatures.FELL_MOSS_PATCH);
            builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, WamPlacedFeatures.FELL_VEGETATION);
            builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, WamPlacedFeatures.FELL_BIRCH_SHRUB);
            builder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, WamPlacedFeatures.FELL_BOULDER);
            builder.feature(GenerationStep.Feature.LAKES, WamPlacedFeatures.FELL_POND);
        });
    }

    private static Biome snowyFell() {
        return fell(Biome.Precipitation.SNOW, 0f, builder -> {
            builder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, WamPlacedFeatures.FELL_BOULDER);
            builder.feature(GenerationStep.Feature.LAKES, WamPlacedFeatures.FELL_POND);
            builder.feature(GenerationStep.Feature.SURFACE_STRUCTURES, WamPlacedFeatures.FROZEN_TREASURE);
        });
    }

    private static Biome pinyGrove() {
        GenerationSettings generationSettings = generationSettings(builder -> {
            OverworldBiomeCreator.addBasicFeatures(builder);
            DefaultBiomeFeatures.addFrozenLavaSpring(builder);
            DefaultBiomeFeatures.addDefaultOres(builder);
            DefaultBiomeFeatures.addDefaultDisks(builder);
            builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, WamPlacedFeatures.PINY_GROVE_TREES);
            DefaultBiomeFeatures.addDefaultVegetation(builder);
            DefaultBiomeFeatures.addEmeraldOre(builder);
            DefaultBiomeFeatures.addInfestedStone(builder);
        });
        SpawnSettings spawnSettings = spawnSettings(builder -> {
            DefaultBiomeFeatures.addFarmAnimals(builder);
            builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.WOLF, 8, 4, 4))
                .spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3))
                .spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.FOX, 8, 2, 4));
            DefaultBiomeFeatures.addBatsAndMonsters(builder);
        });
        return new Biome.Builder()
            .category(Biome.Category.FOREST)
            .effects(
                new BiomeEffects.Builder()
                    .waterColor(OverworldBiomeCreator.DEFAULT_WATER_COLOR)
                    .waterFogColor(OverworldBiomeCreator.DEFAULT_WATER_FOG_COLOR)
                    .fogColor(OverworldBiomeCreator.DEFAULT_FOG_COLOR)
                    .skyColor(getSkyColor(-0.2f))
                    .moodSound(BiomeMoodSound.CAVE)
                    .build()
            )
            .precipitation(Biome.Precipitation.SNOW)
            .downfall(0.8f)
            .temperature(-0.2f)
            .generationSettings(generationSettings)
            .spawnSettings(spawnSettings)
            .build();
    }

    private static GenerationSettings generationSettings(Consumer<GenerationSettings.Builder> configurator) {
        GenerationSettings.Builder builder = new GenerationSettings.Builder();
        configurator.accept(builder);
        return builder.build();
    }

    private static SpawnSettings spawnSettings(Consumer<SpawnSettings.Builder> configurator) {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        configurator.accept(builder);
        return builder.build();
    }
}
