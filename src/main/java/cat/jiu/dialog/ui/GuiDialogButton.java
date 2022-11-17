package cat.jiu.dialog.ui;

import java.awt.Color;

import cat.jiu.caption.element.CaptionText;
import cat.jiu.dialog.element.DialogButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiDialogButton extends GuiButton {
	protected final DialogButton option;
	public GuiDialogButton(DialogButton option, int buttonId, int x, int y) {
		super(buttonId, x, y, "");
		this.option = option;
	}
	public GuiDialogButton(DialogButton option, int buttonId, int x, int y, int widthIn, int heightIn) {
		super(buttonId, x, y, widthIn, heightIn, "");
		this.option = option;
	}
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		CaptionText text = this.option.getOptionString();
		if(text.isCenter()) {
			this.drawCenteredString(mc.fontRenderer, text.format(), this.x + this.width / 2, this.y + (this.height - 8) / 2, Color.WHITE.getRGB());
		}else {
			this.drawString(mc.fontRenderer, text.format(), this.x, this.y, Color.WHITE.getRGB());
		}
	}
	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		this.option.onOptionSelected();
	}
}
