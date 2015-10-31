package chylex.hee.mechanics.compendium.elements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.events.CompendiumEventsClient;
import chylex.hee.mechanics.compendium.render.ObjectDisplayElement;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class KnowledgeNotification{
	private final KnowledgeObject<?> obj;
	private long lastTime;
	private float yOff, yOffPrev;
	private byte state;
	
	public KnowledgeNotification(KnowledgeObject<?> obj){
		this.obj = obj;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean render(Gui gui, float partialTickTime, int x, int y){
		long time = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
		
		if (lastTime != time){
			lastTime = time;
			yOffPrev = yOff;
			
			if (state == 0){
				if ((yOff += 4F) >= 25F){
					yOff = 25F;
					state = 1;
				}
			}
			else if (state < 110)++state;
			else if ((yOff -= 4F) <= 0F)return true;
		}
		
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		ObjectDisplayElement.renderObject(obj,x,MathUtil.ceil(y-(yOffPrev+(yOff-yOffPrev)*partialTickTime)),CompendiumEventsClient.getClientData(),gui);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		
		return false;
	}
}
