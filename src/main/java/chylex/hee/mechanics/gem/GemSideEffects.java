package chylex.hee.mechanics.gem;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.storage.WorldInfo;
import chylex.hee.entity.mob.EntityMobAngryEnderman;
import chylex.hee.system.util.MathUtil;
import chylex.hee.system.weight.IWeightProvider;
import chylex.hee.system.weight.WeightedList;

public enum GemSideEffects implements IWeightProvider{
	DEFLECTION(10), SLOWNESS(10), NAUSEA(8), WEAKNESS(6), HUNGER(5, 0.72F), ENDERMEN(3, 0.78F), ITEMDROP(2, 0.82F), STORM(1, 0.88F);
	
	private byte weight;
	private float minPerc;
	
	private GemSideEffects(int weight, float minPercentage){
		this.weight = (byte)weight;
		this.minPerc = minPercentage;
	}
	
	private GemSideEffects(int weight){
		this(weight,0.66f);
	}
	
	@Override
	public int getWeight(){
		return weight;
	}
	
	public static void performRandomEffect(Entity entity, float percBroken){
		Random rand = entity.worldObj.rand;
		WeightedList<GemSideEffects> list = new WeightedList<>();
		
		for(GemSideEffects effect:values()){
			if (percBroken > effect.minPerc)list.add(effect);
		}
		
		switch(list.getRandomItem(rand)){
			case DEFLECTION:
				double ang = rand.nextDouble()*2D*Math.PI,len = rand.nextDouble()*72D*percBroken*percBroken;
				double x = entity.posX+Math.cos(ang)*len,z = entity.posZ+Math.sin(ang)*len;
				entity.setLocationAndAngles(x+0.5D,entity.worldObj.getTopSolidOrLiquidBlock(MathUtil.floor(x),MathUtil.floor(z))+1D,z+0.5D,entity.rotationYaw,entity.rotationPitch);
				break;
				
			case SLOWNESS:
				if (entity instanceof EntityLivingBase)((EntityLivingBase)entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id,MathUtil.ceil(280f*percBroken),1,true));
				break;
				
			case NAUSEA:
				if (entity instanceof EntityLivingBase)((EntityLivingBase)entity).addPotionEffect(new PotionEffect(Potion.confusion.id,MathUtil.ceil(340f*percBroken),0,true));
				break;
				
			case WEAKNESS:
				if (entity instanceof EntityLivingBase)((EntityLivingBase)entity).addPotionEffect(new PotionEffect(Potion.weakness.id,MathUtil.ceil(320f*percBroken),1,true));
				break;
				
			case HUNGER:
				if (entity instanceof EntityLivingBase)((EntityLivingBase)entity).addPotionEffect(new PotionEffect(Potion.hunger.id,MathUtil.ceil(265f*percBroken),0,true));
				break;
				
			case ENDERMEN:
				for(int a = 0; a < rand.nextInt(3)+2; a++)entity.worldObj.spawnEntityInWorld(new EntityMobAngryEnderman(entity.worldObj,entity.posX,entity.posY,entity.posZ));
				break;
				
			case ITEMDROP:
				if (!(entity instanceof EntityPlayer))break;
				EntityPlayer player = (EntityPlayer)entity;
				
				for(int a = 0,slot; a < 10+rand.nextInt(12); a++){
					slot = rand.nextInt(player.inventory.getSizeInventory());
					ItemStack is = player.inventory.getStackInSlot(slot);
					if (is != null){
						player.entityDropItem(is,0F);
						player.inventory.setInventorySlotContents(slot,null);
					}
				}
				break;
				
			case STORM:
				WorldInfo info = entity.worldObj.getWorldInfo();
				int i = (140+rand.nextInt(75))*20;
				
				info.setRainTime(i);
				info.setThunderTime(i);
				info.setRaining(true);
				info.setThundering(true);
				break;
				
			default:
		}
	}
}
