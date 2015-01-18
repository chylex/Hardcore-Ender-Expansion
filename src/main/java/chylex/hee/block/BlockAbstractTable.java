package chylex.hee.block;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.mechanics.energy.EnergyChunkData;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.EnergySavefile;
import chylex.hee.tileentity.TileEntityAbstractTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockAbstractTable extends BlockAbstractInventory{
	@SideOnly(Side.CLIENT)
	private IIcon iconTop, iconSide, iconBottom;
	
	public BlockAbstractTable(){
		super(Material.rock);
		setHardness(4F);
		setResistance(2000F);
	}
	
	protected abstract int getGuiID();
	
	@Override
	public final boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		player.openGui(HardcoreEnderExpansion.instance,getGuiID(),world,x,y,z);
		return true;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta){
		TileEntity tile = world.getTileEntity(x,y,z);
		
		if (tile instanceof TileEntityAbstractTable){
			TileEntityAbstractTable table = (TileEntityAbstractTable)tile;
			
			if (table.getStoredEnergy() >= EnergyChunkData.minSignificantEnergy){
				float amount = table.getStoredEnergy();
				
				if (world.provider.getDimensionId() == 1){
					amount = WorldDataHandler.<EnergySavefile>get(EnergySavefile.class).getFromBlockCoords(world,x,z,true).addEnergy(amount);
				}
				
				if (amount >= EnergyChunkData.minSignificantEnergy){
					int energyMeta = Math.min(15,3+(int)(amount*0.8F));
					
					for(int attempt = 0, placed = 0, xx, yy, zz; attempt < 20 && placed < 3; attempt++){
						xx = x+world.rand.nextInt(4)-world.rand.nextInt(4);
						yy = y+world.rand.nextInt(4)-world.rand.nextInt(4);
						zz = z+world.rand.nextInt(4)-world.rand.nextInt(4);
						
						if (world.isAirBlock(xx,yy,zz)){
							world.setBlock(xx,yy,zz,BlockList.corrupted_energy_low,energyMeta,3);
							++placed;
						}
					}
				}
			}
		}
		
		super.breakBlock(world,x,y,z,block,meta);
	}
	
	@Override
	public boolean hasComparatorInputOverride(){
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int dir){
		TileEntity tile = world.getTileEntity(x,y,z);
		return tile == null || ((TileEntityAbstractTable)tile).isComparatorOn() ? 15 : 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final IIcon getIcon(int side, int meta){
		return side == 0 ? iconBottom : side == 1 ? iconTop : iconSide;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void registerBlockIcons(IIconRegister iconRegister){
		iconTop = iconRegister.registerIcon("hardcoreenderexpansion:"+textureName+"_top");
		iconSide = iconRegister.registerIcon("hardcoreenderexpansion:"+textureName+"_side");
		iconBottom = iconRegister.registerIcon("hardcoreenderexpansion:table_bottom");
	}
}
