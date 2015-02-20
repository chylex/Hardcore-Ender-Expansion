package chylex.hee.block;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.ForgeDirection;
import chylex.hee.item.block.ItemBlockWithSubtypes.IBlockSubtypes;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.CollectionUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCrossedDecoration extends BlockFlower implements IShearable, IBlockSubtypes{
	private static final String[] decorTypes = new String[]{
		"decor_bullrush_bottom", "decor_bullrush_top", "decor_thorn_bush", "decor_infested_grass", "decor_infested_fern", "decor_infested_tallgrass",
		"decor_lily_fire", "decor_violet_moss_tall", "decor_violet_moss_moderate", "decor_violet_moss_short",
		"decor_flameweed_1", "decor_flameweed_2", "decor_flameweed_3", "decor_shadow_orchid"
	};
	
	public static final byte dataThornBush = 2, dataInfestedGrass = 3, dataInfestedFern = 4, dataInfestedTallgrass = 5,
					   		 dataLilyFire = 6, dataVioletMossTall = 7, dataVioletMossModerate = 8, dataVioletMossShort = 9,
					   		 dataFlameweed1 = 10, dataFlameweed2 = 11, dataFlameweed3 = 12, dataShadowOrchid = 13;
	
	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;
	
	public BlockCrossedDecoration(){
		super(0);
		setBlockBounds(0.1F,0.0F,0.1F,0.9F,0.8F,0.9F);
	}
	
	@Override
	public boolean canBlockStay(World world, int x, int y, int z){
		Block soil = world.getBlock(x,y-1,z);
		return (world.getFullBlockLightValue(x,y,z) >= 8 || world.canBlockSeeTheSky(x,y,z) || world.provider.dimensionId == 1)&&
			   (soil != null && soil.canSustainPlant(world,x,y-1,z,ForgeDirection.UP,this));
	}
	
	@Override
	public boolean canPlaceBlockOn(Block block){
		return block == Blocks.end_stone || block == BlockList.end_terrain || super.canPlaceBlockOn(block);
	}
	
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune){
		return meta == dataLilyFire ? CollectionUtil.newList(new ItemStack(this,1,meta)) : new ArrayList<ItemStack>();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z){
		if (world.getBlockMetadata(x,y,z) == dataLilyFire)return AxisAlignedBB.getBoundingBox(x+0.3F,y,z+0.3F,x+0.7F,y+0.8F,z+0.7F);
		else return super.getSelectedBoundingBoxFromPool(world,x,y,z);
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z){
		int meta = world.getBlockMetadata(x,y,z);
		return meta != dataLilyFire;
	}

	@Override
	public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune){
		return CollectionUtil.newList(new ItemStack(this,1,world.getBlockMetadata(x,y,z)));
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity){
		if (!world.isRemote && world.getBlockMetadata(x,y,z) == dataThornBush){
			entity.attackEntityFrom(DamageSource.generic,1F);
			
			if (world.rand.nextInt(80) == 0 && entity instanceof EntityLivingBase && !((EntityLivingBase)entity).isPotionActive(Potion.poison)){
				((EntityLivingBase)entity).addPotionEffect(new PotionEffect(Potion.poison.id,30+world.rand.nextInt(40),1,true));
			}
		}
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		switch(is.getItemDamage()){
			case dataThornBush: return "tile.crossedDecoration.thornyBush";
			case dataInfestedFern: return "tile.crossedDecoration.infestedFern";
			case dataInfestedGrass: return "tile.crossedDecoration.infestedBush";
			case dataInfestedTallgrass: return "tile.crossedDecoration.infestedGrass";
			case dataLilyFire: return "tile.crossedDecoration.lilyfire";
			case dataVioletMossTall: return "tile.crossedDecoration.violetMoss.tall";
			case dataVioletMossModerate: return "tile.crossedDecoration.violetMoss.moderate";
			case dataVioletMossShort: return "tile.crossedDecoration.violetMoss.short";
			case dataFlameweed1: case dataFlameweed2: case dataFlameweed3: return "tile.crossedDecoration.flameweed";
			case dataShadowOrchid: return "tile.crossedDecoration.shadowOrchid";
			default: return "";
		}
	}

	@Override
	public int getRenderType(){
		return ModCommonProxy.renderIdCrossedDecoration;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return iconArray[meta < decorTypes.length ? meta : 0];
	}

	@SuppressWarnings("unchecked")
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		for(int a = 2; a < decorTypes.length; a++)list.add(new ItemStack(item,1,a));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		iconArray = new IIcon[decorTypes.length];
		for(int a = 2; a < decorTypes.length; a++)iconArray[a] = iconRegister.registerIcon("hardcoreenderexpansion:"+decorTypes[a]);
	}
}
