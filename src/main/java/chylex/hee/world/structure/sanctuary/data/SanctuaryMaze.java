package chylex.hee.world.structure.sanctuary.data;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.util.Direction;
import chylex.hee.system.util.CycleProtection;
import chylex.hee.world.structure.util.Facing;

public final class SanctuaryMaze{
	private final byte width, depth;
	private final boolean[][] openX;
	private final boolean[][] openZ;
	
	SanctuaryMaze(int width, int depth){
		this.width = (byte)width;
		this.depth = (byte)depth;
		this.openX = new boolean[width-1][depth];
		this.openZ = new boolean[depth-1][width];
	}
	
	public boolean hasConnections(int x, int z, Facing facing){
		int dir = facing.get4Directional();
		
		x += Direction.offsetX[dir];
		z += Direction.offsetZ[dir];
		
		if (x < 0 || z < 0 || x >= width || z >= depth)return false;
		
		for(int ldir = 0; ldir < 4; ldir++){
			if (isOpen(x,z,Facing.from4Directional(ldir)))return true;
		}
		
		return false;
	}
	
	public boolean isOpen(int x, int z, Facing facing){
		int dir = facing.get4Directional();
		int x2 = x+Direction.offsetX[dir];
		int z2 = z+Direction.offsetZ[dir];
		if (x2 < 0 || z2 < 0 || x2 >= width || z2 >= depth)return false;
		
		return x == x2 ? openZ[Math.min(z,z2)][x] : openX[Math.min(x,x2)][z];
	}
	
	boolean setOpen(int x, int z, Facing facing){
		int dir = facing.get4Directional();
		int x2 = x+Direction.offsetX[dir];
		int z2 = z+Direction.offsetZ[dir];
		if (x2 < 0 || z2 < 0 || x2 >= width || z2 >= depth)return false;
		
		if (x == x2)openZ[Math.min(z,z2)][x] = true;
		else openX[Math.min(x,x2)][z] = true;
		
		return true;
	}
	
	boolean isDone(){
		for(int x = 0; x < width; x++){
			for(int z = 0; z < depth; z++){
				for(int dir = 0; dir < 4; dir++){
					if (isOpen(x,z,Facing.from4Directional(dir)))break;
					if (dir == 3)return false;
				}
			}
		}
		
		return true;
	}
	
	public static SanctuaryMaze generate(Random rand, int width, int depth){
		SanctuaryMaze maze = new SanctuaryMaze(width,depth);
		CycleProtection.setCounter(1000);
		
		int x = 0, z = rand.nextInt(depth), dir, test = 0;
		List<int[]> used = new ArrayList<>();
		
		while(!maze.isDone() && CycleProtection.proceed()){
			dir = rand.nextInt(4);
			
			if (!maze.hasConnections(x,z,Facing.from4Directional(dir)) && maze.setOpen(x,z,Facing.from4Directional(dir))){
				used.add(new int[]{ x, z });
				x += Direction.offsetX[dir];
				z += Direction.offsetZ[dir];
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
			maze.setOpen(x,z,Facing.from4Directional(rand.nextInt(4)));
		}
		
		return maze;
	}
}
