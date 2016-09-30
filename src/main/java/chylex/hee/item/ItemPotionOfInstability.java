package chylex.hee.item;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import chylex.hee.mechanics.brewing.AbstractPotionData;
import chylex.hee.mechanics.brewing.PotionTypes;
import chylex.hee.mechanics.brewing.TimedPotion;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPotionOfInstability extends ItemAbstractPotion{
	public static final PotionEffect getRandomPotionEffect(Random rand){
		while(true){
			AbstractPotionData potion = CollectionUtil.randomOrNull(PotionTypes.potionData, rand);
			
			if (potion instanceof TimedPotion){
				TimedPotion timed = (TimedPotion)potion;
				return new PotionEffect(timed.getPotionType().id, (int)(rand.nextInt(MathUtil.ceil(timed.getMaxDuration()*0.8D))+timed.getMaxDuration()*0.2D), rand.nextInt(timed.getMaxLevel(false)));
			}
		}
	}
	
	@Override
	public void applyEffectDrunk(ItemStack is, World world, EntityPlayer player){
		player.addPotionEffect(getRandomPotionEffect(itemRand));
	}
	
	@Override
	public void applyEffectThrown(Entity entity, double dist){
		if (!(entity instanceof EntityLivingBase))return;
		
		PotionEffect eff = ItemPotionOfInstability.getRandomPotionEffect(entity.worldObj.rand);
		int dur = (int)(((dist == Double.MAX_VALUE ? 1D : (1D-Math.sqrt(dist)/4D)))*eff.getDuration()+0.5D);
		if (dur > 20)((EntityLivingBase)entity).addPotionEffect(new PotionEffect(eff.getPotionID(), dur, eff.getAmplifier(), eff.getIsAmbient()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		textLines.add(EnumChatFormatting.GRAY+"???");
	}
}
