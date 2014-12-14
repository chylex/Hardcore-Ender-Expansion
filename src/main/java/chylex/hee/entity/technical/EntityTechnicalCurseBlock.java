package chylex.hee.entity.technical;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.mechanics.curse.CurseType;
import chylex.hee.mechanics.curse.CurseType.EnumCurseUse;
import chylex.hee.mechanics.curse.ICurseCaller;

public class EntityTechnicalCurseBlock extends EntityTechnicalBase implements ICurseCaller{
	private UUID owner;
	private CurseType curseType;
	private boolean eternal;
	private byte usesLeft;
	
	public EntityTechnicalCurseBlock(World world){
		super(world);
	}
	
	public EntityTechnicalCurseBlock(World world, int x, int y, int z, EntityPlayer owner, CurseType type, boolean eternal){
		super(world);
		setPosition(x+0.5D,y,z+0.5D);
		this.owner = owner.getPersistentID();
		this.curseType = type;
		this.eternal = eternal;
		this.usesLeft = (byte)type.getUses(EnumCurseUse.BLOCK,rand);
	}

	@Override
	protected void entityInit(){}
	
	@Override
	public void onUpdate(){
		if (worldObj.isRemote){
			// TODO particles
			return;
		}
		
		for(EntityLivingBase entity:(List<EntityLivingBase>)worldObj.getEntitiesWithinAABB(EntityLivingBase.class,boundingBox.expand(1.5D,0.1D,1.5D))){
			if (entity.getPersistentID().equals(owner))continue;
			else if (curseType.handler.tickEntity(entity,this) && --usesLeft <= 0){
				setDead();
				break;
			}
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){
		nbt.setByte("curse",curseType.damage);
		nbt.setBoolean("eternal",eternal);
		nbt.setByte("usesLeft",usesLeft);
		nbt.setLong("own1",owner.getLeastSignificantBits());
		nbt.setLong("own2",owner.getMostSignificantBits());
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){
		if ((curseType = CurseType.getFromDamage(nbt.getByte("curse"))) == null)setDead();
		eternal = nbt.getBoolean("eternal");
		usesLeft = nbt.getByte("usesLeft");
		owner = new UUID(nbt.getLong("own2"),nbt.getLong("own1"));
	}
}
