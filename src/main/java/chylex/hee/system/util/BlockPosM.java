package chylex.hee.system.util;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockPosM extends BlockPos{
	public int x, y, z;
	
	public BlockPosM(){
		this(0,0,0);
	}
	
	public BlockPosM(int x, int y, int z){
		super(0,0,0);
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public int getX(){
		return x;
	}
	
	@Override
	public int getY(){
		return y;
	}
	
	@Override
	public int getZ(){
		return z;
	}
	
	public Block getBlock(World world){
		return world.getBlockState(this).getBlock();
	}
	
	public Material getBlockMaterial(World world){
		return world.getBlockState(this).getBlock().getMaterial();
	}
}
