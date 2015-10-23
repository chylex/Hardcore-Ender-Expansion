package chylex.hee.mechanics.compendium_old.util;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import chylex.hee.init.ItemList;
import chylex.hee.item.ItemSpawnEggs;
import chylex.hee.mechanics.charms.CharmRecipe;
import chylex.hee.mechanics.charms.CharmType;
import chylex.hee.mechanics.compendium_old.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium_old.content.KnowledgeObject;
import chylex.hee.mechanics.compendium_old.content.fragments.KnowledgeFragmentCharm;
import chylex.hee.mechanics.compendium_old.content.fragments.KnowledgeFragmentCrafting;
import chylex.hee.mechanics.compendium_old.events.CompendiumEvents;
import chylex.hee.mechanics.compendium_old.objects.IKnowledgeObjectInstance;
import chylex.hee.mechanics.compendium_old.objects.ObjectItem;
import chylex.hee.mechanics.compendium_old.objects.ObjectMob;
import chylex.hee.mechanics.curse.CurseType;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class KnowledgeUtils{
	public static KnowledgeObject<? extends IKnowledgeObjectInstance<?>> tryGetFromItemStack(ItemStack is){
		UniqueIdentifier uniqueId = null;
		
		try{
			uniqueId = GameRegistry.findUniqueIdentifierFor(is.getItem());
		}
		catch(Exception e){} // protection against idiots who can't register their shit properly
		
		if (uniqueId != null && uniqueId.modId.equalsIgnoreCase("hardcoreenderexpansion")){
			if (is.getItem() == ItemList.spawn_eggs){
				Class<? extends EntityLiving> entity = ItemSpawnEggs.getMobFromDamage(is.getItemDamage());
				if (entity == null)entity = (Class<? extends EntityLiving>)EntityList.IDtoClassMapping.get(is.getItemDamage());
				return entity == null ? null : KnowledgeObject.<ObjectMob>getObject(entity);
			}
			else if (is.getItem() instanceof ItemBlock)return CompendiumEvents.getBlockObject(is);
			else return KnowledgeObject.<ObjectItem>getObject(is.getItem());
		}
		
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public static List<String> getCompendiumTooltip(ItemStack is, EntityPlayer player){
		List<String> tooltip = is.getTooltip(player,false);
		if (KnowledgeUtils.tryGetFromItemStack(is) != null)tooltip.add(EnumChatFormatting.DARK_PURPLE+I18n.format("compendium.viewObject"));
		return tooltip;
	}
	
	public static KnowledgeFragment[] createCharmFragments(int startID, int requiredFragment){
		List<KnowledgeFragment> fragments = new ArrayList<>();
		int a = 0;
		
		for(CharmType type:CharmType.values()){
			for(CharmRecipe recipe:type.recipes){
				fragments.add(new KnowledgeFragmentCharm(startID+(a++)).setRecipe(recipe).setPrice(2).setUnlockRequirements(requiredFragment));
			}
		}
		
		return fragments.toArray(new KnowledgeFragment[fragments.size()]);
	}
	
	public static KnowledgeFragment[] createCurseFragments(int startID, int requiredFragment){
		List<KnowledgeFragment> fragments = new ArrayList<>();
		int a = 0;
		
		for(CurseType type:CurseType.values()){
			fragments.add(new KnowledgeFragmentCrafting(startID+(a++)).setRecipeFromRegistry(new ItemStack(ItemList.curse,8,type.damage)).setPrice(3).setUnlockRequirements(requiredFragment));
		}
		
		return fragments.toArray(new KnowledgeFragment[fragments.size()]);
	}
	
	private KnowledgeUtils(){}
}
