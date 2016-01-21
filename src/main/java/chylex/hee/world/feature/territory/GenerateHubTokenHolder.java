package chylex.hee.world.feature.territory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.entity.block.EntityBlockTokenHolder;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.end.EndTerritory;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.util.IRangeGenerator.RangeGenerator;

public final class GenerateHubTokenHolder implements ITerritoryFeature{
	private RangeGenerator amount;
	private int attempts;
	private double minDistance;
	
	private List<Pos> tempGenerated = new ArrayList<>(4);
	
	public void setAmount(RangeGenerator amount){
		this.amount = amount;
	}
	
	public void setAttempts(int attempts){
		this.attempts = attempts;
	}
	
	public void setMinDistance(double distance){
		this.minDistance = distance;
	}
	
	@Override
	public boolean generate(EndTerritory territory, StructureWorld world, Random rand){
		int left = (amount == null ? 1 : amount.next(rand));
		boolean generatedAny = false;
		
		for(int attempt = 0; attempt < attempts && left > 0; attempt++){
			if (generateTokenHolder(territory,world,rand,false)){
				--left;
				generatedAny = true;
			}
		}
		
		if (!generatedAny){
			for(int attempt = 0; attempt < attempts && left > 0; attempt++){
				if (generateTokenHolder(territory,world,rand,true))--left;
			}
		}

		tempGenerated.clear();
		return left == 0;
	}
	
	private boolean generateTokenHolder(EndTerritory territory, StructureWorld world, Random rand, boolean ignorePosChecks){
		final double angle = rand.nextDouble()*Math.PI*2D;
		final double dist = 30D+rand.nextDouble()*40D;
		
		final int x = MathUtil.floor(Math.cos(angle)*dist);
		final int z = MathUtil.floor(Math.sin(angle)*dist);
		final int y = world.getTopY(x,z);
		
		if (y <= 0)return false;
		
		Facing4 entranceFacing = getEntranceFacing(MathUtil.toDeg(angle),rand);
		Pos entrancePos = Pos.at(x,y,z).offset(entranceFacing,3);
		PosMutable entrancePosMutable = new PosMutable(entrancePos);
		
		// too close check
		for(Pos pos:tempGenerated){
			if (pos.distance(x,y,z) < minDistance)return false;
		}
		
		// position check
		if (!ignorePosChecks){
			for(int offX = -5; offX <= 5; offX++){
				for(int offZ = -5; offZ <= 5; offZ++){
					if (MathUtil.distance(offX,offZ) <= 4.5D && !(offX == 0 && offZ == 0)){
						if (world.getTopY(x+offX,z+offZ) != y)return false;
					}
				}
			}
			
			Pos entranceTestPos = entrancePosMutable.offset(entranceFacing,2);
			
			for(int offX = -1; offX <= 1; offX++){
				for(int offZ = -1; offZ <= 1; offZ++){
					if (world.getTopY(x+offX,z+offZ) != y)return false;
				}
			}
		}
		
		// layout
		for(int offX = -3; offX <= 3; offX++){
			for(int offZ = -3; offZ <= 3; offZ++){
				if (MathUtil.distance(offX,offZ) <= 2.5D){
					world.setBlock(x+offX,y,z+offZ,BlockList.dark_loam);
					world.setBlock(x+offX,y+4,z+offZ,BlockList.ravish_brick);
				}
				else if (MathUtil.distance(offX,offZ) <= 3.5D){
					world.setBlock(x+offX,y+1,z+offZ,BlockList.ravish_brick);
					world.setBlock(x+offX,y+2,z+offZ,BlockList.ravish_brick);
					world.setBlock(x+offX,y+3,z+offZ,BlockList.ravish_brick);
				}
			}
		}
		
		// entrance and path
		for(int offY = 0; offY < 3; offY++){
			entrancePosMutable.moveUp();
			world.setAir(entrancePosMutable);
			world.setAir(entrancePosMutable.offset(entranceFacing.rotateLeft()));
			world.setAir(entrancePosMutable.offset(entranceFacing.rotateRight()));
		}
		
		generatePath(world,rand,entrancePos);
		
		// token holder
		EntityBlockTokenHolder tokenHolder = new EntityBlockTokenHolder(world.getParentWorld());
		tokenHolder.setPosition(x+0.5D,y+1D,z+0.5D);
		tokenHolder.setTerritory(EndTerritory.FORGOTTEN_TOMBS);
		world.addEntity(tokenHolder);
		
		tempGenerated.add(Pos.at(x,y,z));
		return true;
	}
	
	private Facing4 getEntranceFacing(double angleDeg, Random rand){
		List<Facing4> availableFacings = new ArrayList<>(2);
		
		if (angleDeg >= 180 && angleDeg < 360)availableFacings.add(Facing4.SOUTH_POSZ);
		else availableFacings.add(Facing4.NORTH_NEGZ);
		
		if (angleDeg >= 270 || angleDeg < 90)availableFacings.add(Facing4.WEST_NEGX);
		else availableFacings.add(Facing4.EAST_POSX);
		
		return CollectionUtil.randomOrNull(availableFacings,rand);
	}
	
	private void generatePath(StructureWorld world, Random rand, Pos entrancePos){
		final Vec currentPos = Vec.xz(entrancePos.getX()+0.5D,entrancePos.getZ()+0.5D);
		final int length = MathUtil.floor(currentPos.length()), halfLength = length/2;
		final Vec line = currentPos.multiplied(-1D).normalized();
		double chance = 0.85D;
		
		final Vec pathOffset = Vec.xz(-line.z,line.x).multiplied((rand.nextBoolean() ? -1 : 1)*(0.25D+rand.nextDouble()*0.75D)*8D);
		double pathOffsetMp = 0D;
		
		for(int iter = 0; iter < length; iter++){
			for(int offX = -1; offX <= 1; offX++){
				for(int offZ = -1; offZ <= 1; offZ++){
					if (rand.nextDouble() > chance)continue;
					
					placePathBlock(world,MathUtil.floor(currentPos.x+offX+pathOffset.x*pathOffsetMp),MathUtil.floor(currentPos.z+offZ+pathOffset.z*pathOffsetMp));
				}
			}
			
			currentPos.moveBy(line);
			chance = Math.max(0.15D,chance*0.92D);
			pathOffsetMp = Math.pow((iter < halfLength ? iter : length-iter)/(double)halfLength,1.75D);
		}
	}
	
	private void placePathBlock(StructureWorld world, int x, int z){
		int y = world.getTopY(x,z,90);
		
		if (world.getBlock(x,y,z) == Blocks.end_stone){
			world.setBlock(x,y,z,BlockList.dark_loam);
		}
	}
}
