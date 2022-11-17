package cat.jiu.dialog.ui;

import java.awt.Color;

import cat.jiu.dialog.element.IDialogOption;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDialogTextField extends GuiTextField {
	protected final IDialogOption option;
	protected final FontRenderer fontRenderer;
	
	public GuiDialogTextField(IDialogOption option, int componentId, FontRenderer fontrendererObj, int x, int y, int width) {
		super(componentId, fontrendererObj, x, y, width - option.getOptionString().getStringWidth(fontrendererObj), 12);
		this.option = option;
		this.fontRenderer = fontrendererObj;
	}
	@Override
	public void drawTextBox() {
		super.drawTextBox();
		String text = this.option.getOptionString().format();
		this.fontRenderer.drawString(text+":", x - this.fontRenderer.getStringWidth(text), y, Color.white.getRGB());
	}
	
	public ConfirmButton getConfirmButtom(int componentId) {
		return new ConfirmButton(this, componentId, x+option.getOptionString().getStringWidth(this.fontRenderer)+this.width, y);
	}
	
	public static class ConfirmButton extends GuiButton {
		protected static final String confirmText = I18n.format("dialog.text.confirm");
		protected final GuiDialogTextField text;
		
		public ConfirmButton(GuiDialogTextField option, int buttonId, int x, int y) {
			super(buttonId, x, y, option.option.getOptionString().getStringWidth(option.fontRenderer), 12, confirmText);
			this.text = option;
		}
		@Override
		public void mouseReleased(int mouseX, int mouseY) {
			this.text.option.onOptionSelected(this.text.getText());
		}
	}
}
