package cat.jiu.caption.element.style;

import cat.jiu.caption.element.Caption;
import cat.jiu.caption.type.DisplaySideType;
import cat.jiu.caption.type.DisplayStyle;
import cat.jiu.caption.type.DrawState;
import cat.jiu.core.api.ITimer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;

public class UpToDownStyle extends DisplayStyle {
	public static final UpToDownStyle instance = new UpToDownStyle();
	private UpToDownStyle() {
		super(1, DisplaySideType.TYPES);
	}
	protected UpToDownStyle(DisplaySideType... sides) {
		super(sides);
	}
	@Override
	public void draw(GuiIngame gui, DisplaySideType side, DrawState stage, int x, int y, int width, int height, boolean needBg, ITimer show_pre_delay, ITimer show_post_delay, Minecraft mc, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
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
			default:break;
		}
	}
	protected void drawDown(GuiIngame gui, DisplaySideType side, DrawState stage, int x, int y, int width, int height, boolean needBg, ITimer show_pre_delay, ITimer show_post_delay, Minecraft mc, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
		switch(stage) {
			case PRE:
				if(needBg) {
					int pre_part = 6 - show_pre_delay.getPart(5);
					if(pre_part != -1) {
						mc.getTextureManager().bindTexture(Caption.down_texture);
						float pre_h = (height / 5.0F) * pre_part;
						Gui.drawModalRectWithCustomSizedTexture(x - 55, y - 3, 0, 0, 256, (int) pre_h, 256, (int) pre_h);
					}
				}
				break;
			case POST:
				if(needBg) {
					int post_part = show_post_delay.getPart(5);
					if(post_part > 0) {
						mc.getTextureManager().bindTexture(Caption.down_texture);
						float post_h = height / 5.0F * post_part;
						Gui.drawModalRectWithCustomSizedTexture(x - 55, y - 3, 0, 0, 256, (int) post_h, 256, (int) post_h);
					}
				}
				break;
			default:break;
		}
	}
	protected void drawLeft(GuiIngame gui, DisplaySideType side, DrawState stage, int x, int y, int width, int height, boolean needBg, ITimer show_pre_delay, ITimer show_post_delay, Minecraft mc, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
		switch(stage) {
			case PRE:
				if(needBg) {
					int pre_part = 6 - show_pre_delay.getPart(5);
					if(pre_part != -1) {
						mc.getTextureManager().bindTexture(Caption.down_texture);
						float pre_h = (59.0F / 5.0F) * pre_part;
						Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, (int) pre_h, width, pre_h);
					}
				}
				break;
			case POST:
				if(needBg) {
					int post_part = show_post_delay.getPart(5);
					if(post_part > 0) {
						mc.getTextureManager().bindTexture(Caption.down_texture);
						float post_h = (59.0F / 5.0F) * post_part;
						Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, (int) post_h, width, post_h);
					}
				}
				break;
			default:break;
		}
	}
	protected void drawRight(GuiIngame gui, DisplaySideType side, DrawState stage, int x, int y, int width, int height, boolean needBg, ITimer show_pre_delay, ITimer show_post_delay, Minecraft mc, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
		switch(stage) {
			case PRE:
				if(needBg) {
					int pre_part = 6 - show_pre_delay.getPart(5);
					if(pre_part != -1) {
						mc.getTextureManager().bindTexture(Caption.down_texture);
						float pre_h = (59.0F / 5.0F) * pre_part;
						Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, (int) pre_h, width, pre_h);
					}
				}
				break;
			case POST:
				if(needBg) {
					int post_part = show_post_delay.getPart(5);
					if(post_part > 0) {
						mc.getTextureManager().bindTexture(Caption.down_texture);
						float post_h = (59.0F / 5.0F) * post_part;
						Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, (int) post_h, width, post_h);
					}
				}
				break;
			default:break;
		}
	}
}
