package com.enderio.base.common.block.painted;

import com.enderio.base.common.blockentity.SinglePaintedBlockEntity;
import com.enderio.base.common.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class PaintedStairBlock extends StairBlock implements EntityBlock, IPaintedBlock {

    public PaintedStairBlock(Properties properties) {
        super(Blocks.OAK_PLANKS.defaultBlockState(), properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return EIOBlockEntities.SINGLE_PAINTED.create(pos, state);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return getPaintedStack(level, pos, this);
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side, @Nullable BlockState queryState,
        @Nullable BlockPos queryPos) {
        if (level.getBlockEntity(pos) instanceof SinglePaintedBlockEntity painted && painted.getPaint() != null) {
            return painted.getPaint().defaultBlockState();
        }
        return super.getAppearance(state, level, pos, side, queryState, queryPos);
    }
}
