package chylex.hee.item;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.data.KnowledgeCategory;
import chylex.hee.mechanics.knowledge.data.KnowledgeRegistration;
import chylex.hee.system.achievements.AchievementManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEnderCompendium extends Item{
	public ItemEnderCompendium(){
		setHasSubtypes(true);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		player.openGui(HardcoreEnderExpansion.instance,1,world,0,0,0);
		KnowledgeRegistrations.KNOWLEDGE_FRAGMENT.tryUnlockFragment(player,1F);
		if (!world.isRemote && is.stackTagCompound != null)is.stackTagCompound.removeTag("knowledgeLast");
		return is;
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		if (!world.isRemote && is.stackTagCompound != null && is.stackTagCompound.hasKey("knowledgeLast")){
			NBTTagCompound tag = is.stackTagCompound.getCompoundTag("knowledgeLast");
			
			if (tag.hasKey("time")){
				short s = tag.getShort("time");
				if (--s <= 0)is.stackTagCompound.removeTag("knowledgeLast");
				else tag.setShort("time",s);
			}
		}
	}
	
	@Override
	public void onCreated(ItemStack is, World world, EntityPlayer player){
		player.addStat(AchievementManager.THE_MORE_YOU_KNOW,1);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		if (is.getItemDamage() == 1)textLines.add("Creative mode");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list){
		list.add(new ItemStack(item,1,0));
		
		ItemStack fullCompendium = new ItemStack(item,1,1);
		for(KnowledgeCategory category:KnowledgeCategory.categories){
			for(KnowledgeRegistration registration:category.registrations){
				registration.fragmentSet.unlockAllFragments(fullCompendium);
			}
		}
		list.add(fullCompendium);
	}
}
