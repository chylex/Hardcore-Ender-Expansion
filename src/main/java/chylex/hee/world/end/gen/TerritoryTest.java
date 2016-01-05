package chylex.hee.world.end.gen;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.world.end.EndTerritory;
import chylex.hee.world.end.TerritoryGenerator;
import chylex.hee.world.end.TerritoryProperties;
import chylex.hee.world.end.tick.ITerritoryBehavior;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.util.BoundingBox;

public class TerritoryTest extends TerritoryGenerator{
	public TerritoryTest(EndTerritory territory, EnumSet variations, StructureWorld world, Random rand){
		super(territory,variations,world,rand);
	}

	@Override
	public void generate(){
		BoundingBox box = world.getArea();
		
		for(int x = box.x1; x < box.x2; x++){
			for(int z = box.z1; z < box.z2; z++){
				world.setBlock(x,1,z,Blocks.end_stone);
			}
		}
	}
	
	public static enum Variations{
		VAR1, VAR2, VAR3
	}
	
	public static class Properties extends TerritoryProperties<Variations>{
		public Properties(){
			super(Variations.class);
			
			addCommonVariation(Variations.VAR1,10);
			addCommonVariation(Variations.VAR2,5);
			
			addRareVariation(Variations.VAR1,10);
			addRareVariation(Variations.VAR2,5);
			addRareVariation(Variations.VAR3,5);
		}
		
		@Override
		public void setupBehaviorList(List<ITerritoryBehavior> list, EnumSet<Variations> variations, boolean isRare){
			
		}
	}
}
