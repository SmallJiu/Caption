package cat.jiu.caption.element.style;

import cat.jiu.caption.type.DisplaySideType;
import cat.jiu.caption.type.DisplayStyle;
import cat.jiu.caption.type.DrawState;
import cat.jiu.core.api.ITimer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;

public class RightToLeftStyle extends DisplayStyle {
	public static final RightToLeftStyle instance = new RightToLeftStyle();
	private RightToLeftStyle() {
		super(3, DisplaySideType.DOWN, DisplaySideType.RIGHT);
	}
	protected RightToLeftStyle(DisplaySideType... sides) {
		super(sides);
	}
	@Override
	public void draw(GuiIngame gui, DisplaySideType side, DrawState stage, int x, int y, int width, int height, boolean needBg, ITimer show_pre_delay, ITimer show_post_delay, Minecraft mc, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
		
	}
}
