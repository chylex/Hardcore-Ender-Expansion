package chylex.hee.mechanics.compendium.elements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
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
		ObjectDisplayElement.renderObject(obj,x,MathUtil.ceil(y+yOffPrev+(yOff-yOffPrev)*partialTickTime),CompendiumEventsClient.getClientData(),gui);
		
		long time = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
		
		if (lastTime != time){
			time = lastTime;
			yOffPrev = yOff;
			
			if (state == 0){
				if ((yOff += 2F) >= 16F)state = 1;
			}
			else if (state < 100)++state;
			else if ((yOff -= 2F) <= 0F)return true;
		}
		
		return false;
	}
}
