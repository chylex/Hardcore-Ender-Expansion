package chylex.hee.item;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.energy.EnergyClusterData;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.nbt.NBT;
import chylex.hee.system.abstractions.nbt.NBTCompound;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.tileentity.TileEntityEnergyCluster;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEnergyReceptacle extends Item{
	private static final float updateEnergyLevel(float lvl, float limit, int cycles){
		float lossPerCycle = (float)Math.pow(limit, 0.001D)-1F;
		return Math.max(lvl-lossPerCycle*cycles, 0F);
	}
	
	@SideOnly(Side.CLIENT)
	private IIcon iconOverlay;
	
	public ItemEnergyReceptacle(){
		setHasSubtypes(true);
	}
	
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		NBTCompound tag = NBT.item(is, false);
		
		if (tag.hasKey("fuckcreativemode")){
			is.setItemDamage(tag.getByte("fuckcreativemode"));
			tag.removeTag("fuckcreativemode");
		}
		
		if (!world.isRemote && tag.hasKey("cluster")){
			tag = NBT.item(is, true);
			long prevTime = tag.getLong("ltime"), currentTime = world.getTotalWorldTime();
			
			if (currentTime-prevTime >= 10){
				NBTCompound clusterTag = tag.getCompound("cluster");
				float lvl = clusterTag.getFloat("lvl");
				float limit = clusterTag.getFloat("max");
				
				clusterTag.setFloat("lvl", updateEnergyLevel(lvl, limit, 1+(int)((currentTime-prevTime)/10)));
				tag.setLong("ltime", currentTime);
			}
		}
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		if (!world.isRemote){
			Pos pos = Pos.at(x, y, z);
			NBTCompound tag = NBT.item(is, false);
			
			if (tag.hasKey("cluster")){
				if (tag.hasKey("ltime") && world.getTotalWorldTime()-tag.getLong("ltime") >= 11)return false; // needs to be refreshed before placing
				
				if (side > 0)pos = pos.offset(side);
				if (!player.canPlayerEdit(pos.getX(), pos.getY(), pos.getZ(), side, is) || !pos.isAir(world))return false;
				
				tag.getCompound("cluster").setLong("loc", pos.toLong()); // nbt has to exist at this point
				pos.setBlock(world, BlockList.energy_cluster);
				
				TileEntityEnergyCluster tile = pos.getTileEntity(world);
				tile.readTileFromNBT(tag.getCompound("cluster").getUnderlyingTag());
				
				EnergyClusterData data = tile.getData().get();
				if (data.getEnergyLevel()-tag.getFloat("origlvl") >= Math.pow(data.getMaxLevel(), 0.015D)+0.01D)data.weaken();
				
				tile.synchronize();
				
				tag.removeTag("cluster");
				tag.removeTag("ltime");
				tag.removeTag("origlvl");
				
				if (player.capabilities.isCreativeMode)tag.setByte("fuckcreativemode", (byte)0);
				else is.setItemDamage(0);
			}
			else if (pos.getBlock(world) == BlockList.energy_cluster){
				tag = NBT.item(is, true);
				
				NBTTagCompound clusterTag = new NBTTagCompound();
				TileEntityEnergyCluster cluster = pos.getTileEntity(world);
				
				cluster.writeTileToNBT(clusterTag);
				cluster.shouldNotExplode = true;
				pos.breakBlock(world, false);
				
				tag.setTag("cluster", clusterTag);
				tag.setFloat("origlvl", clusterTag.getFloat("lvl"));
				tag.setLong("ltime", world.getTotalWorldTime());
				
				if (player.capabilities.isCreativeMode)tag.setByte("fuckcreativemode", (byte)1);
				else is.setItemDamage(1);
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		NBTCompound tag = NBT.item(is, false);
		
		if (tag.hasKey("cluster")){
			float lvl = tag.getCompound("cluster").getFloat("lvl");
			float limit = tag.getCompound("cluster").getFloat("max");
			long diff = player.worldObj.getTotalWorldTime()-tag.getLong("ltime");
			textLines.add(StringUtils.replaceOnce(I18n.format("item.energyReceptacle.holding"), "$", DragonUtil.formatTwoPlaces.format(updateEnergyLevel(lvl, limit, 1+(int)(diff/10)))));
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int damage, int pass){
		return pass == 0 ? itemIcon : iconOverlay;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack is, int pass){
		if (pass == 1 && NBT.item(is, false).hasKey("cluster")){
			byte[] colors = is.getTagCompound().getCompoundTag("cluster").getByteArray("col");
			if (colors.length == 3)return ((colors[0]+128)<<16)|((colors[1]+128)<<8)|(colors[2]+128);
		}
		
		return 16777215;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses(){
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderPasses(int damage){
		return damage == 0 ? 1 : 2;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister){
		super.registerIcons(iconRegister);
		iconOverlay = iconRegister.registerIcon(getIconString()+"_overlay");
	}
}
