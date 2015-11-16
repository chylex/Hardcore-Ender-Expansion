package chylex.hee.mechanics.compendium.util;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.content.objects.IObjectHolder;
import chylex.hee.mechanics.compendium.content.objects.ObjectBlock;
import chylex.hee.mechanics.compendium.content.objects.ObjectMob;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.abstractions.entity.EntitySelector;
import chylex.hee.system.util.GameRegistryUtil;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class KnowledgeUtils{
	@SideOnly(Side.CLIENT)
	public static List<String> getCompendiumTooltipClient(ItemStack is){
		List<String> tooltip = is.getTooltip(Minecraft.getMinecraft().thePlayer,false);
		if (KnowledgeObject.fromObject(is) != null)tooltip.add(EnumChatFormatting.DARK_PURPLE+I18n.format("compendium.viewObject"));
		return tooltip;
	}
	
	public static boolean isItemStackViable(ItemStack is){
		UniqueIdentifier uniqueId = GameRegistryUtil.findIdentifier(is.getItem());
		return uniqueId != null && (uniqueId.modId.equals("minecraft") || uniqueId.modId.equalsIgnoreCase("hardcoreenderexpansion"));
	}
	
	public static KnowledgeObject<? extends IObjectHolder<?>> getObservedObject(EntityPlayer player){
		World world = player.worldObj;
		Vec posVec = Vec.xyz(player.posX,(world.isRemote ? 1.5D : 0D)+player.boundingBox.minY+player.getEyeHeight()-(player.isSneaking() ? 0.08D : 0D),player.posZ);
		Vec lookVec = Vec.from(player.getLookVec());
		Vec interceptVec = posVec.offset(lookVec,10D);
		
		MovingObjectPosition mopBlock = world.rayTraceBlocks(posVec.toVec3(),interceptVec.toVec3(),true);
		double distBlock = mopBlock != null && mopBlock.typeOfHit == MovingObjectType.BLOCK ? MathUtil.distance(mopBlock.blockX+0.5D-posVec.x,mopBlock.blockY+0.5D-posVec.y,mopBlock.blockZ+0.5D-posVec.z) : Double.MAX_VALUE;
		
		List<Entity> list = EntitySelector.any(world,posVec.offset(lookVec,5D).toAABB().expand(6D,6D,6D));
		Entity tracedEntity = null;
		double distEntity = Double.MAX_VALUE;
		
		for(Entity entity:list){
			if (entity == player)continue;
			
			double size = entity.getCollisionBorderSize(), dist;
			MovingObjectPosition mop = entity.boundingBox.expand(size,size,size).calculateIntercept(posVec.toVec3(),interceptVec.toVec3());

			if (mop != null && (dist = posVec.distance(Vec.from(mop.hitVec))) < distEntity){
				distEntity = dist;
				tracedEntity = entity;
			}
		}
		
		if (distBlock < distEntity && mopBlock != null)return KnowledgeObject.<ObjectBlock>fromObject(Pos.at(mopBlock).getInfo(world));
		else if (tracedEntity != null){
			if (tracedEntity instanceof EntityLiving)return KnowledgeObject.<ObjectMob>fromObject(tracedEntity);
			else if (tracedEntity instanceof EntityItem)return KnowledgeObject.fromObject(((EntityItem)tracedEntity).getEntityItem());
		}
		
		return null;
	}
}
