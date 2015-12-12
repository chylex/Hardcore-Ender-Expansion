package chylex.hee.world.end;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.world.structure.StructureWorld;

public class TerritorySpawnGenerator{
	protected final ISpawnPointFinder pointFinder;
	protected final int attempts;
	
	TerritorySpawnGenerator(ISpawnPointFinder pointFinder){
		this.pointFinder = pointFinder;
		this.attempts = 10000;
	}
	
	TerritorySpawnGenerator(ISpawnPointFinder pointFinder, int attempts){
		this.pointFinder = pointFinder;
		this.attempts = attempts;
	}
	
	public Pos createSpawnPoint(StructureWorld world, Random rand, EndTerritory territory){
		PosMutable pos = new PosMutable();
		
		for(int attempt = 0; attempt < attempts; attempt++){
			pointFinder.findSpawnPoint(world,rand,territory,pos);
			
			if (canSpawnPortalAt(world,rand,pos)){
				generatePortalAt(world,rand,pos);
				return pos;
			}
		}
		
		generatePortalAt(world,rand,pos);
		return pos;
	}
	
	boolean canSpawnPortalAt(StructureWorld world, Random rand, Pos pos){
		return Pos.allBlocksMatch(pos.offset(-2,0,-2),pos.offset(2,1,2),testPos -> world.getBlock(testPos) == Blocks.end_stone);
	}
	
	void generatePortalAt(StructureWorld world, Random rand, Pos pos){
		Pos.forEachBlock(pos.offset(-1,0,-1),pos.offset(1,0,1),testPos -> world.setBlock(testPos,BlockList.void_portal,Meta.voidPortalReturn));
	}
	
	@FunctionalInterface
	public static interface ISpawnPointFinder{
		void findSpawnPoint(StructureWorld world, Random rand, EndTerritory territory, PosMutable pos);
	}
	
	public static final class Empty extends TerritorySpawnGenerator{
		Empty(){
			super((world, rand, territory, pos) -> pos.set(0,world.getTopY(0,0),0),1);
		}
		
		Empty(ISpawnPointFinder pointFinder, int attempts){
			super(pointFinder,attempts);
		}
		
		@Override
		boolean canSpawnPortalAt(StructureWorld world, Random rand, Pos pos){
			return true;
		}
		
		@Override
		void generatePortalAt(StructureWorld world, Random rand, Pos pos){}
	}
}
