package chylex.hee.world.structure._old.sanctuary.data;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.system.logging.Log;
import com.google.common.collect.ImmutableList;

public class SanctuaryConquerPointGen{
	private final byte width, depth;
	private final boolean[][] available;
	private final List<byte[]> chosenPoints = new ArrayList<>();
	
	SanctuaryConquerPointGen(int width, int depth){
		this.width = (byte)width;
		this.depth = (byte)depth;
		this.available = new boolean[width][depth];
		
		for(int x = 0; x < width; x++){
			for(int z = 0; z < depth; z++){
				available[x][z] = true;
			}
		}
	}
	
	boolean tryAddPoint(byte x, byte z){
		if (!available[x][z])return false;
		
		chosenPoints.add(new byte[]{ x, z });
		
		for(byte px = (byte)(x-1); px <= x+1; px++){
			for(byte pz = (byte)(z-1); pz <= z+1; pz++){
				tryBlock(px,pz);
			}
		}
		
		for(Facing4 dir:Facing4.list){
			tryBlock((byte)(x+dir.getX()*2),(byte)(z+dir.getZ()*2));
		}
		
		return true;
	}
	
	boolean tryBlock(byte x, byte z){
		if (x < 0 || z < 0 || x >= width || z >= depth)return false;
		available[x][z] = false;
		return true;
	}
	
	public List<byte[]> getPoints(){
		return ImmutableList.copyOf(chosenPoints);
	}
	
	public static SanctuaryConquerPointGen generate(Random rand, int width, int depth, int conquerPointAmount){
		SanctuaryConquerPointGen pts = new SanctuaryConquerPointGen(width,depth);
		List<byte[]> options = new ArrayList<>();
		
		for(int x = 0; x < width; x++){
			for(int z = 0; z < depth; z++){
				options.add(new byte[]{ (byte)x, (byte)z });
			}
		}
		
		while(!options.isEmpty()){
			byte[] loc = options.remove(rand.nextInt(options.size()));
			if (pts.tryAddPoint(loc[0],loc[1]) && pts.chosenPoints.size() >= conquerPointAmount)break;
		}
		
		if (pts.chosenPoints.size() != conquerPointAmount)Log.warn("Incorrect conquer point amount, expected $0, got $1 ($2)",conquerPointAmount,pts.chosenPoints.size(),pts.chosenPoints);
		
		return pts;
	}
}
