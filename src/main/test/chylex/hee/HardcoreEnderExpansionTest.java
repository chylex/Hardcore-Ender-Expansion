package chylex.hee;
import chylex.hee.test.UnitTest.RunTime;
import chylex.hee.test.UnitTester;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "HardcoreEnderExpansionTest", name = "HEE Unit Tester", version = "0")
public class HardcoreEnderExpansionTest{
	public HardcoreEnderExpansionTest(){
		UnitTester.load();
		UnitTester.trigger(RunTime.CONSTRUCT);
	}
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e){
		UnitTester.trigger(RunTime.PREINIT);
	}
	
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent e){
		try{
			UnitTester.trigger(RunTime.LOADCOMPLETE);
			UnitTester.finalizeEventTests();
		}
		catch(Throwable t){
			FMLCommonHandler.instance().raiseException(t,"Error running LoadComplete event in Hardcore Ender Expansion!",true);
		}
	}
}
