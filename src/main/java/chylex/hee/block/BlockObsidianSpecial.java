package chylex.hee.block;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.item.block.ItemBlockWithSubtypes.IBlockSubtypes;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.abstractions.Pos;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockObsidianSpecial extends Block implements IBlockSubtypes{
	public static final byte metaSmooth = 0, metaChiseled = 1, metaPillarV = 2, metaPillarNS = 3, metaPillarEW = 4,
							 metaSmoothParticlesD = 5, metaChiseledParticlesU = 6;
	
	@SideOnly(Side.CLIENT)
	private IIcon iconSmooth, iconPillar, iconPillarTop, iconChiseled, iconChiseledTop;
	
	private final boolean isGlowing;
	
	public BlockObsidianSpecial(boolean isGlowing){
		super(Material.rock);
		this.isGlowing = isGlowing;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		if (meta == metaChiseled || meta == metaChiseledParticlesU){
			return side == 1 ? iconChiseledTop : iconChiseled;
		}
		else if (meta == metaPillarV || meta == metaPillarNS || meta == metaPillarEW){
			return (meta == metaPillarV && side <= 1) || (meta == metaPillarNS && side >= 4) || (meta == metaPillarEW && (side == 2 || side == 3)) ? iconPillarTop : iconPillar;
		}
		else return iconSmooth;
	}
	
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta){
		return (meta == 2) ? (side == 0 || side == 1 ? 2 : side == 2 || side == 3 ? 4 : side == 4 || side == 5 ? 3 : meta) : meta;
	}
	
	@Override
	public int damageDropped(int meta){
		return meta == metaChiseledParticlesU ? 1 : meta == metaSmoothParticlesD ? 0 : meta == metaPillarNS || meta == metaPillarEW ? 2 : meta;
	}
	
	@Override
	protected ItemStack createStackedBlock(int meta){
		if (meta == metaPillarNS || meta == metaPillarEW)return new ItemStack(this,1,2);
		else if (meta == metaSmoothParticlesD)return new ItemStack(this,1,0);
		else if (meta == metaChiseledParticlesU)return new ItemStack(this,1,1);
		else return super.createStackedBlock(meta);
	}
	
	@Override
	public int getRenderType(){
		return ModCommonProxy.renderIdObsidianSpecial;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		switch(is.getItemDamage()){
			case metaChiseled: return isGlowing ? "tile.obsidianSpecialGlowing.chiseled" : "tile.obsidianSpecial.chiseled";
			case metaPillarV: return isGlowing ? "tile.obsidianSpecialGlowing.pillar" : "tile.obsidianSpecial.pillar";
			default: return isGlowing ? "tile.obsidianSpecialGlowing.smooth" : "tile.obsidianSpecial.smooth";
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand){
		int meta = Pos.at(x,y,z).getMetadata(world);
		
		if (meta == metaSmoothParticlesD){
			for(int a = 0; a < 10; a++){
				world.spawnParticle("portal",x+rand.nextFloat(),y-4F*rand.nextFloat(),z+rand.nextFloat(),0D,0D,0D);
				world.spawnParticle("largesmoke",x+rand.nextFloat(),y-4F*rand.nextFloat(),z+rand.nextFloat(),0D,0D,0D);
			}
		}
		else if (meta == metaChiseledParticlesU){
			for(int a = 0; a < 30; a++){
				world.spawnParticle("portal",x+rand.nextFloat(),y+5F*rand.nextFloat(),z+rand.nextFloat(),0D,0D,0D);
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		list.add(new ItemStack(item,1,0));
		list.add(new ItemStack(item,1,1));
		list.add(new ItemStack(item,1,2));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		iconSmooth = iconRegister.registerIcon("hardcoreenderexpansion:obsidian_smooth");
		iconPillar = iconRegister.registerIcon("hardcoreenderexpansion:obsidian_pillar");
		iconPillarTop = iconRegister.registerIcon("hardcoreenderexpansion:obsidian_pillar_top");
		iconChiseled = iconRegister.registerIcon("hardcoreenderexpansion:obsidian_chiseled");
		iconChiseledTop = iconRegister.registerIcon("hardcoreenderexpansion:obsidian_chiseled_top");
	}
}
