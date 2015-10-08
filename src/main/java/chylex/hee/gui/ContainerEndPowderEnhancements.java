package chylex.hee.gui;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import chylex.hee.gui.slots.SlotEnhancementsSubject;
import chylex.hee.gui.slots.SlotShowCase;
import chylex.hee.mechanics.enhancements.EnhancementData;
import chylex.hee.mechanics.enhancements.EnhancementIngredient;
import chylex.hee.mechanics.enhancements.EnhancementRegistry;
import chylex.hee.mechanics.enhancements.IEnhanceableTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerEndPowderEnhancements extends Container{
	private EntityPlayer owner;
	
	public final IInventory containerInv;
	public final IEnhanceableTile enhanceableTile;
	
	public ContainerEndPowderEnhancements(InventoryPlayer inv, IEnhanceableTile tileOptional){
		containerInv = new InventoryBasic("",false,1);
		this.enhanceableTile = tileOptional;
		
		if (isEnhancingTile()){
			addSlotToContainer(new SlotShowCase(containerInv,0,80,8));
			containerInv.setInventorySlotContents(0,IEnhanceableTile.createItemStack(tileOptional));
		}
		else{
			addSlotToContainer(new SlotEnhancementsSubject(containerInv,0,80,8));
		}
		
		for(int i = 0; i < 3; ++i){
			for(int j = 0; j < 9; ++j)addSlotToContainer(new Slot(inv,j+i*9+9,8+j*18,112+i*18));
		}

		for(int i = 0; i < 9; ++i)addSlotToContainer(new Slot(inv,i,8+i*18,170));
		
		owner = inv.player;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player){
		return true;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId){
		Slot slot = (Slot)inventorySlots.get(slotId);

		if (slot != null && slot.getHasStack()){
			ItemStack is2 = slot.getStack();
			
			if (slotId < 1){
				if (!mergeItemStack(is2,1,inventorySlots.size(),true))return null;
			}
			else if (EnhancementRegistry.canEnhanceItem(is2.getItem()) && !isEnhancingTile()){
				if (!mergeItemStack(is2,0,1,false))return null;
			}
			else return null;

			if (is2.stackSize == 0)slot.putStack(null);
			else slot.onSlotChanged();
		}
		
		return null;
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player){
		super.onContainerClosed(player);
		if (!isEnhancingTile() && containerInv.getStackInSlot(0) != null)player.dropPlayerItemWithRandomChoice(containerInv.getStackInSlot(0),false);
	}
	
	@Override
	public void putStackInSlot(int slot, ItemStack is){
		super.putStackInSlot(slot,is);
		
		if (slot == 0 && isEnhancingTile()){
			// TODO onSubjectChanged();
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void putStacksInSlots(ItemStack[] items){
		super.putStacksInSlots(items);
		// TODO if (isEnhancingTile())onSubjectChanged();
	}

	public void onEnhancementSlotClick(int slot){/* TODO
		
		else if (selectedEnhancement != null){ // TRY ENHANCE
			ItemStack mainIS = getSlot(0).getStack();
			List<IEnhancementEnum> enhancements = EnhancementHandler.getEnhancementsForItem(mainIS.getItem());
			
			if (slot < 0 || slot >= enhancements.size()){
				Log.reportedError("Received S01 enhancement gui packet with invalid slot - $0",slot);
				return;
			}
			
			IEnhancementEnum enhancement = enhancements.get(slot);
			if (EnhancementHandler.getEnhancements(mainIS).contains(enhancement))return;
			
			SlotList slots = EnhancementHandler.getEnhancementSlotsForItem(mainIS.getItem());
			int enhancedAmount = mainIS.stackSize;
			ItemStack is;
			
			for(int a = 0; a < slots.amountIngredient; a++){
				if ((is = ingredientSlots[a].getStack()) == null || !enhancement.getItemSelector().isValid(is)){
					enhancedAmount = 0;
					break;
				}
				else if (is.stackSize < enhancedAmount)enhancedAmount = is.stackSize;
			}
			
			for(int a = 0; a < slots.amountPowder; a++){
				if ((is = powderSlots[a].getStack()) == null){
					enhancedAmount = 0;
					break;
				}
				else if (is.stackSize < enhancedAmount)enhancedAmount = is.stackSize;
			}
			
			if (enhancedAmount > 0){ // PERFORM ENHANCEMENT
				ItemStack newIS = EnhancementHandler.addEnhancement(mainIS,enhancement);
				newIS.stackSize = enhancedAmount;
				
				ItemStack currentOutput = getSlot(1).getStack();
				if (!(currentOutput == null || (currentOutput.getItem() == newIS.getItem() && currentOutput.getItemDamage() == newIS.getItemDamage() && ItemStack.areItemStackTagsEqual(currentOutput,newIS))))return;
				if (currentOutput != null && currentOutput.stackSize+newIS.stackSize > newIS.getMaxStackSize())newIS.stackSize = enhancedAmount = newIS.getMaxStackSize()-currentOutput.stackSize;
				if (currentOutput != null)newIS.stackSize += currentOutput.stackSize;
				if (mainIS.stackSize-enhancedAmount == 0 && mainIS.stackSize > 1)newIS.stackSize = --enhancedAmount;
				
				if (owner != null){
					enhancement.onEnhanced(newIS,owner);
					
					if (isEnhancingTile()){
						enhanceableTile.getEnhancements().clear();
						enhanceableTile.getEnhancements().addAll(EnhancementHandler.getEnhancements(newIS));
					}
				}
				
				containerInv.setInventorySlotContents(isEnhancingTile() ? 0 : 1,newIS);
				if (!isEnhancingTile() && (mainIS.stackSize -= enhancedAmount) <= 0)containerInv.setInventorySlotContents(0,null);

				for(int a = 0; a < slots.amountPowder; a++){
					if ((powderSlots[a].getStack().stackSize -= enhancedAmount) <= 0)containerInv.setInventorySlotContents(2+a,null);
				}
				
				for(int a = 0; a < slots.amountIngredient; a++){
					if ((ingredientSlots[a].getStack().stackSize -= enhancedAmount) <= 0)containerInv.setInventorySlotContents(2+powderSlots.length+a,null);
				}

				if (isEnhancingTile() || getSlot(0).getStack() == null)onSubjectChanged();
				
				if (CompendiumEvents.getPlayerData(owner).tryUnlockFragment(KnowledgeFragmentEnhancement.getEnhancementFragment(selectedEnhancement))){
					PacketPipeline.sendToPlayer(owner,new C19CompendiumData(owner));
				}
				
				PacketPipeline.sendToPlayer(owner,new C08PlaySound(C08PlaySound.EXP_ORB,owner.posX,owner.posY,owner.posZ,1F,1F));
			}
		}*/
	}
	
	public boolean isEnhancingTile(){
		return enhanceableTile != null;
	}
	
	public Collection<EnhancementIngredient> getMissingIngredients(final EnhancementData<?>.EnhancementInfo info, final int level){
		if (owner == null)return new HashSet<>();
		
		TObjectIntHashMap<EnhancementIngredient> left = new TObjectIntHashMap<>(4);
		info.getIngredients(level).forEach(ingredient -> left.put(ingredient,ingredient.getAmount(level)));
		
		ItemStack[] inventory = Arrays.stream(owner.inventory.mainInventory).filter(is -> is != null).map(is -> is.copy()).toArray(ItemStack[]::new);
		
		for(ItemStack is:inventory){
			for(TObjectIntIterator<EnhancementIngredient> iter = left.iterator(); iter.hasNext();){
				iter.advance();
				
				if (iter.key().selector.isValid(is)){
					int newValue = Math.max(0,iter.value()-is.stackSize);
					is.stackSize = Math.max(0,is.stackSize-iter.value());
					
					if (newValue == 0)iter.remove();
					else iter.setValue(newValue);
				}
			}
		}
		
		return left.keySet();
	}
}
