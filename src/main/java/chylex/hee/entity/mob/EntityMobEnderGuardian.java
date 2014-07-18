package chylex.hee.entity.mob;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C22ParticleEnderGuardianTeleportation;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.DragonUtil;

public class EntityMobEnderGuardian extends EntityMob{
	private static final UUID cancelMovementModifierUUID = UUID.fromString("7107DE5E-7CE8-4030-940E-514C1F160890");
	private static final AttributeModifier cancelMovement = (new AttributeModifier(cancelMovementModifierUUID,"Movement cancellation",-1,2)).setSaved(false);
	
	private byte attackPhase = 0, explosionTimer = 10, teleportTimer = 80;
	
	public EntityMobEnderGuardian(World world){
		super(world);
		setSize(1.5F,3.2F);
	}
	
	@Override
	protected Entity findPlayerToAttack(){
		EntityPlayer player = worldObj.getClosestVulnerablePlayerToEntity(this,3D);
		return player != null && canEntityBeSeen(player)?player:null;
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(ModCommonProxy.opMobs?1.1D:0.98D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(ModCommonProxy.opMobs?100D:80D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(ModCommonProxy.opMobs?13D:8D);
	}
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		
		if (!worldObj.isRemote && !isDead){
			getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(cancelMovement);
			
			if (entityToAttack != null){
				if (attackPhase == 0){
					if (getDistanceToEntity(entityToAttack) < 6D){
						attackPhase = 1;
						teleportTimer += 5;
					}
					else if (--explosionTimer < 0){
						DragonUtil.createExplosion(this,entityToAttack.posX+(rand.nextDouble()-0.5D)*0.8D,entityToAttack.posY+0.1D,entityToAttack.posZ+(rand.nextDouble()-0.5D)*0.8D,1.1F+rand.nextFloat()*0.1F,false);
						attackPhase = 1;
						explosionTimer = 10;
						
						for(EntityPlayer observer:ObservationUtil.getAllObservers(entityToAttack,12D))KnowledgeRegistrations.ENDER_GUARDIAN.tryUnlockFragment(observer,0.6F,new short[]{ 0,1,2 });
					}
					else if (explosionTimer == 8)playSound("mob.endermen.portal",3.8F,3.4F+rand.nextFloat()*0.2F);
					
					getEntityAttribute(SharedMonsterAttributes.movementSpeed).applyModifier(cancelMovement);
				}
				else if (attackPhase == 1){
					if ((teleportTimer -= rand.nextInt(3)) < 0){
						teleportTimer = 50;
						
						double xx,zz,ang,len;
						
						for(int attempt = 0,ix,iz; attempt < 300; attempt++){
							ang = rand.nextDouble()*Math.PI*2D;
							len = 9D+rand.nextDouble()*6D;
							
							xx = entityToAttack.posX+Math.cos(ang)*len;
							zz = entityToAttack.posZ+Math.sin(ang)*len;
							
							ix = (int)Math.floor(xx);
							iz = (int)Math.floor(zz);
							
							for(int iy = (int)Math.floor(entityToAttack.posY)-1; iy < entityToAttack.posY+3; iy++){
								if (worldObj.isAirBlock(ix,iy,iz) && worldObj.isAirBlock(ix,iy+1,iz)){
									playSound("mob.endermen.portal",1.5F,1.2F);
									PacketPipeline.sendToAllAround(this,64D,new C22ParticleEnderGuardianTeleportation(this));
									
									setPosition(xx,iy,zz);
									faceEntity(entityToAttack,360F,360F);
									
									attackPhase = 0;
									teleportTimer = 90;
									attempt = 999;
									break;
								}
							}
						}
					}
					else{
						double dist = getDistanceToEntity(entityToAttack);
						if (dist > 4D)setMoveForward((float)getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
						else entityToAttack.attackEntityFrom(DamageSource.causeMobDamage(this),(float)getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
					}
				}
			}
			else{
				attackPhase = 0;
				explosionTimer = 10;
				teleportTimer = 80;
        
				if (rand.nextInt(30) == 0){
					List players = worldObj.getEntitiesWithinAABB(EntityPlayer.class,boundingBox.expand(8D,3D,8D));
					for(Object o:players){
						EntityPlayer player = (EntityPlayer)o;

						for(int a = 0; a < player.inventory.getSizeInventory(); a++){
							ItemStack is = player.inventory.getStackInSlot(a);
							if (is != null && is.getItem() == ItemList.enderman_relic){
								entityToAttack = player;
								KnowledgeRegistrations.ENDER_GUARDIAN.tryUnlockFragment(player,1F,new short[]{ 1 });
								break;
							}
						}

						if (entityToAttack != null)break;
					}
				}
			}
		}
	}
	
	@Override
	protected void dropFewItems(boolean recentlyHit, int looting){
		int amount = rand.nextInt(2+looting)-rand.nextInt(2);
		for(int a = 0; a < amount; a++)dropItem(Items.ender_pearl,1);
		
		amount = 1+rand.nextInt(3+(looting>>1));
		for(int a = 0; a < amount; a++)dropItem(Item.getItemFromBlock(Blocks.obsidian),1);
		
		for(EntityPlayer observer:ObservationUtil.getAllObservers(this,8D))KnowledgeRegistrations.ENDER_GUARDIAN.tryUnlockFragment(observer,0.42F,new short[]{ 3,4 });
	}
	
	@Override
	public void addVelocity(double xVelocity, double yVelocity, double zVelocity){
		double mp = rand.nextInt(5) == 0?0D:0.8D+rand.nextDouble()*0.2D;
		super.addVelocity(xVelocity*mp,yVelocity*mp,zVelocity*mp);
	}
	
	@Override
	public String getCommandSenderName(){
		return StatCollector.translateToLocal("entity.enderGuardian.name");
	}
}
