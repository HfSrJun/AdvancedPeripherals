package de.srendi.advancedperipherals.common.blocks;

import de.srendi.advancedperipherals.common.addons.computercraft.CCEventManager;
import de.srendi.advancedperipherals.common.blocks.tileentity.ChatBoxTileEntity;
import de.srendi.advancedperipherals.common.blocks.tileentity.PlayerDetectorTileEntity;
import de.srendi.advancedperipherals.common.blocks.tileentity.TileEntityList;
import de.srendi.advancedperipherals.common.setup.ModTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerDetectorBlock extends Block {

    public PlayerDetectorBlock() {
        super(Properties.create(Material.IRON).hardnessAndResistance(1, 5).harvestLevel(0).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).setRequiresTool());
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntityTypes.PLAYER_DETECTOR.get().create();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        //Todo: This stuff is called twice. I need to prevent this
        for (TileEntity tileEntity : TileEntityList.get().getTileEntitys(worldIn)) {
            if (tileEntity instanceof PlayerDetectorTileEntity) {
                CCEventManager.getInstance().sendEvent(tileEntity, "playerClickEvent", player.getName().getString());
            }
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        TileEntityList.get().setTileEntity(worldIn, pos);
    }

    @Override
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
        super.onPlayerDestroy(worldIn, pos, state);
        TileEntityList.get().setTileEntity((World) worldIn, pos);
    }

}
