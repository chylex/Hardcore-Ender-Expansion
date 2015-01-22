package chylex.hee.item;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.projectile.EntityProjectileCurse;
import chylex.hee.entity.technical.EntityTechnicalCurseBlock;
import chylex.hee.entity.technical.EntityTechnicalCurseEntity;
import chylex.hee.mechanics.curse.CurseType;
import chylex.hee.system.util.BlockPosM;

public class ItemCurse extends Item{
	public ItemCurse(){
		setHasSubtypes(true);
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ){
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (block == Blocks.snow_layer && ((Integer)state.getValue(BlockSnow.LAYERS)).intValue() < 1)side = EnumFacing.UP;
		else if (!block.isReplaceable(world,pos))pos = pos.offset(side);

		if (is.stackSize == 0 || !player.canPlayerEdit(pos,side,is))return false;
		else{
			CurseType type = CurseType.getFromDamage(is.getItemDamage());
			if (type == null)return false;
			
			if (!world.isRemote)world.spawnEntityInWorld(new EntityTechnicalCurseBlock(world,new BlockPosM(pos),player.getUniqueID(),type,CurseType.isEternal(is.getItemDamage())));
			else world.playSound(pos.getX()+0.5D,pos.getY(),pos.getZ()+0.5D,"hardcoreenderexpansion:mob.random.curse",0.8F,0.9F+itemRand.nextFloat()*0.2F,false);
			
			return true;
		}
	}
	
	@Override
	public boolean itemInteractionForEntity(ItemStack is, EntityPlayer player, EntityLivingBase entity){
		CurseType type = CurseType.getFromDamage(is.getItemDamage());
		if (type == null)return false;
		
		if (!player.worldObj.isRemote)player.worldObj.spawnEntityInWorld(new EntityTechnicalCurseEntity(player.worldObj,entity,type,CurseType.isEternal(is.getItemDamage())));
		else{
			player.worldObj.playSound(entity.posX,entity.posY,entity.posZ,"hardcoreenderexpansion:mob.random.curse",0.8F,0.9F+itemRand.nextFloat()*0.2F,false);
			for(int a = 0; a < 40; a++)HardcoreEnderExpansion.fx.curse(player.worldObj,entity.posX+(itemRand.nextDouble()-0.5D)*1.5D,entity.posY+(itemRand.nextDouble()-0.5D)*1.5D,entity.posZ+(itemRand.nextDouble()-0.5D)*1.5D,type);
		}
		
		return true;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		CurseType type = CurseType.getFromDamage(is.getItemDamage());
		if (type == null)return is;
			
		if (!world.isRemote)world.spawnEntityInWorld(new EntityProjectileCurse(world,player,type,CurseType.isEternal(is.getItemDamage())));
		if (!player.capabilities.isCreativeMode)--is.stackSize;
		return is;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		CurseType type = CurseType.getFromDamage(is.getItemDamage());
		return "item.curse."+(type == null ? "invalid" : type.name().toLowerCase().replaceAll("_",""));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		if (CurseType.isEternal(is.getItemDamage()))textLines.add(EnumChatFormatting.YELLOW+StatCollector.translateToLocal("item.curse.eternal"));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tabs, List list){
		for(CurseType type:CurseType.values()){
			list.add(new ItemStack(item,1,type.damage));
			list.add(new ItemStack(item,1,type.damage|0b100000000));
		}
	}
	
	@Override
	public int getColorFromItemStack(ItemStack is, int pass){
		CurseType type = CurseType.getFromDamage(is.getItemDamage());
		return type == null ? 16777215 : type.getColor(pass);
	}
}
