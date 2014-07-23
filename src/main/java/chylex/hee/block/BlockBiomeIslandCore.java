package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
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
	public void onBlockAdded(World world, int x, int y, int z){
		super.onBlockAdded(world,x,y,z);
		world.scheduleBlockUpdate(x,y,z,this,tickRate(world));
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand){
		world.scheduleBlockUpdate(x,y,z,this,tickRate(world));
		
		int meta = world.getBlockMetadata(x,y,z);
		
		for(IslandBiomeBase biome:IslandBiomeBase.biomeList){
			if (biome.isValidMetadata(meta)){
				biome.updateCore(world,x,y,z,meta);
				break;
			}
		}
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z){
		return null;
	}
}
