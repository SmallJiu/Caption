package cat.jiu.caption;

import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class CaptionNetworkHandler {
	private SimpleNetworkWrapper channel;
	private static int ID = 0;
	private static int nextID() {
		return ID++;
	}
	
	public CaptionNetworkHandler() {
		this.channel = NetworkRegistry.INSTANCE.newSimpleChannel(CaptionMain.MODID);
		this.channel.registerMessage(Caption.MsgCaption::handler, Caption.MsgCaption.class, nextID(), Side.CLIENT);
		this.channel.registerMessage(Caption.MsgCaption::handler, Caption.MsgCaption.class, nextID(), Side.SERVER);
	}
	
	/** server to client */
	public void sendMessageToPlayer(IMessage msg, EntityPlayerMP player) {
		channel.sendTo(msg, player);
	}
	
	/** client to server */
	public void sendMessageToServer(IMessage msg) {
		channel.sendToServer(msg);
	}
}
