package chylex.hee.mechanics.compendium.events;
import gnu.trove.map.hash.TObjectByteHashMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.objects.ObjectBlock;
import chylex.hee.mechanics.compendium.objects.ObjectBlock.BlockMetaWrapper;
import chylex.hee.mechanics.compendium.objects.ObjectItem;
import chylex.hee.mechanics.compendium.objects.ObjectMob;
import chylex.hee.mechanics.compendium.player.PlayerCompendiumData;
import chylex.hee.mechanics.compendium.util.KnowledgeObservation;
import chylex.hee.mechanics.misc.PlayerDataHandler;
import chylex.hee.mechanics.misc.PlayerDataHandler.IExtendedPropertyInitializer;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C19CompendiumData;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;

public final class CompendiumEvents implements IExtendedPropertyInitializer<PlayerCompendiumData>{
	private static final String playerPropertyIdentifier = "HardcoreEnderExpansion~Compendium";
	private static final byte byteZero = 0;
	private static final byte byteOne = 1;
	private static final BlockMetaWrapper bmwReuse = new BlockMetaWrapper(Blocks.air,0);
	private static final KnowledgeObservation observationReuse = new KnowledgeObservation();
	
	private static CompendiumEvents instance;
	
	public static void register(){
		if (instance == null){
			instance = new CompendiumEvents();
			MinecraftForge.EVENT_BUS.register(instance);
			PlayerDataHandler.registerProperty(playerPropertyIdentifier,instance);
		}
	}
	
	public static PlayerCompendiumData getPlayerData(EntityPlayer player){
		return (PlayerCompendiumData)player.getExtendedProperties(playerPropertyIdentifier);
	}
	
	public static KnowledgeObject<ObjectBlock> getBlockObject(ItemStack is){
		bmwReuse.block = ((ItemBlock)is.getItem()).field_150939_a;
		bmwReuse.metadata = (byte)is.getItemDamage();
		return KnowledgeObject.getObject(bmwReuse);
	}
	
	public static KnowledgeObservation getObservation(EntityPlayer player){
		observationReuse.setEmpty();
		
		Vec3 posVec = Vec3.createVectorHelper(player.posX,player.boundingBox.minY+player.getEyeHeight()-(player.isSneaking() ? 0.08D : 0D),player.posZ);
		Vec3 lookVec = player.getLookVec();
		
		MovingObjectPosition mopBlock = player.worldObj.rayTraceBlocks(posVec.addVector(0D,0D,0D),posVec.addVector(lookVec.xCoord*10D,lookVec.yCoord*10D,lookVec.zCoord*10D),true);
		double distBlock = mopBlock != null && mopBlock.typeOfHit == MovingObjectType.BLOCK ? MathUtil.distance(mopBlock.blockX+0.5D-posVec.xCoord,mopBlock.blockY+0.5D-posVec.yCoord,mopBlock.blockZ+0.5D-posVec.zCoord) : Double.MAX_VALUE;
		
		double bbX = posVec.xCoord+lookVec.xCoord*5D, bbY = posVec.yCoord+lookVec.yCoord*5D, bbZ = posVec.zCoord+lookVec.zCoord*5D;
		List<Entity> list = player.worldObj.getEntitiesWithinAABB(Entity.class,AxisAlignedBB.getBoundingBox(bbX-6D,bbY-6D,bbZ-6D,bbX+6D,bbY+6D,bbZ+6D));
		Vec3 interceptVec = posVec.addVector(lookVec.xCoord*10D,lookVec.yCoord*10,lookVec.zCoord*10D);
		Entity tracedEntity = null;
		double distEntity = Double.MAX_VALUE;
		
		for(Entity entity:list){
			if (entity == player)continue;
			
			double size = entity.getCollisionBorderSize(), dist;
			MovingObjectPosition mop = entity.boundingBox.expand(size,size,size).calculateIntercept(posVec,interceptVec);

			if (mop != null && (dist = posVec.distanceTo(mop.hitVec)) < distEntity){
				distEntity = dist;
				tracedEntity = entity;
			}
		}
		
		if (distBlock < distEntity && mopBlock != null){
			BlockMetaWrapper wrapper = new BlockMetaWrapper(player.worldObj.getBlock(mopBlock.blockX,mopBlock.blockY,mopBlock.blockZ),player.worldObj.getBlockMetadata(mopBlock.blockX,mopBlock.blockY,mopBlock.blockZ));
			observationReuse.setBlock(KnowledgeObject.<ObjectBlock>getObject(wrapper));
		}
		else if (tracedEntity != null){
			if (tracedEntity instanceof EntityLiving)observationReuse.setMob(KnowledgeObject.<ObjectMob>getObject((EntityLiving)tracedEntity));
			else if (tracedEntity instanceof EntityItem){
				ItemStack is = ((EntityItem)tracedEntity).getEntityItem();
				
				if (is.getItem() instanceof ItemBlock)observationReuse.setBlock(getBlockObject(is));
				else observationReuse.setItem(KnowledgeObject.<ObjectItem>getObject(is.getItem()));
			}
		}
		
		return observationReuse;
	}
	
	private final TObjectByteHashMap<UUID> playerTickLimiter = new TObjectByteHashMap<>();
	
	private CompendiumEvents(){}

	@Override
	public PlayerCompendiumData createNew(Entity entity){
		return new PlayerCompendiumData();
	}
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent e){
		PacketPipeline.sendToPlayer(e.player,new C19CompendiumData(e.player));
	}
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedOutEvent e){
		playerTickLimiter.remove(e.player.getGameProfile().getId());
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e){
		if (e.phase != Phase.START || e.player.worldObj.isRemote)return;
		
		EntityPlayer player = e.player;
		
		if (playerTickLimiter.adjustOrPutValue(player.getGameProfile().getId(),byteOne,byteOne) >= 7){
			Stopwatch.timeAverage("CompendiumEvents - look tracing",25);
			
			playerTickLimiter.put(player.getGameProfile().getId(),byteZero);
			getObservation(player).discover(player);
			
			Stopwatch.finish("CompendiumEvents - look tracing");
			Stopwatch.timeAverage("CompendiumEvents - inventory",25);

			for(ItemStack is:player.inventory.mainInventory){
				if (is != null)discoverItemStack(player,is);
			}
			
			Stopwatch.finish("CompendiumEvents - inventory");
		}
	}
	
	@SubscribeEvent
	public void onItemPickup(ItemPickupEvent e){
		if (!e.player.worldObj.isRemote)discoverItemStack(e.player,e.pickedUp.getEntityItem());
	}
	
	@SubscribeEvent
	public void onItemCrafted(ItemCraftedEvent e){
		if (!e.player.worldObj.isRemote)discoverItemStack(e.player,e.crafting);
	}
	
	private void discoverItemStack(EntityPlayer player, ItemStack is){
		if (is.getItem() instanceof ItemBlock){
			bmwReuse.block = ((ItemBlock)is.getItem()).field_150939_a;
			bmwReuse.metadata = (byte)is.getItemDamage();
			KnowledgeObject<ObjectBlock> obj = KnowledgeObject.getObject(bmwReuse);
			if (obj != null)observationReuse.setBlock(obj).discover(player);
		}
		else{
			KnowledgeObject<ObjectItem> obj = KnowledgeObject.getObject(is.getItem());
			if (obj != null)observationReuse.setItem(obj).discover(player);
		}
	}
}
