package chylex.hee.item;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.entity.fx.FXType;
import chylex.hee.game.achievements.AchievementManager;
import chylex.hee.item.base.ItemAbstractGem;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20Effect;
import chylex.hee.packets.client.C21EffectEntity;
import chylex.hee.system.abstractions.nbt.NBT;
import chylex.hee.system.abstractions.nbt.NBTCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTransferenceGem extends ItemAbstractGem{
	private static final GemData clientCache = new GemData();
	
	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;
	
	public ItemTransferenceGem(){
		setMaxDamage(204);
	}
	
	@Override
	public int getEnergyAccepted(ItemStack is){
		return 2;
	}
	
	@Override
	public int getEnergyUsage(ItemStack is){
		return 3;
	}
	
	@Override
	protected byte getCooldown(){
		return 50;
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		if (super.onItemUse(is, player, world, x, y, z, side, hitX, hitY, hitZ))return true;
		
		if (!world.isRemote && side == 1 && player.isSneaking()){
			GemData data = new GemData();
			data.set(player.dimension, x, y, z);
			data.saveToItemStack(is);
			PacketPipeline.sendToAllAround(player.dimension, x, y, z, 64D, new C20Effect(FXType.Basic.GEM_LINK, x, y, z));
			return true;
		}
		
		return false;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		return (world.isRemote || player.isSneaking()) ? is : tryTeleportEntity(is, player, player);
	}
	
	@Override
	public boolean itemInteractionForEntity(ItemStack is, EntityPlayer player, EntityLivingBase entity){
		/* TODO if (entity instanceof IBossDisplayData || !EnhancementHandler.hasEnhancement(is, TransferenceGemEnhancements.BEAST) || player.isSneaking())return false;
		tryTeleportEntity(is, player, entity);*/
		return true;
	}
	
	@Override
	public void onCreated(ItemStack is, World world, EntityPlayer player){
		player.addStat(AchievementManager.TRANSFERENCE_GEM, 1);
	}
	
	public ItemStack tryTeleportEntity(ItemStack is, EntityPlayer player, Entity entity){
		if (entity.isRiding() || entity.riddenByEntity != null || !is.hasTagCompound() || !canUse(is))return is;
		
		GemData gemData = new GemData();
		gemData.set(NBT.wrap(is.getTagCompound()));
		
		if (gemData.isLinked() && entity.dimension == gemData.dim){
			int itemDamage = is.getItemDamage();
			if (itemDamage >= is.getMaxDamage())return is;
			
			boolean isLiving = entity instanceof EntityLivingBase;
			
			PacketPipeline.sendToAllAround(entity, 64D, new C21EffectEntity(FXType.Entity.GEM_TELEPORT_FROM, entity));
			
			useEnergy(is, player);
			
			if (isLiving)((EntityLivingBase)entity).setPositionAndUpdate(gemData.x+0.5D, gemData.y+1.001D, gemData.z+0.5D);
			entity.setLocationAndAngles(gemData.x+0.5D, gemData.y+1.001D, gemData.z+0.5D, entity.rotationYaw, entity.rotationPitch);
			entity.fallDistance = 0F;
			
			/* TODO probably remove completely | float percBroken = itemDamage/(float)is.getMaxDamage();
			if (percBroken > 0.66F && entity.worldObj.rand.nextFloat()*1.4F < percBroken){
				GemSideEffects.performRandomEffect(entity, percBroken);
			}*/
			
			PacketPipeline.sendToAllAround(entity, 64D, new C20Effect(FXType.Basic.GEM_TELEPORT_TO, entity));
		}
		
		return is;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		if (is.hasTagCompound()){
			clientCache.set(NBT.wrap(is.getTagCompound()));
			
			if (clientCache.isLinked()){
				textLines.add(EnumChatFormatting.GRAY+I18n.format("item.transferenceGem.info.linked"));
				if (showAdvancedInfo)textLines.add(new StringBuilder().append(EnumChatFormatting.GRAY.toString()).append("DIM ").append(clientCache.dim).append(", X ").append(clientCache.x).append(", Y ").append(clientCache.y).append(", Z ").append(clientCache.z).toString());
			}
		}
		
		super.addInformation(is, player, textLines, showAdvancedInfo);
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

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister){
		iconArray = new IIcon[3];
		for(int a = 0; a < iconArray.length; a++)iconArray[a] = iconRegister.registerIcon("hardcoreenderexpansion:transference_gem_"+(a+1));
	}
	
	private static int getIcon(ItemStack is){
		if (is.getItemDamage() == is.getMaxDamage())return 2;
		float percBroken = is.getItemDamage()/(float)is.getMaxDamage();
		return percBroken > 0.56F ? 1 : 0;
	}
	
	private static class GemData{
		private int dim, x, y, z;
		
		private void set(NBTCompound tag){
			set(tag.hasKey("HED_Gem_Dim") ? tag.getInt("HED_Gem_Dim") : -999, tag.getInt("HED_Gem_X"), tag.getInt("HED_Gem_Y"), tag.getInt("HED_Gem_Z"));
		}
		
		private void set(int dimension, int x, int y, int z){
			this.dim = dimension;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public boolean isLinked(){
			return dim != -999;
		}
		
		public void saveToItemStack(ItemStack is){
			NBTCompound tag = NBT.item(is, true);
			tag.setInt("HED_Gem_Dim", dim);
			tag.setInt("HED_Gem_X", x);
			tag.setInt("HED_Gem_Y", y);
			tag.setInt("HED_Gem_Z", z);
		}
	}
}
