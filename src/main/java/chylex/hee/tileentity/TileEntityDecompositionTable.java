package chylex.hee.tileentity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.item.ItemStack;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.misc.StardustDecomposition;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.ItemDamagePair;

public class TileEntityDecompositionTable extends TileEntityAbstractTable{
	private static final int[] slotsTop = new int[]{ 0 }, slotsSides = new int[]{ 1 }, slotsBottom = new int[]{ 2, 3, 4, 5, 6, 7, 8, 9, 10 };

	private List<ItemStack> chosenIngredients = new ArrayList<>();
	
	@Override
	public void invalidateInventory(){
		if (worldObj != null && worldObj.isRemote)return;
		chosenIngredients.clear();
		resetTable();
		
		if (items[0] != null && worldObj != null){
			List<ItemStack> recipeIngredients = StardustDecomposition.getRandomRecipeIngredientsFor(items[0],worldObj.rand);
			
			if (recipeIngredients != null){
				int originalAmt = recipeIngredients.size();
				Set<ItemDamagePair> diversity = new HashSet<>();
				for(ItemStack ingredient:recipeIngredients)diversity.add(new ItemDamagePair(ingredient.getItem(),ingredient.getItemDamage()));
				
				int amt = Math.max(1,Math.round(recipeIngredients.size()*(0.425F+worldObj.rand.nextFloat()*0.415F)*(items[0].isItemStackDamageable() ? 1F-(float)items[0].getItemDamage()/items[0].getMaxDamage() : 1F)));
				
				ItemStack ingredient;
				for(int a = 0; a < amt; a++){
					(ingredient = recipeIngredients.remove(worldObj.rand.nextInt(recipeIngredients.size())).copy()).stackSize = 1;
					chosenIngredients.add(ingredient);
				}
				
				float stardust = originalAmt * 0.6F;
				if (items[0].stackSize == 1)stardust *= 1.2F;
				stardust *= 0.75F + (diversity.size() * 0.8F / originalAmt);
				
				requiredStardust = (byte)Math.ceil(stardust);
				timeStep = (short)Math.max(2,20-Math.pow(requiredStardust,0.9D));
				updateComparatorStatus();
			}
		}
	}
	
	@Override
	protected boolean onWorkFinished(){
		ItemStack[] itemsCopy = new ItemStack[9];
		for(int slot = 2; slot < items.length; slot++)itemsCopy[slot-2] = items[slot] == null ? null : items[slot].copy();
		
		for(ItemStack ingredient:chosenIngredients){
			boolean canAdd = false;
			
			for(int slot = 0; slot < itemsCopy.length; slot++){
				if (itemsCopy[slot] == null)itemsCopy[slot] = ingredient.copy();
				else if (DragonUtil.canAddOneItemTo(itemsCopy[slot],ingredient))++itemsCopy[slot].stackSize;
				else continue;
				
				canAdd = true;
				break;
			}
			
			if (!canAdd)return false;
		}
		
		for(ItemStack ingredient:chosenIngredients){
			for(int slot = 2; slot < items.length; slot++){
				if (items[slot] == null)items[slot] = ingredient.copy();
				else if (DragonUtil.canAddOneItemTo(items[slot],ingredient))++items[slot].stackSize;
				else continue;
				break;
			}
		}
		
		items[0] = null;
		if ((items[1].stackSize -= requiredStardust) <= 0)items[1] = null;

		chosenIngredients.clear();
		return true;
	}
	
	@Override
	public int getHoldingStardust(){
		return items[1] == null ? 0 : items[1].stackSize;
	}

	@Override
	public int getSizeInventory(){
		return 11;
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack is){
		super.setInventorySlotContents(slot,is);
		if (slot == 0)invalidateInventory();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is){
		return slot == 1 ? is.getItem() == ItemList.stardust : true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side){
		return side == 0 ? slotsBottom : side == 1 ? slotsTop : slotsSides;
	}

	@Override
	protected String getContainerDefaultName(){
		return "container.decompositionTable";
	}
}
