package chylex.hee.item;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import chylex.hee.mechanics.causatum.CausatumMeters;
import chylex.hee.mechanics.causatum.CausatumUtils;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTempleCaller extends ItemAbstractEnergyAcceptor{ // TODO do something with this? probably remove later
	public static boolean isEnabled = true;
	public static int templeX, templeZ = 0, templeY = 246;
	
	@Override
	public boolean canAcceptEnergy(ItemStack is){
		return is.getItemDamage() < getMaxDamage();
	}

	@Override
	public void onEnergyAccepted(ItemStack is){
		is.setItemDamage(is.getItemDamage()+1);
	}
	
	@Override
	public int getEnergyPerUse(ItemStack is){
		return is.getMaxDamage();
	}
	
	@Override
	protected float getRegenSpeedMultiplier(){
		return 3F;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		if (world.isRemote)return is;
		
		if (!isEnabled){
			player.addChatMessage(new ChatComponentTranslation("item.templeCaller.info.disabled"));
			return is;
		}
		
		if (is.getItemDamage() < getMaxDamage()){
			player.addChatMessage(new ChatComponentTranslation("item.templeCaller.info.energy"));
			return is;
		}
		
		if (player.dimension != 1 || !player.inventory.hasItem(Item.getItemFromBlock(Blocks.dragon_egg)) || !WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).isDragonDead()){
			player.addChatMessage(new ChatComponentTranslation("item.templeCaller.info.conditions"));
		}
		else if (player.posY < templeY){
			if (!player.capabilities.isCreativeMode)--is.stackSize;
			
			//PacketPipeline.sendToPlayer(player,new C09SimpleEvent(EventType.BEGIN_TEMPLE_SMOKE));
			//new TempleGenerator(world).spawnTemple(templeX,templeY,templeZ);
			player.setLocationAndAngles(templeX+1.5D,templeY+1,templeZ+6.5D,-90F,0F);
			player.setPositionAndUpdate(templeX+1.5D,templeY+1,templeZ+6.5D);
			WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).setPlayerIsInTemple(player,true);
			
			CausatumUtils.increase(player,CausatumMeters.ITEM_USAGE,150F);
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
