package chylex.hee.tileentity;
import gnu.trove.map.hash.TObjectByteHashMap;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;
import chylex.hee.item.ItemList;
import chylex.hee.system.util.ItemDamagePair;
import chylex.hee.system.util.MathUtil;

public class TileEntityExperienceTable extends TileEntityAbstractTable{
	private static final int[] slotsTop = new int[]{ 0 }, slotsSides = new int[]{ 1 }, slotsBottom = new int[]{ 2 };
	
	private static final TObjectByteHashMap<ItemDamagePair> direct = new TObjectByteHashMap<>();
	
	static{
		addDirectConversion(ItemList.end_powder,1);
		addDirectConversion(Items.redstone,1);
		addDirectConversion(Items.dye,4,1);
		addDirectConversion(Items.iron_ingot,1);
		addDirectConversion(ItemList.igneous_rock,2);
		addDirectConversion(Items.gold_ingot,2);
		addDirectConversion(Items.quartz,2);
		addDirectConversion(Items.emerald,3);
		addDirectConversion(Items.diamond,3);
		addDirectConversion(ItemList.endium_ingot,4);
		addDirectConversion(Item.getItemFromBlock(Blocks.quartz_block),8);
		addDirectConversion(Item.getItemFromBlock(Blocks.iron_block),9);
		addDirectConversion(Item.getItemFromBlock(Blocks.redstone_block),9);
		addDirectConversion(Item.getItemFromBlock(Blocks.lapis_block),9);
		addDirectConversion(Item.getItemFromBlock(Blocks.gold_block),18);
		addDirectConversion(Item.getItemFromBlock(Blocks.emerald_block),27);
		addDirectConversion(Item.getItemFromBlock(Blocks.diamond_block),27);
		addDirectConversion(Item.getItemFromBlock(BlockList.endium_block),36);
	}
	
	private static void addDirectConversion(Item item, int bottleAmount){
		direct.put(new ItemDamagePair(item,-1),(byte)bottleAmount);
	}
	
	private static void addDirectConversion(Item item, int damage, int bottleAmount){
		direct.put(new ItemDamagePair(item,damage),(byte)bottleAmount);
	}
	
	public static boolean addDirectConversion(ItemDamagePair pair, byte bottleAmount){
		if (getDirectExperience(new ItemStack(pair.item,pair.damage == -1 ? 0 : pair.damage)) > 0)return false;
		direct.put(pair,bottleAmount);
		return true;
	}
	
	private static byte getDirectExperience(ItemStack is){
		for(ItemDamagePair idp:direct.keySet()){
			if (idp.check(is))return direct.get(idp);
		}
		
		return 0;
	}
	
	private static boolean canConvertItem(ItemStack is, World world){
		if (is.getItem() instanceof ItemBlock){
			Block block = Block.getBlockFromItem(is.getItem());
			if (block != null && block.getExpDrop(world,is.getItemDamage(),0) > 0)return true;
		}
		
		return getDirectExperience(is) > 0;
	}
	
	private static byte getExperience(ItemStack is, World world){
		if (is.getItem() instanceof ItemBlock){
			Block block = Block.getBlockFromItem(is.getItem());
			
			if (block != null){
				byte exp = (byte)MathUtil.ceil(0.6D*block.getExpDrop(world,is.getItemDamage(),0));
				if (exp > 0)return exp;
			}
		}
		
		return getDirectExperience(is);
	}
	
	/**
	 * 0 = inactive, -1 = active but undecided (because of randomness in exp drop)
	 */
	private byte expAmount;
	
	@Override
	public void invalidateInventory(){
		if (worldObj != null && worldObj.isRemote)return;
		expAmount = 0;
		
		if (items[0] != null){
			if (canConvertItem(items[0],worldObj)){
				expAmount = -1;
				timeStep = 12;
				requiredStardust = 4;
				updateComparatorStatus();
			}
		}
		else resetTable();
	}

	@Override
	protected boolean onWorkFinished(){
		if (expAmount == -1)expAmount = (byte)Math.min(64,getExperience(items[0],worldObj));
		
		if (items[2] == null)items[2] = new ItemStack(ItemList.exp_bottle,expAmount);
		else if (items[2].stackSize+expAmount <= items[2].getMaxStackSize())items[2].stackSize += expAmount;
		else return false;
		
		if ((items[1].stackSize -= requiredStardust) <= 0)items[1] = null;
		
		if (--items[0].stackSize <= 0){
			items[0] = null;
			expAmount = 0;
		}
		
		return true;
	}
	
	@Override
	public int getHoldingStardust(){
		return items[1] == null ? 0 : items[1].stackSize;
	}
	
	@Override
	public int getSizeInventory(){
		return 3;
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack is){
		super.setInventorySlotContents(slot,is);
		if (slot == 0)invalidateInventory();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is){
		return slot == 1 ? is.getItem() == ItemList.stardust : slot == 0;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side){
		return side == 0 ? slotsBottom : side == 1 ? slotsTop : slotsSides;
	}

	@Override
	protected String getContainerDefaultName(){
		return "container.experienceTable";
	}
}
