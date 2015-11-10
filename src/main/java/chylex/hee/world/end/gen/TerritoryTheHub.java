package chylex.hee.world.end.gen;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.world.end.TerritoryGenerator;
import chylex.hee.world.feature.noise.GenerateIslandNoise;
import chylex.hee.world.structure.StructureWorld;

public class TerritoryTheHub extends TerritoryGenerator{
	private final GenerateIslandNoise island;
	
	public TerritoryTheHub(StructureWorld world, Random rand){
		super(world,rand);
		this.island = new GenerateIslandNoise(Blocks.end_stone,rand);
	}
	
	@Override
	public void generate(){
		island.generate(world);
		
		int lowest = 128;
		
		for(int x = -1; x <= 1; x++){
			for(int z = -1; z <= 1; z++){
				lowest = Math.min(lowest,world.getTopY(x,z,Blocks.end_stone));
			}
		}
		
		for(int x = -1; x <= 1; x++){
			for(int z = -1; z <= 1; z++){
				world.setBlock(x,lowest,z,Blocks.end_portal,Meta.endPortalActive);
			}
		}
	}
}
