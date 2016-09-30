package chylex.hee.mechanics.compendium.elements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.events.CompendiumEventsClient;
import chylex.hee.system.abstractions.GL;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class KnowledgeNotification{
	private final KnowledgeObject<?> obj;
	private long lastTime;
	private float yOff, yOffPrev;
	private int state;
	
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
		
		GL.enableRescaleNormal();
		CompendiumObjectElement.renderObject(obj, x, MathUtil.ceil(y-(yOffPrev+(yOff-yOffPrev)*partialTickTime)), CompendiumEventsClient.getClientData(), gui, false);
		GL.disableRescaleNormal();
		
		return false;
	}
}
