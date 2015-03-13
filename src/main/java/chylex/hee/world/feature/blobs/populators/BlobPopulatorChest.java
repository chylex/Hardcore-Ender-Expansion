package chylex.hee.world.feature.blobs.populators;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import chylex.hee.world.feature.blobs.BlobPopulator;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;
import chylex.hee.world.loot.WeightedLootList;
import chylex.hee.world.structure.util.pregen.ITileEntityGenerator;
import chylex.hee.world.util.BlockLocation;
import chylex.hee.world.util.IRandomAmount;

public class BlobPopulatorChest extends BlobPopulator implements ITileEntityGenerator{
	private WeightedLootList lootList;
	private IRandomAmount amountGen = IRandomAmount.exact;
	private int minAmount, maxAmount;
	private boolean onlyInside;
	
	public BlobPopulatorChest(int weight){
		super(weight);
	}
	
	public BlobPopulatorChest loot(WeightedLootList lootList, IRandomAmount amountGen, int minAmount, int maxAmount){
		this.lootList = lootList;
		this.amountGen = amountGen;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		return this;
	}
	
	/**
	 * Makes the chest only spawn in locations covered with End Stone
	 */
	public BlobPopulatorChest onlyInside(){
		this.onlyInside = true;
		return this;
	}

	@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		List<BlockLocation> locs = gen.getUsedLocations();
		
		while(!locs.isEmpty()){
			BlockLocation loc = locs.remove(rand.nextInt(locs.size()));
			
			if (gen.getBlock(loc.x,loc.y,loc.z) == Blocks.end_stone && gen.getBlock(loc.x,loc.y+1,loc.z) == Blocks.air && gen.getBlock(loc.x,loc.y+2,loc.z) == Blocks.air){
				if (onlyInside && gen.getTopBlockY(loc.x,loc.z) < loc.y+2)continue;
				
				gen.setBlock(loc.x,loc.y+1,loc.z,Blocks.chest,rand.nextInt(4));
				gen.setTileEntity(loc.x,loc.y+1,loc.z,this);
				break;
			}
		}
	}
	
	@Override
	public void onTileEntityRequested(String key, TileEntity tile, Random rand){
		TileEntityChest chest = (TileEntityChest)tile;
		
		for(int a = 0, amount = amountGen.generate(rand,minAmount,maxAmount); a < amount; a++){
			chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()),lootList.generateIS(rand));
		}
	}
}
