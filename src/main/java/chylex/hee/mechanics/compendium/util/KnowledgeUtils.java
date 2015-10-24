package chylex.hee.mechanics.compendium.util;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import chylex.hee.init.ItemList;
import chylex.hee.item.ItemSpawnEggs;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.content.objects.IObjectHolder;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.util.GameRegistryUtil;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class KnowledgeUtils{
	public static KnowledgeObject<? extends IObjectHolder<?>> tryGetFromItemStack(ItemStack is){
		UniqueIdentifier uniqueId = GameRegistryUtil.findIdentifier(is.getItem());
		
		if (uniqueId != null && uniqueId.modId.equalsIgnoreCase("hardcoreenderexpansion")){
			if (is.getItem() instanceof ItemBlock){
				return KnowledgeObject.fromObject(new BlockInfo(((ItemBlock)is.getItem()).field_150939_a,is.getItemDamage()));
			}
			else if (is.getItem() == ItemList.spawn_eggs){
				return Optional.ofNullable(ItemSpawnEggs.getMobFromDamage(is.getItemDamage()))
							   .map(cls -> KnowledgeObject.fromObject(cls)).orElse(null);
			}
			else return KnowledgeObject.fromObject(is);
		}
		else if (is.getItem() == Items.spawn_egg){
			return Optional.ofNullable((Class<? extends EntityLiving>)EntityList.IDtoClassMapping.get(is.getItemDamage()))
					   .map(cls -> KnowledgeObject.fromObject(cls)).orElse(null);
		}
		else return null;
	}
	
	@SideOnly(Side.CLIENT)
	public static List<String> getCompendiumTooltip(ItemStack is){
		List<String> tooltip = is.getTooltip(Minecraft.getMinecraft().thePlayer,false);
		if (KnowledgeUtils.tryGetFromItemStack(is) != null)tooltip.add(EnumChatFormatting.DARK_PURPLE+I18n.format("compendium.viewObject"));
		return tooltip;
	}

}
