package de.teamlapen.vampirism.world.gen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blocks.TotemTopBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.util.MixinHooks;
import de.teamlapen.vampirism.world.gen.util.BiomeTopBlockProcessor;
import de.teamlapen.vampirism.world.gen.util.RandomBlockState;
import de.teamlapen.vampirism.world.gen.util.RandomStructureProcessor;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.structure.VillagesPools;
import net.minecraft.world.gen.feature.template.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VampirismWorldGen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final static float TOTEM_PRESET_PERCENTAGE = 0.6f;
    private final static int HUNTER_TRAINER_WEIGHT = 400;
    public static boolean debug = false;

    public static void initVillageStructures() {
        VampirismWorldGen.setupSingleJigsawPieceGeneration();

        //init pools for modification
        VillagesPools.bootstrap();

        Pair<Map<String, List<Pair<JigsawPiece, Integer>>>, Map<String, JigsawPattern>> structures = getStructures();

        VampirismWorldGen.replaceTemples(structures.getFirst());

        addVillageStructures(structures.getFirst());

        VampirismWorldGen.saveChanges(structures.getFirst(), structures.getSecond());

    }

    public static void addVillageStructures() {
        Pair<Map<String, List<Pair<JigsawPiece, Integer>>>, Map<String, JigsawPattern>> structures = getStructures();

        addVillageStructures(structures.getFirst());

        VampirismWorldGen.saveChanges(structures.getFirst(), structures.getSecond());
    }

    public static void addVillageStructures(Map<String, List<Pair<JigsawPiece, Integer>>> map) {
        VampirismWorldGen.addHunterTrainerHouse(map);
        VampirismWorldGen.addTotem(map);
    }

    //
    private static Pair<Map<String, List<Pair<JigsawPiece, Integer>>>, Map<String, JigsawPattern>> getStructures() {
        Map<String, JigsawPattern> patterns = new HashMap<String, JigsawPattern>() {{
            put("plains", WorldGenRegistries.TEMPLATE_POOL.getOptional(new ResourceLocation("village/plains/houses")).get());
            put("desert", WorldGenRegistries.TEMPLATE_POOL.getOptional(new ResourceLocation("village/desert/houses")).get());
            put("savanna", WorldGenRegistries.TEMPLATE_POOL.getOptional(new ResourceLocation("village/savanna/houses")).get());
            put("taiga", WorldGenRegistries.TEMPLATE_POOL.getOptional(new ResourceLocation("village/taiga/houses")).get());
            put("snowy", WorldGenRegistries.TEMPLATE_POOL.getOptional(new ResourceLocation("village/snowy/houses")).get());
            put("plains_zombie", WorldGenRegistries.TEMPLATE_POOL.getOptional(new ResourceLocation("village/plains/zombie/houses")).get());
            put("desert_zombie", WorldGenRegistries.TEMPLATE_POOL.getOptional(new ResourceLocation("village/desert/zombie/houses")).get());
            put("savanna_zombie", WorldGenRegistries.TEMPLATE_POOL.getOptional(new ResourceLocation("village/savanna/zombie/houses")).get());
            put("taiga_zombie", WorldGenRegistries.TEMPLATE_POOL.getOptional(new ResourceLocation("village/taiga/zombie/houses")).get());
            put("snowy_zombie", WorldGenRegistries.TEMPLATE_POOL.getOptional(new ResourceLocation("village/snowy/zombie/houses")).get());
        }};

        //biome string -> list of all JigsawPieces with weight
        Map<String, List<Pair<JigsawPiece, Integer>>> buildings = Maps.newHashMapWithExpectedSize(patterns.size());
        //fill buildings with modifiable lists from the JigsawPattern's
        patterns.forEach((biome, pattern) -> buildings.put(biome, Lists.newArrayList(pattern.rawTemplates)));

        return Pair.of(buildings, patterns);
    }

    /**
     * replaces half of the temples with temples with church altar
     */
    private static void replaceTemples(Map<String, List<Pair<JigsawPiece, Integer>>> buildings) {
        //biome string -> JigsawPattern of the biome
        //toString() of the replaced JigsawPiece -> modified JigsawPiece
        Map<String, Map<String, JigsawPiece>> temples = new HashMap<String, Map<String, JigsawPiece>>() {{
            put("plains", ImmutableMap.of(
                    singleLegacyJigsawString("minecraft:village/plains/houses/plains_temple_3"), singleJigsawPiece("village/plains/houses/plains_temple_3", ProcessorLists.MOSSIFY_10_PERCENT),
                    singleLegacyJigsawString("minecraft:village/plains/houses/plains_temple_4"), singleJigsawPiece("village/plains/houses/plains_temple_4", ProcessorLists.MOSSIFY_10_PERCENT)));
            put("desert", ImmutableMap.of(
                    singleLegacyJigsawString("minecraft:village/desert/houses/desert_temple_1"), singleJigsawPiece("village/desert/houses/desert_temple_1"),
                    singleLegacyJigsawString("minecraft:village/desert/houses/desert_temple_2"), singleJigsawPiece("village/desert/houses/desert_temple_2")));
            put("savanna", ImmutableMap.of(
                    singleLegacyJigsawString("minecraft:village/savanna/houses/savanna_temple_1"), singleJigsawPiece("village/savanna/houses/savanna_temple_1"),
                    singleLegacyJigsawString("minecraft:village/savanna/houses/savanna_temple_2"), singleJigsawPiece("village/savanna/houses/savanna_temple_2")));
            put("taiga", ImmutableMap.of(
                    singleLegacyJigsawString("minecraft:village/taiga/houses/taiga_temple_1"), singleJigsawPiece("village/taiga/houses/taiga_temple_1", ProcessorLists.MOSSIFY_10_PERCENT)));
            put("snowy", ImmutableMap.of(
                    singleLegacyJigsawString("minecraft:village/snowy/houses/snowy_temple_1"), singleJigsawPiece("village/snowy/houses/snowy_temple_1")));
            put("plains_zombie", ImmutableMap.of(
                    singleLegacyJigsawString("minecraft:village/plains/houses/plains_temple_3"), singleJigsawPiece("village/plains/houses/plains_temple_3", ProcessorLists.ZOMBIE_PLAINS),
                    singleLegacyJigsawString("minecraft:village/plains/houses/plains_temple_4"), singleJigsawPiece("village/plains/houses/plains_temple_4", ProcessorLists.ZOMBIE_PLAINS)));
            put("desert_zombie", ImmutableMap.of(
                    singleLegacyJigsawString("minecraft:village/desert/houses/desert_temple_1"), singleJigsawPiece("village/desert/houses/desert_temple_1", ProcessorLists.ZOMBIE_DESERT),
                    singleLegacyJigsawString("minecraft:village/desert/houses/desert_temple_2"), singleJigsawPiece("village/desert/houses/desert_temple_2", ProcessorLists.ZOMBIE_DESERT)));
            put("savanna_zombie", ImmutableMap.of(
                    singleLegacyJigsawString("minecraft:village/savanna/houses/savanna_temple_1"), singleJigsawPiece("village/savanna/houses/savanna_temple_1", ProcessorLists.ZOMBIE_SAVANNA),
                    singleLegacyJigsawString("minecraft:village/savanna/houses/savanna_temple_2"), singleJigsawPiece("village/savanna/houses/savanna_temple_2", ProcessorLists.ZOMBIE_SAVANNA)));
            put("taiga_zombie", ImmutableMap.of(
                    singleLegacyJigsawString("minecraft:village/taiga/zombie/houses/taiga_temple_1"), singleJigsawPiece("village/taiga/houses/taiga_temple_1", ProcessorLists.ZOMBIE_TAIGA)));
            put("snowy_zombie", ImmutableMap.of(
                    singleLegacyJigsawString("minecraft:village/snowy/houses/snowy_temple_1"), singleJigsawPiece("village/snowy/houses/snowy_temple_1", ProcessorLists.ZOMBIE_SNOWY)));
        }};

        Map<String, List<Pair<JigsawPiece, Pair<JigsawPiece, Integer>>>> allPieces = Maps.newHashMapWithExpectedSize(temples.size());

        //saves replaceable JigsawPieces with weight to map with Replace
        temples.forEach((biome, replacer) -> buildings.get(biome).removeIf(house -> {
            if (replacer.containsKey(house.getFirst().toString())) {
                allPieces.computeIfAbsent(biome, key -> Lists.newArrayList()).add(Pair.of(replacer.get(house.getFirst().toString()), house));
                return true;
            }
            return false;
        }));

        if (allPieces.size() > temples.size()) {
            LOGGER.error("Could not find all temples to replace");
        }


        //add old and new JigsawPieces with half weight to the JigsawPattern lists
        temples.forEach((biome, replacer) -> replacer.values().forEach(piece -> {
            if (allPieces.containsKey(biome)) {
                allPieces.get(biome).forEach(pair -> {
                    if (pair.getFirst() == piece) {
                        int weight = Math.max((int) (pair.getSecond().getSecond() * 0.6), 1);
                        buildings.get(biome).add(Pair.of(piece, weight));
                        buildings.get(biome).add(Pair.of(pair.getSecond().getFirst(), weight));
                    }
                });
            }
        }));
    }

    /**
     * adds a hunter trainer house to each village
     */
    private static void addHunterTrainerHouse(Map<String, List<Pair<JigsawPiece, Integer>>> buildings) {
        //all structureProcessors are copied from PlainsVillagePools, DesertVillagePools, TaigaVillagePools, SavannaVillagePools, SnowyVillagePools
        Map<String, StructureProcessorList> processors = ImmutableMap.of("plains_zombie", ProcessorLists.ZOMBIE_PLAINS, "desert_zombie", ProcessorLists.ZOMBIE_DESERT, "snowy_zombie", ProcessorLists.ZOMBIE_SNOWY, "savanna_zombie", ProcessorLists.ZOMBIE_SAVANNA, "taiga_zombie", ProcessorLists.ZOMBIE_TAIGA);

        //hunter trainer JigsawPattern
        JigsawPatternRegistry.register(new JigsawPattern(new ResourceLocation(REFERENCE.MODID, "village/entities/hunter_trainer"), new ResourceLocation("empty"), Lists.newArrayList(Pair.of(singleJigsawPieceFunction("village/entities/hunter_trainer"), 1)), JigsawPattern.PlacementBehaviour.RIGID));

        buildings.forEach((name, list) -> {
            list.removeIf(pair -> pair.getFirst().toString().equals(singleJigsawString(REFERENCE.MODID + ":village/" + name.replace("_zombie", "") + "/houses/hunter_trainer")));
            list.add(Pair.of(singleJigsawPiece("village/" + name.replace("_zombie", "") + "/houses/hunter_trainer", processors.getOrDefault(name, new StructureProcessorList(Collections.emptyList()))), HUNTER_TRAINER_WEIGHT));
        });
    }

    /**
     * adds totem to every village
     */
    private static void addTotem(Map<String, List<Pair<JigsawPiece, Integer>>> buildings) {
        StructureProcessor totemProcessor = new RandomStructureProcessor(ImmutableList.of(new RandomBlockState(new RandomBlockMatchRuleTest(ModBlocks.totem_top, TOTEM_PRESET_PERCENTAGE), AlwaysTrueRuleTest.INSTANCE, ModBlocks.totem_top.defaultBlockState(), TotemTopBlock.getBlocks().stream().filter(totem -> totem != ModBlocks.totem_top && !totem.isCrafted()).map(Block::defaultBlockState).collect(Collectors.toList()))));
        StructureProcessor totemTopBlock = new BiomeTopBlockProcessor(Blocks.BRICK_WALL.defaultBlockState());
        JigsawPiece totem = singleJigsawPiece("village/totem", new StructureProcessorList(Lists.newArrayList(totemProcessor, totemTopBlock)));
        buildings.values().forEach(list -> list.removeIf(pair -> pair.getFirst().toString().equals(singleJigsawString(REFERENCE.MODID + ":village/totem"))));
        buildings.values().forEach(list -> list.add(Pair.of(totem, VampirismConfig.BALANCE.viTotemWeight.get())));
    }

    /**
     * ensure single generation of following structures
     */
    private static void setupSingleJigsawPieceGeneration() {
        MixinHooks.addSingleInstanceStructure(Lists.newArrayList(
                singleJigsawString("vampirism:village/totem"),
                singleJigsawString("vampirism:village/desert/houses/hunter_trainer"),
                singleJigsawString("vampirism:village/plains/houses/hunter_trainer"),
                singleJigsawString("vampirism:village/snowy/houses/hunter_trainer"),
                singleJigsawString("vampirism:village/savanna/houses/hunter_trainer"),
                singleJigsawString("vampirism:village/taiga/houses/hunter_trainer")));
    }

    /**
     * writes the changes made to buildings back into immutablemaps in the pattern
     */
    private static void saveChanges(Map<String, List<Pair<JigsawPiece, Integer>>> buildings, Map<String, JigsawPattern> patterns) {
        //write all Lists back to the specific JigsawPattern
        buildings.forEach((biome, list) -> patterns.get(biome).rawTemplates = ImmutableList.copyOf(list));

        //sync all JigsawPattern JigsawPattern#rawTemplates (pairs piece with weight) with JigsawPattern#jigsawPieces (list of pieces * weights)
        patterns.values().forEach(pattern -> {
            pattern.templates.clear();
            pattern.rawTemplates.forEach(pair -> {
                for (int i = 0; i < pair.getSecond(); i++) {
                    pattern.templates.add(pair.getFirst());
                }
            });
        });
    }

    private static SingleJigsawPiece singleJigsawPiece(@Nonnull String path) {
        return singleJigsawPiece(path, new StructureProcessorList(Collections.emptyList()));
    }

    private static SingleJigsawPiece singleJigsawPiece(@Nonnull String path, @Nonnull StructureProcessorList processors) {
        return SingleJigsawPiece.single(REFERENCE.MODID + ":" + path, processors).apply(JigsawPattern.PlacementBehaviour.RIGID);
    }

    private static Function<JigsawPattern.PlacementBehaviour, SingleJigsawPiece> singleJigsawPieceFunction(@Nonnull String path) {
        return singleJigsawPieceFunction(path, new StructureProcessorList(Collections.emptyList()));
    }

    private static Function<JigsawPattern.PlacementBehaviour, SingleJigsawPiece> singleJigsawPieceFunction(@Nonnull String path, @Nonnull StructureProcessorList processors) {
        return SingleJigsawPiece.single(REFERENCE.MODID + ":" + path, processors);
    }

    private static String singleJigsawString(String resourceLocation) {
        return "Single[Left[" + resourceLocation + "]]";
    }

    private static String singleLegacyJigsawString(String resourceLocation) {
        return "LegacySingle[Left[" + resourceLocation + "]]";
    }

}
