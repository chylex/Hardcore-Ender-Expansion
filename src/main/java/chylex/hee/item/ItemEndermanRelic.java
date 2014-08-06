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
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C05CustomWeather;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEndermanRelic extends ItemAbstractEnergyAcceptor{
	@Override
	protected boolean canAcceptEnergy(ItemStack is){
		return is.getItemDamage() > 0;
	}

	@Override
	protected void onEnergyAccepted(ItemStack is){
		is.setItemDamage(is.getItemDamage()-2);
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
					for(Object o:world.getEntitiesWithinAABB(EntityMob.class,owner.boundingBox.expand(2.75D,1.5D,2.75D))){
						cls = o.getClass();
						
						if (cls == EntityEnderman.class || cls == EntityMobAngryEnderman.class){
							EntityMob mob = (EntityMob)o;
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
							
							is.damageItem(1,(EntityPlayer)owner);
							
							for(EntityPlayer observer:ObservationUtil.getAllObservers(owner,12D)){
								if (KnowledgeRegistrations.ENDERMAN_RELIC.tryUnlockFragment(observer,0.6F).stopTrying)continue;
								KnowledgeRegistrations.PARALYZED_ENDERMAN.tryUnlockFragment(observer,0.3F,new byte[]{ 0 });
							}
							
							break;
						}
					}
				}
			}
			
			is.stackTagCompound.setByte("HEE_relicTimer",timer);
		}
	}
}
