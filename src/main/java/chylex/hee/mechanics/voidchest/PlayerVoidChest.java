package chylex.hee.mechanics.voidchest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants.NBT;
import chylex.hee.mechanics.misc.PlayerDataHandler;
import chylex.hee.mechanics.misc.PlayerDataHandler.IExtendedPropertyInitializer;

public class PlayerVoidChest implements IExtendedEntityProperties{
	private static final String playerPropertyIdentifier = "HardcoreEnderExpansion~VoidChest";
	
	static void register(){
		PlayerDataHandler.registerProperty(playerPropertyIdentifier,new IExtendedPropertyInitializer<PlayerVoidChest>(){
			@Override
			public PlayerVoidChest createNew(Entity entity){
				return new PlayerVoidChest();
			}
		});
	}
	
	public static InventoryVoidChest getInventory(EntityPlayer player){
		return ((PlayerVoidChest)player.getExtendedProperties(playerPropertyIdentifier)).inventory;
	}
	
	public static PlayerVoidChest getData(EntityPlayer player){
		return ((PlayerVoidChest)player.getExtendedProperties(playerPropertyIdentifier));
	}
	
	private final InventoryVoidChest inventory = new InventoryVoidChest();
	private boolean seenNotification;
	
	public boolean hasSeenNotification(){
		return seenNotification;
	}
	
	public void setSeenNotification(){
		seenNotification = true;
	}
	
	@Override
	public void init(Entity entity, World world){}

	@Override
	public void saveNBTData(NBTTagCompound nbt){
		NBTTagList tagItems = new NBTTagList();
		ItemStack is;
		
		for(int a = 0; a < inventory.getSizeInventory(); a++){
			if ((is = inventory.getStackInSlot(a)) == null)continue;
			NBTTagCompound tag = is.writeToNBT(new NBTTagCompound());
			tag.setByte("slot",(byte)a);
			tagItems.appendTag(tag);
		}
		
		nbt.setTag("HEE_VC_items",tagItems);
		nbt.setBoolean("HEE_VC_notif",seenNotification);
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt){
		NBTTagList tagItems = nbt.hasKey("items") ? nbt.getTagList("items",NBT.TAG_COMPOUND) : nbt.getTagList("HEE_VC_items",NBT.TAG_COMPOUND);
		
		for(int a = 0, count = tagItems.tagCount(); a < count; a++){
			NBTTagCompound tag = tagItems.getCompoundTagAt(a);
			inventory.setInventorySlotContents(tag.getByte("slot"),ItemStack.loadItemStackFromNBT(tag));
		}
		
		seenNotification = nbt.getBoolean("HEE_VC_notif");
	}
}
