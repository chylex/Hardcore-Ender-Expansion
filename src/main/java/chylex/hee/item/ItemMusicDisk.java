package chylex.hee.item;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.BlockJukebox.TileEntityJukebox;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C02PlayRecord;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMusicDisk extends ItemRecord{
	private static final List<String[]> musicNames = new ArrayList<>();
	
	static{
		musicNames.add(new String[]{ "Banjolic", "dragoncalm_qwertygiy_Banjolic.ogg" });
		musicNames.add(new String[]{ "In The End", "dragoncalm_qwertygiy_InTheEnd.ogg" });
		musicNames.add(new String[]{ "Asteroid", "dragoncalm_qwertygiy_Asteroid.ogg" });
		musicNames.add(new String[]{ "Stewed", "dragoncalm_qwertygiy_Stewed.ogg" });
		musicNames.add(new String[]{ "Beat The Dragon", "dragonangry_qwertygiy_BeatTheDragon.ogg" });
		musicNames.add(new String[]{ "Granite", "dragonangry_qwertygiy_Granite.ogg" });
		musicNames.add(new String[]{ "Remember This", "dragonangry_qwertygiy_RememberThis.ogg" });
		musicNames.add(new String[]{ "Spyder", "dragonangry_qwertygiy_Spyder.ogg" });
		musicNames.add(new String[]{ "Onion", "random_qwertygiy_Onion.ogg" });
		musicNames.add(new String[]{ "Crying Soul", "random_qwertygiy_CryingSoul.ogg" });
	}
	
	public static int getRecordCount(){
		return musicNames.size();
	}
	
	public static String[] getRecordData(int damage){
		return musicNames.get(Math.max(0,Math.min(musicNames.size()-1,damage)));
	}
	
	private IIcon[] iconArray;
	
	public ItemMusicDisk(){
		super("");
		setHasSubtypes(true);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		if (world.getBlock(x,y,z) == Blocks.jukebox && world.getBlockMetadata(x,y,z) == 0 && world.getTileEntity(x,y,z) instanceof TileEntityJukebox){
			if (world.isRemote)return true;

			((BlockJukebox)Blocks.jukebox).func_149926_b(world,x,y,z,is);
			PacketPipeline.sendToDimension(world.provider.dimensionId,new C02PlayRecord(x,y,z,(byte)is.getItemDamage()));
			--is.stackSize;
			
			KnowledgeRegistrations.MUSIC_DISCS.tryUnlockFragment(player,1F);
			
			return true;
		}
		
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage){
		return iconArray[Math.max(0,Math.min(iconArray.length-1,damage))];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getRecordNameLocal(){
		return "qwertygiy - ";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){	
		textLines.add(getRecordNameLocal()+musicNames.get(Math.max(0,Math.min(musicNames.size()-1,is.getItemDamage())))[0]);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list){
		for(int a = 0; a < musicNames.size(); a++){
			list.add(new ItemStack(item,1,a));
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister){
		iconArray = new IIcon[musicNames.size()];

		for(int index = 0; index < iconArray.length; index++){
			iconArray[index] = iconRegister.registerIcon(getIconString()+"_"+(index+1));
		}
	}
}
