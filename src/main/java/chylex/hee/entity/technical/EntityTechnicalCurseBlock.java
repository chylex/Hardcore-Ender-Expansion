package chylex.hee.entity.technical;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
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
	
	public EntityTechnicalCurseBlock(World world, int x, int y, int z, UUID ownerID, CurseType type, boolean eternal){
		super(world);
		setPosition(x+0.5D,y,z+0.5D);
		this.owner = ownerID;
		this.curseType = type;
		this.eternal = eternal;
		this.usesLeft = (byte)(eternal ? -1 : type.getUses(EnumCurseUse.BLOCK,rand));
	}

	@Override
	protected void entityInit(){
		dataWatcher.addObject(16,Byte.valueOf((byte)0));
	}
	
	@Override
	public void onUpdate(){
		if (worldObj.isRemote){
			if (curseType == null)curseType = CurseType.getFromDamage(dataWatcher.getWatchableObjectByte(16)-1);
			
			if (curseType != null){
				double dist = HardcoreEnderExpansion.proxy.getClientSidePlayer().getDistanceToEntity(this);
				if (dist > 32D)return;
				
				for(int a = 0; a < 1+rand.nextInt(dist > 16D ? 2 : 3); a++)HardcoreEnderExpansion.fx.curse(worldObj,posX+(rand.nextDouble()-0.5D)*3D,posY,posZ+(rand.nextDouble()-0.5D)*3D,curseType);
			}
			
			return;
		}
		else if (ticksExisted == 1)dataWatcher.updateObject(16,(byte)(curseType.damage+1));
		
		for(EntityLivingBase entity:(List<EntityLivingBase>)worldObj.getEntitiesWithinAABB(EntityLivingBase.class,boundingBox.expand(1.5D,0.1D,1.5D))){
			if (entity.getPersistentID().equals(owner) || entity instanceof IBossDisplayData)continue;
			else if (curseType.handler.tickEntity(entity,this) && (usesLeft != -1 && --usesLeft <= 0)){
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
