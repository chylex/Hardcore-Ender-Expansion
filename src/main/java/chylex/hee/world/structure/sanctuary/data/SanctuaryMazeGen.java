package chylex.hee.world.structure.sanctuary.data;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.system.util.CycleProtection;

public final class SanctuaryMazeGen{
	private final byte width, depth;
	private final boolean[][] openX;
	private final boolean[][] openZ;
	
	SanctuaryMazeGen(int width, int depth){
		this.width = (byte)width;
		this.depth = (byte)depth;
		this.openX = new boolean[width-1][depth];
		this.openZ = new boolean[depth-1][width];
	}
	
	public boolean hasConnections(int x, int z, Facing4 facing){
		x += facing.getX();
		z += facing.getZ();
		
		if (x < 0 || z < 0 || x >= width || z >= depth)return false;
		
		for(Facing4 test:Facing4.list){
			if (isOpen(x,z,test))return true;
		}
		
		return false;
	}
	
	public boolean isOpen(int x, int z, Facing4 facing){
		int x2 = x+facing.getX();
		int z2 = z+facing.getZ();
		if (x2 < 0 || z2 < 0 || x2 >= width || z2 >= depth)return false;
		
		return x == x2 ? openZ[Math.min(z,z2)][x] : openX[Math.min(x,x2)][z];
	}
	
	boolean setOpen(int x, int z, Facing4 facing){
		int x2 = x+facing.getX();
		int z2 = z+facing.getZ();
		if (x2 < 0 || z2 < 0 || x2 >= width || z2 >= depth)return false;
		
		if (x == x2)openZ[Math.min(z,z2)][x] = true;
		else openX[Math.min(x,x2)][z] = true;
		
		return true;
	}
	
	boolean isDone(){
		for(int x = 0; x < width; x++){
			for(int z = 0; z < depth; z++){
				for(int dir = 0; dir < Facing4.list.length; dir++){
					if (isOpen(x,z,Facing4.list[dir]))break;
					if (dir == Facing4.list.length-1)return false;
				}
			}
		}
		
		return true;
	}
	
	public static SanctuaryMazeGen generate(Random rand, int width, int depth){
		SanctuaryMazeGen maze = new SanctuaryMazeGen(width,depth);
		CycleProtection.setCounter(4000);
		
		int x = 0, z = rand.nextInt(depth), test = 0;
		Facing4 dir;
		List<int[]> used = new ArrayList<>();
		
		while(!maze.isDone() && CycleProtection.proceed()){
			dir = Facing4.list[rand.nextInt(Facing4.list.length)];
			
			if (!maze.hasConnections(x,z,dir) && maze.setOpen(x,z,dir)){
				used.add(new int[]{ x, z });
				x += dir.getX();
				z += dir.getZ();
			}
			else if (++test > 8 && !used.isEmpty()){
				int[] pos = used.get(rand.nextInt(used.size()));
				x = pos[0];
				z = pos[1];
				test = 0;
			}
		}
		
		if (CycleProtection.failed())return maze;
		
		for(int connect = 6+rand.nextInt(10); connect > 0; connect--){
			x = rand.nextInt(width);
			z = rand.nextInt(depth);
			maze.setOpen(x,z,Facing4.list[rand.nextInt(Facing4.list.length)]);
		}
		
		return maze;
	}
}
