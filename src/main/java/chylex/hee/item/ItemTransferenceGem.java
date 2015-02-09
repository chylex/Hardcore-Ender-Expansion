package chylex.hee.item;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.entity.fx.FXType;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.types.TransferenceGemEnhancements;
import chylex.hee.mechanics.gem.GemData;
import chylex.hee.mechanics.gem.GemSideEffects;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20Effect;
import chylex.hee.packets.client.C21EffectEntity;

public class ItemTransferenceGem extends ItemAbstractEnergyAcceptor{
	@Override
	public boolean canAcceptEnergy(ItemStack is){
		return is.getItemDamage() > 0;
	}

	@Override
	public void onEnergyAccepted(ItemStack is){
		is.setItemDamage(is.getItemDamage()-2);
	}
	
	@Override
	public int getEnergyPerUse(ItemStack is){
		return 2;
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ){
		if (super.onItemUse(is,player,world,pos,side,hitX,hitY,hitZ))return true;
		
		if (!world.isRemote && side == EnumFacing.UP && player.isSneaking()){
			GemData.updateItemStack(is,player.dimension,pos.getX(),pos.getY(),pos.getZ());
			PacketPipeline.sendToAllAround(player.dimension,pos,64D,new C20Effect(FXType.Basic.GEM_LINK,pos.getX(),pos.getY(),pos.getZ()));
			return true;
		}
		
		return false;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		return (world.isRemote || player.isSneaking()) ? is : tryTeleportEntity(is,player,player);
	}
	
	@Override
	public void onCreated(ItemStack is, World world, EntityPlayer player){
		player.addStat(AchievementManager.TRANSFERENCE_GEM,1);
	}
	
	public ItemStack tryTeleportEntity(ItemStack is, EntityPlayer player, Entity entity){
		if (entity.isRiding() || entity.riddenByEntity != null)return is;
		
		GemData gemData = GemData.loadFromItemStack(is);
		
		if (gemData.isLinked() && entity.dimension == gemData.getDimension()){		
			int itemDamage = is.getItemDamage();
			if (itemDamage >= is.getMaxDamage())return is;
			
			boolean isLiving = entity instanceof EntityLivingBase;
			
			PacketPipeline.sendToAllAround(entity,64D,new C21EffectEntity(FXType.Entity.GEM_TELEPORT_FROM,entity));
			
			is.damageItem(getEnergyPerUse(is),player);
			
			if (isLiving)((EntityLivingBase)entity).setPositionAndUpdate(gemData.getX()+0.5D,gemData.getY()+1.001D,gemData.getZ()+0.5D);
			entity.setLocationAndAngles(gemData.getX()+0.5D,gemData.getY()+1.001D,gemData.getZ()+0.5D,entity.rotationYaw,entity.rotationPitch);
			entity.fallDistance = 0F;
			
			float percBroken = itemDamage/(float)is.getMaxDamage();
			if (percBroken > 0.66F && entity.worldObj.rand.nextFloat()*1.4F < percBroken){
				GemSideEffects.performRandomEffect(entity,percBroken);
			}
			
			if (EnhancementHandler.hasEnhancement(is,TransferenceGemEnhancements.HEAL) && isLiving){
				EntityLivingBase e = (EntityLivingBase)entity;
				e.addPotionEffect(new PotionEffect(Potion.regeneration.id,120,1,true,false));
				e.addPotionEffect(new PotionEffect(Potion.saturation.id,1,0,true,false));
				e.extinguish();
			}

			PacketPipeline.sendToAllAround(entity,64D,new C20Effect(FXType.Basic.GEM_TELEPORT_TO,entity));
		}
		
		return is;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		GemData gemData = GemData.loadFromItemStack(is);
		
		if (gemData.isLinked()){
			textLines.add(EnumChatFormatting.GRAY+"Linked");
			if (showAdvancedInfo)textLines.add(new StringBuilder().append(EnumChatFormatting.GRAY.toString()).append("DIM ").append(gemData.getDimension()).append(", X ").append(gemData.getX()).append(", Y ").append(gemData.getY()).append(", Z ").append(gemData.getZ()).toString());
		}
		
		EnhancementHandler.appendEnhancementNames(is,textLines);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack is){
		return EnumRarity.UNCOMMON;
	}
	
	private static int getIcon(ItemStack is){
		if (is.getItemDamage() == is.getMaxDamage())return 2;
		float percBroken = is.getItemDamage()/(float)is.getMaxDamage();
		return percBroken > 0.56F ? 1 : 0;
	}
}
