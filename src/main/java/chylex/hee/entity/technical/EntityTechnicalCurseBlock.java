package chylex.hee.entity.technical;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.game.save.handlers.PlayerDataHandler;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.curse.CurseEvents;
import chylex.hee.mechanics.curse.CurseType;
import chylex.hee.mechanics.curse.CurseType.EnumCurseUse;
import chylex.hee.mechanics.curse.ICurseCaller;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.entity.EntityDataWatcher;
import chylex.hee.system.abstractions.entity.EntitySelector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityTechnicalCurseBlock extends EntityTechnicalBase implements ICurseCaller{
	private enum Data{ CURSE_TYPE, OWNER_ID }
	
	private EntityDataWatcher entityData;
	private String ownerID;
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
	
	public EntityTechnicalCurseBlock(World world, Pos pos, String ownerID, CurseType type, boolean eternal){
		super(world);
		setPosition(pos.getX()+0.5D,pos.getY(),pos.getZ()+0.5D);
		this.ownerID = ownerID;
		this.curseType = type;
		this.eternal = eternal;
		this.usesLeft = (byte)(eternal ? -1 : type.getUses(EnumCurseUse.BLOCK,rand));
	}

	@Override
	protected void entityInit(){
		entityData = new EntityDataWatcher(this);
		entityData.addByte(Data.CURSE_TYPE);
		entityData.addInt(Data.OWNER_ID,-1);
	}
	
	@Override
	public void onUpdate(){
		if (worldObj.isRemote){
			if (curseType == null)curseType = CurseType.getFromDamage(entityData.getByte(Data.CURSE_TYPE)-1);
			
			if (curseType != null){
				EntityPlayer client = HardcoreEnderExpansion.proxy.getClientSidePlayer();
				
				double dist = client.getDistanceToEntity(this);
				if (dist > 32D)return;
				
				if (ownerEntityID == -1)ownerEntityID = entityData.getInt(Data.OWNER_ID);
				
				boolean forceRenderFX = client.getEntityId() == ownerEntityID || (client.getHeldItem() != null && client.getHeldItem().getItem() == ItemList.curse_amulet);
				
				if (!forceRenderFX){
					for(EntityLivingBase entity:EntitySelector.living(worldObj,boundingBox.expand(1.75D,0.1D,1.75D))){
						if (entity == client){
							disappearTimer = 120;
							break;
						}
					}
				}
				
				if (forceRenderFX || (disappearTimer > 0 && --disappearTimer > 0)){
					for(int a = 0; a < 1+rand.nextInt(dist > 16D ? 2 : 3); a++)HardcoreEnderExpansion.fx.curse(posX+(rand.nextDouble()-0.5D)*3D,posY,posZ+(rand.nextDouble()-0.5D)*3D,curseType);
				}
			}
			
			return;
		}
		else if (ticksExisted == 1)entityData.setByte(Data.CURSE_TYPE,curseType.damage+1);
		
		if (ticksExisted%20 == 1){
			if (worldObj.getEntityByID(ownerEntityID) == null)ownerEntityID = -1;
			
			if (ownerEntityID == -1){
				EntitySelector.players(worldObj).stream().filter(player -> PlayerDataHandler.getID(player).equals(ownerID)).findAny().ifPresent(player -> {
					entityData.setInt(Data.OWNER_ID,ownerEntityID = player.getEntityId());
				});
			}
			else if (worldObj.getEntityByID(ownerEntityID) == null)ownerEntityID = -1;
		}
		
		List<EntityLivingBase> newAffectedEntities = new ArrayList<>();
		
		for(EntityLivingBase entity:EntitySelector.living(worldObj,boundingBox.expand(1.5D,0.1D,1.5D))){
			if (ownerEntityID == entity.getEntityId() || entity instanceof IBossDisplayData)continue;
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
		nbt.setString("owner",ownerID);
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){
		if ((curseType = CurseType.getFromDamage(nbt.getByte("curse"))) == null)setDead();
		if (nbt.hasKey("own1"))setDead(); // they would stop working and cause a mess anyways
		
		eternal = nbt.getBoolean("eternal");
		usesLeft = nbt.getByte("usesLeft");
		ownerID = nbt.getString("ownerID");
	}
}
