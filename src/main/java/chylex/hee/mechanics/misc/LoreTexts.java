package chylex.hee.mechanics.misc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.gui.GuiDiaryBook;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LoreTexts{
	public static final int pageAmount = 16;
	
	public static String getPage(int page){
		switch(page){
			
/*
 * CHAPTER 1
 */
			case 0: return
"This could be it, finally found another stronghold, maybe this one will be more fortunate...\n\n"+

"...And it was! After months of searching, there I stood, with the portal before me, deactivated, "+
"but still there.";
			
			case 1: return
"There were a few empty slots for missing eyes of ender, thankfully I had enough on "+
"me to activate it. I hardly stood there for a second before plunging into it. "+
"This was the moment, I was finally going to kill it.";

/*
 * CHAPTER 2
 */
			case 2: return
"After what seemed like hours, the dragon was dead. I nearly died at the very beginning when I "+
"destroyed the first crystal; no one had told me they dropped a bomb when you do that. I knew the "+
"endermen wouldn't take kindly to me destroying the crystals, but I guess the crystals didn't either.";
			
			case 3: return
"Although I had killed it, I was still poisoned, bleeding, and close to death. There were holes "+
"and craters dotted around the End where the dragon had swooped down towards me. The floor was "+
"littered with a stange purple essense, I picked it up and examined it. I decided to keep it with me, "+
"but I didn't enter the portal to go home just yet.";
			
/*
 * CHAPTER 3
 */
			case 4: return
"With my head down, I carefully walked over to the egg now lying where the beast fell. After examining "+
"it, I tapped it with my sword, and all of a sudden it seemed to feel safe. Maybe it would be possible "+
"for me to --";
			
/*
 * CHAPTER 4
 */
			case 5: return
"My mission here was complete, yet the adventuring blood in me stopped me from leaving right away. I "+
"glanced around the End, what caught my eyes was the smaller floating islands dotted around the main "+
"one.";
			
			case 6: return
"Upon closer inspection, the smaller islands had weird, cave-like holes containing patches of "+
"a strange purple powder, maybe it was the same thing as the purple essence I found before, perhaps "+
"this is the raw form of the same material.";
			
/*
 * CHAPTER 5
 */
			case 7: return
"After jumping back through the portal, I took my backpack off to take out the purple powder to examine "+
"it further, however it has all vanished! Even stranger, my collection of ender pearls had started "+
"glowing. Perhaps the purple powder had something to do with it...";
			
/*
 * CHAPTER 6
 */
			case 8: return
"I visited a nearby village the next day, and showed the pearls to a Scientist. After taking a quick "+
"look, he told me they've gained special abilities. At that point he also noticed the essence I attained from "+
"the dragon, glanced behind him, then leaned in and whispered that he's got something special.";

/*
 * CHAPTER 7
 */
			case 9: return
"Downstairs, he rummaged through a chest and took out a weird block; it looked similar to an "+
"enchanting table, but instead of a book, it displayed what seemed to be, the dragon essence. He said "+
"he didn't know what it was, and wondered if I could figure it out.";

/*
 * CHAPTER 8
 */
			case 10: return
"When I got home, I placed the altar down. The moment I did that, all the essence I had was "+
"swallowed up by it. I carefully walked towards it, but as I did, my armor and sword both got repaired! "+
"I wonder what else this altar can do...";
			
/*
 * CHAPTER 9
 */
			case 11: return
"It's been a few months since I last wrote in this, but I feel this is linked. This morning I went "+
"caving, and in a dungeon I found what I first thought was an Eye of Ender, however, this one was "+
"purple.";
			
			case 12: return
"When I got home, the egg started shaking, as if both objects were connected. I picked up "+
"both and went back to the stronghold with the end portal, and jumped through it again.";

/*
 * CHAPTER 10
 */
			case 13: return
"The purple eye of ender started vibrating again, and in a massive puff of smoke I was teleported "+
"to a bright temple that looked just like the stronghold's portal room, only made of pale stone.";
			
			case 14: return
"Before the portal was a black pedestal, I placed the egg on it carefully. The last things I remember "+ 
"were the egg slowly floating upwards, then a cataclysmic explosion...";
			
/*
 * CHAPTER 11
 */
			case 15: return
"I woke up in my bed. Today's the day where I slay the beast. I have prepared my items and I have my Eyes "+
"of Ender, now it's time to find and activate the portal...";
			
			default: return EnumChatFormatting.RED+"Woops, page not included in the mod!";
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void openBook(EntityClientPlayerMP player, byte[] unlockedPages){
		ItemStack is = new ItemStack(Items.written_book);
		NBTTagList pages = new NBTTagList();
		
		for(int a = 0; a < LoreTexts.pageAmount; a++){
			pages.appendTag(new NBTTagString(ArrayUtils.contains(unlockedPages,(byte)(a+1))?LoreTexts.getPage(a):EnumChatFormatting.GRAY+"This page is missing."));
		}
		
		is.setTagInfo("author",new NBTTagString("The Adventurer :P"));
		is.setTagInfo("title",new NBTTagString("The Adventurer's Diary"));
		is.setTagInfo("pages",pages);

		Minecraft mc = Minecraft.getMinecraft();
		mc.displayGuiScreen(new GuiDiaryBook(player,is));

		if (mc.currentScreen instanceof GuiDiaryBook){
			GuiDiaryBook bookGui = (GuiDiaryBook)mc.currentScreen;
			int n = 0;
			for(byte b:unlockedPages)n = Math.max(b-1,n);
			
			bookGui.currPage = n;
			bookGui.updateButtons();
		}
	}
}
