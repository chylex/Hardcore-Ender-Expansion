package chylex.hee.render;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import org.lwjgl.opengl.GL11;
import chylex.hee.block.BlockEnderGoo;
import chylex.hee.gui.helpers.GuiAchievementOverlay;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.elements.KnowledgeNotification;
import chylex.hee.mechanics.energy.EnergyClusterData;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.GameRegistryUtil;
import chylex.hee.tileentity.TileEntityEnergyCluster;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class OverlayManager{
	private static final OverlayManager instance = new OverlayManager();
	private static final ResourceLocation texGoo = new ResourceLocation("hardcoreenderexpansion:textures/overlay/endergoo.png");
	private static final PosMutable tmpPos = new PosMutable();
	
	private static final KnowledgeNotification[] notifications = new KnowledgeNotification[8];
	private static boolean hasNotification;
	
	private static final GuiAchievementOverlay achievementOverlay = new GuiAchievementOverlay();
	
	private TileEntityEnergyCluster clusterLookedAt;
	
	public static void addNotification(final KnowledgeObject<?> obj){
		hasNotification = true;
		
		IntStream.range(0,notifications.length).filter(index -> notifications[index] == null).findFirst().ifPresent(index -> {
			notifications[index] = new KnowledgeNotification(obj);
		});
	}
	
	public static GuiAchievementOverlay getAchievementOverlay(){
		return achievementOverlay;
	}
	
	public static void register(){
		GameRegistryUtil.registerEventHandler(instance);
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
			achievementOverlay.update();
			
			if (hasNotification){
				GL11.glColor4f(1F,1F,1F,1F);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				
				for(int ind = 0; ind < notifications.length; ind++){
					if (notifications[ind] != null && notifications[ind].render(mc.ingameGUI,e.partialTicks,e.resolution.getScaledWidth()-13-24*ind,e.resolution.getScaledHeight()+12)){
						notifications[ind] = null;
						if (Arrays.stream(notifications).allMatch(Objects::isNull))hasNotification = false;
					}
				}
				
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}
			
			if (clusterLookedAt != null){
				FontRenderer font = mc.fontRenderer;
				int x = e.resolution.getScaledWidth()>>1;
				int y = e.resolution.getScaledHeight()>>1;
				
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glColor4f(1F,1F,1F,1F);
				
				EnergyClusterData data = clusterLookedAt.getData().orElse(null);
				
				if (data != null){
					drawStringCentered(font,I18n.format("energy.overlay.title"),x,y-40,255,255,255);
					drawStringCentered(font,I18n.format("energy.overlay.holding").replace("$",DragonUtil.formatTwoPlaces.format(data.getEnergyLevel())),x,y-30,220,220,220);
					drawStringCentered(font,I18n.format("energy.overlay.regen").replace("$",DragonUtil.formatTwoPlaces.format(data.getMaxLevel())),x,y-20,220,220,220);
					drawStringCentered(font,I18n.format(data.getHealth().translationText),x,y-10,data.getHealth().color);
				}

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
		if (e.target.typeOfHit != MovingObjectType.BLOCK)return; // why the fuck is this getting called for entities
		
		tmpPos.set(e.target.blockX,e.target.blockY,e.target.blockZ);
		
		if (tmpPos.getBlock(e.player.worldObj) == BlockList.energy_cluster){
			clusterLookedAt = (TileEntityEnergyCluster)tmpPos.getTileEntity(e.player.worldObj);
			e.setCanceled(true);
		}
	}
}
