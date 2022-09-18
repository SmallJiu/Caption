package cat.jiu.caption.event;

import cat.jiu.caption.Caption;
import cat.jiu.caption.type.DrawState;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Cancelable
@SideOnly(Side.CLIENT)
public class CaptionDrawEvent extends Event {
	public final Caption.Element element;
	public final DrawState state;
	public CaptionDrawEvent(Caption.Element element, DrawState state) {
		this.element = element;
		this.state = state;
	}
}
