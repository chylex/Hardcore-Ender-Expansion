package chylex.hee.game.save.types.player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import chylex.hee.game.save.types.PlayerFile;
import chylex.hee.system.util.NBTUtil;

public class RespawnFile extends PlayerFile{
	private InventoryBasic inventory = new InventoryBasic("",false,36);
	
	public RespawnFile(String filename){
		super("respawn",filename);
	}
	
	public void setInventoryItem(int slot, ItemStack is){
		inventory.setInventorySlotContents(slot,is);
		setModified();
	}
	
	public ItemStack getInventoryItem(int slot){
		return inventory.getStackInSlot(slot);
	}
	
	public void loadInventory(EntityPlayer player){
		for(int slot = 0; slot < inventory.getSizeInventory(); slot++){
			ItemStack is = inventory.getStackInSlot(slot);
			
			if (is != null){
				if (player.inventory.mainInventory[slot] == null)player.inventory.mainInventory[slot] = is;
				else player.func_146097_a(is,true,false);
				
				inventory.setInventorySlotContents(slot,null);
			}
		}
	}

	@Override
	protected void onSave(NBTTagCompound nbt){
		nbt.setTag("inv",NBTUtil.writeInventory(inventory));
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		NBTUtil.readInventory(nbt.getTagList("inv",NBT.TAG_COMPOUND),inventory);
	}
}
