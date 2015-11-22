package chylex.hee.world.feature.blobs.populators;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.feature.blobs.StructureWorldBlob;
import chylex.hee.world.util.RandomAmount;
import chylex.hee.world.util.RangeGenerator;

public class BlobPopulatorLiquidStream extends BlobPopulator{
	private Block block = Blocks.air;
	private RangeGenerator amount;
	private boolean canHaveAirAbove;
	
	public BlobPopulatorLiquidStream(int weight){
		super(weight);
	}
	
	public BlobPopulatorLiquidStream setBlock(Block block){
		this.block = block;
		return this;
	}
	
	public BlobPopulatorLiquidStream setAmount(int min, int max){
		this.amount = new RangeGenerator(min,max,RandomAmount.linear);
		return this;
	}
	
	public BlobPopulatorLiquidStream setAmount(RangeGenerator amount){
		this.amount = amount;
		return this;
	}
	
	public BlobPopulatorLiquidStream setCanHaveAirAbove(){
		this.canHaveAirAbove = true;
		return this;
	}

	@Override
	public void populate(StructureWorldBlob world, Random rand){
		int targetAmount = amount.next(rand);
		
		List<Pos> endStoneBlocks = world.getEndStoneBlocks();
		int attempts = endStoneBlocks.size()/2;
		
		for(int attempt = 0; attempt < attempts; attempt++){
			Pos pos = endStoneBlocks.remove(rand.nextInt(endStoneBlocks.size())); // size never gets to 0
			
			Facing4 facing = findStreamStart(world,pos);
			if (facing == null)continue;
			
			world.setBlock(pos.getX()+facing.getX(),pos.getY(),pos.getZ()+facing.getZ(),block);
			
			int y = pos.getY();
			while(--y > 0 && !isContained(world,pos.getX(),y,pos.getZ()))world.setAir(pos.getX(),y,pos.getZ());
		}
	}
	
	private @Nullable Facing4 findStreamStart(StructureWorldBlob world, Pos pos){
		if (!world.isAir(pos.getX(),pos.getY(),pos.getZ()))return null;
		
		Facing4 suitable = null;
		
		for(Facing4 facing:Facing4.list){
			Pos offset = pos.offset(facing);
			
			if (world.getBlock(offset.getX(),offset.getY(),offset.getZ()) == Blocks.end_stone &&
				(canHaveAirAbove || world.getBlock(offset.getX(),offset.getY()+1,offset.getZ()) == Blocks.end_stone)){
				if (suitable == null)suitable = facing;
				else return null; // only one side
			}
		}
		
		return suitable;
	}
	
	private boolean isContained(StructureWorldBlob world, int x, int y, int z){
		for(Facing4 facing:Facing4.list){
			if (world.getBlock(x+facing.getX(),y,z+facing.getZ()) != Blocks.end_stone)return false;
		}
		
		return true;
	}
}
