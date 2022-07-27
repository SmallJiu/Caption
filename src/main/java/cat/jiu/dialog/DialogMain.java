package cat.jiu.dialog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cat.jiu.dialog.proxy.ServerProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
	modid = DialogMain.MODID,
	name = DialogMain.NAME,
	version = "1.0.0",
	useMetadata = true,
//	guiFactory = "cat.jiu.email.util.ConfigGuiFactory",
	dependencies = "after:jiucore@[1.1.1-a0-20220728034743,);",
	acceptedMinecraftVersions = "[1.12.2]")
@Mod.EventBusSubscriber
public class DialogMain {
	public static final String MODID = "dialog";
	public static final String NAME = "Dialog";
	public static final String OWNER = "small_jiu";
	public static final Logger log = LogManager.getLogger(NAME);
	public static DialogNetworkHandler net;

	@SidedProxy(
		serverSide = "cat.jiu.dialog.proxy.ServerProxy",
		clientSide = "cat.jiu.dialog.proxy.ClientProxy",
		modId = DialogMain.MODID)
	public static ServerProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		net = new DialogNetworkHandler();
	}
}
