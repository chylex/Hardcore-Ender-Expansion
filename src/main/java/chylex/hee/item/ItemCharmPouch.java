package chylex.hee.item;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.mechanics.charms.CharmPouchInfo;
import chylex.hee.mechanics.charms.handler.CharmPouchHandler;
import chylex.hee.mechanics.charms.handler.CharmPouchHandlerClient;
import chylex.hee.system.achievements.AchievementManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCharmPouch extends Item{	
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		if (is.stackTagCompound != null && is.stackTagCompound.getBoolean("isPouchActive") && entity instanceof EntityPlayer){
			if (world.isRemote)CharmPouchHandlerClient.onActivePouchUpdate((EntityPlayer)entity,is);
			else{
				CharmPouchInfo pouchInfo = CharmPouchHandler.getActivePouch((EntityPlayer)entity);
				
				if (pouchInfo == null){
					CharmPouchHandler.setActivePouch((EntityPlayer)entity,is);
					CharmPouchHandler.getActivePouch((EntityPlayer)entity).update(world);
				}
				else if (pouchInfo.pouchID != getPouchID(is))is.stackTagCompound.setBoolean("isPouchActive",false);
				else pouchInfo.update(world);
			}
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		if (world.isRemote)return is;
		
		if (player.isSneaking()){
			CharmPouchInfo activePouch = CharmPouchHandler.getActivePouch(player);
			boolean deactivate = activePouch != null && activePouch.pouchID == getPouchID(is);
			
			CharmPouchHandler.setActivePouch(player,deactivate ? null : is);
			(is.stackTagCompound == null ? is.stackTagCompound = new NBTTagCompound() : is.stackTagCompound).setBoolean("isPouchActive",!deactivate);
			if (!deactivate)CharmPouchHandler.getActivePouch(player).update(world);
		}
		else player.openGui(HardcoreEnderExpansion.instance,5,world,0,0,0);
		
		return is;
	}
	
	@Override
	public boolean onEntityItemUpdate(EntityItem entity){
		ItemStack is = entity.getEntityItem();
		if (is == null || is.stackTagCompound == null)return false;
		
		if (is.stackTagCompound.getBoolean("isPouchActive"))is.stackTagCompound.setBoolean("isPouchActive",false);
		return false;
	}
	
	@Override
	public void onCreated(ItemStack is, World world, EntityPlayer player){
		player.addStat(AchievementManager.CHARM_POUCH,1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		textLines.add(I18n.format(is.stackTagCompound != null && is.stackTagCompound.getBoolean("isPouchActive") ? "item.charmPouch.info.active" : "item.charmPouch.info.inactive"));
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
		
		for(int a = 0; a < tagCharms.tagCount(); a++){
			NBTTagCompound tag = tagCharms.getCompoundTagAt(a);
			items[a] = tag.getBoolean("null") ? null : ItemStack.loadItemStackFromNBT(tag);
		}
		
		return items;
	}
	
	public static final void setPouchCharms(ItemStack pouch, ItemStack[] charms){
		if (pouch.getItem() != ItemList.charm_pouch)return;
		
		NBTTagCompound nbt = pouch.stackTagCompound != null ? pouch.stackTagCompound : (pouch.stackTagCompound = new NBTTagCompound());
		NBTTagList tagCharms = new NBTTagList();
		
		for(ItemStack charm:charms){
			if (charm == null){
				NBTTagCompound tag = new NBTTagCompound();
				tag.setBoolean("null",true);
				tagCharms.appendTag(tag);
			}
			else tagCharms.appendTag(charm.writeToNBT(new NBTTagCompound()));
		}
		
		nbt.setTag("pouchCharms",tagCharms);
	}
}
