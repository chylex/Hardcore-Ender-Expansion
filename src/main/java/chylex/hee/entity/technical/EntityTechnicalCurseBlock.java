package chylex.hee.entity.technical;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.curse.CurseEvents;
import chylex.hee.mechanics.curse.CurseType;
import chylex.hee.mechanics.curse.CurseType.EnumCurseUse;
import chylex.hee.mechanics.curse.ICurseCaller;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityTechnicalCurseBlock extends EntityTechnicalBase implements ICurseCaller{
	private UUID owner;
	private int ownerEntityID = -1;
	private CurseType curseType;
	private boolean eternal;
	private byte usesLeft;
	
	private final List<EntityLivingBase> prevAffectedEntities = new ArrayList<>();
	
	@SideOnly(Side.CLIENT)
	private byte disappearTimer;
	
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
		dataWatcher.addObject(17,-1);
	}
	
	@Override
	public void onUpdate(){
		if (worldObj.isRemote){
			if (curseType == null)curseType = CurseType.getFromDamage(dataWatcher.getWatchableObjectByte(16)-1);
			
			if (curseType != null){
				EntityPlayer client = HardcoreEnderExpansion.proxy.getClientSidePlayer();
				
				double dist = client.getDistanceToEntity(this);
				if (dist > 32D)return;
				
				if (ownerEntityID == -1)ownerEntityID = dataWatcher.getWatchableObjectInt(17);
				
				boolean forceRenderFX = client.getEntityId() == ownerEntityID || (client.getHeldItem() != null && client.getHeldItem().getItem() == ItemList.curse_amulet);
				
				if (!forceRenderFX){
					for(EntityLivingBase entity:(List<EntityLivingBase>)worldObj.getEntitiesWithinAABB(EntityLivingBase.class,boundingBox.expand(1.75D,0.1D,1.75D))){
						if (entity == client){
							disappearTimer = 120;
							break;
						}
					}
				}
				
				if (forceRenderFX || (disappearTimer > 0 && --disappearTimer > 0)){
					for(int a = 0; a < 1+rand.nextInt(dist > 16D ? 2 : 3); a++)HardcoreEnderExpansion.fx.curse(worldObj,posX+(rand.nextDouble()-0.5D)*3D,posY,posZ+(rand.nextDouble()-0.5D)*3D,curseType);
				}
			}
			
			return;
		}
		else if (ticksExisted == 1)dataWatcher.updateObject(16,(byte)(curseType.damage+1));
		
		if (ticksExisted%20 == 1){
			if (ownerEntityID == -1){
				for(EntityPlayer player:(List<EntityPlayer>)worldObj.playerEntities){
					if (player.getUniqueID().equals(owner)){
						dataWatcher.updateObject(17,ownerEntityID = player.getEntityId());
						break;
					}
				}
			}
			else if (worldObj.getEntityByID(ownerEntityID) == null)ownerEntityID = -1;
		}
		
		List<EntityLivingBase> newAffectedEntities = new ArrayList<>();
		
		for(EntityLivingBase entity:(List<EntityLivingBase>)worldObj.getEntitiesWithinAABB(EntityLivingBase.class,boundingBox.expand(1.5D,0.1D,1.5D))){
			if (entity.getUniqueID().equals(owner) || entity instanceof IBossDisplayData)continue;
			else{
				newAffectedEntities.add(entity);
				
				if (curseType.handler.tickEntity(entity,this) && usesLeft != -1 && (--usesLeft <= 0 || (CurseEvents.hasAmulet(entity) && --usesLeft <= 0))){
					curseType.handler.end(entity,this);
					setDead();
					break;
				}
			}
		}
		
		for(EntityLivingBase prevAffected:prevAffectedEntities){
			if (!newAffectedEntities.contains(prevAffected))curseType.handler.end(prevAffected,this);
		}
		
		prevAffectedEntities.clear();
		prevAffectedEntities.addAll(newAffectedEntities);
	}
	
	@Override
	public void onPurify(){
		if (curseType != null){
			for(EntityLivingBase prevAffected:prevAffectedEntities)curseType.handler.end(prevAffected,this);
			prevAffectedEntities.clear();
		}
	}
	
	@Override
	public Entity getEntity(){
		return this;
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
