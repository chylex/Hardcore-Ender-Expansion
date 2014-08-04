package chylex.hee.block;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import chylex.hee.entity.fx.handler.FXType;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C30Effect;
import chylex.hee.proxy.ModCommonProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSpookyLeaves extends BlockLeaves{
	protected BlockSpookyLeaves(){
		super();
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor){
		world.scheduleBlockUpdate(x,y,z,this,world.rand.nextInt(20)+3);
		if (neighbor == BlockList.spooky_log)beginLeavesDecay(world,x,y,z);
	}
	
	@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune){
		if (!world.isRemote){
			PacketPipeline.sendToAllAround(world.provider.dimensionId,x+0.5D,y+0.5D,z+0.5D,64D,new C30Effect(FXType.SPOOKY_LEAVES_DECAY,x,y,z));
		}
	}
	
	@Override
	public boolean isOpaqueCube(){
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}

	@Override
	public int getRenderType(){
		return ModCommonProxy.renderIdSpookyLeaves;
	}
	
	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z){
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int meta){
		return 16777215;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess world, int x, int y, int z){
		return 16777215;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer){
		for(int a = 0; a < 10; a++){
			int x = target.blockX,y = target.blockY,z = target.blockZ;
			effectRenderer.addEffect((new EntityDiggingFX(world,x+world.rand.nextFloat(),y+world.rand.nextFloat(),z+world.rand.nextFloat(),world.rand.nextFloat()-0.5F,0D,world.rand.nextFloat()-0.5F,this,0)).applyColourMultiplier(x,y,z).multiplyVelocity(0.3F+world.rand.nextFloat()*0.6F).multipleParticleScaleBy(0.2F+world.rand.nextFloat()*2F));
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffectsCustom(World world, int x, int y, int z){
		EffectRenderer eff = Minecraft.getMinecraft().effectRenderer;
		for(int a = 0; a < 30; a++){
			eff.addEffect((new EntityDiggingFX(world,x+world.rand.nextFloat(),y+world.rand.nextFloat()*1.5F,z+world.rand.nextFloat(),world.rand.nextFloat()-0.5F,0D,world.rand.nextFloat()-0.5F,this,0)).applyColourMultiplier(x,y,z).multiplyVelocity(0.1F+world.rand.nextFloat()*0.2F).multipleParticleScaleBy(world.rand.nextFloat()*2.2F));
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return blockIcon;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		list.add(new ItemStack(item,1,0));
	}

	@Override
	protected ItemStack createStackedBlock(int meta){
		return new ItemStack(this,1,0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		blockIcon = iconRegister.registerIcon(getTextureName());
	}

	@Override
	public String[] func_150125_e(){ // OBFUSCATED
		return new String[]{ getUnlocalizedName() };
	}
}
