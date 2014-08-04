package chylex.hee.tileentity;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.mechanics.enhancements.types.SoulCharmEnhancements;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.data.UnlockResult;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;

public class TileEntitySoulCharm extends TileEntity{
	private byte charmTimer = 0, damageTimer = 0, fireTimer = 0;
	private NBTTagCompound enhancements = new NBTTagCompound();
	
	@Override
	public void updateEntity(){
		Random rand = worldObj.rand;
		
		if (worldObj.isRemote){
			if (rand.nextInt(3) == 0)HardcoreEnderExpansion.fx.soulCharm(worldObj,xCoord,yCoord,zCoord);
			return;
		}
		
		/*
		 * CHARM
		 */
		
		if (++charmTimer > Math.max(35,90-getLevel(SoulCharmEnhancements.SPEED)*7)){
			charmTimer = 0;

			byte range = getLevel(SoulCharmEnhancements.RANGE),eff = getLevel(SoulCharmEnhancements.EFFICIENCY);
			
			List<EntityLiving> list = worldObj.getEntitiesWithinAABB(EntityLiving.class,AxisAlignedBB.getBoundingBox(
				xCoord+0.5D-12D-4D*range,yCoord+0.5D-3D-2D*range,zCoord+0.5D-12D-4D*range,
				xCoord+0.5D+12D+4D*range,yCoord+0.5D+3D+2D*range,zCoord+0.5D+12D+4D*range)
			);
			if (list.isEmpty())return;

			for(int a = 0,size = list.size(); a < 1+rand.nextInt(Math.max(1,eff*2+1)); a++){
				EntityLiving e = list.get(rand.nextInt(size));
				if (e.getDistance(xCoord+0.5D,yCoord,zCoord+0.5D) < 8D)continue;
				
				if (Math.abs(e.getAIMoveSpeed()-0.1F) > 0.0001F && !(e instanceof EntityBat)){
					IAttributeInstance attr = e.getEntityAttribute(SharedMonsterAttributes.followRange);
					double prevValue = attr.getBaseValue();
					
					attr.setBaseValue(12.5D+4D*range);
					e.getNavigator().tryMoveToXYZ(xCoord+0.5D+(rand.nextFloat()-0.5F)*6D,yCoord,zCoord+0.5D+(rand.nextFloat()-0.5F)*6D,1D);
					attr.setBaseValue(prevValue);
				}
				else if (e instanceof EntityCreature){
					((EntityCreature)e).setPathToEntity(worldObj.getEntityPathToXYZ(e,(int)(xCoord+0.5D+(rand.nextFloat()-0.5F)*6D),yCoord,(int)(zCoord+0.5D+(rand.nextFloat()-0.5F)*6D),12.5F+4F*range,true,false,false,true));
				}
				else continue;
				
				triggerKnowledge(e);
			}
		}
		
		/*
		 * DAMAGE
		 */
		
		byte damage = getLevel(SoulCharmEnhancements.DAMAGE);
		if (damage > 0 && ++damageTimer > Math.max(70,120-getLevel(SoulCharmEnhancements.SPEED)*6)){
			damageTimer = 0;
			
			byte range = (byte)Math.min(3,getLevel(SoulCharmEnhancements.RANGE)),eff = getLevel(SoulCharmEnhancements.EFFICIENCY);
			
			List<EntityLiving> list = worldObj.getEntitiesWithinAABB(EntityLiving.class,AxisAlignedBB.getBoundingBox(
				xCoord+0.5D-3D-1.85D*range,yCoord+0.5D-1D-range*0.75D,zCoord+0.5D-3D-1.85D*range,
				xCoord+0.5D+3D+1.85D*range,yCoord+0.5D+1D+range*0.75D,zCoord+0.5D+3D+1.85D*range)
			);
			if (list.isEmpty())return;
			
			for(int a = 0,size = list.size(); a < 1+rand.nextInt((int)Math.max(1,eff*2.25D+1)); a++){
				EntityLiving e = list.get(rand.nextInt(size));
				if (e.getDistance(xCoord+0.5D,yCoord,zCoord+0.5D) > 2D*(3.5D+1.85D*range))continue;
				
				e.attackEntityFrom(DamageSource.magic,2F+damage*2F);
				triggerKnowledge(e);
			}
		}
		
		/*
		 * FIRE
		 */
		
		byte fire = getLevel(SoulCharmEnhancements.FIRE);
		if (fire > 0 && ++fireTimer > Math.max(61,113-getLevel(SoulCharmEnhancements.SPEED)*3-fire*2)){
			fireTimer = 0;
			
			byte range = (byte)Math.min(3,getLevel(SoulCharmEnhancements.RANGE)),eff = getLevel(SoulCharmEnhancements.EFFICIENCY);
			
			List<EntityLiving> list = worldObj.getEntitiesWithinAABB(EntityLiving.class,AxisAlignedBB.getBoundingBox(
				xCoord+0.5D-3D-1.85D*range,yCoord+0.5D-1D-range*0.75D,zCoord+0.5D-3D-1.85D*range,
				xCoord+0.5D+3D+1.85D*range,yCoord+0.5D+1D+range*0.75D,zCoord+0.5D+3D+1.85D*range)
			);
			if (list.isEmpty())return;
			
			for(int a = 0,size = list.size(); a < 1+rand.nextInt((int)Math.max(1,eff*2.25D+1)); a++){
				EntityLiving e = list.get(rand.nextInt(size));
				if (e.getDistance(xCoord+0.5D,yCoord,zCoord+0.5D) > 2D*(3.5D+1.85D*range))continue;
				
				e.setFire(2+fire*2);
				triggerKnowledge(e);
			}
		}
	}
	
	private void triggerKnowledge(Entity entity){
		for(EntityPlayer observer:ObservationUtil.getAllObservers(entity,8D)){
			if (KnowledgeRegistrations.SOUL_CHARM.tryUnlockFragment(observer,0.16F,new byte[]{ 0,1 }).stopTrying)continue;
			if (KnowledgeRegistrations.SOUL_CHARM.tryUnlockFragment(observer,0.09F,new byte[]{ 2 }) == UnlockResult.NOTHING_TO_UNLOCK){
				KnowledgeRegistrations.SOUL_CHARM_ENH.tryUnlockFragment(observer,1F);
			}
		}
	}
	
	public void setEnhancementTag(NBTTagCompound tag){
		this.enhancements = (NBTTagCompound)tag.copy();
	}
	
	public NBTTagCompound getEnhancementTag(){
		return enhancements;
	}
	
	private byte getLevel(SoulCharmEnhancements enhancement){
		return enhancements.getByte(enhancement.name);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setTag("enhancements",enhancements);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		enhancements = nbt.getCompoundTag("enhancements");
	}
}
