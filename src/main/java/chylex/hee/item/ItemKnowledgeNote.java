package chylex.hee.item;
import java.util.List;
import java.util.Random;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.game.save.SaveData;
import chylex.hee.game.save.types.player.LoreFile;
import chylex.hee.mechanics.compendium.content.LoreTexts;
import chylex.hee.mechanics.compendium.events.CompendiumEvents;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C03KnowledgeNote;
import chylex.hee.packets.client.C19CompendiumData;
import chylex.hee.system.util.ItemUtil;
import chylex.hee.world.loot.interfaces.IItemPostProcessor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemKnowledgeNote extends Item{
	public ItemKnowledgeNote(){
		setHasSubtypes(true);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		if (!world.isRemote)player.worldObj.playSoundAtEntity(player,"hardcoreenderexpansion:player.random.pageflip",1.5F,0.5F*((world.rand.nextFloat()-world.rand.nextFloat())*0.7F+1.8F));
		
		if (!world.isRemote && is.hasTagCompound()){
			NBTTagCompound nbt = is.getTagCompound();
			int added = nbt.getByte("notePts");
			
			if (nbt.getByte("notePts") > 0){
				CompendiumEvents.getPlayerData(player).offsetPoints(is.getTagCompound().getByte("notePts"));
				PacketPipeline.sendToPlayer(player,new C19CompendiumData(player));
				nbt.setByte("notePts",(byte)0);
			}
			
			if (nbt.hasKey("noteCat")){
				LoreTexts category = LoreTexts.fromTitle(nbt.getString("noteCat"));
				int index;
				
				if (nbt.hasKey("noteInd")){
					index = nbt.getByte("noteInd");
					SaveData.player(player,LoreFile.class).markAsRead(category,index);
				}
				else{
					index = SaveData.player(player,LoreFile.class).getRandomTextIndex(category,world.rand);
					nbt.setByte("noteInd",(byte)index);
				}
				
				PacketPipeline.sendToPlayer(player,new C03KnowledgeNote(category,index,added));
			}
		}
		
		return is;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		NBTTagCompound nbt = ItemUtil.getTagRoot(is,false);
		if (nbt.hasKey("noteCat"))textLines.add(I18n.format("ec.note."+nbt.getString("noteCat")+".title"));
		
		if (nbt.hasKey("notePts"))textLines.add(nbt.getByte("notePts") > 0 ? I18n.format("item.knowledgeNote.pts").replace("$",String.valueOf(nbt.getByte("notePts"))) : I18n.format("item.knowledgeNote.used"));
		else if (nbt.hasKey("pts"))textLines.add(I18n.format("item.knowledgeNote.useless"));
	}
	
	public static IItemPostProcessor createNoteProcessor(final LoreTexts category, final int minMultiplier, final int maxMultiplier){
		return (is, rand) -> setNoteInfo(is,rand,category,minMultiplier+rand.nextInt(1+maxMultiplier-minMultiplier));
	}
	
	public static final ItemStack setNoteInfo(ItemStack is, Random rand, LoreTexts category, int multiplier){
		ItemUtil.getTagRoot(is,true).setByte("notePts",(byte)(5*multiplier));
		ItemUtil.getTagRoot(is,true).setString("noteCat",category.getTitle());
		return is;
	}
}
