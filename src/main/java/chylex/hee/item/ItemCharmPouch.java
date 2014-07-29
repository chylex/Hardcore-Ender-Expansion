package chylex.hee.item;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import chylex.hee.mechanics.charms.CharmPouchHandler;
import chylex.hee.mechanics.charms.CharmPouchInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCharmPouch extends Item{	
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		if (!world.isRemote && is.stackTagCompound != null && is.stackTagCompound.getBoolean("isPouchActive") && entity instanceof EntityPlayer){
			CharmPouchInfo pouchInfo = CharmPouchHandler.getActivePouch((EntityPlayer)entity);
			if (pouchInfo == null || pouchInfo.pouchID != getPouchID(is))is.stackTagCompound.removeTag("isPouchActive");
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		CharmPouchHandler.setActivePouch(player,is);
		(is.stackTagCompound == null ? is.stackTagCompound = new NBTTagCompound() : is.stackTagCompound).setBoolean("isPouchActive",true);
		return is;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack is, int pass){
		return is.stackTagCompound != null && is.stackTagCompound.getBoolean("isPouchActive");
	}
	
	public static final long getPouchID(ItemStack is){
		if (is.getItem() != ItemList.charm_pouch)return 0;
		
		NBTTagCompound nbt = is.stackTagCompound != null ? is.stackTagCompound : (is.stackTagCompound = new NBTTagCompound());
		
		long id = nbt.getLong("pouchID");
		if (id == 0)nbt.setLong("pouchID",id = itemRand.nextLong());
		return id;
	}
	
	public static final ItemStack[] getPouchCharms(ItemStack is){
		if (is.getItem() != ItemList.charm_pouch)return new ItemStack[0];
		
		NBTTagCompound nbt = is.stackTagCompound != null ? is.stackTagCompound : (is.stackTagCompound = new NBTTagCompound());
		NBTTagList tagCharms = nbt.getTagList("pouchCharms",Constants.NBT.TAG_COMPOUND);
		
		ItemStack[] items = new ItemStack[tagCharms.tagCount()];
		for(int a = 0; a < tagCharms.tagCount(); a++)items[a] = ItemStack.loadItemStackFromNBT(tagCharms.getCompoundTagAt(a));
		return items;
	}
	
	public static final void setPouchCharms(ItemStack pouch, ItemStack[] charms){
		if (pouch.getItem() != ItemList.charm_pouch)return;
		
		NBTTagCompound nbt = pouch.stackTagCompound != null ? pouch.stackTagCompound : (pouch.stackTagCompound = new NBTTagCompound());
		
		NBTTagList tagCharms = new NBTTagList();
		for(ItemStack charm:charms)tagCharms.appendTag(charm.writeToNBT(new NBTTagCompound()));
		
		nbt.setTag("pouchCharms",tagCharms);
	}
}
