package cat.jiu.caption.element;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import cat.jiu.caption.type.CaptionType;
import cat.jiu.caption.type.DisplaySideType;
import cat.jiu.caption.type.DisplayStyle;
import cat.jiu.caption.type.DrawState;
import cat.jiu.caption.util.CaptionImp;
import cat.jiu.core.api.ITimer;
import cat.jiu.core.api.element.IImage;
import cat.jiu.core.api.element.ISound;
import cat.jiu.core.api.element.IText;
import cat.jiu.core.util.element.Image;
import cat.jiu.core.util.element.Sound;
import cat.jiu.core.util.element.Text;
import cat.jiu.core.util.timer.Timer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("incomplete-switch")
public class Caption {
	protected static final Random rand = new Random();
	protected static final ITimer SHOW_PRE_DELAY = new Timer(5);
	protected static final ITimer SHOW_POST_DELAY = new Timer(5);
	public static final ResourceLocation down_texture = new ResourceLocation("caption:textures/gui/down.png");
	public static final ResourceLocation side_texture = new ResourceLocation("caption:textures/gui/side.png");
	public static final ResourceLocation side_down_texture = new ResourceLocation("caption:textures/gui/side_down.png");
	public static final ResourceLocation default_img = new ResourceLocation("caption:textures/gui/default_img.png");
	
	/** show pos. if is {@link CaptionType#Secondary} and textFields is empty, it will no show on window, but will play sound, and the side will always is DOWN */
	protected final CaptionType type;
	/** speaker textFields, can be translate text */
	protected IText displayName;
	/** speaker speak text, can be translate text */
	protected IText displayText;
	/** caption display delayTick */
	protected final ITimer delay;
	/** show pre transition time */
	protected ITimer show_pre_delay;
	/** caption time of display */
	protected final ITimer displayTime;
	/** show post transition time */
	protected ITimer show_post_delay;
	/** caption display side */
	protected DisplaySideType side;
	/** true if you need black background */
	protected boolean needBg;
	/** speaker images, can be null, the size must be 100x60 */
	protected IImage img;
	/** speak sound, can be null */
	protected final ISound sound;
	/** pre and post delay show style*/
	protected final DisplayStyle style;
	
	public Caption(CaptionType type, IText displayName, IText displayText, DisplayStyle style, ITimer displayTime, DisplaySideType displaySide, ITimer displayDelay, boolean needBG, @Nullable IImage img, @Nullable ISound sound) {
		this.type = type;
		this.displayName = displayName == null ? Text.empty : displayName;
		this.displayText = displayText == null ? Text.empty : displayText;
		this.style = style;
		this.displayTime = displayTime;
		this.delay = displayDelay;
		this.needBg = needBG;
		this.img = img == null ? IImage.DEFAULT_IMAGE : img;
		this.sound = sound;
		this.side = type.isMainCaption() ? DisplaySideType.rand(displaySide) : DisplaySideType.DOWN;
		this.show_pre_delay = SHOW_PRE_DELAY.copy();
		this.show_post_delay = SHOW_POST_DELAY.copy();
	}
	
	@SideOnly(Side.CLIENT)
	public final void draw(ScaledResolution sr, Minecraft mc, GuiIngame gui, FontRenderer fr) {
		int centerX = sr.getScaledWidth() / 2 -1;
        int centerY = sr.getScaledHeight() / 2 - 4;
        
        switch(this.side) {
			case DOWN:
				this.drawDown(DrawState.DRAW, mc, gui, fr, sr, centerX, centerY);
				break;
			case LEFT:
				if(this.type.isMainCaption()) {
					this.drawLeft(DrawState.DRAW, mc, gui, fr, sr, centerX, centerY);
				}
				break;
			case RIGHT:
				if(this.type.isMainCaption()) {
					this.drawRight(DrawState.DRAW, mc, gui, fr, sr, centerX, centerY);
				}
				break;
		}
	}
	
	@SideOnly(Side.CLIENT)
	public final void preDraw(ScaledResolution sr, Minecraft mc, GuiIngame gui, FontRenderer fr) {
        int centerX = sr.getScaledWidth() / 2 -1;
        int centerY = sr.getScaledHeight() / 2 - 4;
        
        switch(this.side) {
			case DOWN:
				this.drawDown(DrawState.PRE, mc, gui, fr, sr, centerX, centerY);
				break;
			case LEFT:
				if(this.type.isMainCaption()) {
					this.drawLeft(DrawState.PRE, mc, gui, fr, sr, centerX, centerY);
				}
				break;
			case RIGHT:
				if(this.type.isMainCaption()) {
					this.drawRight(DrawState.PRE, mc, gui, fr, sr, centerX, centerY);
				}
				break;
		}
	}
	
	@SideOnly(Side.CLIENT)
	public final void postDraw(ScaledResolution sr, Minecraft mc, GuiIngame gui, FontRenderer fr) {
		int centerX = sr.getScaledWidth() / 2 -1;
        int centerY = sr.getScaledHeight() / 2 - 4;
        
        switch(this.side) {
			case DOWN:
				this.drawDown(DrawState.POST, mc, gui, fr, sr, centerX, centerY);
				break;
			case LEFT:
				if(this.type.isMainCaption()) {
					this.drawLeft(DrawState.POST, mc, gui, fr, sr, centerX, centerY);
				}
				break;
			case RIGHT:
				if(this.type.isMainCaption()) {
					this.drawRight(DrawState.POST, mc, gui, fr, sr, centerX, centerY);
				}
				break;
		}
	}

	@SideOnly(Side.CLIENT)
	protected int getX(ScaledResolution sr) {
		return sr.getScaledWidth() / 2 - 73;
	}

	@SideOnly(Side.CLIENT)
	protected int getY(ScaledResolution sr) {
		return sr.getScaledHeight() - 16 - 3 - 73;
	}
	
	@SideOnly(Side.CLIENT)
	protected void drawDown(DrawState stage, Minecraft mc, GuiIngame gui, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
		if(!this.type.isMainCaption() && this.displayName.getText().isEmpty() && this.displayText.getText().isEmpty()) return;
		int x = this.getX(sr);
		int y = this.getY(sr);
		
        List<String> texts = this.splitString(this.displayText.format(), fr);
		int height = 16 + (texts.size() * 10);
		
		int mainY = y;
		if(this.type.isMainCaption() && CaptionImp.hasCurrentCaption(CaptionType.Secondary) && CaptionImp.getCurrentCaption(CaptionType.Secondary).getDelay().isDone()) {
			mainY = y - height - 2;
		}
		
		switch(stage) {
			case PRE:
				if(this.style.isEffectiveSide(side)) {
					this.style.draw(gui, side, stage, x, y, 0, height, needBg, this.show_pre_delay, this.show_post_delay, mc, fr, sr, centerX, centerY);
					break;
				}
				if(needBg) {
					int pre_part = 6 - show_pre_delay.getPart(5);
					if(pre_part != -1) {
						mc.getTextureManager().bindTexture(Caption.down_texture);
						float pre_h = (height / 5.0F) * pre_part;
						Gui.drawModalRectWithCustomSizedTexture(x - 55, y - 3, 0, 0, 256, (int) pre_h, 256, (int) pre_h);
					}
				}
				break;
			case DRAW:
				if(this.needBg) {
					mc.getTextureManager().bindTexture(down_texture);
					Gui.drawModalRectWithCustomSizedTexture(x - 55, mainY - 3, 0, 0, 256, height, 256, height);
					Gui.drawModalRectWithCustomSizedTexture(x - 55, mainY + 9, 0, 0, 256, 1, 256, 1);
				}
				
				if(this.displayName.isCenter()) {
					String name = this.displayName.format();
					fr.drawString(name, centerX - fr.getStringWidth(name) / 2, mainY, Color.YELLOW.getRGB(), false);
					for(int i = 0; i < texts.size(); i++) {
						String text = texts.get(i);
						fr.drawString(text, centerX - fr.getStringWidth(text) / 2, mainY + 1 + 10 + (i * 10), 16777215);
					}
				}else {
					x = x - 55 + 5;
					fr.drawString(this.displayName.format(), x, mainY, Color.YELLOW.getRGB(), false);
					for(int i = 0; i < texts.size(); i++) {
						fr.drawString(texts.get(i), x, mainY + 1 + 10 + (i * 10), 16777215);
					}
				}
				break;
			case POST:
				if(this.style.isEffectiveSide(side)) {
					this.style.draw(gui, side, stage, x, y, 0, height, needBg, this.show_pre_delay, this.show_post_delay, mc, fr, sr, centerX, centerY);
					break;
				}
				if(needBg) {
					int post_part = show_post_delay.getPart(5);
					if(post_part > 0) {
						mc.getTextureManager().bindTexture(Caption.down_texture);
						float post_h = height / 5.0F * post_part;
						Gui.drawModalRectWithCustomSizedTexture(x - 55, y - 3, 0, 0, 256, (int) post_h, 256, (int) post_h);
					}
				}
				break;
		}
	}

	@SideOnly(Side.CLIENT)
	protected void drawLeft(DrawState stage, Minecraft mc, GuiIngame gui, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
		int y = this.getY(sr);
		int textureY = y - 70;
		String name = this.displayName.format();
        List<String> texts = this.splitString(this.displayText.format(), fr);
		int height = 2 + (texts.size() * 10);
		
		switch(stage) {
			case PRE:
				if(this.style.isEffectiveSide(side)) {
					this.style.draw(gui, side, stage, 0, textureY, 95, 59, needBg, this.show_pre_delay, this.show_post_delay, mc, fr, sr, centerX, centerY);
					break;
				}
				if(this.needBg) {
					int pre_part = 6 - this.show_pre_delay.getPart(5);
					if(pre_part > -1) {
						float pre_h = (59.0F / 5.0F) * pre_part;
						float pre_w = (95.0F / 5.0F) * pre_part;
						mc.getTextureManager().bindTexture(side_texture);
						Gui.drawModalRectWithCustomSizedTexture(0, textureY, 0, 0, (int) pre_w, (int) pre_h, pre_w, pre_h);
					}
				}
				break;
			case DRAW:
				if(this.needBg) {
					mc.getTextureManager().bindTexture(side_texture);
					Gui.drawModalRectWithCustomSizedTexture(0, textureY, 0, 0, 95, 59, 50, 35);
					mc.getTextureManager().bindTexture(side_down_texture);
					Gui.drawModalRectWithCustomSizedTexture(0, textureY + 59, 0, 0, 95, height, 50, 35);
				}
				
				if(this.img.hasMoreFrame() && this.img.isDonePlay()) {
					this.img.resetDelay();
				}
				mc.getTextureManager().bindTexture(this.img.getImage());
				Gui.drawModalRectWithCustomSizedTexture(0, textureY, 0, 0, 95, 59, 100, 60);
				
				fr.drawString(name, 3, textureY + 49, Color.YELLOW.getRGB(), false);
				for(int i = 0; i < texts.size(); i++) {
					fr.drawString(texts.get(i), 3, textureY + 61 + (i * 10), 16777215);
				}
				break;
			case POST:
				if(this.style.isEffectiveSide(side)) {
					this.style.draw(gui, side, stage, 0, textureY, 95, 59, needBg, this.show_pre_delay, this.show_post_delay, mc, fr, sr, centerX, centerY);
					break;
				}
				if(this.needBg) {
					int post_part = this.show_post_delay.getPart(5);
					if(post_part > 0) {
						float post_w = (95.0F / 5.0F) * post_part;
						float post_h = (59.0F / 5.0F) * post_part;
						mc.getTextureManager().bindTexture(side_texture);
						Gui.drawModalRectWithCustomSizedTexture(0, textureY, 0, 0, (int) post_w, (int) post_h, post_w, post_h);
					}
				}
				break;
		}
	}
/*
*       -3
*       -2
*       -1
* -3-2-1 0 1 2 3  W
*        1
*        2
*        3
*        H
*/
	@SideOnly(Side.CLIENT)
	protected void drawRight(DrawState stage, Minecraft mc, GuiIngame gui, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
		int y = this.getY(sr);
		int textureY = y - 70;
		String name = this.displayName.format();
        List<String> texts = this.splitString(this.displayText.format(), fr);
		int height = 2 + (texts.size() * 10);
		
		switch(stage) {
			case PRE:
				if(this.style.isEffectiveSide(side)) {
					this.style.draw(gui, side, stage, sr.getScaledWidth() - 95, textureY, 95, 59, needBg, this.show_pre_delay, this.show_post_delay, mc, fr, sr, centerX, centerY);
					break;
				}
				if(this.needBg) {
					int pre_part = 6 - this.show_pre_delay.getPart(5);
					if(pre_part > -1) {
						float pre_w = (95.0F / 5.0F) * pre_part;
						float pre_h = (59.0F / 5.0F) * pre_part;
						mc.getTextureManager().bindTexture(side_texture);
						Gui.drawModalRectWithCustomSizedTexture(sr.getScaledWidth(), textureY + (int)pre_h, 0, 0, (int)-pre_w, (int) -pre_h, pre_w, pre_h);
					}
				}
				break;
			case DRAW:
				if(this.needBg) {
					mc.getTextureManager().bindTexture(side_texture);
					Gui.drawModalRectWithCustomSizedTexture(sr.getScaledWidth() - 95, textureY, 0, 0, 95, 59, 50, 35);
					mc.getTextureManager().bindTexture(side_down_texture);
					Gui.drawModalRectWithCustomSizedTexture(sr.getScaledWidth() - 95, textureY + 59, 0, 0, 95, height, 50, 35);
				}
				if(this.img.hasMoreFrame() && this.img.isDonePlay()) {
					this.img.resetDelay();
				}
				mc.getTextureManager().bindTexture(this.img.getImage());
				Gui.drawModalRectWithCustomSizedTexture(sr.getScaledWidth() - 95, textureY, 0, 0, 95, 59, 100, 60);
				
				fr.drawString(name, sr.getScaledWidth() - fr.getStringWidth(name) - 3, textureY + 49, Color.YELLOW.getRGB(), false);
				for(int i = 0; i < texts.size(); i++) {
					fr.drawString(texts.get(i), sr.getScaledWidth() - 93, textureY + 61 + (i * 10), 16777215);
				}
				break;
			case POST:
				if(this.style.isEffectiveSide(side)) {
					this.style.draw(gui, side, stage, sr.getScaledWidth() - 95, textureY, 95, 59, needBg, this.show_pre_delay, this.show_post_delay, mc, fr, sr, centerX, centerY);
					break;
				}
				if(this.needBg) {
					int post_part = this.show_post_delay.getPart(5);
					if(post_part > 0) {
						float post_h = (59.0F / 5.0F) * post_part;
						float post_w = (95.0F / 5.0F) * post_part;
						mc.getTextureManager().bindTexture(side_texture);
						Gui.drawModalRectWithCustomSizedTexture(sr.getScaledWidth(), textureY + (int)post_h, 0, 0, (int) -post_w, (int) -post_h, post_w, post_h);
					}
				}
				break;
		}
	}

	@SideOnly(Side.CLIENT)
	protected int getTextMaxPixelLength() {
		return this.side == DisplaySideType.DOWN ? 246 : 85;
	}

	@SideOnly(Side.CLIENT)
	protected final List<String> splitString(String text, FontRenderer fr) {
		List<String> texts = Lists.newArrayList();
		int sideLength = this.getTextMaxPixelLength();
		if(fr.getStringWidth(text) >= sideLength) {
			StringBuilder s = new StringBuilder();
			
			for(int i = 0; i < text.length(); i++) {
				s.append(text.charAt(i));
				String str = s.toString();
				if(fr.getStringWidth(str) >= sideLength) {
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
	
	public Caption copy() {
		return new Caption(type, displayName.copy(), displayText.copy(), style, displayTime.copy(), side, delay.copy(), needBg, img.copy(), sound.copy());
	}
	
	public void changeTo(Caption other) {
		this.displayName = other.displayName;
		this.displayText = other.displayText;
		this.side = other.side;
		this.needBg = other.needBg;
		this.displayTime.add(other.displayTime);
	}
	
	public final void updataTime() {
		if(!this.delay.isDone()) {
			this.delay.update();
		}else if(this.show_pre_delay != null && !this.show_pre_delay.isDone()) {
			this.show_pre_delay.update();
		}else if(!this.displayTime.isDone()) {
			this.displayTime.update();
		}else {
			this.show_post_delay.update();
		}
		if(this.img!=null && !this.img.isUseMillisToTiming() && !this.img.isDonePlay()) {
			this.img.update();
		}
	}
	
	public CaptionType getType() {return this.type;}
	public IText getDisplayName() {return displayName;}
	public IText getDisplayText() {return displayText;}
	public ITimer getTalkTime() {return displayTime;}
	public ITimer getDelay() {return delay;}
	public ITimer getShowPreDelay() {return show_pre_delay;}
	public ITimer getShowPostDelay() {return show_post_delay;}
	public DisplaySideType getDisplaySide() {return side;}
	public IImage getDisplayImg() {return img;}
	public ISound getSound() {return sound;}
	
	public Caption setDisplayName(IText displayName) {this.displayName = displayName; return this;}
	public Caption setDisplayText(IText displayText) {this.displayText = displayText; return this;}
	public Caption setDisplayImg(Image displayImg) {this.img = displayImg; return this;}
	public Caption setShowPreDelay(ITimer show_pre_delay) {this.show_pre_delay = show_pre_delay; return this;}
	public Caption setShowPostDelay(ITimer show_post_delay) {this.show_post_delay = show_post_delay; return this;}

	public NBTTagCompound toNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("isMain", this.type.isMainCaption());
		nbt.setTag("name", this.displayName.writeTo(NBTTagCompound.class));
		nbt.setTag("text", this.displayText.writeTo(NBTTagCompound.class));
		
		nbt.setTag("time", this.displayTime.writeTo(NBTTagCompound.class));
		nbt.setTag("delayTick", this.delay.writeTo(NBTTagCompound.class));
		nbt.setInteger("side", this.side.getID());
		nbt.setBoolean("needBG", this.needBg);
		nbt.setInteger("style", this.style.id);
		
		if(this.img!=null)nbt.setTag("image", this.img.writeTo(NBTTagCompound.class));
		if(this.sound!=null)nbt.setTag("sound", this.sound.writeTo(NBTTagCompound.class));
		
		return nbt;
	}
	
	public static Caption fromNBT(NBTTagCompound nbt) {
		CaptionType type = nbt.getBoolean("isMain") ? CaptionType.Main : CaptionType.Secondary;
		Text name = new Text(nbt.getCompoundTag("name"));
		Text text = new Text(nbt.getCompoundTag("text"));
		DisplayStyle style = DisplayStyle.getStyleByID(nbt.getInteger("style"));
		ITimer talkTime = ITimer.from(nbt.getCompoundTag("time"));
		ITimer delay = ITimer.from(nbt.getCompoundTag("delayTick"));
		
		DisplaySideType side = DisplaySideType.getType(nbt.getInteger("side"));
		
		Image image = nbt.hasKey("image") ? new Image(nbt.getCompoundTag("image")) : null;
		Sound sound = nbt.hasKey("sound") ? new Sound(nbt.getCompoundTag("sound")) : null;
		
		return new Caption(type, name, text, style, talkTime, side, delay, nbt.getBoolean("needBG"), image, sound);
	}
	
	@Override
	public String toString() {
		return "Caption@" + Integer.toHexString(hashCode());
	}
}
