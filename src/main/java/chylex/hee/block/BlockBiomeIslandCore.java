package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;

public class BlockBiomeIslandCore extends Block{
	public BlockBiomeIslandCore(){
		super(Material.rock);
		disableStats();
	}
	
	@Override
	public int tickRate(World world){
		return 4;
	}
	
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state){
		super.onBlockAdded(world,pos,state);
		world.scheduleUpdate(pos,this,tickRate(world));
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand){
		world.scheduleUpdate(pos,this,tickRate(world));
		
		int meta = world.getBlockMetadata(x,y,z);
		
		for(IslandBiomeBase biome:IslandBiomeBase.biomeList){
			if (biome.isValidMetadata(meta)){
				biome.updateCore(world,x,y,z,meta);
				break;
			}
		}
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos){
		return null;
	}
}
