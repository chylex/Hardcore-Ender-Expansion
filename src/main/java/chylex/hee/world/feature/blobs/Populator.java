package chylex.hee.world.feature.blobs;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public abstract class Populator{
	protected int[] size,minPos,maxPos;
	
	public void populate(int[] size, int[] minPos, int[] maxPos, World world, Random rand, int x, int y, int z){
		this.size = size;
		this.minPos = minPos;
		this.maxPos = maxPos;
		populate(world,rand,x,y,z);
	}
	
	protected abstract void populate(World world, Random rand, int x, int y, int z);
	
	protected final Block getBlock(World world, int x, int y, int z){
		return isInRange(x,y,z)?world.getBlock(x,y,z):null;
	}
	
	protected final boolean isInRange(int x, int y, int z){
		return x >= minPos[0] && x <= maxPos[0] && y >= minPos[1] && y <= maxPos[1] && z >= minPos[2] && z <= maxPos[2];
	}
}
