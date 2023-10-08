package cat.jiu.caption.iface;

import cat.jiu.caption.type.CaptionType;
import cat.jiu.caption.type.DisplaySideType;
import cat.jiu.core.api.ITimer;
import cat.jiu.core.api.element.IImage;
import cat.jiu.core.api.element.ISound;
import cat.jiu.core.api.element.IText;
import crafttweaker.annotations.ZenRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("caption.Caption")
public interface ICaption {
	@SideOnly(Side.CLIENT)
	void preDraw(ScaledResolution sr, Minecraft mc, GuiIngame gui, FontRenderer fr);
	@SideOnly(Side.CLIENT)
	void draw(ScaledResolution sr, Minecraft mc, GuiIngame gui, FontRenderer fr);
	@SideOnly(Side.CLIENT)
	void postDraw(ScaledResolution sr, Minecraft mc, GuiIngame gui, FontRenderer fr);
	
	@ZenMethod
	ICaption copy();
	@ZenMethod
	void changeTo(ICaption other);
	@ZenMethod
	void updataTime();
	
								public CaptionType getType();
	@ZenGetter("type") 			default String getTypeAsString() {{return String.valueOf(this.getType()).toLowerCase();}}
	@ZenGetter("name") 			public IText getDisplayName();
	@ZenGetter("text") 			public IText getDisplayText();
	@ZenGetter("time") 			public ITimer getTalkTime();
	@ZenGetter("delay") 		public ITimer getDelay();
	@ZenGetter("delay_pre") 	public ITimer getShowPreDelay();
	@ZenGetter("delay_post") 	public ITimer getShowPostDelay();
								public DisplaySideType getDisplaySide();
	@ZenGetter("side")			default String getDisplaySideAsString() {return String.valueOf(this.getDisplaySide()).toLowerCase();}
	@ZenGetter("image") 		public IImage getDisplayImg();
	@ZenGetter("sound") 		public ISound getSound();
	@ZenGetter("needBG") 		public boolean isNeedBackground();
	
	@ZenMethod("name") 			public ICaption setDisplayName(IText displayName);
	@ZenMethod("text") 			public ICaption setDisplayText(IText displayText);
	@ZenMethod("image") 		public ICaption setDisplayImg(IImage displayImg);
	@ZenMethod("delay_pre") 	public ICaption setShowPreDelay(ITimer show_pre_delay);
	@ZenMethod("delay_post") 	public ICaption setShowPostDelay(ITimer show_post_delay);

	@ZenMethod
	public NBTTagCompound toNBT();
}
