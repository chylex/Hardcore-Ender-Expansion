package chylex.hee.item;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.effect.EntityWeatherEffect;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.entity.mob.EntityMobAngryEnderman;
import chylex.hee.entity.mob.EntityMobParalyzedEnderman;
import chylex.hee.entity.weather.EntityWeatherLightningBoltDemon;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C05CustomWeather;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEndermanRelic extends ItemAbstractEnergyAcceptor{
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
		return 1;
	}
	
	@Override
	protected float getRegenSpeedMultiplier(){
		return 1F;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack is){
		return EnumRarity.uncommon;
	}
	
	@Override
	public void onUpdate(ItemStack is, World world, Entity owner, int slot, boolean isHeld){
		if (!world.isRemote){
			if (is.stackTagCompound == null)is.stackTagCompound = new NBTTagCompound();
			byte timer = is.stackTagCompound.getByte("HEE_relicTimer");
			
			if (++timer > 8){
				timer = 0;
				
				if (owner instanceof EntityPlayer){
					Class<?> cls;
					for(EntityMob mob:(List<EntityMob>)world.getEntitiesWithinAABB(EntityMob.class,owner.boundingBox.expand(2.75D,1.5D,2.75D))){
						cls = mob.getClass();
						
						if (cls == EntityEnderman.class || cls == EntityMobAngryEnderman.class){
							if (mob.getEntityToAttack() != owner)continue;
							
							EntityWeatherEffect bolt = new EntityWeatherLightningBoltDemon(world,mob.posX,mob.posY,mob.posZ,null,false);
							world.addWeatherEffect(bolt);
							PacketPipeline.sendToAllAround(bolt,512D,new C05CustomWeather(bolt,(byte)0));
							
							EntityMobParalyzedEnderman paralyzed = new EntityMobParalyzedEnderman(world);
							paralyzed.copyLocationAndAnglesFrom(mob);
							
							if (mob.getLastAttacker() != owner && world.rand.nextInt(3) == 0){
								List<EntityLiving> otherEntities = world.getEntitiesWithinAABB(EntityLiving.class,paralyzed.boundingBox.expand(8D,4D,8D));
								
								if (!otherEntities.isEmpty()){
									EntityLiving target = otherEntities.get(world.rand.nextInt(otherEntities.size()));
									if (!paralyzed.canEntityBeSeen(target))target = null;
									paralyzed.setTarget(target);
								}
							}
							
							world.removeEntity(mob);
							world.spawnEntityInWorld(paralyzed);
							
							damageItem(is,(EntityPlayer)owner);
							
							break;
						}
					}
				}
			}
			
			is.stackTagCompound.setByte("HEE_relicTimer",timer);
		}
	}
}
