package cat.jiu.caption.element.style;

import java.awt.Color;

import cat.jiu.caption.Caption;
import cat.jiu.caption.jiucore.time.ICaptionTime;
import cat.jiu.caption.type.DisplaySideType;
import cat.jiu.caption.type.DisplayStyle;
import cat.jiu.caption.type.DrawState;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;

@SuppressWarnings("incomplete-switch")
public class StyleUpToDown extends DisplayStyle {
	public static final StyleUpToDown instance = new StyleUpToDown();
	private StyleUpToDown() {
		super(1, DisplaySideType.VALUES);
	}
	@Override
	public void draw(GuiIngame gui, DisplaySideType side, DrawState stage, int x, int y, int width, int height, boolean needBg, ICaptionTime show_pre_delay, ICaptionTime show_post_delay, Minecraft mc, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
		switch(side) {
			case DOWN:
				this.drawDown(gui, side, stage, x, y, width, height, needBg, show_pre_delay, show_post_delay, mc, fr, sr, centerX, centerY);
				break;
			case LEFT:
				this.drawLeft(gui, side, stage, x, y, width, height, needBg, show_pre_delay, show_post_delay, mc, fr, sr, centerX, centerY);
				break;
			case RIGHT:
				this.drawRight(gui, side, stage, x, y, width, height, needBg, show_pre_delay, show_post_delay, mc, fr, sr, centerX, centerY);
				break;
		}
	}
	protected void drawDown(GuiIngame gui, DisplaySideType side, DrawState stage, int x, int y, int width, int height, boolean needBg, ICaptionTime show_pre_delay, ICaptionTime show_post_delay, Minecraft mc, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
		switch(stage) {
			case PRE:
				if(needBg) {
					int pre_part = 6 - show_pre_delay.getPart(5);
					if(pre_part != -1) {
						mc.getTextureManager().bindTexture(Caption.Element.down_texture);
						float pre_h = (height / 5.0F) * pre_part;
						Gui.drawModalRectWithCustomSizedTexture(x - 55, y - 3, 0, 0, 256, (int) pre_h, 256, (int) pre_h);
					}
				}
				break;
			case POST:
				if(needBg) {
					int post_part = show_post_delay.getPart(5);
					if(post_part > 0) {
						mc.getTextureManager().bindTexture(Caption.Element.down_texture);
						float post_h = height / 5.0F * post_part;
						Gui.drawModalRectWithCustomSizedTexture(x - 55, y - 3, 0, 0, 256, (int) post_h, 256, (int) post_h);
					}
				}
				break;
		}
	}
	protected void drawLeft(GuiIngame gui, DisplaySideType side, DrawState stage, int x, int y, int width, int height, boolean needBg, ICaptionTime show_pre_delay, ICaptionTime show_post_delay, Minecraft mc, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
		switch(stage) {
			case PRE:
				if(needBg) {
					int pre_part = 6 - show_pre_delay.getPart(5);
					if(pre_part != -1) {
						mc.getTextureManager().bindTexture(Caption.Element.down_texture);
						float pre_h = (59.0F / 5.0F) * pre_part;
						Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, (int) pre_h, width, pre_h);
					}
				}
				break;
			case POST:
				if(needBg) {
					int post_part = show_post_delay.getPart(5);
					if(post_part > 0) {
						mc.getTextureManager().bindTexture(Caption.Element.down_texture);
						float post_h = (59.0F / 5.0F) * post_part;
						Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, (int) post_h, width, post_h);
					}
				}
				break;
		}
	}
	protected void drawRight(GuiIngame gui, DisplaySideType side, DrawState stage, int x, int y, int width, int height, boolean needBg, ICaptionTime show_pre_delay, ICaptionTime show_post_delay, Minecraft mc, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
		gui.drawString(fr, x + " x " + y, 0, 0, Color.RED.getRGB());
		switch(stage) {
			case PRE:
				if(needBg) {
					int pre_part = 6 - show_pre_delay.getPart(5);
					if(pre_part != -1) {
						mc.getTextureManager().bindTexture(Caption.Element.down_texture);
						float pre_h = (59.0F / 5.0F) * pre_part;
						Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, (int) pre_h, width, pre_h);
					}
				}
				break;
			case POST:
				if(needBg) {
					int post_part = show_post_delay.getPart(5);
					if(post_part > 0) {
						mc.getTextureManager().bindTexture(Caption.Element.down_texture);
						float post_h = (59.0F / 5.0F) * post_part;
						Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, (int) post_h, width, post_h);
					}
				}
				break;
		}
	}
}
