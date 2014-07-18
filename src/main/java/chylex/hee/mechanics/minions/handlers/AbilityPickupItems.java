package chylex.hee.mechanics.minions.handlers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ReportedException;
import net.minecraftforge.common.util.Constants;
import chylex.hee.entity.mob.EntityMobMinion;
import chylex.hee.mechanics.minions.properties.MinionAttributes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AbilityPickupItems extends AbstractAbilityEntityTargeter{
	private InventoryMinion inventory;
	public AbilityPickupItems(EntityMobMinion minion){
		super(minion,EntityItem.class,5);
		inventory = new InventoryMinion(8+4*minionData.getAttributeLevel(MinionAttributes.CAPACITY));
	}

	@Override
	protected void onEntityCollision(Entity e){
		EntityItem item = (EntityItem)e;
		if (item.delayBeforeCanPickup > 0)return;
		
		minion.playSound("random.pop",0.2F,((rand.nextFloat()-rand.nextFloat())*0.7F+1.0F)*2.0F);
		if (inventory.addItemStackToInventory(item.getEntityItem()))item.setDead();
	}

	@Override
	protected boolean canTargetEntity(Entity e){
		if (inventory.getFirstEmptySlot() >= 0)return true;
		
		ItemStack is = ((EntityItem)e).getEntityItem();
		for(int a = 0; a < inventory.getSizeInventory(); a++){
			ItemStack slotIS = inventory.getStackInSlot(a);
			
			if (slotIS != null && inventory.canStackItemStacks(slotIS,is))return true;
		}
		
		return false;
	}
	
	@Override
	public void onDeath(){
		for(int a = 0; a < inventory.getSizeInventory(); a++){
			ItemStack is = inventory.getStackInSlot(a);
			if (is != null)minion.entityDropItem(is,0F);
		}
	}

	@Override
	public void writeDataToNBT(NBTTagCompound nbt){
		NBTTagList tagItems = new NBTTagList();
		
		for(int a = 0; a < inventory.getSizeInventory(); a++){
			ItemStack is = inventory.getStackInSlot(a);
			if (is == null)continue;
			
			NBTTagCompound tagSlot = is.writeToNBT(new NBTTagCompound());
			tagSlot.setByte("slot",(byte)a);
			tagItems.appendTag(tagSlot);
		}
		
		nbt.setTag("items",tagItems);
	}

	@Override
	public void readDataFromNBT(NBTTagCompound nbt){
		NBTTagList tagItems = nbt.getTagList("items",Constants.NBT.TAG_COMPOUND);
		
		for(int a = 0; a < tagItems.tagCount(); a++){
			NBTTagCompound tagSlot = tagItems.getCompoundTagAt(a);
			inventory.setInventorySlotContents(tagSlot.getByte("slot"),ItemStack.loadItemStackFromNBT(tagSlot));
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void render(){
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		int col = (255<<24)|(255<<16)|(255<<8)|255,y = 20;
		
		for(int a = 0; a < inventory.getSizeInventory(); a++){
			ItemStack is = inventory.getStackInSlot(a);
			if (is == null)continue;

			font.drawString(is.toString(),1,y,col);
			y += 16;
		}
	}
	
	class InventoryMinion extends InventoryBasic{
		public InventoryMinion(int slotAmount){
			super("MinionInventory",false,slotAmount);
		}

		public boolean addItemStackToInventory(ItemStack is){
			if (is == null || is.stackSize == 0)return false;
			
			try{
				if (is.isItemDamaged()){
					int emptySlot = getFirstEmptySlot();

					if (emptySlot >= 0){
						setInventorySlotContents(emptySlot,ItemStack.copyItemStack(is));
						is.stackSize = 0;
						return true;
					}
					else return false;
				}
				else{
					int size;
					do{
						size = is.stackSize;
						is.stackSize = storePartialItemStack(is);
					}while(is.stackSize > 0 && is.stackSize < size);

					return is.stackSize < size;
				}
			}catch(Throwable throwable){
				CrashReport crashReport = CrashReport.makeCrashReport(throwable,"Adding item to minion inventory");
				CrashReportCategory crashCategory = crashReport.makeCategory("Item being added");
				crashCategory.addCrashSection("Item ID",Integer.valueOf(Item.getIdFromItem(is.getItem())));
				crashCategory.addCrashSection("Item data",Integer.valueOf(is.getItemDamage()));
				crashCategory.addCrashSection("Item name",is.getDisplayName());
				throw new ReportedException(crashReport);
			}
		}
		
		private int getFirstEmptySlot(){
			for(int a = 0; a < getSizeInventory(); a++){
				if (getStackInSlot(a) == null)return a;
			}
			return -1;
		}
		
		private int storePartialItemStack(ItemStack is){
			int slot;

			if (is.getMaxStackSize() == 1){
				if ((slot = getFirstEmptySlot()) < 0)return is.stackSize;
				if (getStackInSlot(slot) == null)setInventorySlotContents(slot,ItemStack.copyItemStack(is));

				return 0;
			}
			else{
				if ((slot = storeItemStack(is)) < 0)slot = getFirstEmptySlot();
				if (slot < 0)return is.stackSize;
				
				ItemStack slotIS = getStackInSlot(slot);

				if (slotIS == null){
					setInventorySlotContents(slot,slotIS = new ItemStack(is.getItem(),0,is.getItemDamage()));
					if (is.hasTagCompound())slotIS.setTagCompound((NBTTagCompound)is.getTagCompound().copy());
				}

				int amountToStore = Math.min(is.stackSize,slotIS.getMaxStackSize()-slotIS.stackSize);
				if ((amountToStore = Math.min(amountToStore,getInventoryStackLimit()-slotIS.stackSize)) == 0)return is.stackSize;

				slotIS.stackSize += amountToStore;
				return is.stackSize-amountToStore;
			}
		}
		
		private int storeItemStack(ItemStack toStore){
			for(int a = 0; a < getSizeInventory(); a++){
				ItemStack is = getStackInSlot(a);
				
				if (is != null && canStackItemStacks(is,toStore))return a;
			}

			return -1;
		}
		
		public boolean canStackItemStacks(ItemStack is1, ItemStack is2){
			return is1.getItem() == is2.getItem() && is1.isStackable()&&
				   is1.stackSize < is1.getMaxStackSize() && is1.stackSize < getInventoryStackLimit()&&
				   (!is1.getHasSubtypes() || is1.getItemDamage() == is2.getItemDamage())&&
				   ItemStack.areItemStackTagsEqual(is1,is2);
		}
	}
}
