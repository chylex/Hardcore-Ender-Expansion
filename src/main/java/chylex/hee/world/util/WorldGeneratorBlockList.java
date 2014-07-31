package chylex.hee.world.util;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import chylex.hee.system.util.DragonUtil;

public class WorldGeneratorBlockList extends ArrayList<BlockLocation>{
	private static final long serialVersionUID = -2395610713307276330L;
	
	private World world;
	
	public WorldGeneratorBlockList(World world){
		super(100);
		this.world = world;
	}
	
	public World getWorld(){
		return world;
	}
	
	public int[][] getData(){
		int[] size = new int[]{ 0, 0, 0 }, min = new int[3], max = new int[3];
		
		if (size() > 0){
			BlockLocation loc = get(0);
			min[0] = max[0] = loc.x;
			min[1] = max[1] = loc.y;
			min[2] = max[2] = loc.z;
		}
		
		for(int a = 1; a < size(); a++){
			BlockLocation loc = get(a);
			if (loc.x < min[0])min[0] = loc.x;
			else if (loc.x > max[0])max[0] = loc.x;
			else if (loc.y < min[1])min[1] = loc.y;
			else if (loc.y > max[1])max[1] = loc.y;
			else if (loc.z < min[2])min[2] = loc.z;
			else if (loc.z > max[2])max[2] = loc.z;
		}
		
		size[0] = 1+max[0]-min[0];
		size[1] = 1+max[1]-min[1];
		size[2] = 1+max[2]-min[2];
		return new int[][]{ size, min, max };
	}

	public boolean generate(Block block){
		if (size() == 0)return false;
		try{
			for(BlockLocation loc:this)world.setBlock(loc.x,loc.y,loc.z,block);
		}catch(Exception e){
			DragonUtil.warning("Ouch, WorldGeneratorBlockList's generation gone bad :(");
			return false;
		}
		return true;
	}
}
