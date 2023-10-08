package cat.jiu.caption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cat.jiu.caption.proxy.ServerProxy;
import cat.jiu.caption.util.CaptionNetworkHandler;
import cat.jiu.core.api.element.IImage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
	modid = ModMain.MODID,
	name = ModMain.NAME,
	version = ModMain.VERSION,
	useMetadata = true,
	dependencies = "after:jiucore@[1.1.6-a1,)",
	acceptedMinecraftVersions = "[1.12.2]")
public class ModMain {
	public static final String MODID = "caption";
	public static final String NAME = "Caption";
	public static final String OWNER = "small_jiu";
	public static final String VERSION = "1.0.1-a3";
	public static final Logger log = LogManager.getLogger("Caption");
	private static final CaptionNetworkHandler net= new CaptionNetworkHandler();
	public static CaptionNetworkHandler getNetworkHandler() {
		return net;
	}
	
	@SidedProxy(
		serverSide = "cat.jiu.caption.proxy.ServerProxy",
		clientSide = "cat.jiu.caption.proxy.ClientProxy",
		modId = ModMain.MODID)
	public static ServerProxy proxy;
	static {
		IImage.DEFAULT_IMAGE.setImg(new ResourceLocation(MODID, "textures/gui/default_img.png"));
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {}
}
