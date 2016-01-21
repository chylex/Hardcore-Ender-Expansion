package chylex.hee.item;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.game.achievements.AchievementManager;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.charms.CharmPouchInfo;
import chylex.hee.mechanics.charms.handler.CharmPouchHandler;
import chylex.hee.mechanics.charms.handler.CharmPouchHandlerClient;
import chylex.hee.system.abstractions.nbt.NBT;
import chylex.hee.system.abstractions.nbt.NBTCompound;
import chylex.hee.system.abstractions.nbt.NBTList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCharmPouch extends Item{
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		if (NBT.item(is,false).getBool("isPouchActive") && entity instanceof EntityPlayer){
			if (world.isRemote)CharmPouchHandlerClient.onActivePouchUpdate((EntityPlayer)entity,is);
			else{
				CharmPouchInfo pouchInfo = CharmPouchHandler.getActivePouch((EntityPlayer)entity);
				
				if (pouchInfo == null){
					CharmPouchHandler.setActivePouch((EntityPlayer)entity,is);
					CharmPouchHandler.getActivePouch((EntityPlayer)entity).update(world);
				}
				else if (pouchInfo.pouchID != getPouchID(is))is.getTagCompound().setBoolean("isPouchActive",false);
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
			NBT.item(is,true).setBool("isPouchActive",!deactivate);
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
	public void onCreated(ItemStack is, World world, EntityPlayer player){
		player.addStat(AchievementManager.CHARM_POUCH,1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		textLines.add(I18n.format(NBT.item(is,false).getBool("isPouchActive") ? "item.charmPouch.info.active" : "item.charmPouch.info.inactive"));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack is, int pass){
		return NBT.item(is,false).getBool("isPouchActive");
	}
	
	public static final long getPouchID(ItemStack is){
		if (is.getItem() != ItemList.charm_pouch)return 0;
		
		NBTCompound tag = NBT.item(is,true);
		
		long id = tag.getLong("pouchID");
		if (id == 0)tag.setLong("pouchID",id = itemRand.nextLong());
		return id;
	}
	
	public static final ItemStack[] getPouchCharms(ItemStack is){
		if (is.getItem() != ItemList.charm_pouch)return new ItemStack[0];
		
		NBTList tagCharms = NBT.item(is,true).getList("pouchCharms");
		
		ItemStack[] items = new ItemStack[tagCharms.size()];
		
		for(int a = 0; a < tagCharms.size(); a++){
			NBTCompound tag = tagCharms.getCompound(a);
			items[a] = tag.getBool("null") ? null : ItemStack.loadItemStackFromNBT(tag.getUnderlyingTag());
		}
		
		return items;
	}
	
	public static final void setPouchCharms(ItemStack pouch, ItemStack[] charms){
		if (pouch.getItem() != ItemList.charm_pouch)return;
		
		NBTList tagCharms = new NBTList();
		
		for(ItemStack charm:charms){
			if (charm == null){
				NBTCompound tag = new NBTCompound();
				tag.setBool("null",true);
				tagCharms.appendCompound(tag);
			}
			else tagCharms.appendTag(charm.writeToNBT(new NBTTagCompound()));
		}
		
		NBT.item(pouch,true).setList("pouchCharms",tagCharms);
	}
}
