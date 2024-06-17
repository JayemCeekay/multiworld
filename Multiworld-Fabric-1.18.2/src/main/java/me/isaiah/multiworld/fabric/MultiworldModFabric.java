package me.isaiah.multiworld.fabric;

import me.isaiah.multiworld.MultiworldMod;
import me.isaiah.multiworld.command.Util;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

import java.util.Optional;
import java.util.OptionalLong;

public class MultiworldModFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        PermFabric.init();
        FabricWorldCreator.init();
        RegistryKey<DimensionType> MORIA_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("multiworld", "moria"));
        DimensionType MoriaDimensionType = new DimensionType(OptionalLong.of(18000), false, false, false, true, 1.0D,
                false, false, -512, 768, 768, BlockTags.INFINIBURN_OVERWORLD, MORIA_REGISTRY_KEY.getValue(), 0.0f, new DimensionType.MonsterSettings(false, false, UniformIntProvider.create(0, 15), 0));

        BuiltinRegistries.add(BuiltinRegistries.DYNAMIC_REGISTRY_MANAGER.get(Registry.DIMENSION_TYPE_KEY), new Identifier("multiworld", "moria"), MoriaDimensionType);
        Registry.register(Registry.CHUNK_GENERATOR, new Identifier("multiworld", "plots"), PlotChunkGenerator.CODEC);


        ServerLifecycleEvents.SERVER_STARTED.register(mc -> {


            MultiworldMod.mc = mc;
            Fantasy fantasy = Fantasy.get(mc);

            RuntimeWorldConfig worldConfig3 = new RuntimeWorldConfig().setDimensionType(Util.OVERWORLD_REGISTRY_KEY).setDifficulty(Difficulty.NORMAL)
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

            RuntimeWorldHandle handle3 = fantasy.getOrOpenPersistentWorld(new Identifier("multiworld", "freebuild"), worldConfig3);
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

            RuntimeWorldHandle handle2 = fantasy.getOrOpenPersistentWorld(new Identifier("multiworld", "moria_big"), worldConfig2);
            ServerWorld level2 = handle2.asWorld();

            FlatChunkGeneratorConfig config = new FlatChunkGeneratorConfig(Optional.of(RegistryEntryList.of()), mc.getRegistryManager().get(Registry.BIOME_KEY));

            RuntimeWorldConfig worldConfig4 = new RuntimeWorldConfig().setDimensionType(DimensionTypes.OVERWORLD).setDifficulty(Difficulty.NORMAL)
                    .setGameRule(GameRules.DO_DAYLIGHT_CYCLE, false)
                    .setGameRule(GameRules.DO_MOB_GRIEFING, false)
                    .setGameRule(GameRules.DO_MOB_SPAWNING, false)
                    .setGameRule(GameRules.DISABLE_RAIDS, true)
                    .setGameRule(GameRules.DO_FIRE_TICK, false)
                    .setGameRule(GameRules.DO_WEATHER_CYCLE, false)
                    .setGenerator(new PlotChunkGenerator(BuiltinRegistries.STRUCTURE_SET, config)).setSeed(1234);

            RuntimeWorldHandle handle4 = fantasy.getOrOpenPersistentWorld(new Identifier("multiworld", "plots"), worldConfig4);
            ServerWorld level4 = handle4.asWorld();


        });

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            MultiworldMod.register_commands(dispatcher);
        });

        MultiworldMod.init();


    }

}