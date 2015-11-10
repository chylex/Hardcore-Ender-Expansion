package chylex.hee.world.end.gen;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.world.end.TerritoryGenerator;
import chylex.hee.world.feature.noise.GenerateIslandNoise;
import chylex.hee.world.structure.StructureWorld;

public class TerritoryTheHub extends TerritoryGenerator{
	public static boolean $regenerate = false; // TODO remove
	
	private final GenerateIslandNoise island;
	
	public TerritoryTheHub(StructureWorld world, Random rand){
		super(world,rand);
		
		this.island = new GenerateIslandNoise(Blocks.end_stone,rand);
		this.island.terrainSize = 56;
		this.island.noiseHeight = 20;
		this.island.peakSmoothness = 50D;
		this.island.surfaceHillScale = 92F;
	}
	
	@Override
	public void generate(){
		if ($regenerate){
			for(int x = -size; x < size; x++){
				for(int z = -size; z < size; z++){
					for(int y = 0; y < height; y++){
						world.setAir(x,y,z);
					}
				}
			}
		}
		
		island.generate(world);
		
		int lowest = height;
		
		for(int x = -1; x <= 1; x++){
			for(int z = -1; z <= 1; z++){
				lowest = Math.min(lowest,world.getTopY(x,z,Blocks.end_stone));
			}
		}
		
		for(int x = -2; x <= 2; x++){
			for(int z = -2; z <= 2; z++){
				if (Math.abs(x) <= 1 && Math.abs(z) <= 1)world.setAttentionWhore(x,lowest,z,new BlockInfo(Blocks.end_portal,Meta.endPortalActive));
				
				for(int y = lowest+1; y < height; y++){
					if (!world.isAir(x,y,z))world.setAir(x,y,z);
				}
			}
		}
	}
}
