package cat.jiu.caption.iface;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICaption {
	@SideOnly(Side.CLIENT)
	void preDraw(ScaledResolution sr, Minecraft mc, GuiIngame gui, FontRenderer fr);
	@SideOnly(Side.CLIENT)
	void draw(ScaledResolution sr, Minecraft mc, GuiIngame gui, FontRenderer fr);
	@SideOnly(Side.CLIENT)
	void postDraw(ScaledResolution sr, Minecraft mc, GuiIngame gui, FontRenderer fr);
}
