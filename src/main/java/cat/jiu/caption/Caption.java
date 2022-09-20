package cat.jiu.caption;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cat.jiu.caption.element.CaptionImage;
import cat.jiu.caption.element.CaptionSound;
import cat.jiu.caption.event.CaptionDrawEvent;
import cat.jiu.caption.jiucore.time.CaptionTime;
import cat.jiu.caption.jiucore.time.ICaptionTime;
import cat.jiu.caption.type.CaptionType;
import cat.jiu.caption.type.DisplaySideType;
import cat.jiu.caption.type.DrawState;

import cat.jiu.caption.util.CapitonSndSound;

import io.netty.buffer.ByteBuf;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public  class Caption {
	private static final ICaptionTime NO_DELAY = new CaptionTime();
	public static ICaptionTime noDelay() {return NO_DELAY.copy();}
	public static final Utils utils = new Utils();
	
	static void add(EntityPlayer player, Caption.Element element) {
		if(player instanceof EntityPlayerMP) {
			CaptionMain.net.sendMessageToPlayer(new MsgCaption(element), (EntityPlayerMP) player);
		}else {
			CaptionMain.net.sendMessageToServer(new MsgCaption(element));
		}
	}
	
	static void add(EntityPlayer player, CaptionType type, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, boolean needBg, @Nullable CaptionImage image, @Nullable CaptionSound sound) {
		add(player, new Element(type, displayName, nameArg, displayText, textArg, displayTime, displaySide, displayDelay, needBg, image, sound));
	}

	@Optional.Method(modid = "jiucore")
	static void add(EntityPlayer player, CaptionType type, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, DisplaySideType displaySide, cat.jiu.core.api.ITime displayDelay, boolean needBg, @Nullable CaptionImage image, @Nullable CaptionSound sound) {
		add(player, new Element(type, displayName, nameArg, displayText, textArg, ICaptionTime.from(displayTime), displaySide, ICaptionTime.from(displayDelay), needBg, image, sound));
	}
	
	
	
	// Test
	private static final SoundEvent DEV_SOT_SeaLord_Last_Calipso = new SoundEvent(new ResourceLocation("caption:dev_sound")).setRegistryName(new ResourceLocation("caption:dev_sound"));
	private static boolean test() {return true;}
	@SubscribeEvent
	public static void onPlayerBreakBlock(BlockEvent.BreakEvent event) {
		if(event.getState().getBlock() == Blocks.DIAMOND_BLOCK) {
			if(test()) {
				List<ResourceLocation> imgs = Lists.newArrayList();
				imgs.add(new ResourceLocation("caption:textures/gui/dev/bat.png"));
				for(int i = 0; i < 13; i++) {
					imgs.add(new ResourceLocation("caption:textures/gui/dev/dev-gif/dev-gif_" + i + ".png"));
				}
				Caption.add(event.getPlayer(), CaptionType.Main, TextFormatting.RED + "森林蝙蝠", null, "不是很喜欢加速火把嘛，把火把插进你PY让你好好加速", null, new CaptionTime(0, 5, 0), DisplaySideType.LEFT, new CaptionTime(1, 0), true, new CaptionImage(imgs, 1), null);
			}else {
				Caption.add(event.getPlayer(), CaptionType.Main, "caption.dev.name", new Object[] {""}, "caption.dev.msg.0", null, new CaptionTime(0, 4, 0), DisplaySideType.LEFT, new CaptionTime(1, 0), true, new CaptionImage(new ResourceLocation("caption:textures/gui/dev/dev.png")), new CaptionSound(DEV_SOT_SeaLord_Last_Calipso, SoundCategory.PLAYERS, 1F, 1F));
				Caption.add(event.getPlayer(), CaptionType.Main, "caption.dev.name", new Object[] {""}, "caption.dev.msg.1", null, new CaptionTime(0, 8, 0), DisplaySideType.LEFT, new CaptionTime(1, 0), true, new CaptionImage(new ResourceLocation("caption:textures/gui/dev/dev.png")), null);
			}
		}
		if(event.getState().getBlock() == Blocks.GOLD_BLOCK) {
			Caption.add(event.getPlayer(), CaptionType.Main, "caption.dev.name", new Object[] {""}, "caption.dev.msg", null, new CaptionTime(0, 12, 0), DisplaySideType.RIGHT, new CaptionTime(1, 0), true, new CaptionImage(new ResourceLocation("caption:textures/gui/dev/dev.png")), new CaptionSound(DEV_SOT_SeaLord_Last_Calipso, SoundCategory.PLAYERS, 1F, 1F));
		}
	}
	@SubscribeEvent
	public static void onSoundEvenrRegistration(RegistryEvent.Register<SoundEvent> event) {
	    event.getRegistry().register(DEV_SOT_SeaLord_Last_Calipso);
	}
	
	
	
	private static final Map<CaptionType, Map<String, Caption.Element>> currentCaptions = Maps.newHashMap();
	private static final Map<CaptionType, Map<String, List<Caption.Element>>> alternativeCaptions = Maps.newHashMap();
	
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
	
	public static Caption.Element getCurrentCaption(CaptionType type, String name) {
		if(!currentCaptions.containsKey(type)) return null;
		return currentCaptions.get(type).get(name);
	}
	public static boolean hasCurrentCaption(CaptionType type, String name) {return getCurrentCaption(type, name) != null;}
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
		return e!=null && e.size() > 0;
	}
	
	private static void next(String playerName) {
		for(Entry<CaptionType, Map<String, List<Element>>> currents : alternativeCaptions.entrySet()) {
			if(currents.getValue()!=null && currents.getValue().containsKey(playerName)) {
				CaptionType type = currents.getKey();
				List<Element> elements = currents.getValue().get(playerName);
				if(elements==null || elements.isEmpty()) continue;
				
				if(hasNextCaption(type, playerName) && !hasCurrentCaption(type, playerName)) {
					if(!currentCaptions.containsKey(type)) currentCaptions.put(type, Maps.newHashMap());
					currentCaptions.get(type).put(playerName, elements.remove(0));
				}else if(hasNextCaption(type, playerName) && hasCurrentCaption(type, playerName) && getCurrentCaption(type, playerName).displayTime.isDone()) {
					if(getCurrentCaption(type, playerName).displayName.equalsIgnoreCase(getNextCaption(type, playerName).displayName)) {
						getCurrentCaption(type, playerName).changeTo(elements.remove(0));
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onPlayerLeaveServer(PlayerLoggedOutEvent event) {
		for(Entry<CaptionType, Map<String, Element>> currents : Sets.newHashSet(currentCaptions.entrySet())) {
			currentCaptions.get(currents.getKey()).remove(event.player.getName());
		}
		for(Entry<CaptionType, Map<String, List<Element>>> currents : Sets.newHashSet(alternativeCaptions.entrySet())) {
			alternativeCaptions.get(currents.getKey()).remove(event.player.getName());
		}
	}
	
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		String name = event.player.getName();
		next(name);
		
		for(CaptionType type : CaptionType.VALUES) {
			if(hasCurrentCaption(type, name)) {
				Caption.Element current = getCurrentCaption(type, name);
				if(current.show_post_delay.isDone()) {
					Caption.currentCaptions.get(type).remove(name);
					return;
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onTick(TickEvent.PlayerTickEvent event) {
		String name = event.player.getName();
		for(CaptionType type : CaptionType.VALUES) {
			if(hasCurrentCaption(type, name)) {
				Caption.Element current = getCurrentCaption(type, name);
				if(current.show_pre_delay.isDone() && current.sound != null && !current.sound.isPlayed()) {
					current.sound.setPlayed();
					if(current.sound.isFollowPlayer()) {
						CapitonSndSound sound = type.isMainCaption() ? new CapitonSndSound(event.player, current.sound, current.displayTime) : new SecondarySndSound(event.player, current.sound, current.displayTime);
						Minecraft.getMinecraft().getSoundHandler().playSound(sound);
					}else {
						event.player.world.playSound(event.player, current.sound.getPos(), current.sound.getSound(), current.sound.getCategory(), current.sound.getVolume(), current.sound.getPitch());
					}
				}
			}
		}
	}
	
	private static class SecondarySndSound extends CapitonSndSound {
		public SecondarySndSound(EntityPlayer player, CaptionSound sound, ICaptionTime playTime) {
			super(player, sound, playTime);
		}
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
						Map<String, List<Element>> values = currents.getValue();
						if(values==null || values.isEmpty()) alternativeCaptions.remove(currents.getKey());
						for(Entry<String, List<Element>> elements : Sets.newHashSet(currents.getValue().entrySet())) {
							List<Element> element = elements.getValue();
							if(element==null || element.isEmpty()) alternativeCaptions.get(currents.getKey()).remove(elements.getKey());
						}
					}
				}
				
				if(!currentCaptions.isEmpty()) {
					for(Entry<CaptionType, Map<String, Element>> allCurrents : currentCaptions.entrySet()) {
						for(Entry<String, Caption.Element> currents : allCurrents.getValue().entrySet()) {
							Caption.Element current = currents.getValue();
							if(!current.delay.isDone()) {
								current.delay.update();
							}else if(current.show_pre_delay != null && !current.show_pre_delay.isDone()) {
								current.show_pre_delay.update();
							}else if(!current.displayTime.isDone()) {
								current.displayTime.update();
							}else {
								current.show_post_delay.update();
							}
							if(!current.img.isDone()) {
								current.img.update();
							}
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
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onRenderDebugInfo(RenderGameOverlayEvent.Text event) {
		if(Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			event.getLeft().add("");
			event.getLeft().add("Caption: " + getAllCaptionsSize(Minecraft.getMinecraft().player.getName()));
			if(hasCurrentCaption(CaptionType.Main)) {
				Caption.Element current = getCurrentCaption(CaptionType.Main);
				event.getLeft().add("  Main: " + current);
				event.getLeft().add("    Delay: " + current.delay.toStringTime(false));
				event.getLeft().add("     Time: " + current.displayTime.toStringTime(false));
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
		if(event.getType() == ElementType.CHAT && !Minecraft.getMinecraft().gameSettings.hideGUI) {
			Minecraft mc = Minecraft.getMinecraft();
			ScaledResolution sr = new ScaledResolution(mc);
			
			for(CaptionType type : CaptionType.VALUES) {
				if(hasCurrentCaption(type)) {
					Caption.Element current = getCurrentCaption(type);

					GlStateManager.pushMatrix();
					GlStateManager.color(1,1,1,1);
					if(current.delay.isDone() && !current.show_pre_delay.isDone()) {
						current.preDraw(sr, mc, mc.ingameGUI, mc.fontRenderer);
					}else if(current.show_pre_delay.isDone() && !current.displayTime.isDone()) {
						current.draw(sr, mc, mc.ingameGUI, mc.fontRenderer);
					}else if(current.displayTime.isDone() && !current.show_post_delay.isDone()) {
						current.postDraw(sr, mc, mc.ingameGUI, mc.fontRenderer);
					}
					GlStateManager.popMatrix();
				}
			}
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	public static class Element {
		protected static final Random rand = new Random();
		protected static final ICaptionTime SHOW_PRE_DELAY = new CaptionTime(5);
		protected static final ICaptionTime SHOW_POST_DELAY = new CaptionTime(5);
		protected static final ResourceLocation down_texture = new ResourceLocation("caption:textures/gui/down.png");
		protected static final ResourceLocation side_texture = new ResourceLocation("caption:textures/gui/side.png");
		protected static final ResourceLocation side_down_texture = new ResourceLocation("caption:textures/gui/side_down.png");
		protected static final ResourceLocation default_img = new ResourceLocation("caption:textures/gui/default_img.png");
		protected static final Object[] EMPTY_ARGS = new Object[0];
		
		/** show pos. if is {@link CaptionType#Secondary} and name is empty, it will no show on window, but will play sound, and the side will always is DOWN */
		protected final CaptionType type;
		/** speaker name, can be translate key */
		protected String displayName;
		/** if name is translate key, this is the args */
		protected Object[] nameArg;
		/** speaker speak text, can be translate key */
		protected String displayText;
		/** if text is translate key, this is the args */
		protected Object[] textArg;
		/** caption display delay */
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
		
		public Element(CaptionType type, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, boolean needBG, @Nullable CaptionImage img, @Nullable CaptionSound sound) {
			this(type, displayName, nameArg, displayText, textArg, displayTime, displaySide, displayDelay, needBG, img, 0, sound);
		}
		
		public Element(CaptionType type, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, boolean needBG, @Nullable CaptionImage img, long delayTicks, @Nullable CaptionSound sound) {
			this.type = type;
			this.displayName = displayName;
			this.nameArg = nameArg == null ? EMPTY_ARGS : nameArg;
			this.displayText = displayText;
			this.textArg = textArg == null ? EMPTY_ARGS : textArg;
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
		public void draw(ScaledResolution sr, Minecraft mc, GuiIngame gui, FontRenderer fr) {
			if(MinecraftForge.EVENT_BUS.post(new CaptionDrawEvent(this, DrawState.DRAW))) return;
			int width = sr.getScaledWidth();
	        int height = sr.getScaledHeight();
	        int centerX = width / 2 -1;
	        int centerY = height / 2 - 4;
	        
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
		public void preDraw(ScaledResolution sr, Minecraft mc, GuiIngame gui, FontRenderer fr) {
			if(MinecraftForge.EVENT_BUS.post(new CaptionDrawEvent(this, DrawState.PRE))) return;
			int width = sr.getScaledWidth();
	        int height = sr.getScaledHeight();
	        int centerX = width / 2 -1;
	        int centerY = height / 2 - 4;
	        
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
		public void postDraw(ScaledResolution sr, Minecraft mc, GuiIngame gui, FontRenderer fr) {
			if(MinecraftForge.EVENT_BUS.post(new CaptionDrawEvent(this, DrawState.POST))) return;
			int width = sr.getScaledWidth();
	        int height = sr.getScaledHeight();
	        int centerX = width / 2 -1;
	        int centerY = height / 2 - 4;
	        
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
		protected void drawDown(DrawState stage, Minecraft mc, GuiIngame gui, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
			if(!this.type.isMainCaption() && this.displayName.isEmpty() && this.displayText.isEmpty()) return;
			int x = sr.getScaledHeight() - 16 - 3 - 73;
			int y = sr.getScaledHeight() - 16 - 3 - 73;
			
			String name = I18n.format(this.displayName, this.nameArg);
	        List<String> texts = this.splitString(I18n.format(this.displayText, this.textArg), fr);
			int height = 16 + (texts.size() * 10);
			
			int mainY = y;
			if(this.type.isMainCaption() && hasCurrentCaption(CaptionType.Secondary) && getCurrentCaption(CaptionType.Secondary).delay.isDone()) {
				mainY = y - height - 2;
			}
			
			switch(stage) {
				case PRE:
					if(this.needBg) {
						int pre_part = 6 - this.show_pre_delay.getPart(5);
						if(pre_part != -1) {
							mc.getTextureManager().bindTexture(down_texture);
							float pre_h = (height / 5.0F) * pre_part;
							Gui.drawModalRectWithCustomSizedTexture(x - 55, mainY - 3, 0, 0, 256, (int) pre_h, 256, (int) pre_h);
						}
					}
					break;
				case DRAW:
					if(this.needBg) {
						mc.getTextureManager().bindTexture(down_texture);
						Gui.drawModalRectWithCustomSizedTexture(x - 55, mainY - 3, 0, 0, 256, height, 256, height);
						Gui.drawModalRectWithCustomSizedTexture(x - 55, mainY + 9, 0, 0, 256, 1, 256, 1);
					}
					fr.drawString(name, x - 55 + 5, mainY, Color.YELLOW.getRGB(), false);
					for(int i = 0; i < texts.size(); i++) {
						fr.drawString(texts.get(i), x - 55 + 5, mainY + 1 + 10 + (i * 10), 16777215);
					}
					break;
				case POST:
					if(this.needBg) {
						int post_part = this.show_post_delay.getPart(5);
						if(post_part > 0) {
							mc.getTextureManager().bindTexture(down_texture);
							float post_h = height / 5.0F * post_part;
							Gui.drawModalRectWithCustomSizedTexture(x - 55, mainY - 3, 0, 0, 256, (int) post_h, 256, (int) post_h);
						}
					}
					break;
			}
		}

		@SideOnly(Side.CLIENT)
		protected void drawLeft(DrawState stage, Minecraft mc, GuiIngame gui, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
//			int x = sr.getScaledWidth() - 16 - 3 - 73;
			int y = sr.getScaledHeight() - 16 - 3 - 73;
			int textureY = y - 70;
			String name = I18n.format(this.displayName, this.nameArg);
	        List<String> texts = this.splitString(I18n.format(this.displayText, this.textArg), fr);
			int height = 2 + (texts.size() * 10);
			
			switch(stage) {
				case PRE:
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
					
					if(this.img.hasMoreFrame() && this.img.isDone()) {
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
					if(this.needBg) {
						int post_part = this.show_post_delay.getPart(5);
						if(post_part > 0) {
							float post_h = (59.0F / 5.0F) * post_part;
							float post_w = (95.0F / 5.0F) * post_part;
							mc.getTextureManager().bindTexture(side_texture);
							Gui.drawModalRectWithCustomSizedTexture(0, textureY, 0, 0, (int) post_w, (int) post_h, post_w, post_h);
						}
					}
					break;
			}
		}
		
		protected int displayImgSerial = 0;
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
//			int x = sr.getScaledWidth() - 16 - 3 - 73;
			int y = sr.getScaledHeight() - 16 - 3 - 73;
			int textureY = y - 70;
			String name = I18n.format(this.displayName, this.nameArg);
	        List<String> texts = this.splitString(I18n.format(this.displayText, this.textArg), fr);
			int height = 2 + (texts.size() * 10);
			
			switch(stage) {
				case PRE:
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
					if(this.img.hasMoreFrame() && this.img.isDone()) {
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
		protected List<String> splitString(String text, FontRenderer fr) {
			List<String> texts = Lists.newArrayList();
			int sideLength = this.side == DisplaySideType.DOWN ? 246 : 85;
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
			return new Element(type, displayName, nameArg, displayText, textArg, displayTime, side, delay, true, img, sound);
		}
		
		public void changeTo(Element other) {
			this.nameArg = other.nameArg;
			this.displayText = other.displayText;
			this.textArg = other.textArg;
			this.side = other.side;
			this.needBg = other.needBg;
			this.displayTime.add(other.displayTime);
		}
		
		public CaptionType getType() {return this.type;}
		public String getDisplayName() {return displayName;}
		public String getDisplayText() {return displayText;}
		public ICaptionTime getTalkTime() {return displayTime;}
		public ICaptionTime getDelay() {return delay;}
		public ICaptionTime getShowPreDelay() {return show_pre_delay;}
		public ICaptionTime getShowPostelay() {return show_post_delay;}
		public DisplaySideType getDisplaySide() {return side;}
		public CaptionImage getDisplayImg() {return img;}
		public CaptionSound getSound() {return sound;}
		
		public void setDisplayName(String displayName) {this.displayName = displayName;}
		public void setDisplayText(String displayText) {this.displayText = displayText;}
		public void setDisplayImg(CaptionImage displayImg) {this.img = displayImg;}

		public NBTTagCompound toNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setBoolean("type", this.type.isMainCaption());
			nbt.setString("name", this.displayName);
			NBTTagList nameArgList = new NBTTagList();
			for(int i = 0; i < this.nameArg.length; i++) {
				nameArgList.appendTag(new NBTTagString(String.valueOf(this.nameArg[i])));
			}
			nbt.setTag("nameArg", nameArgList);
			
			nbt.setString("text", this.displayText);
			NBTTagList textArgList = new NBTTagList();
			for(int i = 0; i < this.textArg.length; i++) {
				textArgList.appendTag(new NBTTagString(String.valueOf(this.textArg[i])));
			}
			nbt.setTag("textArg", textArgList);
			
			nbt.setTag("time", this.displayTime.writeToNBT(new NBTTagCompound(), false));
			nbt.setTag("delay", this.delay.writeToNBT(new NBTTagCompound(), false));
			nbt.setInteger("side", this.side.getID());
			nbt.setBoolean("needBG", this.needBg);
			
			if(this.img!=null)nbt.setTag("image", this.img.toNBT());
			if(this.sound!=null)nbt.setTag("sound", this.sound.toNBT());
			
			return nbt;
		}
		
		public static Element fromNBT(NBTTagCompound nbt) {
			CaptionType type = nbt.getBoolean("type") ? CaptionType.Main : CaptionType.Secondary;
			String talkEntityName = nbt.getString("name");
			NBTTagList nameArgList = nbt.getTagList("nameArg", 8);
			Object[] nameArg = new Object[nameArgList.tagCount()];
			for(int i = 0; i < nameArgList.tagCount(); i++) {
				nameArg[i] = ((NBTTagString)nameArgList.get(i)).getString();
			}
			String text = nbt.getString("text");
			NBTTagList textArgList = nbt.getTagList("textArg", 8);
			Object[] textArg = new Object[textArgList.tagCount()];
			for(int i = 0; i < textArgList.tagCount(); i++) {
				textArg[i] = ((NBTTagString)textArgList.get(i)).getString();
			}
			ICaptionTime talkTime = ICaptionTime.from(nbt.getCompoundTag("time"));
			ICaptionTime delay = ICaptionTime.from(nbt.getCompoundTag("delay"));
			
			DisplaySideType side = DisplaySideType.getType(nbt.getInteger("side"));
			
			CaptionImage image = nbt.hasKey("image") ? CaptionImage.fromNBT(nbt.getCompoundTag("image")) : null;
			CaptionSound sound = nbt.hasKey("sound") ? CaptionSound.fromNBT(nbt.getCompoundTag("sound")) : null;
			return new Element(type, talkEntityName, nameArg, text, textArg, talkTime, side, delay, nbt.getBoolean("needBG"), image, sound);
		}
		
		@Override
		public String toString() {
			return "Caption@" + Integer.toHexString(hashCode());
		}
	}
	
	public static class MsgCaption implements IMessage {
		private Caption.Element element;
		public MsgCaption() {}
		private MsgCaption(Caption.Element e) {
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
			if(!alternativeCaptions.get(this.element.type).containsKey(name)) alternativeCaptions.get(this.element.type).put(name, Lists.newArrayList());
			
			alternativeCaptions.get(this.element.type).get(name).add(this.element);
			return null;
		}
	}
}
