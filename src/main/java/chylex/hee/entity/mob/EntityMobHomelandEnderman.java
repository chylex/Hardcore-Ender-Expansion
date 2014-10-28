package chylex.hee.entity.mob;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.api.interfaces.IIgnoreEnderGoo;
import chylex.hee.block.BlockList;
import chylex.hee.entity.fx.FXType;
import chylex.hee.entity.mob.util.IEndermanRenderer;
import chylex.hee.mechanics.misc.HomelandEndermen;
import chylex.hee.mechanics.misc.HomelandEndermen.EndermanTask;
import chylex.hee.mechanics.misc.HomelandEndermen.HomelandRole;
import chylex.hee.mechanics.misc.HomelandEndermen.OvertakeGroupRole;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C22EffectLine;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.biome.feature.island.StructureEndermanStash;

public class EntityMobHomelandEnderman extends EntityMob implements IEndermanRenderer, IIgnoreEnderGoo{
	private static final UUID attackingSpeedBoostModifierUUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final AttributeModifier attackingSpeedBoostModifier = (new AttributeModifier(attackingSpeedBoostModifierUUID,"Attacking speed boost",6.2D,0)).setSaved(false);
    
	private HomelandRole homelandRole;
	private long groupId = -1;
	private OvertakeGroupRole overtakeGroupRole;
	
	private EndermanTask currentTask = EndermanTask.NONE;
	private int currentTaskTimer;
	private Object currentTaskData;
	
	private Entity lastEntityToAttack;
	private Boolean prevTeleportAttempt;
	
	private byte stareTimer, fallTimer, randomTpTimer, attackTpTimer, screamTimer, recruitCooldown;
	public byte attackedRecentlyTimer;
	
	public EntityMobHomelandEnderman(World world){
		super(world);
		setSize(0.6F,2.9F);
        stepHeight = 1.0F;
	}
	
	@Override
	protected void entityInit(){
        super.entityInit();
        dataWatcher.addObject(16,Byte.valueOf((byte)0));
        dataWatcher.addObjectByDataType(17,5);
        dataWatcher.addObject(18,Byte.valueOf((byte)0));
    }
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3D);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(7D);
		updateAttributes();
	}
	
	private void updateAttributes(){
		if (homelandRole == null)return;
		
		switch(homelandRole){
			case ISLAND_LEADERS:
				boolean maxHealth = MathUtil.floatEquals(getHealth(),getMaxHealth());
				getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(70D);
				getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(12D);
				if (maxHealth)setHealth(getMaxHealth());
				break;
				
			case GUARD:
				getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(15D);
				break;
				
			case BUSINESSMAN:
				getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.65D);
				break;
				
			case WORKER:
				getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.45D);
				// fall through
				
			case INTELLIGENCE:
				getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(5D);
				break;
				
			default:
		}
	}
	
	@Override
	public void onLivingUpdate(){
		entityAge = 0; // 5 seconds and mobs stop moving, this is fucking stupid
		
		if (worldObj.isRemote){
			refreshRoles();
			
			int chance = 1+(int)Math.floor(HardcoreEnderExpansion.proxy.getClientSidePlayer().getDistanceToEntity(this)/20F);
			
			if (rand.nextInt(chance) == 0){
				float colFactor = rand.nextFloat()*0.6F+0.4F;
				HardcoreEnderExpansion.fx.portalColor(worldObj,posX+(rand.nextDouble()-0.5D)*width,posY+rand.nextDouble()*height-0.25D,posZ+(rand.nextDouble()-0.5D)*width,(rand.nextDouble()-0.5D)*2D,-rand.nextDouble(),(rand.nextDouble()-0.5D)*2D,colFactor*0.9F,colFactor*0.3F,colFactor);
				
				if (homelandRole != null && rand.nextInt(3) == 0){
					HardcoreEnderExpansion.fx.portalColor(worldObj,posX+(rand.nextDouble()-0.5D)*width,posY+rand.nextDouble()*height-0.25D,posZ+(rand.nextDouble()-0.5D)*width,(rand.nextDouble()-0.5D)*2D,-rand.nextDouble(),(rand.nextDouble()-0.5D)*2D,homelandRole.red,homelandRole.green,homelandRole.blue);
				}
				
				if (overtakeGroupRole != null && rand.nextInt(7) == 0){
					HardcoreEnderExpansion.fx.portalColor(worldObj,posX+(rand.nextDouble()-0.5D)*width,posY+rand.nextDouble()*height-0.25D,posZ+(rand.nextDouble()-0.5D)*width,(rand.nextDouble()-0.5D)*2D,-rand.nextDouble(),(rand.nextDouble()-0.5D)*2D,0.3F,0.3F,0.3F);
				}
			}
		}
		else if (isEntityAlive()){
			if (isWet())attackEntityFrom(DamageSource.drown,1F);
			
			if (isWet() || isBurning()){
				setTarget(null);
				setScreaming(false);
			}
			
			if (!onGround && ++fallTimer > 17+rand.nextInt(15)){
				fallTimer = 0;
				
				for(int attempt = 0; attempt < 500; attempt++){
					if (teleportRandomly(48D)){
						resetTask();
						break;
					}
				}
			}
			
			prevTeleportAttempt = null;
			
			if (attackedRecentlyTimer > 0)attackedRecentlyTimer -= rand.nextInt(2);
			
			long overtakeGroup = HomelandEndermen.getOvertakeGroup(this);
			
			if (currentTask == EndermanTask.WAIT || currentTask == EndermanTask.GET_TNT){
				moveForward = moveStrafing = 0F;
				fallDistance = 0F;
				fallTimer = 0;
				posY = 10000D;
				
				if (--currentTaskTimer == 0){
					int tpX, tpY, tpZ;
					
					for(int attempt = 0; attempt < 100; attempt++){
						tpX = (int)Math.floor(posX)+rand.nextInt(40)-20;
						tpZ = (int)Math.floor(posZ)+rand.nextInt(40)-20;
						tpY = worldObj.getTopSolidOrLiquidBlock(tpX,tpZ)-1;
						
						if (worldObj.getBlock(tpX,tpY,tpZ) == BlockList.end_terrain){
							teleportTo(tpX+0.3D+rand.nextDouble()*0.4D,tpY+1D,tpZ+0.3D+rand.nextDouble()*0.4D,true);
							
							if (overtakeGroupRole == OvertakeGroupRole.CHAOSMAKER){
								setCarrying(new ItemStack(Blocks.tnt));
							}
							else if (homelandRole == HomelandRole.COLLECTOR && rand.nextInt(4) != 0){
								setCarrying(new ItemStack(StructureEndermanStash.getRandomBlock(rand)));
							}
							
							break;
						}
					}
					
					resetTask();
				}
			}
			
			if (overtakeGroup == -1){ // calm situation
				if (currentTask != EndermanTask.NONE && currentTask != EndermanTask.WAIT){ // has a task
					--currentTaskTimer;
					
					if (currentTask == EndermanTask.LISTEN_TO_RECRUITER || currentTask == EndermanTask.RECRUIT_TO_GROUP){
						moveForward = moveStrafing = 0F;
						
						if (currentTaskTimer == 0 && currentTask == EndermanTask.RECRUIT_TO_GROUP){
							int chance = 50, reportChance = 10;
							EntityMobHomelandEnderman target = (EntityMobHomelandEnderman)currentTaskData;
							
							switch(target.homelandRole){
								case WORKER: chance = 30; break;
								case GUARD: chance = 15; reportChance = 35; break;
								case INTELLIGENCE: chance = 80; reportChance = 22; break;
								case BUSINESSMAN: chance = 40; reportChance = 14; break;
								default:
							}
							
							if (rand.nextInt(100) < chance){
								target.setGroupMember(groupId,OvertakeGroupRole.getRandomMember(rand));
								
								for(int attempt = 0; attempt < 50; attempt++){
									if (teleportRandomly())break;
								}
								
								//System.out.println("recruiting successful!");
							}
							else if (rand.nextInt(100) < reportChance){
								boolean escaped = false;
								double oldX = posX, oldY = posY, oldZ = posZ;
								
								if (rand.nextInt(5) != 0){
									for(int attempt = 0; attempt < 20; attempt++){
										if (teleportRandomly()){
											escaped = true;
											break;
										}
									}
								}
								
								List<EntityMobHomelandEnderman> guards = HomelandEndermen.getByHomelandRole(this,HomelandRole.GUARD);
								//System.out.println("guards alerted!");
								
								for(int a = 0, amt = Math.max(3,(int)Math.round(guards.size()*0.3D)); a < amt; a++){
									EntityMobHomelandEnderman guard = guards.get(rand.nextInt(guards.size()));
									guard.setScreaming(true);
									
									if (!escaped)guard.setTarget(this);
									else if (rand.nextInt(4) != 0)guard.teleportTo(oldX+4D*(rand.nextDouble()-0.5D),oldY+2D+rand.nextDouble()*4D,oldZ+4D*(rand.nextDouble()-0.5D));
								}
							}
							
							recruitCooldown = 120;
						}
					}
					else if (currentTask == EndermanTask.STROLL){
						if (currentTaskTimer > 0 && currentTaskTimer < 20 && rand.nextInt(3) == 0){
							Vec3 obj = (Vec3)currentTaskData;
							
							if ((obj.distanceTo(Vec3.createVectorHelper(posX,posY,posZ)) <= 0.5D || rand.nextInt(10) == 0) && randomTpTimer > 30){
								for(int attempt = 0; attempt < 30; attempt++){
									if (teleportRandomly(48D)){
										currentTaskData = Vec3.createVectorHelper(posX,posY,posZ);
										randomTpTimer -= 40+rand.nextInt(30);
										break;
									}
								}
							}
						}
						
						if (currentTaskTimer == 0 && rand.nextInt(5) == 0){
							currentTaskTimer = 10+rand.nextInt(60);
							//System.out.println("leader waiting...");
						}
					}
					else if (currentTask == EndermanTask.WALK){
						ChunkPosition pos = (ChunkPosition)currentTaskData;
						
						if (MathUtil.distance(posX-pos.chunkPosX,posY-pos.chunkPosY,posZ-pos.chunkPosZ) < 3D)resetTask();
					}
					else if (currentTask == EndermanTask.COMMUNICATE){
						moveForward = moveStrafing = 0F;
					}
					
					if (currentTaskTimer <= 0)resetTask();
				}
				else if (entityToAttack == null){ // no task, not attacking
					if (groupId != -1 && rand.nextInt(400) == 0 && (recruitCooldown < -120 || --recruitCooldown < -120)){
						List<EntityMobHomelandEnderman> total = HomelandEndermen.getAll(this);
						int groupAmt = HomelandEndermen.getInSameGroup(this).size();
						int totalAmt = total.size();
						
						if (totalAmt > 8 && (rand.nextInt(5) <= 2 || groupAmt < (totalAmt>>2)+rand.nextInt(totalAmt>>3)-rand.nextInt(4))){
							for(int attempt = 0; attempt < 5; attempt++){
								EntityMobHomelandEnderman enderman = total.get(rand.nextInt(totalAmt));
								
								if (enderman == this || enderman.groupId != -1 || !enderman.onGround || enderman.homelandRole == HomelandRole.ISLAND_LEADERS || enderman.getDistanceToEntity(this) > 80D)continue;
								if (groupAmt > 3 && ((enderman.homelandRole == HomelandRole.GUARD || enderman.homelandRole == HomelandRole.WORKER) && rand.nextInt(5) != 0))continue;
								
								boolean tp = false;
								
								for(int tpAttempt = 0; tpAttempt < 30; tpAttempt++){
									if (teleportTo(enderman.posX+(rand.nextDouble()-0.5D)*2D,enderman.posY,enderman.posZ+(rand.nextDouble()-0.5D)*2D)){
										tp = true;
										break;
									}
								}
								
								if (!tp)continue;
								
								currentTask = EndermanTask.RECRUIT_TO_GROUP;
								currentTaskData = enderman;
								enderman.currentTask = EndermanTask.LISTEN_TO_RECRUITER;
								enderman.currentTaskTimer = currentTaskTimer = 20+rand.nextInt(60);
								//System.out.println("trying to recruit at "+posX+","+posY+","+posZ);
								break;
							}
						}
					}
					
					if (currentTask == EndermanTask.NONE && rand.nextInt(80) == 0){
						switch(homelandRole){
							case ISLAND_LEADERS:
								if (rand.nextInt(13) == 0){
									teleportRandomly();
								}
								else{
									Vec3 look = getLookVec();
									
									for(int attempt = 0, pathX, pathY, pathZ; attempt < 12; attempt++){
										if (attempt > 8 || rand.nextInt(6) == 0)look = Vec3.createVectorHelper(rand.nextDouble()-0.5D,0D,rand.nextDouble()-0.5D).normalize();
										
										pathX = (int)(posX+look.xCoord*16D+(rand.nextDouble()-0.5D)*5D);
										pathZ = (int)(posZ+look.zCoord*16D+(rand.nextDouble()-0.5D)*5D);
										pathY = worldObj.getTopSolidOrLiquidBlock(pathX,pathZ)-1;
										
										if (worldObj.getBlock(pathX,pathY,pathZ) == BlockList.end_terrain && MathUtil.distance(posX-pathX,posY-pathY,posZ-pathZ) > 5D){
											setPathToEntity(worldObj.getEntityPathToXYZ(this,pathX,pathY+1,pathZ,30F,true,false,false,true));
											currentTask = EndermanTask.STROLL;
											currentTaskTimer = 65+rand.nextInt(60);
											currentTaskData = Vec3.createVectorHelper(posX,posY,posZ);
											//System.out.println("leader strolling to "+pathX+","+pathY+","+pathZ);
											break;
										}
									}
								}
								
								break;
								
							case BUSINESSMAN:
								if (rand.nextInt(18) == 0){
									List<EntityMobHomelandEnderman> businessmen = HomelandEndermen.getByHomelandRole(this,HomelandRole.BUSINESSMAN);
									
									if (!businessmen.isEmpty()){
										EntityMobHomelandEnderman enderman = businessmen.get(rand.nextInt(businessmen.size()));
										
										if (enderman.currentTask == EndermanTask.NONE){
											for(int tpAttempt = 0; tpAttempt < 20; tpAttempt++){
												if (teleportTo(enderman.posX+(rand.nextDouble()-0.5D)*2D,enderman.posY,enderman.posZ+(rand.nextDouble()-0.5D)*2D)){
													currentTask = enderman.currentTask = EndermanTask.COMMUNICATE;
													currentTaskTimer = enderman.currentTaskTimer = 30+rand.nextInt(50+rand.nextInt(80));
													//System.out.println("businessman communicating at "+posX+","+posY+","+posZ);
													break;
												}
											}
										}
									}
								}
								else if (rand.nextInt(10) == 0){
									int walkToX, walkToY, walkToZ;
									
									for(int attempt = 0; attempt < 10; attempt++){
										walkToX = (int)posX+rand.nextInt(250)-125;
										walkToZ = (int)posZ+rand.nextInt(250)-125;
										walkToY = worldObj.getTopSolidOrLiquidBlock(walkToX,walkToZ)-1;
										
										if (worldObj.getBlock(walkToX,walkToY,walkToZ) == BlockList.end_terrain){
											setPathToEntity(worldObj.getEntityPathToXYZ(this,walkToX,walkToY+1,walkToZ,100F,true,false,false,true));
											currentTask = EndermanTask.WALK;
											currentTaskTimer = 200+rand.nextInt(100);
											currentTaskData = new ChunkPosition(walkToX,walkToY,walkToZ);
											//System.out.println("businessman walking to "+walkToX+","+walkToY+","+walkToZ);
											break;
										}
									}
								}
								
								break;
								
							case WORKER:
								if (rand.nextInt(270) == 0 && worldObj.getBlock((int)Math.floor(posX),(int)Math.floor(posY)+1,(int)Math.floor(posZ)) != BlockList.ender_goo){
									for(int attempt = 0, tpX, tpY, tpZ; attempt < 50; attempt++){
										tpX = (int)posX+rand.nextInt(70)-35;
										tpZ = (int)posZ+rand.nextInt(70)-35;
										tpY = worldObj.getTopSolidOrLiquidBlock(tpX,tpZ)-1;
										
										if (worldObj.getBlock(tpX,tpY,tpZ) == BlockList.ender_goo){
											teleportTo(tpX+0.5D+(rand.nextDouble()-0.5D)*0.3D,tpY+1D,tpZ+0.5D+(rand.nextDouble()-0.5D)*0.3D,true);
											//System.out.println("worker tp'd to "+tpX+","+tpY+","+tpZ);
											break;
										}
									}
								}
								
								break;
								
							case COLLECTOR:
								if (rand.nextInt(50) == 0 && (!isCarrying() || rand.nextInt(4) == 0)){
									currentTask = EndermanTask.WAIT;
									currentTaskTimer = 150+rand.nextInt(600+rand.nextInt(1800));
									setCarrying(null);
									teleportTo(posX,10000D,posZ,true);
									//System.out.println("collector teleporting");
								}
								
								break;
								
							case OVERWORLD_EXPLORER:
								if (rand.nextInt(200) == 0){
									currentTask = EndermanTask.WAIT;
									currentTaskTimer = 500+rand.nextInt(800)+rand.nextInt(1000)*(1+rand.nextInt(4));
									
									List<EntityMobHomelandEnderman> explorers = HomelandEndermen.getByHomelandRole(this,HomelandRole.OVERWORLD_EXPLORER);
									
									for(int a = 0; a < 1+rand.nextInt(7) && !explorers.isEmpty(); a++){
										if (rand.nextInt(3) == 0)break;
										
										EntityMobHomelandEnderman enderman = explorers.remove(rand.nextInt(explorers.size()));
										if (enderman == this)continue;
										
										enderman.currentTask = EndermanTask.WAIT;
										enderman.currentTaskTimer = currentTaskTimer+rand.nextInt(500);
										enderman.teleportTo(enderman.posX,10000D,enderman.posZ,true);
										//System.out.println("overworld explorer teleporting [multi]");
									}
									
									teleportTo(posX,10000D,posZ,true);
									//System.out.println("overworld explorer teleporting");
								}
								
								break;
								
							default:
						}
					}
				}
			}
			
			if (overtakeGroup != -1){
				if (overtakeGroup == groupId){ // overtaking group
					if (currentTask == EndermanTask.NONE){
						switch(overtakeGroupRole){
							case TELEPORTER:
								if (rand.nextInt(8) == 0)teleportRandomly(12D);
								break;
								
							case CHAOSMAKER:
								if (isCarrying() && getCarrying().getItem() == Item.getItemFromBlock(Blocks.tnt)){
									if (rand.nextInt(50) == 0){
										for(int attempt = 0, xx, yy, zz; attempt < 30; attempt++){
											xx = (int)Math.floor(posX)+rand.nextInt(6)-3;
											yy = (int)Math.floor(posY)+rand.nextInt(3)-1;
											zz = (int)Math.floor(posZ)+rand.nextInt(6)-3;
											
											if (worldObj.getBlock(xx,yy,zz) == BlockList.end_terrain && worldObj.isAirBlock(xx,yy+1,zz)){
												worldObj.spawnEntityInWorld(new EntityTNTPrimed(worldObj,xx+0.5D,yy+1.5D,zz+0.5D,this));
												setCarrying(null);
												break;
											}
										}
									}
								}
								else{
									currentTask = EndermanTask.GET_TNT;
									currentTaskTimer = 30+rand.nextInt(80);
									setPosition(posX,10000D,posZ);
								}
								
								break;
								
							case LEADER:
							case FIGHTER:
								if (!(entityToAttack instanceof EntityMobHomelandEnderman)){
									EntityMobHomelandEnderman target = null;
									
									if (overtakeGroupRole == OvertakeGroupRole.LEADER || rand.nextInt(4) == 0){
										List<EntityMobHomelandEnderman> list = HomelandEndermen.getByHomelandRole(this,HomelandRole.ISLAND_LEADERS);
										if (!list.isEmpty())target = list.get(rand.nextInt(list.size()));
									}
									
									if (target == null){
										List<EntityMobHomelandEnderman> other = HomelandEndermen.getAll(this);
										
										for(int attempt = 0, size = other.size(); attempt < 10; attempt++){
											EntityMobHomelandEnderman potentialTarget = other.get(rand.nextInt(size));
											if (potentialTarget == this || potentialTarget.isInSameGroup(this))continue;
											
											HomelandRole role = potentialTarget.homelandRole;
											if (role == HomelandRole.GUARD && rand.nextInt(3) != 0)target = potentialTarget;
											else if (role != HomelandRole.WORKER || rand.nextInt(5) <= 1)target = potentialTarget;
											
											break;
										}
									}
									
									setTarget(target);
								}
								
								break;
								
							default:
						}
					}
					
					if (!worldObj.getEntitiesWithinAABB(EntityTNTPrimed.class,boundingBox.expand(5D,3D,5D)).isEmpty())teleportRandomly();
				}
				else if (groupId != -1){ // different group - join them or be against them
					if (rand.nextInt(3) == 0)setGroupMember(overtakeGroup,OvertakeGroupRole.getRandomMember(rand));
					else setGroupMember(-1,null);
				}
				else if (entityToAttack == null && ((homelandRole == HomelandRole.GUARD && rand.nextInt(30) == 0) || rand.nextInt(200) == 0)){
					List<EntityMobHomelandEnderman> all = HomelandEndermen.getAll(this);
					
					for(int attempt = 0; attempt < 4 && !all.isEmpty(); attempt++){
						EntityMobHomelandEnderman enderman = all.remove(rand.nextInt(all.size()));
						
						if (enderman.groupId == overtakeGroup){
							setTarget(enderman);
							break;
						}
					}
				}
			}
			
			if (entityToAttack != null){
				++attackTpTimer;
				
				if (((attackTpTimer > 50+rand.nextInt(30) && rand.nextInt(20) == 0) || attackTpTimer > 120) && entityToAttack.getDistanceSqToEntity(this) < 16D){
					teleportRandomly(8D);
					attackTpTimer = -80;
				}
				else if (entityToAttack.getDistanceSqToEntity(this) > 256D && attackTpTimer > 30 && teleportToEntity(entityToAttack)){
					attackTpTimer = -80;
				}
			}
			else{
				attackTpTimer = -80;
				
				if (currentTask == EndermanTask.NONE && entityToAttack == null && ++randomTpTimer > 70+rand.nextInt(50)){
					if (rand.nextInt(19) == 0){
						for(int attempt = 0; attempt < 5; attempt++){
							if (teleportRandomly(10D))break;
						}
					}
					
					randomTpTimer -= 110+rand.nextInt(40);
				}
				
				if (screamTimer > 0 && --screamTimer == 0 && isScreaming())setScreaming(false);
			}
			
			if (lastEntityToAttack != entityToAttack){
				IAttributeInstance attribute = getEntityAttribute(SharedMonsterAttributes.movementSpeed);
				attribute.removeModifier(attackingSpeedBoostModifier);
				if (entityToAttack != null)attribute.applyModifier(attackingSpeedBoostModifier);
				
				lastEntityToAttack = entityToAttack;
			}
		}
		
		isJumping = false;

		if (entityToAttack != null)faceEntity(entityToAttack,100F,100F);
		
		super.onLivingUpdate();
	}
	
	@Override
	protected Entity findPlayerToAttack(){
		if (worldObj.isRemote)return null;
		
		List<EntityPlayerMP> players = worldObj.playerEntities;
		
		for(EntityPlayerMP player:players){
			if (!player.capabilities.disableDamage && getDistanceSqToEntity(player) <= 4096D){
				if (isPlayerStaringIntoEyes(player)){
					if (!shouldActHostile(player) && teleportRandomly(10D))return null;
					
					if (stareTimer == 0)worldObj.playSoundEffect(posX,posY,posZ,"mob.endermen.stare",1F,1F);
					
					if (stareTimer++ == 5){
						stareTimer = 0;
						setScreaming(true);
						setTarget(player);
						return null;
					}
				}
				else stareTimer = 0;
			}
		}
		
		return null;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (isEntityInvulnerable())return false;
		
		attackedRecentlyTimer = 120;
		
		if (worldObj.difficultySetting == EnumDifficulty.PEACEFUL && source.getEntity() instanceof EntityPlayer){
			if (super.attackEntityFrom(DamageSource.generic,amount)){
				double diffX = source.getEntity().posX-posX, diffZ = source.getEntity().posZ-posZ;

				while(diffX*diffX+diffZ*diffZ < 1E-4D){
					diffX = (Math.random()-Math.random())*0.01D;
					diffZ = (Math.random()-Math.random())*0.01D;
				}

				attackedAtYaw = (float)(Math.atan2(diffZ,diffX)*180D/Math.PI)-rotationYaw;
				knockBack(source.getEntity(),amount,diffX,diffZ);
				
				if (homelandRole == HomelandRole.ISLAND_LEADERS || homelandRole == HomelandRole.GUARD || rand.nextInt(3) == 0){
					for(int attempt = 0; attempt < 10; attempt++){
						if (teleportRandomly())break;
					}
				}
				
				return true;
			}
			else return false;
		}
		
		if (source.getEntity() instanceof EntityPlayer || source.getEntity() instanceof EntityMobHomelandEnderman){
			boolean callGuards = entityToAttack == null || rand.nextInt(4) == 0;
			
			setTarget(source.getEntity());
			setScreaming(true);
			
			if (callGuards){
				float guardPerc = 0F, guardDist = 0F;
				
				switch(homelandRole){
					case ISLAND_LEADERS: guardPerc = 0.95F; guardDist = 260F; break;
					case GUARD: guardPerc = 0.4F; guardDist = 100F; break;
					case WORKER: guardPerc = rand.nextFloat()*0.2F; guardDist = 30F; break;
					case BUSINESSMAN:
					case INTELLIGENCE: guardPerc = 0.1F+rand.nextFloat()*0.15F; guardDist = 60F; break;
					case COLLECTOR:
					case OVERWORLD_EXPLORER: guardPerc = 0.2F+rand.nextFloat()*0.1F; guardDist = 80F; break;
					default:
				}
				
				guardPerc *= 0.7F+rand.nextFloat()*0.3F;
				
				List<EntityMobHomelandEnderman> list = worldObj.getEntitiesWithinAABB(EntityMobHomelandEnderman.class,boundingBox.expand(guardDist,128D,guardDist));
				
				for(Iterator<EntityMobHomelandEnderman> iter = list.iterator(); iter.hasNext();){
					if (iter.next().homelandRole != HomelandRole.GUARD)iter.remove();
				}
				
				for(int a = 0, amt = Math.max(2,Math.round(list.size()*guardPerc)); a < amt && !list.isEmpty(); a++){
					EntityMobHomelandEnderman guard = list.remove(rand.nextInt(list.size()));
					guard.setTarget(source.getEntity());
					guard.setScreaming(true);
				}
			}
		}
		else if (source instanceof EntityDamageSourceIndirect){
			for(int attempt = 0; attempt < 64; attempt++){
				if (teleportRandomly()){
					setTarget(null);
					setScreaming(false);
					return true;
				}
			}
		}
		
		return super.attackEntityFrom(source,amount);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setByte("homelandRole",(byte)homelandRole.ordinal());
		nbt.setLong("groupId",groupId);
		if (groupId != -1)nbt.setByte("groupRole",(byte)overtakeGroupRole.ordinal());
		
		if (currentTask == EndermanTask.WAIT)nbt.setInteger("wait",currentTaskTimer);
		else if (currentTask == EndermanTask.GET_TNT)nbt.setInteger("gettnt",currentTaskTimer);
		
		ItemStack carrying = getCarrying();
		if (carrying != null)nbt.setTag("carrying",carrying.writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		
		byte homelandRoleId = nbt.getByte("homelandRole");
		homelandRole = homelandRoleId >= 0 && homelandRoleId < HomelandRole.values.length ? HomelandRole.values[homelandRoleId] : null;
		
		if ((groupId = nbt.getLong("groupId")) != -1){
			byte groupRoleId = nbt.getByte("groupRole");
			overtakeGroupRole = groupRoleId >= 0 && groupRoleId < OvertakeGroupRole.values.length ? OvertakeGroupRole.values[groupRoleId] : null;
		}
		
		if (groupId != -1 && overtakeGroupRole == null){
			Log.warn("Homeland Enderman overtake group role is null, resetting group membership.");
			groupId = -1;
		}
		
		if (nbt.hasKey("wait")){
			currentTask = EndermanTask.WAIT;
			currentTaskTimer = nbt.getInteger("wait");
			posY = 10000D;
		}
		else if (nbt.hasKey("gettnt")){
			currentTask = EndermanTask.GET_TNT;
			currentTaskTimer = nbt.getInteger("gettnt");
			posY = 10000D;
		}
		
		if (nbt.hasKey("carrying"))setCarrying(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("carrying")));
		
		if (homelandRole == null || (groupId != -1 && overtakeGroupRole == null))setDead();
		else if (!worldObj.isRemote)refreshRoles();
		
		updateAttributes();
	}
	
	@Override
	public void setWorld(World world){
		super.setWorld(world);
		refreshRoles();
	}
	
	@Override
	public void setTarget(Entity target){
		if (worldObj.difficultySetting == EnumDifficulty.PEACEFUL && target instanceof EntityPlayer)return;
		
		super.setTarget(target);
		
		if (entityToAttack != null){
			resetTask();
			setPathToEntity(worldObj.getPathEntityToEntity(this,entityToAttack,16F,true,false,false,true));
		}
		
		//System.out.println("attacking "+target);
	}
	
	private void resetTask(){
		currentTask = EndermanTask.NONE;
		currentTaskTimer = 0;
		currentTaskData = null;
	}
	
	@Override
	public String getCommandSenderName(){
		return StatCollector.translateToLocal("entity.homelandEnderman.name");
	}
	
	@Override
	protected void despawnEntity(){}
	
	// LOGIC HANDLING
	
	private boolean canTeleport(){
		if (HomelandEndermen.isOvertakeHappening(this))return HomelandEndermen.getOvertakeGroup(this) == groupId || rand.nextInt(100) > 30+rand.nextInt(50)+12*HomelandEndermen.getByGroupRole(this,OvertakeGroupRole.TELEPORTER).size();
		else return true;
	}
	
	private boolean shouldActHostile(Entity entity){
		if (worldObj.difficultySetting == EnumDifficulty.PEACEFUL && entity instanceof EntityPlayer)return false;
		else if (homelandRole == HomelandRole.ISLAND_LEADERS || (groupId != -1 && rand.nextInt(5) != 0) || HomelandEndermen.isOvertakeHappening(this))return false;
		else return rand.nextInt(3) != 0;
	}
	
	// GETTERS, SETTERS AND DATA WATCHER
	
	public void refreshRoles(){
		if (worldObj == null)return;
		
		if (!worldObj.isRemote){
			int data = (homelandRole.ordinal() & 0b1111) << 4;
			if (overtakeGroupRole != null)data |= ((overtakeGroupRole.ordinal()+1) & 0b1111);
			dataWatcher.updateObject(18,Byte.valueOf((byte)data));
		}
		else{
			byte data = dataWatcher.getWatchableObjectByte(18);
			homelandRole = HomelandRole.values[(data >> 4) & 0b1111];
			if ((data & 0b1111) > 0)overtakeGroupRole = OvertakeGroupRole.values[(data & 0b1111)-1];
		}
	}
	
	public void setHomelandRole(HomelandRole role){
		this.homelandRole = role;
		refreshRoles();
		updateAttributes();
	}
	
	public HomelandRole getHomelandRole(){
		return homelandRole;
	}
	
	public long setNewGroupLeader(){
		if (homelandRole == HomelandRole.ISLAND_LEADERS)return -1L;
		this.overtakeGroupRole = OvertakeGroupRole.LEADER;
		return this.groupId = UUID.randomUUID().getLeastSignificantBits();
	}
	
	public void setGroupMember(long groupId, OvertakeGroupRole role){
		this.groupId = groupId;
		this.overtakeGroupRole = role;
		refreshRoles();
	}
	
	public OvertakeGroupRole getGroupRole(){
		return overtakeGroupRole;
	}
	
	public long getGroupId(){
		return groupId;
	}
	
	public boolean isInSameGroup(EntityMobHomelandEnderman enderman){
		return groupId == enderman.groupId;
	}
	
	public void setCarrying(ItemStack is){
		dataWatcher.updateObject(17,is);
	}

	@Override
	public boolean isCarrying(){
		return getCarrying() != null;
	}
	
	@Override
	public ItemStack getCarrying(){
		return dataWatcher.getWatchableObjectItemStack(17);
	}
	
	public void setScreaming(boolean isScreaming){
		dataWatcher.updateObject(16,Byte.valueOf((byte)(isScreaming ? 1 : 0)));
		screamTimer = isScreaming ? (byte)(60+rand.nextInt(50)) : 0;
	}

	@Override
	public boolean isScreaming(){
		return dataWatcher.getWatchableObjectByte(16) == 1;
	}
	
	// ENDERMAN METHODS
	
	private boolean teleportRandomly(){
		return teleportTo(posX+(rand.nextDouble()-0.5D)*64D,posY+(rand.nextInt(64)-32),posZ+(rand.nextDouble()-0.5D)*64D);
	}
	
	private boolean teleportRandomly(double maxDist){
		return teleportTo(posX+(rand.nextDouble()-0.5D)*2D*maxDist,posY+(rand.nextInt((int)maxDist)-maxDist*0.5D),posZ+(rand.nextDouble()-0.5D)*2D*maxDist);
	}

	private boolean teleportToEntity(Entity entity){
		Vec3 vec = Vec3.createVectorHelper(posX-entity.posX,boundingBox.minY+(height/2F)-entity.posY+entity.getEyeHeight(),posZ-entity.posZ).normalize();
		double newX = posX+(rand.nextDouble()-0.5D)*8D-vec.xCoord*16D;
		double newY = posY+(rand.nextInt(16)-8)-vec.yCoord*16D;
		double newZ = posZ+(rand.nextDouble()-0.5D)*8D-vec.zCoord*16D;
		return teleportTo(newX,newY,newZ);
	}
	
	private boolean teleportTo(double x, double y, double z){
		return teleportTo(x,y,z,false);
	}

	private boolean teleportTo(double x, double y, double z, boolean ignoreChecks){
		if (prevTeleportAttempt != null)return prevTeleportAttempt.booleanValue();
		else if (!(prevTeleportAttempt = Boolean.valueOf(canTeleport())))return false;
		
		double oldX = posX, oldY = posY, oldZ = posZ;
		posX = x;
		posY = y;
		posZ = z;
		
		boolean hasTeleported = false;
		int ix = MathHelper.floor_double(posX), iy = MathHelper.floor_double(posY), iz = MathHelper.floor_double(posZ);

		if (worldObj.blockExists(ix,iy,iz)){
			boolean foundTopBlock = ignoreChecks;

			while(!foundTopBlock && iy > 0){
				if (worldObj.getBlock(ix,iy-1,iz).getMaterial().blocksMovement())foundTopBlock = true;
				else{
					--posY;
					--iy;
				}
			}

			if (foundTopBlock){
				setPosition(posX,posY,posZ);

				if ((worldObj.getCollidingBoundingBoxes(this,boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox)) || ignoreChecks){
					hasTeleported = true;
				}
			}
		}

		if (!hasTeleported){
			setPosition(oldX,oldY,oldZ);
			return false;
		}
		else{
			if (!worldObj.isRemote)PacketPipeline.sendToAllAround(this,256D,new C22EffectLine(FXType.Line.ENDERMAN_TELEPORT,oldX,oldY,oldZ,posX,posY,posZ));
			return true;
		}
	}
	
	private boolean isPlayerStaringIntoEyes(EntityPlayer player){
		ItemStack is  = player.inventory.armorInventory[3];

		if (is != null && is.getItem() == Item.getItemFromBlock(Blocks.pumpkin))return false;
		else{
			Vec3 playerLook = player.getLook(1F).normalize();
			Vec3 eyeVecDiff = Vec3.createVectorHelper(posX-player.posX,boundingBox.minY+(height*0.5F)-(player.posY+player.getEyeHeight()),posZ-player.posZ);
			double eyeVecLen = eyeVecDiff.lengthVector();
			return playerLook.dotProduct(eyeVecDiff.normalize()) > 1D-0.025D/eyeVecLen && player.canEntityBeSeen(this);
		}
	}
	
	// OVERRIDDEN METHODS
	
	@Override
	protected String getLivingSound(){
		return isScreaming() ? "mob.endermen.scream" : "mob.endermen.idle";
	}

	@Override
	protected String getHurtSound(){
		return "mob.endermen.hit";
	}

	@Override
	protected String getDeathSound(){
		return "mob.endermen.death";
	}

	@Override
	protected Item getDropItem(){
		return Items.ender_pearl;
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting){
		Item item = getDropItem();
		
		if (item != null){
			for(int a = 0, total = rand.nextInt(2+looting); a < total; a++)dropItem(item,1);
		}
	}
	
	// NO DESPAWN ON PEACEFUL, MOSTLY COPIED FROM onUpdate WITH UNIMPORTANT BITS REMOVED
	
	@Override
	public void onUpdate(){
		if (ForgeHooks.onLivingUpdate(this))return;
		onEntityUpdate();
		
		if (!worldObj.isRemote){
			if (ticksExisted%20 == 0)func_110142_aN().func_94549_h();
		}

		onLivingUpdate();
		
		double xDiff = posX-prevPosX, zDiff = posZ-prevPosZ;
		float distanceMoved = (float)(xDiff*xDiff+zDiff*zDiff);
		float yawOffset = renderYawOffset;
		float f2 = 0F, f3 = 0F;
		field_70768_au = field_110154_aX;

		if (distanceMoved > 0.0025F){
			f3 = 1F;
			f2 = (float)Math.sqrt(distanceMoved)*3F;
			yawOffset = (float)Math.atan2(zDiff,xDiff)*180F/(float)Math.PI-90F;
		}
		
		if (swingProgress > 0F)yawOffset = rotationYaw;
		if (!onGround)f3 = 0F;

		field_110154_aX += (f3-field_110154_aX)*0.3F;
		worldObj.theProfiler.startSection("headTurn");
		f2 = func_110146_f(yawOffset,f2);
		worldObj.theProfiler.endSection();
		worldObj.theProfiler.startSection("rangeChecks");

		while(rotationYaw-prevRotationYaw < -180F)prevRotationYaw -= 360F;
		while(rotationYaw-prevRotationYaw >= 180F)prevRotationYaw += 360F;
		while(renderYawOffset-prevRenderYawOffset < -180F)prevRenderYawOffset -= 360F;
		while(renderYawOffset-prevRenderYawOffset >= 180F)prevRenderYawOffset += 360F;
		while(rotationPitch-prevRotationPitch < -180F)prevRotationPitch -= 360F;
		while(rotationPitch-prevRotationPitch >= 180F)prevRotationPitch += 360F;
		while(rotationYawHead-prevRotationYawHead < -180F)prevRotationYawHead -= 360F;
		while(rotationYawHead-prevRotationYawHead >= 180F)prevRotationYawHead += 360F;

		worldObj.theProfiler.endSection();
		field_70764_aw += f2;
		
		if (!worldObj.isRemote)updateLeashedState();
	}
}
