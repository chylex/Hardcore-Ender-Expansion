package chylex.hee.block;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.entity.fx.FXType;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20Effect;
import chylex.hee.system.util.BlockPosM;

public class BlockSpookyLeaves extends BlockLeaves{
	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighbor){
		world.scheduleUpdate(pos,this,world.rand.nextInt(20)+3);
		if (neighbor == BlockList.spooky_log)beginLeavesDecay(world,pos);
	}
	
	@Override
	public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune){
		if (!world.isRemote){
			PacketPipeline.sendToAllAround(world.provider.getDimensionId(),pos,64D,new C20Effect(FXType.Basic.SPOOKY_LEAVES_DECAY,pos.getX()+0.5D,pos.getY()+0.5D,pos.getZ()+0.5D));
		}
	}
	
	@Override
	public boolean isOpaqueCube(){
		return false;
	}
	
	@Override
	public boolean isFullCube(){
		return false;
	}
	
	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos){
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(IBlockState state){
		return 16777215;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess world, BlockPos pos, int pass){
		return 16777215;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer){
		BlockPosM pos = new BlockPosM(target.getBlockPos());
		
		for(int a = 0; a < 10; a++){
			effectRenderer.addEffect(new EntityDiggingFX(world,pos.x+world.rand.nextFloat(),pos.y+world.rand.nextFloat(),pos.z+world.rand.nextFloat(),world.rand.nextFloat()-0.5F,0D,world.rand.nextFloat()-0.5F,world.getBlockState(pos)){}.multiplyVelocity(0.3F+world.rand.nextFloat()*0.6F).multipleParticleScaleBy(0.2F+world.rand.nextFloat()*2F));
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffectsCustom(World world, BlockPosM pos){
		EffectRenderer eff = Minecraft.getMinecraft().effectRenderer;
		
		for(int a = 0; a < 30; a++){
			eff.addEffect(new EntityDiggingFX(world,pos.x+world.rand.nextFloat(),pos.y+world.rand.nextFloat()*1.5F,pos.z+world.rand.nextFloat(),world.rand.nextFloat()-0.5F,0D,world.rand.nextFloat()-0.5F,world.getBlockState(pos)){}.multiplyVelocity(0.1F+world.rand.nextFloat()*0.2F).multipleParticleScaleBy(world.rand.nextFloat()*2.2F));
		}
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		list.add(new ItemStack(item,1,0));
	}

	@Override
	protected ItemStack createStackedBlock(IBlockState state){
		return new ItemStack(this,1,0);
	}
	
	@Override
	public EnumType getWoodType(int meta){
		return EnumType.OAK;
	}
	
	@Override
	public List<ItemStack> onSheared(ItemStack is, IBlockAccess world, BlockPos pos, int fortune){
		return new ArrayList<>();
	}
}
