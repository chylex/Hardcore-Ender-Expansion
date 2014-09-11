package chylex.hee.item;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.mechanics.compendium.events.CompendiumEvents;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C19CompendiumData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemKnowledgeNote extends Item{
	public ItemKnowledgeNote(){
		setHasSubtypes(true);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		if (world.isRemote || is.stackTagCompound == null)return is;
		
		CompendiumEvents.getPlayerData(player).givePoints(is.stackTagCompound.getByte("pts"));
		PacketPipeline.sendToPlayer(player,new C19CompendiumData(player));
		// TODO sound
		
		--is.stackSize;
		return is;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		if (is.stackTagCompound == null)return;
		textLines.add(is.stackTagCompound.getByte("pts")+" Knowledge Points");
	}
	
	public static ItemStack setRandomFragment(ItemStack is, Random rand){
		is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setByte("pts",(byte)((rand.nextInt(5)*rand.nextInt(4)+rand.nextInt(3)+2)*5));
		return is;
	}
}
