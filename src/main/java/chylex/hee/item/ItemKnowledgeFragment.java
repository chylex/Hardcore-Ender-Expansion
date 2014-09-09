package chylex.hee.item;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.events.CompendiumEvents;
import chylex.hee.mechanics.compendium.objects.IKnowledgeObjectInstance;
import chylex.hee.packets.PacketPipeline;
import com.google.common.collect.ImmutableList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import chylex.hee.packets.client.C19CompendiumData;

public class ItemKnowledgeFragment extends Item{
	public ItemKnowledgeFragment(){
		setHasSubtypes(true);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		if (world.isRemote || is.stackTagCompound == null || !is.stackTagCompound.hasKey("hasPts"))return is;
		
		boolean hasPts = is.stackTagCompound.getBoolean("hasPts");
		
		if (hasPts)CompendiumEvents.getPlayerData(player).givePoints(is.stackTagCompound.getByte("pts"));
		
		PacketPipeline.sendToPlayer(player,new C19CompendiumData(player));
		// TODO sound
		
		--is.stackSize;
		return is;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		if (is.stackTagCompound == null)return;
		
		boolean hasPts = is.stackTagCompound.getBoolean("hasPts");
		textLines.add(hasPts ? is.stackTagCompound.getByte("pts")+" Knowledge Points" : "");
	}
	
	public static ItemStack setRandomFragment(ItemStack is, Random rand){
		is.stackTagCompound = new NBTTagCompound();
		
		if (rand.nextInt(5) == 0 && false){ // random object
			is.stackTagCompound.setBoolean("hasPts",false);
			
			ImmutableList<KnowledgeObject<? extends IKnowledgeObjectInstance>> list = KnowledgeObject.getAllObjects();
			//is.stackTagCompound.setString("objectId",list.get(rand.nextInt(list.size())).getObject()); // TODO finish
		}
		else{ // points
			is.stackTagCompound.setBoolean("hasPts",true);
			is.stackTagCompound.setByte("pts",(byte)((rand.nextInt(5)*rand.nextInt(4)+rand.nextInt(3)+2)*5));
		}
		
		return is;
	}
}
