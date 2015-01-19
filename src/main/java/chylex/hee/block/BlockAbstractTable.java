package chylex.hee.block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.mechanics.energy.EnergyChunkData;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.EnergySavefile;
import chylex.hee.tileentity.TileEntityAbstractTable;

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
	public final boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
		player.openGui(HardcoreEnderExpansion.instance,getGuiID(),world,pos.getX(),pos.getY(),pos.getZ());
		return true;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		TileEntity tile = world.getTileEntity(pos);
		
		if (tile instanceof TileEntityAbstractTable){
			TileEntityAbstractTable table = (TileEntityAbstractTable)tile;
			
			if (table.getStoredEnergy() >= EnergyChunkData.minSignificantEnergy){
				float amount = table.getStoredEnergy();
				
				if (world.provider.getDimensionId() == 1){
					amount = WorldDataHandler.<EnergySavefile>get(EnergySavefile.class).getFromBlockCoords(world,pos.getX(),pos.getZ(),true).addEnergy(amount);
				}
				
				if (amount >= EnergyChunkData.minSignificantEnergy){
					int energyMeta = Math.min(15,3+(int)(amount*0.8F));
					
					for(int attempt = 0, placed = 0, xx, yy, zz; attempt < 20 && placed < 3; attempt++){
						xx = pos.getX()+world.rand.nextInt(4)-world.rand.nextInt(4);
						yy = pos.getY()+world.rand.nextInt(4)-world.rand.nextInt(4);
						zz = pos.getZ()+world.rand.nextInt(4)-world.rand.nextInt(4);
						
						if (world.isAirBlock(xx,yy,zz)){
							world.setBlock(xx,yy,zz,BlockList.corrupted_energy_low,energyMeta,3);
							++placed;
						}
					}
				}
			}
		}
		
		super.breakBlock(world,pos,state);
	}
	
	@Override
	public boolean hasComparatorInputOverride(){
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(World world, BlockPos pos){
		TileEntity tile = world.getTileEntity(pos);
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
