package cat.jiu.caption.event;

import cat.jiu.caption.element.Caption;
import cat.jiu.caption.type.DrawState;

import net.minecraft.client.gui.GuiIngame;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CaptionDrawEvent extends Event {
	public final Caption element;
	public final DrawState state;
	public final GuiIngame gui;
	
	protected CaptionDrawEvent(GuiIngame gui, Caption element, DrawState state) {
		this.element = element;
		this.state = state;
		this.gui = gui;
	}

	@Cancelable
	public static class Pre extends CaptionDrawEvent {
		public Pre(GuiIngame gui, Caption element, DrawState state) {
			super(gui, element, state);
		}
	}
	public static class Post extends CaptionDrawEvent {
		public Post(GuiIngame gui, Caption element, DrawState state) {
			super(gui, element, state);
		}
	}
}
