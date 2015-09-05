package chylex.hee.block;
import java.util.Random;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockVine;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDryVine extends BlockVine{
	private static final int dryRed = 161, dryGreen = 135, dryBlue = 87;
	
	private static final int recolor(int color){
		int red = (color>>16)&255, green = (color>>8)&255, blue = color&255;
		red += MathUtil.floor((dryRed-red)*0.4F);
		green += MathUtil.floor((dryGreen-green)*0.4F);
		blue += MathUtil.floor((dryBlue-blue)*0.4F);
		return (red<<16)|(green<<8)|blue;
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand){}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return Blocks.vine.getIcon(side,meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor(){
		return recolor(ColorizerFoliage.getFoliageColorBasic());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int damage){
		return recolor(ColorizerFoliage.getFoliageColorBasic());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess world, int x, int y, int z){
		return recolor(super.colorMultiplier(world,x,y,z));
	}

    @Override
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister){}
}
