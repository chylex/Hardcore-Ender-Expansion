package chylex.hee.world.feature.blobs.populators;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.facing.Facing6;
import chylex.hee.world.feature.blobs.StructureWorldBlob;

public class BlobPopulatorScatter extends BlobPopulator{
	private Block block = Blocks.air;
	private IRandGenerator chance;
	private boolean visibleOnly;
	
	public BlobPopulatorScatter(int weight){
		super(weight);
	}
	
	public BlobPopulatorScatter setBlock(Block block){
		this.block = block;
		return this;
	}
	
	public BlobPopulatorScatter setChance(double chance){
		this.chance = rand -> chance;
		return this;
	}
	
	public BlobPopulatorScatter setChance(IRandGenerator chanceGenerator){
		this.chance = chanceGenerator;
		return this;
	}
	
	public BlobPopulatorScatter setVisibleOnly(){
		this.visibleOnly = true;
		return this;
	}
	
	@Override
	public void populate(StructureWorldBlob world, Random rand){
		double genChance = (chance == null ? 0.05D : chance.generate(rand));
		
		for(Pos pos:world.getEndStoneBlocks()){
			if (rand.nextDouble() < genChance && (!visibleOnly || isVisible(world,pos))){
				world.setBlock(pos,block);
			}
		}
	}
	
	private boolean isVisible(StructureWorldBlob world, Pos pos){
		for(Facing6 facing:Facing6.list){
			if (world.isAir(pos.offset(facing)))return true;
		}
		
		return false;
	}
}
