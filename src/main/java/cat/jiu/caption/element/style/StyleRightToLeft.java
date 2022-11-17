package cat.jiu.caption.element.style;

import cat.jiu.caption.jiucore.time.ICaptionTime;
import cat.jiu.caption.type.DisplaySideType;
import cat.jiu.caption.type.DisplayStyle;
import cat.jiu.caption.type.DrawState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;

public class StyleRightToLeft extends DisplayStyle {
	public static final StyleRightToLeft instance = new StyleRightToLeft();
	private StyleRightToLeft() {
		super(3, DisplaySideType.DOWN, DisplaySideType.RIGHT);
	}
	@Override
	public void draw(GuiIngame gui, DisplaySideType side, DrawState stage, int x, int y, int width, int height, boolean needBg, ICaptionTime show_pre_delay, ICaptionTime show_post_delay, Minecraft mc, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
		
	}
}
