package chylex.hee.world.feature.blobs.populators;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.feature.blobs.StructureWorldBlob;
import chylex.hee.world.util.IRangeGenerator;
import chylex.hee.world.util.IRangeGenerator.RangeGenerator;
import chylex.hee.world.util.RandomAmount;

public class BlobPopulatorLiquidPool extends BlobPopulator{
	private Block block = Blocks.air;
	private IRangeGenerator amount;
	private int minBlocks;
	
	public BlobPopulatorLiquidPool(int weight){
		super(weight);
	}
	
	public BlobPopulatorLiquidPool setBlock(Block block){
		this.block = block;
		return this;
	}
	
	public BlobPopulatorLiquidPool setAmount(int min, int max){
		this.amount = new RangeGenerator(min,max,RandomAmount.linear);
		return this;
	}
	
	public BlobPopulatorLiquidPool setAmount(IRangeGenerator amount){
		this.amount = amount;
		return this;
	}
	
	public BlobPopulatorLiquidPool setMinBlocks(int minBlocks){
		this.minBlocks = minBlocks;
		return this;
	}

	@Override
	public void populate(StructureWorldBlob world, Random rand){
		int poolsLeft = (amount == null ? 0 : amount.next(rand));
		if (poolsLeft <= 0)return;
		
		List<Pos> endStone = world.getEndStoneBlocks();
		int attempts = endStone.size()/3;
		
		while(--attempts >= 0 && poolsLeft > 0){
			Pos pos = endStone.remove(rand.nextInt(endStone.size()));
			
			if (canBeChanged(world,pos)){
				Set<Pos> fillBlocks = new HashSet<>();
				fillRecursive(world,pos,fillBlocks);
				
				if (fillBlocks.size() >= minBlocks){
					for(Pos toFill:fillBlocks){
						world.setBlock(toFill,block);
					}
					
					--poolsLeft;
				}
			}
		}
	}
	
	private void fillRecursive(StructureWorldBlob world, Pos pos, Set<Pos> fill){
		if (!canBeChanged(world,pos) || !fill.add(pos))return;
		
		for(Facing4 facing:Facing4.list){
			fillRecursive(world,pos.offset(facing),fill);
		}
	}
	
	private boolean canBeChanged(StructureWorldBlob world, Pos pos){
		if (world.getBlock(pos) != Blocks.end_stone)return false;
		if (world.getBlock(pos.getDown()) != Blocks.end_stone)return false;
		if (!world.isAir(pos.getUp()))return false;
		
		for(Facing4 facing:Facing4.list){
			if (world.getBlock(pos.offset(facing)) != Blocks.end_stone)return false;
		}
		
		return true;
	}
}
