package chylex.hee.item;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.types.TransferenceGemEnhancements;
import chylex.hee.mechanics.gem.GemData;
import chylex.hee.mechanics.gem.GemSideEffects;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.data.UnlockResult;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C10ParticleTransferenceGemLink;
import chylex.hee.packets.client.C11ParticleTransferenceGemTeleportFrom;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTransferenceGem extends ItemAbstractEnergyAcceptor{
	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;
	
	@Override
	protected boolean canAcceptEnergy(ItemStack is){
		return is.getItemDamage() > 0;
	}

	@Override
	protected void onEnergyAccepted(ItemStack is){
		is.setItemDamage(is.getItemDamage()-3);
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		if (super.onItemUse(is,player,world,x,y,z,side,hitX,hitY,hitZ))return true;
		
		if (!world.isRemote && side == 1 && player.isSneaking()){
			GemData.updateItemStack(is,player.dimension,x,y,z);
			PacketPipeline.sendToAllAround(player.dimension,x,y,z,64D,new C10ParticleTransferenceGemLink(true,x,y,z));
			return true;
		}
		
		return false;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		return (world.isRemote || player.isSneaking())?is:tryTeleportEntity(is,player,player);
	}
	
	public ItemStack tryTeleportEntity(ItemStack is, EntityPlayer player, Entity entity){
		if (entity.isRiding() || entity.riddenByEntity != null)return is;
		
		GemData gemData = GemData.loadFromItemStack(is);
		
		if (gemData.isLinked() && entity.dimension == gemData.getDimension()){		
			int itemDamage = is.getItemDamage();
			if (itemDamage >= is.getMaxDamage())return is;
			
			boolean isLiving = entity instanceof EntityLivingBase;
			
			PacketPipeline.sendToAllAround(entity,64D,new C11ParticleTransferenceGemTeleportFrom(entity));
			
			is.setItemDamage(itemDamage+1);
			entity.fallDistance = 0F;
			if (isLiving)((EntityLivingBase)entity).setPositionAndUpdate(gemData.getX()+0.5D,gemData.getY()+1.001D,gemData.getZ()+0.5D);
			entity.setLocationAndAngles(gemData.getX()+0.5D,gemData.getY()+1.001D,gemData.getZ()+0.5D,entity.rotationYaw,entity.rotationPitch);
			
			float percBroken = itemDamage/(float)is.getMaxDamage();
			if (percBroken > 0.66F && entity.worldObj.rand.nextFloat()*1.4F < percBroken){
				GemSideEffects.performRandomEffect(entity,percBroken);
				if (entity == player)KnowledgeRegistrations.TRANSFERENCE_GEM.tryUnlockFragment((EntityPlayer)entity,0.26F);
			}
			
			if (EnhancementHandler.hasEnhancement(is,TransferenceGemEnhancements.HEAL) && isLiving){
				EntityLivingBase e = (EntityLivingBase)entity;
				e.addPotionEffect(new PotionEffect(Potion.regeneration.id,120,1,true));
				e.addPotionEffect(new PotionEffect(Potion.field_76443_y.id,1,0,true));
				e.extinguish();
			}

			PacketPipeline.sendToAllAround(entity.dimension,entity.posX,entity.posY,entity.posZ,64D,new C10ParticleTransferenceGemLink(false,(int)Math.floor(entity.posX),(int)Math.floor(entity.posY+1.25D),(int)Math.floor(entity.posZ)));

			if (!KnowledgeRegistrations.TRANSFERENCE_GEM.tryUnlockFragment(player,0.15F).stopTrying &&
				KnowledgeRegistrations.TRANSFERENCE_GEM.tryUnlockFragment(player,0.1F,new byte[]{ 7 }) == UnlockResult.NOTHING_TO_UNLOCK){
				KnowledgeRegistrations.TRANSFERENCE_GEM_ENH.tryUnlockFragment(player,1F);
			}
			
			for(EntityPlayer observer:ObservationUtil.getAllObservers(entity,10D))KnowledgeRegistrations.TRANSFERENCE_GEM.tryUnlockFragment(observer,0.13F);
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
		return EnumRarity.uncommon;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack is, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining){
		return iconArray[getIcon(is)];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconIndex(ItemStack is){
		return iconArray[getIcon(is)];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister){
		iconArray = new IIcon[3];
		for(int a = 0; a < iconArray.length; a++)iconArray[a] = iconRegister.registerIcon("hardcoreenderexpansion:transference_gem_"+(a+1));
	}
	
	private static int getIcon(ItemStack is){
		if (is.getItemDamage() == is.getMaxDamage())return 2;
		float percBroken = is.getItemDamage()/(float)is.getMaxDamage();
		return percBroken > 0.56F ? 1 : 0;
	}
}
