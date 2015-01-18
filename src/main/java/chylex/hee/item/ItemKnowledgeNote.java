package chylex.hee.item;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.mechanics.compendium.events.CompendiumEvents;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C19CompendiumData;
import chylex.hee.system.util.ItemUtil;

public class ItemKnowledgeNote extends Item{
	public ItemKnowledgeNote(){
		setHasSubtypes(true);
	}
	
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		if (!world.isRemote && !is.hasTagCompound())setRandomNote(is,itemRand,5);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		world.playSoundAtEntity(player,"hardcoreenderexpansion:player.random.pageflip",1.5F,0.5F*((player.getRNG().nextFloat()-player.getRNG().nextFloat())*0.7F+1.8F));
		
		if (!world.isRemote && is.hasTagCompound()){
			CompendiumEvents.getPlayerData(player).givePoints(is.getTagCompound().getByte("pts"));
			PacketPipeline.sendToPlayer(player,new C19CompendiumData(player));
			--is.stackSize;
		}
		
		return is;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		textLines.add(ItemUtil.getNBT(is,false).getByte("pts")+" Knowledge Points");
	}
	
	public static ItemStack setRandomNote(ItemStack is, Random rand, int multiplier){
		ItemUtil.getNBT(is,true).setByte("pts",(byte)((rand.nextInt(4)*rand.nextInt(4)+rand.nextInt(3)+2)*multiplier));
		return is;
	}
}
