package me.isaiah.multiworld.fabric;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class PlotChunkGenerator extends FlatChunkGenerator {

    private static final int PLOT_SIZE = 201;
    private static final int ROAD_WIDTH = 3;
    private static final int EDGE_WIDTH = 1;
    public static final Codec<PlotChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
        return createStructureSetRegistryGetter(instance).and(FlatChunkGeneratorConfig.CODEC.fieldOf("settings").forGetter(FlatChunkGenerator::getConfig)).apply(instance, instance.stable(PlotChunkGenerator::new));
    });

    public PlotChunkGenerator(Registry<StructureSet> registry, FlatChunkGeneratorConfig flatChunkGeneratorConfig) {
        super(registry, flatChunkGeneratorConfig);
    }


    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }


    @Override
    public int getWorldHeight() {
        return 384;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y <= 64; y++) {
                    chunk.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState(), false);

                    if (y == 64) {
                        int regionX = chunk.getPos().x * 16;
                        int regionZ = chunk.getPos().z * 16;
                        int modX = Math.floorMod(regionX + x, PLOT_SIZE + 2 * ROAD_WIDTH + 2 * EDGE_WIDTH);
                        int modZ = Math.floorMod(regionZ + z, PLOT_SIZE + 2 * ROAD_WIDTH + 2 * EDGE_WIDTH);
                        if (modX >= EDGE_WIDTH + ROAD_WIDTH && modX < PLOT_SIZE + EDGE_WIDTH + ROAD_WIDTH && modZ >= EDGE_WIDTH + ROAD_WIDTH && modZ < PLOT_SIZE + EDGE_WIDTH + ROAD_WIDTH) {
                            chunk.setBlockState(new BlockPos(regionX + x, y, regionZ + z), Blocks.BLACK_WOOL.getDefaultState(), false);
                        } else if (modX == EDGE_WIDTH + ROAD_WIDTH - 1 || modX == PLOT_SIZE + EDGE_WIDTH + ROAD_WIDTH || modZ == EDGE_WIDTH + ROAD_WIDTH - 1 || modZ == PLOT_SIZE + EDGE_WIDTH + ROAD_WIDTH) {
                            chunk.setBlockState(new BlockPos(regionX + x, y + 1, regionZ + z), Blocks.SANDSTONE_SLAB.getDefaultState(), false);
                        }
                        if (modX < EDGE_WIDTH + ROAD_WIDTH - 1 || modX > PLOT_SIZE + EDGE_WIDTH + ROAD_WIDTH || modZ < EDGE_WIDTH + ROAD_WIDTH - 1 || modZ > PLOT_SIZE + EDGE_WIDTH + ROAD_WIDTH) {
                            chunk.setBlockState(new BlockPos(regionX + x, y, regionZ + z), Blocks.QUARTZ_BRICKS.getDefaultState(), false);
                            chunk.setBlockState(new BlockPos(regionX + x, y+1, regionZ + z), Blocks.AIR.getDefaultState(), false);
                        }
                    }
                    heightmap.trackUpdate(x, y, z, Blocks.STONE.getDefaultState());
                }
            }
        }
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getMinimumY() {
        return -64;
    }


    @Override
    public void getDebugHudText(List<String> list, NoiseConfig noiseConfig, BlockPos blockPos) {

    }

    @Override
    public void generateFeatures(StructureWorldAccess structureWorldAccess, Chunk chunk, StructureAccessor structureAccessor) {

    }

    @Override
    public CompletableFuture<Chunk> populateBiomes(Registry<Biome> registry, Executor executor, NoiseConfig noiseConfig, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
        return super.populateBiomes(registry, executor, noiseConfig, blender, structureAccessor, chunk);
    }
}
