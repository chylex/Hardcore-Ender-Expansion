package chylex.hee.gui;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.gui.slots.SlotCharmPouchItem;
import chylex.hee.gui.slots.SlotCharmPouchRune;
import chylex.hee.gui.slots.SlotCharmPouchRuneResult;
import chylex.hee.item.ItemCharmPouch;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.charms.CharmPouchInfo;
import chylex.hee.mechanics.charms.CharmRecipe;
import chylex.hee.mechanics.charms.CharmType;
import chylex.hee.mechanics.charms.RuneType;
import chylex.hee.mechanics.charms.handler.CharmPouchHandler;

public class ContainerCharmPouch extends Container{
	private final EntityPlayer player;
	private final IInventory charmInv = new InventoryBasic("container.charmPouch",false,3);
	private final IInventory runeInv = new InventoryBasic("container.runeCrafting",false,5);
	private final IInventory runeResultInv = new InventoryBasic("container.runeCrafting",false,1);
	private final long pouchID;
	
	public ContainerCharmPouch(EntityPlayer player){
		this.player = player;
		this.pouchID = ItemCharmPouch.getPouchID(player.getHeldItem());
		
		for(int a = 0; a < 3; a++)addSlotToContainer(new SlotCharmPouchItem(charmInv,this,a,39,20+a*20));
		
		ItemStack[] charms = ItemCharmPouch.getPouchCharms(player.getHeldItem());
		for(int a = 0; a < Math.min(charmInv.getSizeInventory(),charms.length); a++)charmInv.setInventorySlotContents(a,charms[a]);
		
		addSlotToContainer(new SlotCharmPouchRune(runeInv,this,0,122,18,ItemList.rune,16));
		addSlotToContainer(new SlotCharmPouchRune(runeInv,this,1,98,38,ItemList.rune,16));
		addSlotToContainer(new SlotCharmPouchRune(runeInv,this,2,146,38,ItemList.rune,16));
		addSlotToContainer(new SlotCharmPouchRune(runeInv,this,3,109,63,ItemList.rune,16));
		addSlotToContainer(new SlotCharmPouchRune(runeInv,this,4,135,63,ItemList.rune,16));
		
		addSlotToContainer(new SlotCharmPouchRuneResult(runeResultInv,runeInv,this,0,122,41));
		
		for(int a = 0; a < 3; a++){
			for(int b = 0; b < 9; ++b)addSlotToContainer(new Slot(player.inventory,b+a*9+9,8+b*18,99+a*18));
		}

		for(int a = 0; a < 9; a++)addSlotToContainer(new Slot(player.inventory,a,8+a*18,157));
	}
	
	private boolean isHoldingPouch(){
		ItemStack is = player.getHeldItem();
		return is != null && is.getItem() == ItemList.charm_pouch;
	}
	
	public void saveCharmPouch(){
		if (!player.worldObj.isRemote && isHoldingPouch())ItemCharmPouch.setPouchCharms(player.getHeldItem(),new ItemStack[]{ charmInv.getStackInSlot(0), charmInv.getStackInSlot(1), charmInv.getStackInSlot(2) });
	}
	
	@Override
	public void detectAndSendChanges(){
		super.detectAndSendChanges();
		if (!player.worldObj.isRemote && !isHoldingPouch())player.closeScreen();
	}
	
	@Override
	public void onCraftMatrixChanged(IInventory inventory){
		if (inventory == runeInv){
			runeResultInv.setInventorySlotContents(0,null);
			
			List<RuneType> runes = new ArrayList<RuneType>(5);
			
			for(int a = 0; a < 5; a++){
				ItemStack rune = runeInv.getStackInSlot(a);
				
				if (rune != null){
					int damage = rune.getItemDamage();
					if (damage >= 0 && damage < RuneType.values.length)runes.add(RuneType.values[damage]);
				}
			}
			
			if (runes.size() >= 3){
				Pair<CharmType,CharmRecipe> charm = CharmType.findRecipe(runes.toArray(new RuneType[runes.size()]));
				if (charm.getRight() != null)runeResultInv.setInventorySlotContents(0,new ItemStack(ItemList.charm,1,charm.getRight().id));
			}
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer player){
		super.onContainerClosed(player);

		for(int a = 0; a < 5; a++){
			ItemStack is = runeInv.getStackInSlot(a);
			if (is != null)player.dropPlayerItemWithRandomChoice(is,false);
		}

		runeResultInv.setInventorySlotContents(0,null);
		
		CharmPouchInfo activePouch = CharmPouchHandler.getActivePouch(player);
		if (activePouch != null && activePouch.pouchID == pouchID)CharmPouchHandler.setActivePouch(player,player.getHeldItem());
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId){
		ItemStack is = null;
		Slot slot = (Slot)inventorySlots.get(slotId);

		if (slot != null && slot.getHasStack()){
			ItemStack is2 = slot.getStack();
			is = is2.copy();

			if (slotId < 9){
				if (!mergeItemStack(is2,9,inventorySlots.size(),true))return null;
			}
			else{
				if (is2.getItem() == ItemList.charm){
					if (!mergeItemStack(is2,0,3,false))return null;
				}
				else if (is2.getItem() == ItemList.rune){
					if (!mergeItemStack(is2,3,8,false))return null;
				}
				else return null;
			}

			if (is2.stackSize == 0)slot.putStack(null);
			else slot.onSlotChanged();
		}

		return is;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player){
		return true;
	}
}
