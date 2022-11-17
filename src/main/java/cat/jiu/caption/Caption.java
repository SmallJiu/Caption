package cat.jiu.caption;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cat.jiu.caption.element.CaptionImage;
import cat.jiu.caption.element.CaptionSound;
import cat.jiu.caption.element.CaptionText;
import cat.jiu.caption.jiucore.time.CaptionTime;
import cat.jiu.caption.jiucore.time.ICaptionTime;
import cat.jiu.caption.type.CaptionType;
import cat.jiu.caption.type.DisplaySideType;
import cat.jiu.caption.type.DisplayStyle;
import cat.jiu.caption.type.DrawState;

import io.netty.buffer.ByteBuf;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class Caption {
	private static final ICaptionTime NO_DELAY = new CaptionTime();
	public static ICaptionTime noDelay() {return NO_DELAY.copy();}
	public static final Utils utils = new Utils();
	public static final Logger log = LogManager.getLogger("Caption");
	
	static final Map<CaptionType, Map<String, Caption.Element>> currentCaptions = Maps.newHashMap();
	static final Map<CaptionType, Map<String, List<Caption.Element>>> alternativeCaptions = Maps.newHashMap();
	static final Map<CaptionType, Map<String, Caption.Element>> lastCurrentCaptions = Maps.newHashMap();
	
	public static int getAllCaptionsSize(String name) {
		int i = 0;
		for(CaptionType type : CaptionType.VALUES) {
			if(currentCaptions.containsKey(type) && currentCaptions.get(type).containsKey(name)) {
				i++;
			}
			if(alternativeCaptions.containsKey(type) && alternativeCaptions.get(type).containsKey(name)) {
				i += alternativeCaptions.get(type).get(name).size();
			}
		}
		return i;
	}
	
	@SideOnly(Side.CLIENT)
	public static Caption.Element getCurrentCaption(CaptionType type) {return getCurrentCaption(type, Minecraft.getMinecraft().player.getName());}
	@SideOnly(Side.CLIENT)
	public static boolean hasCurrentCaption() {String name = Minecraft.getMinecraft().player.getName(); return hasCurrentCaption(CaptionType.Main, name) || hasCurrentCaption(CaptionType.Secondary, name);}
	@SideOnly(Side.CLIENT)
	public static boolean hasCurrentCaption(CaptionType type) {return hasCurrentCaption(type, Minecraft.getMinecraft().player.getName());}
	@SideOnly(Side.CLIENT)
	public static Caption.Element getNextCaption(CaptionType type) {return getNextCaption(type, Minecraft.getMinecraft().player.getName());}
	@SideOnly(Side.CLIENT)
	public static boolean hasNextCaption(CaptionType type) {return hasNextCaption(type, Minecraft.getMinecraft().player.getName());}
	
	public static boolean hasCurrentCaption(CaptionType type, String name) {return getCurrentCaption(type, name) != null;}
	public static Caption.Element getCurrentCaption(CaptionType type, String name) {
		if(!currentCaptions.containsKey(type)) return null;
		return currentCaptions.get(type).get(name);
	}
	public static Caption.Element getNextCaption(CaptionType type, String name) {
		if(hasNextCaption(type, name)) {
			return alternativeCaptions.get(type).get(name).get(0);
		}
		return null;
	}
	public static boolean hasNextCaption(CaptionType type, String name) {
		if(!alternativeCaptions.containsKey(type)) {
			return false;
		}
		List<Element> e = alternativeCaptions.get(type).get(name);
		return e!=null && !e.isEmpty();
	}
	public static Caption.Element getLastCaption(CaptionType type, String name) {
		if(lastCurrentCaptions.containsKey(type)) {
			return lastCurrentCaptions.get(type).get(name);
		}
		return null;
	}
	
	static {
		new Thread(()->{
			while(true) {
				try {Thread.sleep(50);}catch(InterruptedException e) { e.printStackTrace();}
				if(CaptionMain.proxy.isClient() && Minecraft.getMinecraft().isGamePaused()) continue;
				/*
				// 会导致post阶段不绘制(It will cause no drawing in the post stage)
				if(!currentCaptions.isEmpty()) {
					for(Entry<CaptionType, Map<String, Element>> currents : Sets.newHashSet(currentCaptions.entrySet())) {
						Map<String, Element> value = currents.getValue();
						if(value==null || value.isEmpty()) currentCaptions.remove(currents.getKey());
						for(Entry<String, Element> element : Sets.newHashSet(value.entrySet())) {
							if(element.getValue()==null) currentCaptions.get(currents.getKey()).remove(element.getKey());
						}
					}
				}
				*/
				
				if(!alternativeCaptions.isEmpty()) {
					for(Entry<CaptionType, Map<String, List<Element>>> currents : Sets.newHashSet(alternativeCaptions.entrySet())) {
						Map<String, List<Element>> values = alternativeCaptions.get(currents.getKey());
						if(values==null || values.isEmpty()) alternativeCaptions.remove(currents.getKey());
						for(Entry<String, List<Element>> elements : Sets.newHashSet(values.entrySet())) {
							List<Element> element = values.get(elements.getKey());
							if(element==null || element.isEmpty()) values.remove(elements.getKey());
							for(int i = 0; i < element.size(); i++) {
								if(element.get(i).displayTime.isDone()) element.remove(i);
							}
						}
					}
				}
				
				if(!currentCaptions.isEmpty()) {
					for(Entry<CaptionType, Map<String, Element>> allCurrents : currentCaptions.entrySet()) {
						for(Entry<String, Caption.Element> currents : allCurrents.getValue().entrySet()) {
							currents.getValue().updataTime();
						}
					}
				}
			}
		}).start();
	}
	
	public static void clearAllCaptions() {
		alternativeCaptions.clear();
		currentCaptions.clear();
	}
	
	@SuppressWarnings("incomplete-switch")
	public static class Element {
		protected static final Random rand = new Random();
		protected static final ICaptionTime SHOW_PRE_DELAY = new CaptionTime(5);
		protected static final ICaptionTime SHOW_POST_DELAY = new CaptionTime(5);
		public static final ResourceLocation down_texture = new ResourceLocation("caption:textures/gui/down.png");
		public static final ResourceLocation side_texture = new ResourceLocation("caption:textures/gui/side.png");
		public static final ResourceLocation side_down_texture = new ResourceLocation("caption:textures/gui/side_down.png");
		public static final ResourceLocation default_img = new ResourceLocation("caption:textures/gui/default_img.png");
		
		/** show pos. if is {@link CaptionType#Secondary} and textFields is empty, it will no show on window, but will play sound, and the side will always is DOWN */
		protected final CaptionType type;
		/** speaker textFields, can be translate text */
		protected CaptionText displayName;
		/** speaker speak text, can be translate text */
		protected CaptionText displayText;
		/** caption display delayTick */
		protected final ICaptionTime delay;
		/** show pre transition time */
		protected final ICaptionTime show_pre_delay;
		/** caption time of display */
		protected final ICaptionTime displayTime;
		/** show post transition time */
		protected final ICaptionTime show_post_delay;
		/** caption display side */
		protected DisplaySideType side;
		/** true if you need black background */
		protected boolean needBg;
		/** speaker images, can be null, the size must be 100x60 */
		protected CaptionImage img;
		/** speak sound, can be null */
		protected final CaptionSound sound;
		protected final DisplayStyle style;
		
		public Element(CaptionType type, CaptionText displayName, CaptionText displayText, DisplayStyle style, ICaptionTime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, boolean needBG, @Nullable CaptionImage img, @Nullable CaptionSound sound) {
			this.type = type;
			this.displayName = displayName == null ? CaptionText.empty : displayName;
			this.displayText = displayText == null ? CaptionText.empty : displayText;
			this.style = style;
			this.displayTime = displayTime;
			this.delay = displayDelay;
			this.needBg = needBG;
			this.img = img == null ? CaptionImage.DEFAULT_IMAGE : img;
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
			if(this.type.isMainCaption() && hasCurrentCaption(CaptionType.Secondary) && getCurrentCaption(CaptionType.Secondary).delay.isDone()) {
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
							mc.getTextureManager().bindTexture(Caption.Element.down_texture);
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
					}
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
					
					if(this.img.hasMoreFrame() && this.img.isDoneDelay()) {
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
					if(this.img.hasMoreFrame() && this.img.isDoneDelay()) {
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
		
		public Element copy() {
			return new Element(type, displayName, displayText, style, displayTime, side, delay, needBg, img, sound);
		}
		
		public void changeTo(Element other) {
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
			if(this.img!=null && !this.img.isUseMillisToTiming() && !this.img.isDoneDelay()) {
				this.img.update();
			}
		}
		
		public CaptionType getType() {return this.type;}
		public CaptionText getDisplayName() {return displayName;}
		public CaptionText getDisplayText() {return displayText;}
		public ICaptionTime getTalkTime() {return displayTime;}
		public ICaptionTime getDelay() {return delay;}
		public ICaptionTime getShowPreDelay() {return show_pre_delay;}
		public ICaptionTime getShowPostelay() {return show_post_delay;}
		public DisplaySideType getDisplaySide() {return side;}
		public CaptionImage getDisplayImg() {return img;}
		public CaptionSound getSound() {return sound;}
		
		public void setDisplayName(CaptionText displayName) {this.displayName = displayName;}
		public void setDisplayText(CaptionText displayText) {this.displayText = displayText;}
		public void setDisplayImg(CaptionImage displayImg) {this.img = displayImg;}

		public NBTTagCompound toNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setBoolean("isMain", this.type.isMainCaption());
			nbt.setTag("textFields", this.displayName.writeToNBT());
			nbt.setTag("text", this.displayText.writeToNBT());
			
			nbt.setTag("time", this.displayTime.writeToNBT(new NBTTagCompound(), false));
			nbt.setTag("delayTick", this.delay.writeToNBT(new NBTTagCompound(), false));
			nbt.setInteger("side", this.side.getID());
			nbt.setBoolean("needBG", this.needBg);
			nbt.setInteger("style", this.style.id);
			
			if(this.img!=null)nbt.setTag("image", this.img.toNBT());
			if(this.sound!=null)nbt.setTag("sound", this.sound.toNBT());
			
			return nbt;
		}
		
		public static Element fromNBT(NBTTagCompound nbt) {
			CaptionType type = nbt.getBoolean("isMain") ? CaptionType.Main : CaptionType.Secondary;
			CaptionText name = CaptionText.get(nbt.getCompoundTag("textFields"));
			CaptionText text = CaptionText.get(nbt.getCompoundTag("text"));
			DisplayStyle style = DisplayStyle.getStyleByID(nbt.getInteger("style"));
			ICaptionTime talkTime = ICaptionTime.from(nbt.getCompoundTag("time"));
			ICaptionTime delay = ICaptionTime.from(nbt.getCompoundTag("delayTick"));
			
			DisplaySideType side = DisplaySideType.getType(nbt.getInteger("side"));
			
			CaptionImage image = nbt.hasKey("image") ? CaptionImage.fromNBT(nbt.getCompoundTag("image")) : null;
			CaptionSound sound = nbt.hasKey("sound") ? CaptionSound.fromNBT(nbt.getCompoundTag("sound")) : null;
			
			return new Element(type, name, text, style, talkTime, side, delay, nbt.getBoolean("needBG"), image, sound);
		}
		
		@Override
		public String toString() {
			return "Caption@" + Integer.toHexString(hashCode());
		}
	}
	
	public static final class MsgCaption implements IMessage {
		private Caption.Element element;
		public MsgCaption() {}
		MsgCaption(Caption.Element e) {
			this.element = e;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			try {
				this.element = Caption.Element.fromNBT(new PacketBuffer(buf).readCompoundTag());
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void toBytes(ByteBuf buf) {
			new PacketBuffer(buf).writeCompoundTag(this.element.toNBT());
		}
		
		public IMessage handler(MessageContext ctx) {
			String name = ctx.side.isClient() ? Minecraft.getMinecraft().player.getName() : ctx.getServerHandler().player.getName();
			if(!alternativeCaptions.containsKey(this.element.type)) alternativeCaptions.put(this.element.type, Maps.newHashMap());
			if(!alternativeCaptions.get(this.element.type).containsKey(name)) alternativeCaptions.get(this.element.type).put(name, Lists.newLinkedList());
			
			alternativeCaptions.get(this.element.type).get(name).add(this.element);
			return null;
		}
	}
}
