package cat.jiu.caption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cat.jiu.caption.proxy.ServerProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

@Mod(
	modid = CaptionMain.MODID,
	name = CaptionMain.NAME,
	version = "1.0.0",
	useMetadata = true,
	dependencies = "after:jiucore@[1.1.1-a0-20220728034743,);",
	acceptedMinecraftVersions = "[1.12.2]")
public class CaptionMain {
	public static final String MODID = "caption";
	public static final String NAME = "Caption";
	public static final String OWNER = "small_jiu";
	public static final Logger log = LogManager.getLogger(NAME);
	static CaptionNetworkHandler net;

	@SidedProxy(
		serverSide = "cat.jiu.caption.proxy.ServerProxy",
		clientSide = "cat.jiu.caption.proxy.ClientProxy",
		modId = CaptionMain.MODID)
	public static ServerProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		net = new CaptionNetworkHandler();
	}

	@Mod.EventHandler
	public void onServerStopped(FMLServerStoppedEvent event) {
		Caption.clearCaptions();
	}
}
