package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.*;
import net.minecraft.data.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;

public class TagGenerator {

    public static void register(DataGenerator generator, ExistingFileHelper helper) {
        BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(generator, helper);
        generator.addProvider(blockTagsProvider);
        generator.addProvider(new ModItemTagsProvider(generator, blockTagsProvider, helper));
        generator.addProvider(new ModEntityTypeTagsProvider(generator, helper));
        generator.addProvider(new ModFluidTagsProvider(generator, helper));
    }

    public static class ModBlockTagsProvider extends BlockTagsProvider {
        public ModBlockTagsProvider(DataGenerator dataGenerator, ExistingFileHelper helper) {
            super(dataGenerator, REFERENCE.MODID, helper);
        }

        @Nonnull
        @Override
        public String getName() {
            return REFERENCE.MODID + " " + super.getName();
        }

        @Override
        protected void addTags() {
            tag(Tags.Blocks.DIRT).add(ModBlocks.cursed_earth);
            tag(ModTags.Blocks.CURSEDEARTH).add(ModBlocks.cursed_earth);
            tag(ModTags.Blocks.CASTLE_BLOCK).add(ModBlocks.castle_block_dark_brick, ModBlocks.castle_block_dark_brick_bloody, ModBlocks.castle_block_dark_stone, ModBlocks.castle_block_normal_brick, ModBlocks.castle_block_purple_brick);
            tag(ModTags.Blocks.CASTLE_SLAPS).add(ModBlocks.castle_slab_dark_brick, ModBlocks.castle_slab_dark_stone, ModBlocks.castle_slab_purple_brick);
            tag(ModTags.Blocks.CASTLE_STAIRS).add(ModBlocks.castle_stairs_dark_stone, ModBlocks.castle_stairs_dark_brick, ModBlocks.castle_stairs_purple_brick);
            tag(BlockTags.STAIRS).addTag(ModTags.Blocks.CASTLE_STAIRS);
            tag(BlockTags.SLABS).addTag(ModTags.Blocks.CASTLE_SLAPS);
            tag(BlockTags.FLOWER_POTS).add(ModBlocks.potted_vampire_orchid);
            tag(BlockTags.SPRUCE_LOGS).add(ModBlocks.bloody_spruce_log);
            tag(BlockTags.LEAVES).add(ModBlocks.vampire_spruce_leaves, ModBlocks.bloody_spruce_leaves);
            tag(BlockTags.SAPLINGS).add(ModBlocks.bloody_spruce_sapling);
        }
    }

    public static class ModItemTagsProvider extends ItemTagsProvider {
        public ModItemTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagsProvider, ExistingFileHelper helper) {
            super(dataGenerator, blockTagsProvider, REFERENCE.MODID, helper);
        }

        @Nonnull
        @Override
        public String getName() {
            return REFERENCE.MODID + " " + super.getName();
        }

        @Override
        protected void addTags() {
            copy(ModTags.Blocks.CASTLE_BLOCK, ModTags.Items.CASTLE_BLOCK);
            copy(ModTags.Blocks.CASTLE_STAIRS, ModTags.Items.CASTLE_STAIRS);
            copy(ModTags.Blocks.CASTLE_SLAPS, ModTags.Items.CASTLE_SLAPS);
            copy(ModTags.Blocks.CURSEDEARTH, ModTags.Items.CURSEDEARTH);

            tag(ModTags.Items.CROSSBOW_ARROW).add(ModItems.crossbow_arrow_normal, ModItems.crossbow_arrow_spitfire, ModItems.crossbow_arrow_vampire_killer);
            tag(ModTags.Items.HUNTER_INTEL).add(ModItems.hunter_intel_0, ModItems.hunter_intel_1, ModItems.hunter_intel_2, ModItems.hunter_intel_3, ModItems.hunter_intel_4, ModItems.hunter_intel_5, ModItems.hunter_intel_6, ModItems.hunter_intel_7, ModItems.hunter_intel_8, ModItems.hunter_intel_9);
            tag(ModTags.Items.PURE_BLOOD).add(ModItems.pure_blood_0, ModItems.pure_blood_1, ModItems.pure_blood_2, ModItems.pure_blood_3, ModItems.pure_blood_4);
            tag(ModTags.Items.VAMPIRE_CLOAK).add(ModItems.vampire_cloak_black_blue, ModItems.vampire_cloak_black_red, ModItems.vampire_cloak_black_white, ModItems.vampire_cloak_red_black, ModItems.vampire_cloak_white_black);
            tag(ItemTags.SMALL_FLOWERS).add(ModItems.vampire_orchid);
            tag(ModTags.Items.GARLIC).add(ModItems.item_garlic);
            tag(ModTags.Items.HOLY_WATER).add(ModItems.holy_water_bottle_normal, ModItems.holy_water_bottle_enhanced, ModItems.holy_water_bottle_ultimate);
            tag(ModTags.Items.HOLY_WATER_SPLASH).add(ModItems.holy_water_splash_bottle_normal, ModItems.holy_water_splash_bottle_enhanced, ModItems.holy_water_splash_bottle_ultimate);
            tag(ItemTags.STAIRS).addTag(ModTags.Items.CASTLE_STAIRS);
            tag(ItemTags.SLABS).addTag(ModTags.Items.CASTLE_SLAPS);
            tag(ItemTags.SPRUCE_LOGS).add(ModBlocks.bloody_spruce_log.asItem());
            tag(ItemTags.LEAVES).add(ModBlocks.vampire_spruce_leaves.asItem(), ModBlocks.bloody_spruce_leaves.asItem());
            tag(ItemTags.SAPLINGS).add(ModBlocks.bloody_spruce_sapling.asItem());
        }
    }

    public static class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {
        public ModEntityTypeTagsProvider(DataGenerator dataGenerator, ExistingFileHelper helper) {
            super(dataGenerator, REFERENCE.MODID, helper);
        }

        @Override
        protected void addTags() {
            tag(ModTags.Entities.HUNTER).add(ModEntities.hunter, ModEntities.hunter_imob, ModEntities.advanced_hunter, ModEntities.advanced_hunter_imob, ModEntities.hunter_trainer, ModEntities.hunter_trainer, ModEntities.hunter_trainer_dummy, ModEntities.task_master_hunter);
            tag(ModTags.Entities.VAMPIRE).add(ModEntities.vampire, ModEntities.vampire_imob, ModEntities.advanced_vampire, ModEntities.advanced_vampire_imob, ModEntities.vampire_baron, ModEntities.task_master_vampire);
            tag(ModTags.Entities.ADVANCED_HUNTER).add(ModEntities.advanced_hunter, ModEntities.advanced_hunter_imob);
            tag(ModTags.Entities.ADVANCED_VAMPIRE).add(ModEntities.advanced_vampire, ModEntities.advanced_vampire_imob);
        }
    }

    public static class ModFluidTagsProvider extends FluidTagsProvider {
        public ModFluidTagsProvider(DataGenerator generatorIn, ExistingFileHelper helper) {
            super(generatorIn, REFERENCE.MODID, helper);
        }

        @Override
        public String getName() {
            return REFERENCE.MODID + " " + super.getName();
        }

        @Override
        protected void addTags() {
            tag(ModTags.Fluids.BLOOD).add(ModFluids.blood);
            tag(ModTags.Fluids.IMPURE_BLOOD).add(ModFluids.impure_blood);
        }
    }
}
