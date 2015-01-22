package chylex.hee.item;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.mechanics.charms.CharmPouchInfo;
import chylex.hee.mechanics.charms.handler.CharmPouchHandler;
import chylex.hee.mechanics.charms.handler.CharmPouchHandlerClient;
import chylex.hee.system.util.ItemUtil;

public class ItemCharmPouch extends Item{	
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		if (ItemUtil.getNBT(is,false).getBoolean("isPouchActive") && entity instanceof EntityPlayer){
			if (world.isRemote)CharmPouchHandlerClient.onActivePouchUpdate((EntityPlayer)entity,is);
			else{
				CharmPouchInfo pouchInfo = CharmPouchHandler.getActivePouch((EntityPlayer)entity);
				
				if (pouchInfo == null){
					CharmPouchHandler.setActivePouch((EntityPlayer)entity,is);
					CharmPouchHandler.getActivePouch((EntityPlayer)entity).update(world);
				}
				else if (pouchInfo.pouchID != getPouchID(is))ItemUtil.getNBT(is,false).setBoolean("isPouchActive",false);
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
			ItemUtil.getNBT(is,true).setBoolean("isPouchActive",!deactivate);
			if (!deactivate)CharmPouchHandler.getActivePouch(player).update(world);
		}
		else player.openGui(HardcoreEnderExpansion.instance,5,world,0,0,0);
		
		return is;
	}
	
	@Override
	public boolean onEntityItemUpdate(EntityItem entity){
		ItemStack is = entity.getEntityItem();
		if (is == null || !is.hasTagCompound())return false;
		
		if (is.getTagCompound().getBoolean("isPouchActive"))is.getTagCompound().setBoolean("isPouchActive",false);
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		textLines.add(ItemUtil.getNBT(is,false).getBoolean("isPouchActive") ? "Active" : "Inactive");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack is){
		return ItemUtil.getNBT(is,false).getBoolean("isPouchActive");
	}
	
	public static final long getPouchID(ItemStack is){
		if (is.getItem() != ItemList.charm_pouch)return 0;
		
		NBTTagCompound nbt = ItemUtil.getNBT(is,true);
		
		long id = nbt.getLong("pouchID");
		if (id == 0)nbt.setLong("pouchID",id = itemRand.nextLong());
		return id;
	}
	
	public static final ItemStack[] getPouchCharms(ItemStack is){
		if (is.getItem() != ItemList.charm_pouch)return new ItemStack[0];
		
		NBTTagCompound nbt = ItemUtil.getNBT(is,true);
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
		
		NBTTagCompound nbt = ItemUtil.getNBT(pouch,true);
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
