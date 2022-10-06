package cat.jiu.caption.event;

import cat.jiu.caption.Caption;
import cat.jiu.caption.Caption.Element;
import cat.jiu.caption.type.DrawState;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CaptionDrawEvent extends Event {
	public final Caption.Element element;
	public final DrawState state;
	public final GuiIngame gui;
	
	protected CaptionDrawEvent(Caption.Element element, DrawState state) {
		this.element = element;
		this.state = state;
		this.gui = Minecraft.getMinecraft().ingameGUI;
	}

	@Cancelable
	public static class Pre extends CaptionDrawEvent {
		public Pre(Element element, DrawState state) {
			super(element, state);
		}
	}
	public static class Post extends CaptionDrawEvent {
		public Post(Element element, DrawState state) {
			super(element, state);
		}
	}
}
