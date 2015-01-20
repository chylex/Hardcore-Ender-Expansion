package chylex.hee.world.util;
import net.minecraft.util.BlockPos;

public class BlockLocation{
	public final int x, y, z;
	
	public BlockLocation(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public BlockLocation(BlockPos pos){
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof BlockLocation){
			BlockLocation loc = (BlockLocation)o;
			return x == loc.x && y == loc.y && z == loc.z;
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return y+31*x+961*z;
	}
}
