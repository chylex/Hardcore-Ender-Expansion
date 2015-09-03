package chylex.hee.render;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import chylex.hee.block.BlockEnderGoo;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.energy.EnergyClusterHealth;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.tileentity.TileEntityEnergyCluster;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class OverlayManager{
	private static OverlayManager instance;
	private static final ResourceLocation texGoo = new ResourceLocation("hardcoreenderexpansion:textures/overlay/endergoo.png");
	private static final PosMutable tmpPos = new PosMutable();
	
	private TileEntityEnergyCluster clusterLookedAt;
	private final List<Notification> notifications = new ArrayList<>();
	
	public static void addNotification(String notification){
		if (instance == null)register();
		instance.notifications.add(new Notification(notification));
	}
	
	public static void register(){
		if (instance == null)MinecraftForge.EVENT_BUS.register(instance = new OverlayManager());
	}
	
	private OverlayManager(){}

	@SubscribeEvent
	public void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre e){
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.thePlayer == null)return;
		
		if (e.type == ElementType.HELMET && mc.thePlayer.isInsideOfMaterial(BlockEnderGoo.enderGoo)){
			int w = e.resolution.getScaledWidth(), h = e.resolution.getScaledHeight();

			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(1F,1F,1F,1F);
			
			mc.getTextureManager().bindTexture(texGoo);
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(0D,h,-90D,0D,1D);
			tessellator.addVertexWithUV(w,h,-90D,1D,1D);
			tessellator.addVertexWithUV(w,0D,-90D,1D,0D);
			tessellator.addVertexWithUV(0D,0D,-90D,0D,0D);
			tessellator.draw();
			
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glColor4f(1F,1F,1F,1F);
		}
	}
	
	@SubscribeEvent
	public void onPostRenderGameOverlay(RenderGameOverlayEvent.Post e){
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.thePlayer == null)return;
		
		if (e.type == ElementType.HOTBAR){
			if (!notifications.isEmpty()){
				FontRenderer font = mc.fontRenderer;
				
				boolean prevUnicode = font.getUnicodeFlag();
				font.setUnicodeFlag(true);
	
				int scale = e.resolution.getScaleFactor();
				double fontScale = scale == 1 ? 2D:
								   scale == 2 ? 1D:
								   scale == 3 ? 0.677D : 0.5D,
					   fontScale2 = 1D/fontScale;
				
				GL11.glPushMatrix();
				GL11.glScaled(fontScale,fontScale,fontScale);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glColor4f(1F,1F,1F,1F);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				
				int scaledW = (int)(e.resolution.getScaledWidth()*fontScale2), scaledH = (int)(e.resolution.getScaledHeight()*fontScale2);
	
				for(Iterator<Notification> iter = notifications.iterator(); iter.hasNext();){
					Notification n = iter.next();
					
					int w = font.getStringWidth(n.text);
					font.drawStringWithShadow(n.text,scaledW-w-3,scaledH-10-n.yy,((int)Math.max(4,Math.floor(255F*n.alpha)))<<24|255<<16|255<<8|255);
					if (n.update())iter.remove();
				}
				
				font.setUnicodeFlag(prevUnicode);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glPopMatrix();
			}
			
			if (clusterLookedAt != null){
				FontRenderer font = mc.fontRenderer;
				int x = e.resolution.getScaledWidth()>>1;
				int y = e.resolution.getScaledHeight()>>1;
				
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glColor4f(1F,1F,1F,1F);
				
				drawStringCentered(font,I18n.format("energy.overlay.title"),x,y-40,255,255,255);
				drawStringCentered(font,I18n.format("energy.overlay.holding").replace("$",DragonUtil.formatTwoPlaces.format(clusterLookedAt.data.getEnergyLevel())),x,y-30,220,220,220);
				drawStringCentered(font,I18n.format("energy.overlay.regen").replace("$",DragonUtil.formatTwoPlaces.format(clusterLookedAt.data.getMaxEnergyLevel())),x,y-20,220,220,220);

				EnergyClusterHealth health = clusterLookedAt.data.getHealthStatus();
				drawStringCentered(font,I18n.format(health.translationText),x,y-10,health.color);

				GL11.glDisable(GL11.GL_BLEND);
				clusterLookedAt = null;
			}
		}
	}
	
	private void drawStringCentered(FontRenderer font, String text, int x, int y, int red, int green, int blue){
		font.drawStringWithShadow(text,x-(font.getStringWidth(text)>>1),y-(font.FONT_HEIGHT>>1),220<<24|red<<16|green<<8|blue);
	}
	
	private void drawStringCentered(FontRenderer font, String text, int x, int y, int color){
		font.drawStringWithShadow(text,x-(font.getStringWidth(text)>>1),y-(font.FONT_HEIGHT>>1),color);
	}
	
	@SubscribeEvent
	public void onRenderBlockOutline(DrawBlockHighlightEvent e){
		tmpPos.set(e.target.blockX,e.target.blockY,e.target.blockZ);
		
		if (tmpPos.getBlock(e.player.worldObj) == BlockList.energy_cluster){
			clusterLookedAt = (TileEntityEnergyCluster)tmpPos.getTileEntity(e.player.worldObj);
			e.setCanceled(true);
		}
	}
	
	private static class Notification{
		final String text;
		byte yy;
		float alpha;
		long lastTime;

		Notification(String text){
			this.text = text;
		}

		boolean update(){
			if (lastTime == Minecraft.getMinecraft().theWorld.getTotalWorldTime())return false;

			if (((lastTime = Minecraft.getMinecraft().theWorld.getTotalWorldTime())&3) == 0)++yy;

			if (yy < 6)alpha = Math.min(1F,alpha+0.1F);
			else if (yy > 10 && (alpha -= 0.1F) <= 0F)return true;

			return false;
		}
	}
}
