package cat.jiu.caption.element.style;

import cat.jiu.caption.type.DisplaySideType;
import cat.jiu.caption.type.DisplayStyle;
import cat.jiu.caption.type.DrawState;
import cat.jiu.core.api.ITimer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;

public class CenterToSideStyle extends DisplayStyle {
	public static final CenterToSideStyle instance = new CenterToSideStyle();
	private CenterToSideStyle() {
		super(0, DisplaySideType.DOWN);
	}
	protected CenterToSideStyle(DisplaySideType[] sides) {
		super(sides);
	}
	@Override
	public void draw(GuiIngame gui, DisplaySideType side, DrawState stage, int x, int y, int width, int height, boolean needBg, ITimer show_pre_delay, ITimer show_post_delay, Minecraft mc, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
		switch(stage) {
			case PRE:
				
				break;
			case POST:
				
				break;
			default:break;
		}
	}
}
