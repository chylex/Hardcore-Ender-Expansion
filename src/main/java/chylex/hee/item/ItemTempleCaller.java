package chylex.hee.item;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C09SimpleEvent;
import chylex.hee.packets.client.C09SimpleEvent.EventType;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;
import chylex.hee.world.feature.TempleGenerator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTempleCaller extends ItemAbstractEnergyAcceptor{
	public static boolean isEnabled = true;
	public static int templeX, templeZ = 0, templeY = 246;
	
	@Override
	protected boolean canAcceptEnergy(ItemStack is){
		return is.getItemDamage() < getMaxDamage();
	}

	@Override
	protected void onEnergyAccepted(ItemStack is){
		is.setItemDamage(is.getItemDamage()+1);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		if (world.isRemote)return is;
		
		if (!isEnabled){
			player.addChatMessage(new ChatComponentText("The server has disabled Temple Caller."));
			return is;
		}
		
		if (is.getItemDamage() < getMaxDamage()){
			player.addChatMessage(new ChatComponentText("The Temple Caller does not have enough energy."));
			return is;
		}
		
		if (player.dimension != 1 || !player.inventory.hasItem(Item.getItemFromBlock(Blocks.dragon_egg)) || !WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).isDragonDead())player.addChatMessage(new ChatComponentText("This is not the time to use that!"));
		else if (player.posY < templeY){
			if (!player.capabilities.isCreativeMode)--is.stackSize;
			
			new TempleGenerator(world).spawnTemple(templeX,templeY,templeZ);
			player.setLocationAndAngles(templeX+1.5D,templeY+1,templeZ+6.5D,-90F,0F);
			player.setPositionAndUpdate(templeX+1.5D,templeY+1,templeZ+6.5D);
			WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).setPlayerIsInTemple(player,true);
			
			PacketPipeline.sendToPlayer(player,new C09SimpleEvent(EventType.BEGIN_TEMPLE_SMOKE));
		}

		return is;
	}
	
	@Override
	public boolean isDamaged(ItemStack is){
		return getDurabilityForDisplay(is) > 0D;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack is){
		return (getMaxDamage()-super.getDamage(is))/(double)getMaxDamage();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list){
		list.add(new ItemStack(item,1,0));
		list.add(new ItemStack(item,1,getMaxDamage()));
	}
}
