package chylex.hee.entity.technical;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.mechanics.voidchest.PlayerVoidChest;

public class EntityTechnicalVoidChest extends EntityTechnicalBase{
	private EntityPlayerMP player;
	private List<EntityItem> items = new ArrayList<>();
	
	public EntityTechnicalVoidChest(World world){
		super(world);
	}
	
	public EntityTechnicalVoidChest(World world, double x, double y, double z, EntityPlayerMP player, Collection<? extends EntityItem> items){
		super(world);
		setPosition(x,y,z);
		this.player = player;
		this.items.addAll(items);
	}

	@Override
	protected void entityInit(){}
	
	@Override
	public void onUpdate(){
		if (worldObj.isRemote)return;
		
		if (items == null || items.isEmpty() || player == null || player.isDead || !player.playerNetServerHandler.func_147362_b().isChannelOpen())setDead();
		else{
			for(Iterator<EntityItem> iter = items.iterator(); iter.hasNext();){
				EntityItem entity = iter.next();
				
				if (entity.isDead)iter.remove();
				else if (entity.posY <= -32D){
					PlayerVoidChest.getInventory(player).putItemRandomly(entity.getEntityItem(),rand);
					entity.setDead();
					iter.remove();
				}
			}
		}
		
		if (++ticksExisted > 1200)setDead();
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){}
}
