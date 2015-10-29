package chylex.hee.item;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.system.util.ColorUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityEnergyCluster;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEnergyOracle extends Item{
	@SideOnly(Side.CLIENT)
	private static boolean showColor;
	
	private static int updateTimer;
	private static Pos clusterPos = null, lastPlayerPos = null;
	private static float clusterHue;
	
	@SideOnly(Side.CLIENT)
	private IIcon iconMarker;
	
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		if (entity.worldObj.isRemote && entity == HardcoreEnderExpansion.proxy.getClientSidePlayer()){
			if (!isHeld)clusterPos = null;
			else if ((clusterPos == null || ++updateTimer >= 4) && (lastPlayerPos == null || lastPlayerPos.distance(entity) > 1D)){
				updateTimer = 0;
				lastPlayerPos = Pos.at(entity);
				
				if (clusterPos != null && lastPlayerPos.distance(clusterPos) > 80D)clusterPos = null;
				
				final int chunkX = lastPlayerPos.getX()>>4, chunkZ = lastPlayerPos.getZ()>>4;
				
				List<TileEntityEnergyCluster> clusters = new ArrayList<>();
				
				Pos.forEachBlock(Pos.at(-5,0,-5),Pos.at(5,0,5),offset -> {
					if (MathUtil.square(16*offset.x-8)+MathUtil.square(16*offset.z-8) > 6400)return; // 80 blocks
					
					((Map<ChunkPosition,TileEntity>)world.getChunkFromChunkCoords(chunkX+offset.getX(),chunkZ+offset.getZ()).chunkTileEntityMap).entrySet()
					.stream()
					.filter(entry -> entry.getValue().getClass() == TileEntityEnergyCluster.class && lastPlayerPos.distanceSquared(entry.getValue()) <= 6400D)
					.map(entry -> (TileEntityEnergyCluster)entry.getValue())
					.forEach(clusters::add);
				});
				
				CollectionUtil.min(clusters,cluster -> lastPlayerPos.distanceSquared(cluster)).ifPresent(cluster -> {
					clusterPos = Pos.at(cluster);
					clusterHue = ColorUtil.getHue(cluster.getColor(0),cluster.getColor(1),cluster.getColor(2));
				});
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack is, int pass){
		return pass == 1 ? iconMarker : itemIcon;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack is, int pass, EntityPlayer player, ItemStack usingItem, int useRemaining){
		showColor = player == HardcoreEnderExpansion.proxy.getClientSidePlayer() && is == player.getHeldItem();
		return super.getIcon(is,pass,player,usingItem,useRemaining);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack is, int pass){
		if (pass == 1 && showColor && clusterPos != null){
			float mp = (float)lastPlayerPos.distance(clusterPos)/80F;
			float[] color = ColorUtil.hsvToRgb(clusterHue,0.35F+mp*0.15F,1F-mp*0.75F);
			return ((int)(color[0]*255F)<<16)|((int)(color[1]*255F)<<8)|(int)(color[2]*255F);
		}
		else return pass == 1 ? (32<<16)|(32<<8)|32 : 16777215;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses(){
		return true;
	}

	@Override
	public int getRenderPasses(int metadata){
		return 2;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister){
		super.registerIcons(iconRegister);
		iconMarker = iconRegister.registerIcon(getIconString()+"_marker");
	}
}
