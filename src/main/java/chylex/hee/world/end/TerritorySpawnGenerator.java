package chylex.hee.world.end;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.StructureWorld;

public class TerritorySpawnGenerator{
	private static final int defaultAttempts = 10000;
	
	protected final ISpawnPointFinder pointFinder;
	protected final int attempts;
	
	TerritorySpawnGenerator(ISpawnPointFinder pointFinder){
		this.pointFinder = pointFinder;
		this.attempts = defaultAttempts;
	}
	
	TerritorySpawnGenerator(ISpawnPointFinder pointFinder, int attempts){
		this.pointFinder = pointFinder;
		this.attempts = attempts;
	}
	
	public Pos createSpawnPoint(StructureWorld world, Random rand, EndTerritory territory, boolean isRare){
		PosMutable pos = new PosMutable();
		if (attempts == 0)pointFinder.findSpawnPoint(world,rand,territory,pos);
		
		for(int attempt = 0; attempt < attempts; attempt++){
			pointFinder.findSpawnPoint(world,rand,territory,pos);
			
			if (canSpawnPortalAt(world,rand,pos)){
				generatePortalAt(world,rand,pos,isRare);
				return pos;
			}
		}
		
		if (pos.y <= 0)pos.y = 1;
		generatePortalAt(world,rand,pos,isRare);
		return pos;
	}
	
	boolean canSpawnPortalAt(StructureWorld world, Random rand, Pos pos){ // TODO profile
		return Pos.allBlocksMatch(pos.offset(-2,-1,-2),pos.offset(2,0,2),testPos -> world.getBlock(testPos) == Blocks.end_stone) &&
			   Pos.allBlocksMatch(pos.offset(-1,1,-1),pos.offset(1,1,1),testPos -> world.isAir(testPos));
	}
	
	void generatePortalAt(StructureWorld world, Random rand, Pos pos, boolean isRare){
		for(int offX = -2; offX <= 2; offX++){
			for(int offZ = -2; offZ <= 2; offZ++){
				if (Math.abs(offX) <= 1 && Math.abs(offZ) <= 1)world.setAttentionWhore(pos.getX()+offX,pos.getY(),pos.getZ()+offZ,new BlockInfo(BlockList.void_portal,isRare ? Meta.voidPortalDisabled : Meta.voidPortalReturn));
				else if (MathUtil.distance(offX,offZ) <= 2.32D)world.setAttentionWhore(pos.getX()+offX,pos.getY(),pos.getZ()+offZ,new BlockInfo(BlockList.void_portal_frame,Meta.voidPortalFramePlain));
			}
		}
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
		void generatePortalAt(StructureWorld world, Random rand, Pos pos, boolean isRare){}
	}
	
	public static final class Origin extends TerritorySpawnGenerator{
		Origin(){
			super((world, rand, territory, pos) -> pos.set(0,world.getTopY(0,0),0),1);
		}
	}
	
	public static final class InSquare extends TerritorySpawnGenerator{
		public InSquare(final double distanceMultiplier){
			this(distanceMultiplier,defaultAttempts);
		}
		
		public InSquare(final double distanceMultiplier, int attempts){
			super((world, rand, territory, pos) -> {
				int maxDist = MathUtil.floor(territory.createBoundingBox().x2*distanceMultiplier);
				
				pos.set(rand.nextInt(maxDist*2+1)-maxDist,0,rand.nextInt(maxDist*2+1)-maxDist);
				pos.y = world.getTopY(pos.getX(),pos.getZ());
			},attempts);
		}
	}
	
	public static final class InSquareCenter extends TerritorySpawnGenerator{
		public InSquareCenter(final double distanceMultiplier){
			this(distanceMultiplier,defaultAttempts);
		}
		
		public InSquareCenter(final double distanceMultiplier, int attempts){
			super((world, rand, territory, pos) -> {
				int maxDist = MathUtil.floor(territory.createBoundingBox().x2*distanceMultiplier);
				
				pos.set(rand.nextInt(maxDist*2+1)-maxDist,0,rand.nextInt(maxDist*2+1)-maxDist);
				pos.y = world.getTopY(pos.getX(),pos.getZ());
				
				if (pos.y >= 0){
					int[] distancesX = new int[2], distancesZ = new int[2];
					Block targetBlock = world.getBlock(pos);
					
					for(int test = 1; test < 10; test++){
						if (world.getBlock(pos.offset(Facing4.EAST_POSX,test)) != targetBlock)distancesX[0] = test-1;
					}
					
					for(int test = 1; test < 10; test++){
						if (world.getBlock(pos.offset(Facing4.WEST_NEGX,test)) != targetBlock)distancesX[1] = test-1;
					}
					
					for(int test = 1; test < 10; test++){
						if (world.getBlock(pos.offset(Facing4.SOUTH_POSZ,test)) != targetBlock)distancesZ[0] = test-1;
					}
					
					for(int test = 1; test < 10; test++){
						if (world.getBlock(pos.offset(Facing4.NORTH_NEGZ,test)) != targetBlock)distancesZ[1] = test-1;
					}
					
					if (distancesX[0] != distancesX[1])pos.x += (distancesX[1]-distancesX[0])/2;
					if (distancesZ[0] != distancesZ[1])pos.z += (distancesZ[1]-distancesZ[0])/2;
				}
			},attempts);
		}
		
	}
}
