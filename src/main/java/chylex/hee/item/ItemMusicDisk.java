package chylex.hee.item;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.BlockJukebox.TileEntityJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C02PlayRecord;
import chylex.hee.system.util.MathUtil;

public class ItemMusicDisk extends ItemRecord implements IMultiModel{
	private static final List<String[]> musicNames = new ArrayList<>();
	
	static{
		musicNames.add(new String[]{ "Banjolic", "records.qwertygiy.banjolic" });
		musicNames.add(new String[]{ "In The End", "records.qwertygiy.intheend" });
		musicNames.add(new String[]{ "Asteroid", "records.qwertygiy.asteroid" });
		musicNames.add(new String[]{ "Stewed", "records.qwertygiy.stewed" });
		musicNames.add(new String[]{ "Beat The Dragon", "records.qwertygiy.beatthedragon" });
		musicNames.add(new String[]{ "Granite", "records.qwertygiy.granite" });
		musicNames.add(new String[]{ "Remember This", "records.qwertygiy.rememberthis" });
		musicNames.add(new String[]{ "Spyder", "records.qwertygiy.spyder" });
		musicNames.add(new String[]{ "Onion", "records.qwertygiy.onion" });
		musicNames.add(new String[]{ "Crying Soul", "records.qwertygiy.cryingsoul" });
	}
	
	public static int getRecordCount(){
		return musicNames.size();
	}
	
	public static String[] getRecordData(int damage){
		return musicNames.get(MathUtil.clamp(damage,0,musicNames.size()-1));
	}
	
	public ItemMusicDisk(){
		super("");
		setHasSubtypes(true);
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ){
		IBlockState state = world.getBlockState(pos);
		
		if (state.getBlock() == Blocks.jukebox && !((Boolean)state.getValue(BlockJukebox.HAS_RECORD)).booleanValue() && world.getTileEntity(pos) instanceof TileEntityJukebox){
			if (world.isRemote)return true;

			((BlockJukebox)Blocks.jukebox).insertRecord(world,pos,world.getBlockState(pos),is);
			PacketPipeline.sendToDimension(world,new C02PlayRecord(pos,(byte)is.getItemDamage()));
			--is.stackSize;
			
			return true;
		}
		
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getRecordNameLocal(){
		return "qwertygiy - ";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){	
		textLines.add(getRecordNameLocal()+musicNames.get(MathUtil.clamp(is.getItemDamage(),0,musicNames.size()-1))[0]);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list){
		for(int a = 0; a < musicNames.size(); a++){
			list.add(new ItemStack(item,1,a));
		}
	}
	
	@Override
	public String[] getModels(){
		List<String> list = new ArrayList<>();
		for(int a = 0; a < musicNames.size(); a++)list.add("^music_disk_"+a);
		return list.toArray(new String[musicNames.size()]);
	}
}
