package cat.jiu.caption;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import cat.jiu.dialog.element.Dialog;
import cat.jiu.dialog.element.IDialogOption;
import cat.jiu.dialog.ui.GuiDialog;

import io.netty.buffer.ByteBuf;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public final class DialogAPI {
	public static void displayDialog(EntityPlayer player, Dialog dialog) {
		if(player.world.isRemote) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiDialog(dialog));
		}else {
			CaptionMain.net.sendMessageToPlayer(new MsgDialog(dialog), (EntityPlayerMP) player);
		}
	}
	private static final HashMap<Integer, Class<? extends IDialogOption>> options = Maps.newHashMap();
	public static boolean registryOption(Class<? extends IDialogOption> option) {
		if(!options.containsValue(option)) {
			options.put(options.size(), option);
			return true;
		}
		return false;
	}
	public static int getOptionType(Class<? extends IDialogOption> option) {
		for(Entry<Integer, Class<? extends IDialogOption>> opt : options.entrySet()) {
			if(opt.getValue()==option) {
				return opt.getKey();
			}
		}
		return -1;
	}
	public static Class<? extends IDialogOption> getOptionType(int type) {
		return options.get(type);
	}
	public static boolean hasOption(int type) {
		return options.containsKey(type);
	}
	
	public final static class MsgDialog implements IMessage {
		private Dialog dialog;
		public MsgDialog() {}
		private MsgDialog(Dialog dialog) {
			this.dialog = dialog;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			try {
				this.dialog = Dialog.get(new PacketBuffer(buf).readCompoundTag());
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void toBytes(ByteBuf buf) {
			new PacketBuffer(buf).writeCompoundTag(this.dialog.writeToNBT(new NBTTagCompound()));
		}
		
		public IMessage handler(MessageContext ctx) {
			if(ctx.side.isClient()) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiDialog(this.dialog));
			}
			return null;
		}
	}
}
