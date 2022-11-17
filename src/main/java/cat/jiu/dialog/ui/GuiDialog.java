package cat.jiu.dialog.ui;

import java.awt.Color;
import java.util.List;

import com.google.common.collect.Lists;

import cat.jiu.dialog.element.Dialog;
import cat.jiu.dialog.element.DialogButton;
import cat.jiu.dialog.element.IDialogOption;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDialog extends GuiScreen {
	protected final Dialog dialog;
	protected final int maxTextWidth;
	protected List<GuiDialogTextField> textFields;
	
	public GuiDialog(Dialog dialog) {
		this.dialog = dialog;
		int width = 0;
		for(IDialogOption option : dialog.getOptions()) {
			width = Math.max(width, this.fontRenderer.getStringWidth(option.getOptionString().format()));
		}
		this.maxTextWidth = width;
	}
	protected static final int textX = 50;
	protected int getTextLength() {
		return 100;
	}
	protected int getDrawX() {
		return 100;
	}
	protected int getDrawY() {
		return 100;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		int y = this.getDrawY();
		int id = 0;
		for(int i = 0; i < this.dialog.getOptions().size(); i++) {
			IDialogOption option = this.dialog.getOptions().get(i);
			if(option instanceof DialogButton) {
				super.addButton(new GuiDialogButton((DialogButton) option, id++, this.getDrawX(), y));
			}else if(option instanceof GuiDialogTextField) {
				if(this.textFields==null) this.textFields=Lists.newArrayList();
				GuiDialogTextField field = new GuiDialogTextField(option, id++, fontRenderer, this.getDrawX(), y, 100);
				this.textFields.add(field);
				super.addButton(field.getConfirmButtom(id++));
			}else {
				this.addCustomOption(option, this.getDrawX(), y);
			}
			y += 11;
		}
	}
	protected void addCustomOption(IDialogOption option, int x, int y) {}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		int y = this.getDrawY();
		List<String> title = splitString(dialog.getTitle().format(), this.getTextLength(), this.fontRenderer);
		for(int j = 0; j < title.size(); j++) {
			y += 11;
			super.drawString(this.fontRenderer, this.dialog.getTitle().format(), this.getDrawX(), y + (j*11), Color.WHITE.getRGB());
		}
		for(int i = 0; i < this.dialog.getOptions().size(); i++) {
			IDialogOption dialog = this.dialog.getOptions().get(i);
			
			List<String> option = splitString(dialog.getOptionString().format(), this.getTextLength(), this.fontRenderer);
			for(int j = 0; j < option.size(); j++) {
				y += 11;
				super.drawString(this.fontRenderer, option.get(j), this.getDrawX(), y, Color.WHITE.getRGB());
			}
		}
	}
	
	public static boolean isInRange(int mouseX, int mouseY, int x, int y, int width, int height) {
		int maxX = x + width;
		int maxY = y + height;
		return (mouseX >= x && mouseY >= y) && (mouseX <= maxX && mouseY <= maxY);
	}
	
	public static List<String> splitString(String text, int textMaxLength, FontRenderer fr) {
		List<String> texts = Lists.newArrayList();
		if(fr.getStringWidth(text) >= textMaxLength) {
			StringBuilder s = new StringBuilder();
			for(int i = 0; i < text.length(); i++) {
				s.append(text.charAt(i));
				String str = s.toString();
				if(fr.getStringWidth(str) >= textMaxLength) {
					texts.add(str);
					s.setLength(0);
				}
			}
			if(s.length() > 0) {
				texts.add(s.toString());
			}
		}else {
			texts.add(text);
		}
		
		return texts;
	}
}
