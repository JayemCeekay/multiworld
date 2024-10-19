package me.isaiah.multiworld.fabric;

import me.isaiah.multiworld.MultiworldMod;
import me.isaiah.multiworld.config.FileConfiguration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.fabricmc.fabric.api.event.registry.DynamicRegistryView;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.impl.biome.BiomeSourceAccess;
import net.fabricmc.fabric.impl.client.rendering.DimensionRenderingRegistryImpl;
import net.fabricmc.fabric.impl.dimension.FabricDimensionInternals;
import net.fabricmc.fabric.impl.registry.sync.DynamicRegistryViewImpl;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.fabricmc.fabric.mixin.biome.modification.DynamicRegistryManagerImmutableImplMixin;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.data.report.DynamicRegistriesProvider;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.dimension.*;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.feature.PlacedFeatures;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

public class MultiworldModFabric implements ModInitializer {


    @Override
    public void onInitialize() {

        PermFabric.init();
        FabricWorldCreator.init();
        /*
        RegistryKey<DimensionType> MORIA_REGISTRY_KEY = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, new Identifier("multiworld", "moria"));

        DimensionType MoriaDimensionType = new DimensionType(OptionalLong.of(18000), false, false, false, true, 1.0D,
                false, false, -512, 768, 768, BlockTags.INFINIBURN_OVERWORLD, MORIA_REGISTRY_KEY.getValue(), 0.0f, new DimensionType.MonsterSettings(false, false, UniformIntProvider.create(0, 15), 0));

        // BuiltinRegistries.add(BuiltinRegistries.DYNAMIC_REGISTRY_MANAGER.get(RegistryKeys), new Identifier("multiworld", "moria"), MoriaDimensionType);
        Registry.register(Registries.CHUNK_GENERATOR, new Identifier("multiworld", "plots"), PlotChunkGenerator.CODEC);

/*
        DynamicRegistrySetupCallback.EVENT.register(registryView -> {
            Registry.register(registryView.asDynamicRegistryManager().get(RegistryKeys.DIMENSION_TYPE), new Identifier("multiworld", "moria"), MoriaDimensionType);
        });*/
/*
        ServerLifecycleEvents.SERVER_STARTED.register(mc -> {
            RuntimeWorldConfig worldConfig3 = new RuntimeWorldConfig().setDimensionType(DimensionTypes.OVERWORLD).setDifficulty(Difficulty.NORMAL)
                    .setGameRule(GameRules.RANDOM_TICK_SPEED, 0)
                    .setGameRule(GameRules.KEEP_INVENTORY, false)
                    .setGameRule(GameRules.DO_DAYLIGHT_CYCLE, false)
                    .setGameRule(GameRules.DO_MOB_GRIEFING, false)
                    .setGameRule(GameRules.DISABLE_RAIDS, true)
                    .setGameRule(GameRules.DO_MOB_SPAWNING, false)
                    .setGameRule(GameRules.DO_FIRE_TICK, false)
                    .setGameRule(GameRules.DO_WEATHER_CYCLE, false)
                    .setGameRule(GameRules.LOG_ADMIN_COMMANDS, true)
                    .setGameRule(GameRules.SHOW_DEATH_MESSAGES, true)
                    .setGameRule(GameRules.NATURAL_REGENERATION, false)
                    .setGameRule(GameRules.COMMAND_BLOCK_OUTPUT, false)
                    .setGameRule(GameRules.DO_ENTITY_DROPS, false)
                    .setGameRule(GameRules.DO_TILE_DROPS, false)
                    .setGameRule(GameRules.DO_MOB_LOOT, false)
                    .setGameRule(GameRules.SPAWN_RADIUS, 0)
                    .setGameRule(GameRules.DO_WARDEN_SPAWNING, false)
                    .setGameRule(GameRules.DO_TRADER_SPAWNING, false)
                    .setGameRule(GameRules.DO_PATROL_SPAWNING, false)
                    .setGameRule(GameRules.DO_IMMEDIATE_RESPAWN, false)
                    .setGameRule(GameRules.DO_INSOMNIA, false)
                    .setGameRule(GameRules.DO_LIMITED_CRAFTING, false)
                    .setGameRule(GameRules.FALL_DAMAGE, false)
                    .setGameRule(GameRules.FREEZE_DAMAGE, false)
                    .setGameRule(GameRules.FIRE_DAMAGE, false)
                    .setGameRule(GameRules.DROWNING_DAMAGE, false)
                    .setGameRule(GameRules.PLAYERS_SLEEPING_PERCENTAGE, 0)
                    .setGenerator(mc.getOverworld().getChunkManager().getChunkGenerator()).setSeed(1234);

            RuntimeWorldHandle handle3 = Fantasy.get(mc).getOrOpenPersistentWorld(new Identifier("multiworld", "freebuild"), worldConfig3);
            ServerWorld level3 = handle3.asWorld();

            RuntimeWorldConfig worldConfig2 = new RuntimeWorldConfig().setDimensionType(
                            MORIA_REGISTRY_KEY
                    ).setDifficulty(Difficulty.NORMAL)
                    .setGameRule(GameRules.DO_DAYLIGHT_CYCLE, false)
                    .setGameRule(GameRules.DO_MOB_GRIEFING, false)
                    .setGameRule(GameRules.DO_MOB_SPAWNING, false)
                    .setGameRule(GameRules.DISABLE_RAIDS, true)
                    .setGameRule(GameRules.DO_FIRE_TICK, false)
                    .setGameRule(GameRules.DO_WEATHER_CYCLE, false)
                    .setGenerator(mc.getOverworld().getChunkManager().getChunkGenerator()).setSeed(1234);


            RuntimeWorldHandle handle2 = Fantasy.get(mc).getOrOpenPersistentWorld(new Identifier("multiworld", "moria_big"), worldConfig2);
            ServerWorld level2 = handle2.asWorld();

/*
            FlatChunkGeneratorConfig config = new FlatChunkGeneratorConfig(Optional.of(RegistryEntryList.of()), mc.getRegistryManager().get(RegistryKeys.BIOME).entryOf(BiomeKeys.PLAINS), List.of());

            RuntimeWorldConfig worldConfig4 = new RuntimeWorldConfig().setDimensionType(DimensionTypes.OVERWORLD).setDifficulty(Difficulty.NORMAL)
                    .setGameRule(GameRules.DO_DAYLIGHT_CYCLE, false)
                    .setGameRule(GameRules.DO_MOB_GRIEFING, false)
                    .setGameRule(GameRules.DO_MOB_SPAWNING, false)
                    .setGameRule(GameRules.DISABLE_RAIDS, true)
                    .setGameRule(GameRules.DO_FIRE_TICK, false)
                    .setGameRule(GameRules.DO_WEATHER_CYCLE, false)
                    .setGenerator(new PlotChunkGenerator(config)).setSeed(1234);

            RuntimeWorldHandle handle4 = Fantasy.get(mc).getOrOpenPersistentWorld(new Identifier("multiworld", "plots"), worldConfig4);
            ServerWorld level4 = handle4.asWorld();

*//*
        });
*/
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            MultiworldMod.register_commands(dispatcher);
        });

        MultiworldMod.init();

    }
    public static void make_config(ServerWorld w, String dim, long seed) {
        File config_dir = new File(FabricLoader.getInstance().getConfigDirectory().toString());
        config_dir.mkdirs();

        File cf = new File(config_dir, "multiworld");
        cf.mkdirs();

        File worlds = new File(cf, "worlds");
        worlds.mkdirs();

        Identifier id = w.getRegistryKey().getValue();
        File namespace = new File(worlds, id.getNamespace());
        namespace.mkdirs();

        File wc = new File(namespace, id.getPath() + ".yml");
        FileConfiguration config;
        try {
            if (!wc.exists()) {
                wc.createNewFile();
            }
            config = new FileConfiguration(wc);
            config.set("namespace", id.getNamespace());
            config.set("path", id.getPath());
            config.set("environment", dim);
            config.set("seed", seed);
            config.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}