package chylex.hee.tileentity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.item.ItemStack;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.misc.StardustDecomposition;
import chylex.hee.system.util.ItemDamagePair;
import chylex.hee.system.util.ItemUtil;

public class TileEntityDecompositionTable extends TileEntityAbstractTable{
	public static final int slotStardust = 0, slotSubject = 1;
	
	private static final int[] slotsTop = new int[]{ slotSubject },
	                           slotsSides = new int[]{ slotStardust },
	                           slotsBottom = new int[]{ 2, 3, 4, 5, 6, 7, 8, 9, 10 };

	private List<ItemStack> chosenIngredients = new ArrayList<>();
	
	@Override
	public void invalidateInventory(){
		if (worldObj != null && worldObj.isRemote)return;
		chosenIngredients.clear();
		resetTable();
		
		if (items[slotSubject] != null && worldObj != null){
			List<ItemStack> recipeIngredients = StardustDecomposition.getRandomRecipeIngredientsFor(items[slotSubject], worldObj.rand);
			
			if (recipeIngredients != null){
				int originalAmt = recipeIngredients.size();
				Set<ItemDamagePair> diversity = new HashSet<>();
				for(ItemStack ingredient:recipeIngredients)diversity.add(new ItemDamagePair(ingredient.getItem(), ingredient.getItemDamage()));
				
				float damageMp = items[slotSubject].isItemStackDamageable() ? 1F-(float)items[slotSubject].getItemDamage()/items[slotSubject].getMaxDamage() : 1F;
				int amt = Math.max(1, Math.round(recipeIngredients.size()*(0.425F+worldObj.rand.nextFloat()*0.415F)*damageMp));
				
				ItemStack ingredient;
				for(int a = 0; a < amt; a++){
					(ingredient = recipeIngredients.remove(worldObj.rand.nextInt(recipeIngredients.size())).copy()).stackSize = 1;
					chosenIngredients.add(ingredient);
				}
				
				float stardust = originalAmt*0.6F;
				if (items[slotSubject].stackSize == 1)stardust *= 1.2F;
				stardust *= 0.75F+(diversity.size()*0.8F/originalAmt);
				
				requiredStardust = (byte)Math.ceil(stardust);
				timeStep = (short)Math.max(2, 20-Math.pow(requiredStardust, 0.9D));
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
				else if (ItemUtil.canAddOneItemTo(itemsCopy[slot], ingredient))++itemsCopy[slot].stackSize;
				else continue;
				
				canAdd = true;
				break;
			}
			
			if (!canAdd)return false;
		}
		
		for(ItemStack ingredient:chosenIngredients){
			for(int slot = 2; slot < items.length; slot++){
				if (items[slot] == null)items[slot] = ingredient.copy();
				else if (ItemUtil.canAddOneItemTo(items[slot], ingredient))++items[slot].stackSize;
				else continue;
				break;
			}
		}
		
		items[slotSubject] = null;
		if ((items[slotStardust].stackSize -= requiredStardust) <= 0)items[slotStardust] = null;

		chosenIngredients.clear();
		return true;
	}
	
	@Override
	public int getHoldingStardust(){
		return items[slotStardust] == null ? 0 : items[slotStardust].stackSize;
	}

	@Override
	public int getSizeInventory(){
		return 11;
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack is){
		super.setInventorySlotContents(slot, is);
		if (slot == slotSubject)invalidateInventory();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is){
		return slot == slotStardust ? is.getItem() == ItemList.stardust : true;
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
