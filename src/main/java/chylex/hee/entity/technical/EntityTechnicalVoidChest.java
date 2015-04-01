package chylex.hee.entity.technical;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.mechanics.voidchest.PlayerVoidChest;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C09SimpleEvent;
import chylex.hee.packets.client.C09SimpleEvent.EventType;
import chylex.hee.system.util.MathUtil;

public class EntityTechnicalVoidChest extends EntityTechnicalBase{
	private EntityPlayerMP player;
	private List<EntityItem> items = new ArrayList<>();
	private boolean simulateFall;
	private short age;
	
	public EntityTechnicalVoidChest(World world){
		super(world);
	}
	
	public EntityTechnicalVoidChest(World world, double x, double y, double z, EntityPlayerMP player, Collection<? extends EntityItem> items, boolean simulateFall){
		super(world);
		setPosition(x,y,z);
		this.player = player;
		this.items.addAll(items);
		this.simulateFall = simulateFall;
	}

	@Override
	protected void entityInit(){}
	
	@Override
	public void onUpdate(){
		if (worldObj.isRemote)return;
		
		++age;
		
		if (items == null || items.isEmpty() || player == null || player.isDead || !player.playerNetServerHandler.func_147362_b().isChannelOpen())setDead();
		else{
			if (age == 1 && simulateFall){
				boolean triggered = false;
				
				for(Iterator<EntityItem> iter = items.iterator(); iter.hasNext();){
					EntityItem item = iter.next();
					EntityItem copy = new EntityItem(worldObj,item.posX,item.posY,item.posZ,new ItemStack(Blocks.bedrock,64));
					copy.addVelocity(item.motionX,item.motionY,item.motionZ);
					copy.isAirBorne = item.isAirBorne;
					copy.isDead = true;
					
					double prevPosY = item.posY;
					
					for(int attempt = 0; attempt < 300; attempt++){
						copy.onUpdate();
						
						if (MathUtil.floatEquals((float)copy.posY,(float)prevPosY))break;
						else if ((prevPosY = copy.posY) <= -8D){
							takeItem(item);
							triggered = true;
							iter.remove();
							break;
						}
					}
				}
				
				if (triggered)PacketPipeline.sendToPlayer(player,new C09SimpleEvent(EventType.SHOW_VOID_CHEST));
			}
			
			for(Iterator<EntityItem> iter = items.iterator(); iter.hasNext();){
				EntityItem entity = iter.next();
				
				if (entity.isDead)iter.remove();
				else if (entity.posY <= -8D){
					takeItem(entity);
					iter.remove();
				}
			}
		}
		
		if (++age > 1200)setDead();
	}
	
	private void takeItem(EntityItem entity){
		PlayerVoidChest.getInventory(player).putItemRandomly(entity.getEntityItem(),rand);
		entity.setDead();
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){
		nbt.setShort("vcage",age);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){
		age = nbt.getShort("vcage");
	}
}
