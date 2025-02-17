package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.HolyWaterBottleItem;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Random;

public class CursedEarthBlock extends VampirismBlock implements IGrowable {

    private static final String name = "cursed_earth";

    public CursedEarthBlock() {
        super(name, Properties.of(Material.DIRT).strength(0.5f, 2.0f).sound(SoundType.GRAVEL));

    }

    @Override
    public boolean isBonemealSuccess(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction direction, IPlantable plantable) {
        return plantable instanceof BushBlock || plantable.getPlantType(world, pos).equals(VReference.VAMPIRE_PLANT_TYPE);
    }

    @Override
    public boolean isValidBonemealTarget(IBlockReader iBlockReader, BlockPos blockPos, BlockState iBlockState, boolean b) {
        return true;
    }

    @Override
    public int getHarvestLevel(BlockState p_getHarvestLevel_1_) {
        return 0;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState p_getHarvestTool_1_) {
        return ToolType.SHOVEL;
    }

    @Override
    public void onPlantGrow(BlockState state, IWorld world, BlockPos pos, BlockPos source) {
        world.setBlock(pos, ModBlocks.cursed_earth.defaultBlockState(), 2);
    }

    @Override
    public void performBonemeal(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
        BlockPos blockpos = pos.above();

        for (int i = 0; i < 128; ++i) {
            BlockPos blockpos1 = blockpos;
            int j = 0;

            while (true) {
                if (j >= i / 16) {
                    if (worldIn.isEmptyBlock(blockpos1)) {
                        if (rand.nextInt(8) == 0) {
                            VampirismFlowerBlock blockflower = ModBlocks.vampire_orchid;
                            BlockState iblockstate = blockflower.defaultBlockState();

                            if (blockflower.canSurvive(iblockstate, worldIn, blockpos1)) {
                                worldIn.setBlock(blockpos1, iblockstate, 3);
                            }
                        } else {
                            BlockState iblockstate1 = Blocks.TALL_GRASS.defaultBlockState();

                            if (Blocks.TALL_GRASS.canSurvive(iblockstate1, worldIn, blockpos1)) {
                                worldIn.setBlock(blockpos1, iblockstate1, 3);
                            }
                        }
                    }

                    break;
                }

                blockpos1 = blockpos1.offset(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

                if (worldIn.getBlockState(blockpos1.below()).getBlock() != ModBlocks.cursed_earth || worldIn.getBlockState(blockpos1).isRedstoneConductor(worldIn, blockpos1)) {
                    break;
                }

                ++j;
            }
        }
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack heldItemStack = player.getItemInHand(handIn);
        Item heldItem = heldItemStack.getItem();
        if (heldItem instanceof HolyWaterBottleItem) {
            int uses = heldItem == ModItems.holy_water_bottle_ultimate ? 100 : (heldItem == ModItems.holy_water_bottle_enhanced ? 50 : 25);
            if (player.getRandom().nextInt(uses) == 0) {
                heldItemStack.setCount(heldItemStack.getCount() - 1);
            }
            worldIn.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState());
            return ActionResultType.SUCCESS;
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }
}
