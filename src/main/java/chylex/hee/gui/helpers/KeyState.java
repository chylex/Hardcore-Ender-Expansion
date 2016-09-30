package chylex.hee.gui.helpers;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.apache.commons.lang3.BooleanUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class KeyState{
	private static final TIntObjectHashMap<Boolean> keyMap = new TIntObjectHashMap<>(8);
	
	public static void startTracking(int keyCode){
		keyMap.putIfAbsent(keyCode, Boolean.FALSE);
	}
	
	public static void stopTracking(int keyCode){
		keyMap.remove(keyCode);
	}
	
	public static void setState(int keyCode, boolean isHeld){
		if (keyMap.contains(keyCode))keyMap.put(keyCode, Boolean.valueOf(isHeld));
	}
	
	public static boolean isHeld(int keyCode){
		return BooleanUtils.isTrue(keyMap.get(keyCode));
	}
}
