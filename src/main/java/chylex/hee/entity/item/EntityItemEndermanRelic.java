package chylex.hee.entity.item;
/*import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.boss.EntityBossEnderDemon;
import chylex.hee.entity.mob.EntityMobAngryEnderman;
import chylex.hee.entity.weather.EntityWeatherLightningBoltSafe;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.misc.ApocalypseEvents;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C23EnderDemonScreechEffect;
import chylex.hee.system.savedata.ApocalypseSavefile;
import chylex.hee.system.savedata.WorldData;

public class EntityItemEndermanRelic extends EntityItem{
	private byte pickupCheck = 1;
	
	public EntityItemEndermanRelic(World world){
		super(world);
		delayBeforeCanPickup = 5;
	}
	
	public EntityItemEndermanRelic(World world, double x, double y, double z, int meta){
		super(world,x,y,z,new ItemStack(ItemList.enderman_relic_shattered,1,meta));
		delayBeforeCanPickup = 5;
	}
	
	@Override
	public void onUpdate(){
		onEntityUpdate();
		
		if (ApocalypseEvents.isEnabled && --pickupCheck < 0){
			if (new ApocalypseSavefile(WorldData.get(worldObj)).getApocalypseStage() >= ApocalypseSavefile.WAITING_FOR_NOON)delayBeforeCanPickup = 105;
			pickupCheck = 100;
		}

		if (delayBeforeCanPickup > 0)--delayBeforeCanPickup;
		
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		
		++age;

		ItemStack item = dataWatcher.getWatchableObjectItemStack(10);
		if (item != null && item.stackSize <= 0)setDead();
		
		if (worldObj.isRemote && rand.nextInt(20) == 0){
			int ix = (int)Math.floor(posX),iy = (int)Math.floor(posY)-1+rand.nextInt(5),iz = (int)Math.floor(posZ),rx,ry,rz;
			for(int attempt = 0; attempt < 3; attempt++){
				rx = ix;
				ry = iy;
				rz = iz;
			
				if (rand.nextBoolean()){
					ix += rand.nextBoolean()?4:-4;
					iz += rand.nextInt(7)-3;
				}
				else{
					ix += rand.nextInt(7)-3;
					iz += rand.nextBoolean()?4:-4;
				}
				
				for(int yAttempt = 0; yAttempt < 5; yAttempt++){
					iy = (int)Math.floor(posY)-1+rand.nextInt(5);
					
					if (worldObj.getBlock(ix,iy,iz) == Blocks.bookshelf){
						HardcoreEnderExpansion.proxy.spawnCustomParticle("relicglyph",worldObj,ix+0.5D,iy+0.5D,iz+0.5D,posX,posY+0.2D,posZ);
						break;
					}
				}
			}
		}
	}
	
	@Override
	public boolean combineItems(EntityItem item){
		return false;
	}
	
	@Override
	public boolean isEntityInvulnerable(){
		return true;
	}
	
	@Override
	public void setDead(){
		super.setDead();
		
		if (worldObj.isRemote)return;
		
		if (!ApocalypseEvents.isEnabled){
			EntityPlayer player = worldObj.getClosestPlayerToEntity(this,12D);
			if (player == null)return;
			
			KnowledgeRegistrations.ENDERMAN_RELIC.tryUnlockFragment(player,1F,new short[]{ 0,1,2 });
			worldObj.spawnEntityInWorld(new EntityBossEnderDemon(worldObj,posX,276D,posZ));
			
			doEffects(player);
			return;
		}
		
		ApocalypseSavefile save = new ApocalypseSavefile(WorldData.get(worldObj));
		
		if (save.getApocalypseStage() <= ApocalypseSavefile.WAITING_FOR_OVERWORLD){
			EntityPlayer player = worldObj.getClosestPlayerToEntity(this,12D);
			
			if (player != null && save.addStartingPlayer(player.getCommandSenderName())){
				save.setApocalypseStage(ApocalypseSavefile.WAITING_FOR_OVERWORLD);
				ApocalypseEvents.reloadCache();
				
				MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText(
					EnumChatFormatting.DARK_PURPLE+"The ground shakes, and scream of a distant creature echoes through the air..."
				));
				
				doEffects(player);
			}
		}
	}
	
	private void doEffects(EntityPlayer player){
		PacketPipeline.sendToAll(new C23EnderDemonScreechEffect());
		
		worldObj.addWeatherEffect(new EntityWeatherLightningBoltSafe(worldObj,posX,posY,posZ));
		
		byte[] offx = new byte[]{ -5, 5, 0, 0 }, offz = new byte[]{ 0, 0, 5, -5 };
		int ix = (int)Math.floor(posX),iy = (int)Math.floor(posY),iz = (int)Math.floor(posZ);
		
		for(int a = 0; a < 4; a++){
			if (worldObj.isAirBlock(ix+offx[a],iy,iz+offz[a])){
				for(int b = 0; b < 5; b++){
					EntityMobAngryEnderman enderman = new EntityMobAngryEnderman(worldObj,ix+offx[a],iy-1,iz+offz[a]);
					enderman.setTarget(player);
					worldObj.spawnEntityInWorld(enderman);
				}
				break;
			}
		}
	}
}*/
