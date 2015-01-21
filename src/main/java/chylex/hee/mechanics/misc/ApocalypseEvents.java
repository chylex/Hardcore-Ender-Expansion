package chylex.hee.mechanics.misc;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;

public final class ApocalypseEvents{
	/*private static ApocalypseEvents instance;
	
	private static final WeightedList<SpawnEntry> spawnList = new WeightedList<>(
		new SpawnEntry(EntityMobEnderman.class,32,100),
		new SpawnEntry(EntityMobAngryEnderman.class,13,23),
		new SpawnEntry(EntityMobEnderGuardian.class,6,5)
	);
	
	public static void register(){
		instance = new ApocalypseEvents();
		FMLCommonHandler.instance().bus().register(instance);
	}
	
	public static void reloadCache(){
		if (instance.save != null){
			WorldServer world = DimensionManager.getWorld(0);
			
			if (!instance.save.isWorldEqual(world))instance.save = new ApocalypseSavefile(WorldData.get(world));
			else instance.save.load();
		}
	}
	
	private ApocalypseSavefile save;
	private boolean isApocalypseRunning = false;
	private int tickTimer;
	
	private ApocalypseEvents(){}
	
	@SubscribeEvent
	public void onServerTick(ServerTickEvent e){
		if (e.phase != Phase.START || ++tickTimer<(isApocalypseRunning || save == null?15:2400))return;
		tickTimer = 0;
		
		WorldServer world = DimensionManager.getWorld(0);
		if (world == null){
			DragonUtil.warning("EndermanpocalypseTicker world is null");
			return;
		}
		
		if (save == null || !save.isWorldEqual(world))save = new ApocalypseSavefile(WorldData.get(world));
		
		byte stage = save.getApocalypseStage();
		
		if (stage == DISABLED)isApocalypseRunning = false;
		else if (stage == WAITING_FOR_OVERWORLD){
			boolean found = false;
			
			for(String username:save.getStartingPlayers()){
				for(Object o:world.playerEntities){
					if (((EntityPlayer)o).getCommandSenderName().equals(username)){
						found = true;
						break;
					}
				}
				if (found)break;
			}
			
			if (found)save.setApocalypseStage(WAITING_FOR_NOON);
		}
		else{
			if (!isApocalypseRunning)isApocalypseRunning = true;
			int worldTime = (int)(world.getWorldTime()%24000);
			
			if (stage == WAITING_FOR_NOON){
				if (worldTime >= 6000 && worldTime <= 6200){
					WorldInfo info = world.getWorldInfo();
					info.setRaining(true);
					info.setThundering(true);
					info.setRainTime(40000+world.rand.nextInt(10000));
					info.setThunderTime(70000);
					save.setApocalypseStage(WAITING_FOR_NIGHT);
				}
			}
			else if (stage == WAITING_FOR_NIGHT){
				doRandomLightning(world,3500,Math.min(500,100+Math.min(10,world.playerEntities.size()>>1)*20+((worldTime-6000)>>4)));
				if (worldTime >= 12500 && worldTime <= 12700)world.getWorldInfo().setRaining(false);
				else if (worldTime >= 13500)save.setApocalypseStage(APOCALYPSE_RUNNING);
			}
			else if (stage == APOCALYPSE_RUNNING){
				doRandomLightning(world,2500,Math.min(400,100+Math.min(10,world.playerEntities.size()>>1)*20));
				
				int sz = world.playerEntities.size();
				if (sz == 0)return;
				
				if (world.getDifficulty().getDifficultyId() > 0 && world.rand.nextFloat() < 0.45F){
					EntityPlayer player = (EntityPlayer)world.playerEntities.get(world.rand.nextInt(sz));
					
					SpawnEntry spawnEntry = spawnList.getRandomItem(world.rand);
					if (spawnEntry != null && world.getEntitiesWithinAABB(spawnEntry.getMobClass(),player.boundingBox.expand(52D,256D,52D)).size() < spawnEntry.getMaxAmount()){
						double ang = world.rand.nextDouble()*Math.PI*2D,
							   dist = Math.abs(world.rand.nextGaussian()*40D);
						
						if (dist > 10D){
							EntityLiving entity = spawnEntry.createMob(world);
							entity.setLocationAndAngles(player.posX+Math.cos(ang)*dist,270,player.posZ+Math.sin(ang)*dist,world.rand.nextFloat()*360F,0F);
							entity.func_110163_bv();
							entity.hurtResistantTime = 2000;
							world.spawnEntityInWorld(entity);
						}
					}
				}
				
				if (worldTime > 22000){
					GameRules gr = world.getGameRules();
					save.saveDaylightRuleState(gr);
					gr.setOrCreateGameRule("doDaylightCycle","false");
					
					WorldInfo info = world.getWorldInfo();
					world.spawnEntityInWorld(new EntityBossEnderDemon(world,info.getSpawnX()+(world.rand.nextDouble()-0.5D)*80D,276D,info.getSpawnZ()+(world.rand.nextDouble()-0.5D)*80D));
					
					MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText(
						EnumChatFormatting.DARK_PURPLE+"Creature from a different world is descending from the sky, ready to threaten the human newborns..."
					));
					PacketPipeline.sendToAll(new C23EnderDemonScreechEffect());
					
					for(int a = 0; a < 5; a++)doRandomLightning(world,1,0);
					save.setApocalypseStage(APOCALYPSE_BOSS);
				}
			}
			else if (stage == APOCALYPSE_BOSS){}
			else if (stage == APOCALYPSE_BOSS_DEAD){
				save.restoreDaylightRuleState(world.getGameRules());
				world.getWorldInfo().setThundering(false);
				save.setApocalypseStage(DISABLED);
			}
		}
	}
	
	private void doRandomLightning(World world, int chanceRange, int chance){
		if (world.rand.nextInt(chanceRange) >= chance || world.playerEntities.isEmpty())return;
		
		EntityPlayer randPlayer = (EntityPlayer)world.playerEntities.get(world.rand.nextInt(world.playerEntities.size()));
		double x = randPlayer.posX+world.rand.nextGaussian()*70D,
			   z = randPlayer.posZ+world.rand.nextGaussian()*70D,
			   y = world.getPrecipitationHeight(MathUtil.floor(x),MathUtil.floor(z));
		
		if (world.rand.nextInt(5) != 0 && MathUtil.distance(x-randPlayer.posX,z-randPlayer.posZ) < 40D)return;
		
		if (world.rand.nextInt(8) == 0)world.addWeatherEffect(new EntityLightningBolt(world,x,y,z));
		else world.addWeatherEffect(new EntityWeatherLightningBoltSafe(world,x,y,z));
	}*/
	
	/*public static boolean checkEndermanpocalypseStructure(World world, int x, int y, int z){
		for(int xx = x-1; xx <= x+1; xx++){
			for(int zz = z-1; zz <= z+1; zz++){
				if (world.getBlock(xx,y-1,zz) != BlockList.obsidian_special || world.getBlockMetadata(xx,y-1,zz) != 0)return false;
			}
		}
		
		for(int a = 0; a < 2; a++){
			for(int b = 0; b < 2; b++){
				for(int yy = y; yy <= y+4; yy++){
					if (world.getBlock(x-3+6*a,yy,z-3+6*b) != BlockList.obsidian_special || world.getBlockMetadata(x-3+6*a,yy,z-3+6*b) != (yy == y+4 ? 1 : 2))return false;
				}
			}
		}
		
		return true;
	}*/
}
