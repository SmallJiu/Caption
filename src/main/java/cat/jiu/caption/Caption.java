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

import cat.jiu.caption.jiucore.time.ICaptionTime;
import cat.jiu.caption.event.CaptionDrawEvent;
import cat.jiu.caption.jiucore.time.CaptionTime;
import cat.jiu.caption.type.CaptionType;
import cat.jiu.caption.type.DisplaySideType;
import cat.jiu.caption.type.DrawState;
import cat.jiu.caption.util.CapitonSound;
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
import net.minecraft.util.math.BlockPos;

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
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 2.实现渲染
 */
@Mod.EventBusSubscriber
public final class Caption {
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
	
	static void add(EntityPlayer player, CaptionType type, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, boolean needBg, @Nullable List<ResourceLocation> displayImgs, long displayImgDelayTicks, @Nullable CaptionSoundEvent sound) {
		add(player, new Element(type, displayName, nameArg, displayText, textArg, displayTime, displaySide, displayDelay, needBg, displayImgs, displayImgDelayTicks, sound));
	}

	@Optional.Method(modid = "jiucore")
	static void add(EntityPlayer player, CaptionType type, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, cat.jiu.core.api.ITime displayTime, DisplaySideType displaySide, cat.jiu.core.api.ITime displayDelay, boolean needBg, @Nullable List<ResourceLocation> displayImgs, long displayImgDelayTicks, @Nullable CaptionSoundEvent sound) {
		add(player, new Element(type, displayName, nameArg, displayText, textArg, ICaptionTime.fromCoreTime(displayTime), displaySide, ICaptionTime.fromCoreTime(displayDelay), needBg, displayImgs, displayImgDelayTicks, sound));
	}
	
	
	
	// Test
	private static final SoundEvent DEV_SOT_SeaLord_Last_Calipso = new SoundEvent(new ResourceLocation("caption:dev_sound")).setRegistryName(new ResourceLocation("caption:dev_sound"));
	private static boolean test() {return true;}
	@SubscribeEvent
	public static void onPlayerBreakBlock(BlockEvent.BreakEvent event) {
		if(event.getState().getBlock() == Blocks.DIAMOND_BLOCK) {
			if(test()) {
				List<ResourceLocation> imgs = Lists.newArrayList();
//				imgs.add(new ResourceLocation("caption:textures/gui/bat.png"));
				for(int i = 0; i < 13; i++) {
					imgs.add(new ResourceLocation("caption:textures/gui/dev/dev-gif/dev-gif_" + i + ".png"));
				}
				Caption.add(event.getPlayer(), CaptionType.Main, "森林蝙蝠", null, "不是很喜欢加速火把嘛，把火把插进你PY让你好好加速saudyfasudfhbasudbsudgbyysudhvausdgfy", null, new CaptionTime(0, 5, 0), DisplaySideType.LEFT, new CaptionTime(1, 0), true, imgs, 1, null);
			}else {
				Caption.add(event.getPlayer(), CaptionType.Main, "caption.dev.name", new Object[] {""}, "caption.dev.msg.0", null, new CaptionTime(0, 4, 0), DisplaySideType.DOWN, new CaptionTime(1, 0), true, Lists.newArrayList(new ResourceLocation("caption:textures/gui/dev/dev.png")), 0, new Caption.CaptionSoundEvent(event.getPos(), true, DEV_SOT_SeaLord_Last_Calipso, SoundCategory.PLAYERS, 1F, 1F));
				Caption.add(event.getPlayer(), CaptionType.Main, "caption.dev.name", new Object[] {""}, "caption.dev.msg.1", null, new CaptionTime(0, 8, 0), DisplaySideType.DOWN, new CaptionTime(1, 0), true, Lists.newArrayList(new ResourceLocation("caption:textures/gui/dev/dev.png")), 0, null);
			}
		}
		if(event.getState().getBlock() == Blocks.GOLD_BLOCK) {
			Caption.add(event.getPlayer(), CaptionType.Secondary, "caption.dev.name", new Object[] {""}, "caption.dev.msg", null, new CaptionTime(0, 12, 0), DisplaySideType.DOWN, new CaptionTime(1, 0), true, Lists.newArrayList(new ResourceLocation("caption:textures/gui/dev/dev.png")), 0, new Caption.CaptionSoundEvent(event.getPos(), true, DEV_SOT_SeaLord_Last_Calipso, SoundCategory.PLAYERS, 1F, 1F));
		}
	}
	@SubscribeEvent
	public static void onSoundEvenrRegistration(RegistryEvent.Register<SoundEvent> event) {
	    event.getRegistry().register(DEV_SOT_SeaLord_Last_Calipso);
	}
	
	
	
	private static final Map<String, Caption.Element> current = Maps.newHashMap();
	private static final Map<String, List<Caption.Element>> currentCaptions = Maps.newHashMap();
	
	private static final Map<String, Caption.Element> secondaryCurrent = Maps.newHashMap();
	private static final Map<String, List<Caption.Element>> secondaryCurrentCaptions = Maps.newHashMap();
	
	public static int getAllCaptions(String name) {
		int i = 0;
		if(current.containsKey(name)) i++;
		if(secondaryCurrent.containsKey(name)) i++;
		if(currentCaptions.containsKey(name) && currentCaptions.get(name) !=null) i += currentCaptions.get(name).size();
		if(secondaryCurrentCaptions.containsKey(name) && secondaryCurrentCaptions.get(name) !=null) i += secondaryCurrentCaptions.get(name).size();
		return i;
	}
	
	@SideOnly(Side.CLIENT)
	public static Caption.Element getCurrentCaption() {return current.get(Minecraft.getMinecraft().player.getName());}
	@SideOnly(Side.CLIENT)
	public static boolean hasCurrentCaption() {return current.containsKey(Minecraft.getMinecraft().player.getName());}
	@SideOnly(Side.CLIENT)
	public static Caption.Element getNextCaption() {return getNextCaption(Minecraft.getMinecraft().player.getName());}
	@SideOnly(Side.CLIENT)
	public static boolean hasNextCaption() {return hasNextCaption(Minecraft.getMinecraft().player.getName());}
	
	public static Caption.Element getCurrentCaption(String name) {return current.get(name);}
	public static boolean hasCurrentCaption(String name) {return getCurrentCaption(name) != null;}
	public static Caption.Element getNextCaption(String name) {
		if(hasNextCaption(name)) {
			return currentCaptions.get(name).get(0);
		}
		return null;
	}
	public static boolean hasNextCaption(String name) {
		if(!currentCaptions.containsKey(name)) {
			return false;
		}
		return currentCaptions.get(name).size() > 0;
	}
	
	@SideOnly(Side.CLIENT)
	public static Caption.Element getCurrentSecondaryCaption() {return secondaryCurrent.get(Minecraft.getMinecraft().player.getName());}
	@SideOnly(Side.CLIENT)
	public static boolean hasCurrentSecondaryCaption() {return secondaryCurrent.containsKey(Minecraft.getMinecraft().player.getName());}
	@SideOnly(Side.CLIENT)
	public static Caption.Element getNextSecondaryCaption() {return getNextSecondaryCaption(Minecraft.getMinecraft().player.getName());}
	@SideOnly(Side.CLIENT)
	public static boolean hasNextSecondaryCaption() {return hasNextSecondaryCaption(Minecraft.getMinecraft().player.getName());}
	
	public static Caption.Element getCurrentSecondaryCaption(String name) {return secondaryCurrent.get(name);}
	public static boolean hasCurrentSecondaryCaption(String name) {return getCurrentSecondaryCaption(name) != null;}
	public static Caption.Element getNextSecondaryCaption(String name) {
		if(hasNextSecondaryCaption(name)) {
			return secondaryCurrentCaptions.get(name).get(0);
		}
		return null;
	}
	public static boolean hasNextSecondaryCaption(String name) {
		if(!secondaryCurrentCaptions.containsKey(name)) {
			return false;
		}
		return secondaryCurrentCaptions.get(name).size() > 0;
	}
	
	private static void next(String playerName) {
		if(hasNextCaption(playerName) && !hasCurrentCaption(playerName)) {
			current.put(playerName, currentCaptions.get(playerName).remove(0));
		}else if(hasNextCaption(playerName) && hasCurrentCaption(playerName) && getCurrentCaption(playerName).displayTime.isDone()) {
			if(getCurrentCaption(playerName).displayName.equalsIgnoreCase(getNextCaption(playerName).displayName)) {
				getCurrentCaption(playerName).changeTo(currentCaptions.get(playerName).remove(0));
			}
		}
		
		if(hasNextSecondaryCaption(playerName) && !hasCurrentSecondaryCaption(playerName)) {
			secondaryCurrent.put(playerName, secondaryCurrentCaptions.get(playerName).remove(0));
		}else if(hasNextSecondaryCaption(playerName) && hasCurrentSecondaryCaption(playerName) && getCurrentSecondaryCaption(playerName).displayTime.isDone()) {
			if(getCurrentSecondaryCaption(playerName).displayName.equalsIgnoreCase(getNextSecondaryCaption(playerName).displayName)) {
				getCurrentSecondaryCaption(playerName).changeTo(secondaryCurrentCaptions.get(playerName).remove(0));
			}
		}
	}
	
	@SubscribeEvent
	public static void onPlayerLevelServer(PlayerLoggedOutEvent event) {
		current.remove(event.player.getName());
		currentCaptions.remove(event.player.getName());
		
		secondaryCurrent.remove(event.player.getName());
		secondaryCurrentCaptions.remove(event.player.getName());
	}
	
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if(event.phase == Phase.END) {
			String name = event.player.getName();
			next(name);
			
			if(hasCurrentCaption(name)) {
				Caption.Element current = getCurrentCaption(name);
				if(current.show_post_delay.isDone()) {
					Caption.current.remove(name);
					return;
				}
			}
			if(hasCurrentSecondaryCaption(name)) {
				Caption.Element current = getCurrentSecondaryCaption(name);
				if(current.show_post_delay.isDone()) {
					Caption.secondaryCurrent.remove(name);
					return;
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onTick(TickEvent.PlayerTickEvent event) {
		String name = event.player.getName();
		if(hasCurrentCaption(name)) {
			Caption.Element current = getCurrentCaption(name);
			if(current.show_pre_delay.isDone() && current.sound != null && !current.sound.isPlayed()) {
				current.sound.setPlayed();
				if(current.sound.isFollowPlayer()) {
					Minecraft.getMinecraft().getSoundHandler().playSound(new CapitonSound(event.player, current.sound, current.displayTime));
				}else {
					event.player.world.playSound(event.player, current.sound.pos, current.sound.sound, current.sound.category, current.sound.volume, current.sound.pitch);
				}
			}
		}
		if(hasCurrentSecondaryCaption(name)) {
			Caption.Element current = getCurrentSecondaryCaption(name);
			if(current.show_pre_delay.isDone() && current.sound != null && !current.sound.isPlayed()) {
				current.sound.setPlayed();
				if(current.sound.isFollowPlayer()) {
					Minecraft.getMinecraft().getSoundHandler().playSound(new SecondarySound(event.player, current.sound, current.displayTime));
				}else {
					event.player.world.playSound(event.player, current.sound.pos, current.sound.sound, current.sound.category, current.sound.volume, current.sound.pitch);
				}
			}
		}
	}
	private static class SecondarySound extends CapitonSound {
		public SecondarySound(EntityPlayer player, CaptionSoundEvent sound, ICaptionTime playTime) {
			super(player, sound, playTime);
		}
	}
	
	static {
		new Thread(()->{
			while(true) {
				try {Thread.sleep(50);}catch(InterruptedException e) { e.printStackTrace();}
				if(CaptionMain.proxy.isClient() && Minecraft.getMinecraft().isGamePaused()) continue;
				
				if(!current.isEmpty()) {
					for(Entry<String, Caption.Element> currents : Maps.newHashMap(current).entrySet()) {
						if(currents.getValue() == null) {
							current.remove(currents.getKey());
						}
					}
				}
				if(!currentCaptions.isEmpty()) {
					for(Entry<String, List<Caption.Element>> currents : Maps.newHashMap(currentCaptions).entrySet()) {
						if(currents.getValue() == null || currents.getValue().isEmpty()) {
							currentCaptions.remove(currents.getKey());
						}
					}
				}
				
				if(!current.isEmpty()) {
					for(Entry<String, Caption.Element> currents : current.entrySet()) {
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
						if(!current.imgDelay.isDone()) {
							current.imgDelay.update();
						}
					}
				}
				
				if(!secondaryCurrent.isEmpty()) {
					for(Entry<String, Caption.Element> currents : Maps.newHashMap(secondaryCurrent).entrySet()) {
						if(currents.getValue() == null) {
							secondaryCurrent.remove(currents.getKey());
						}
					}
				}
				if(!secondaryCurrentCaptions.isEmpty()) {
					for(Entry<String, List<Caption.Element>> currents : Maps.newHashMap(secondaryCurrentCaptions).entrySet()) {
						if(currents.getValue() == null || currents.getValue().isEmpty()) {
							secondaryCurrentCaptions.remove(currents.getKey());
						}
					}
				}
				
				if(!secondaryCurrent.isEmpty()) {
					for(Entry<String, Caption.Element> currents : secondaryCurrent.entrySet()) {
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
						if(!current.imgDelay.isDone()) {
							current.imgDelay.update();
						}
					}
				}
			}
		}).start();
	}
	
	public static void clearAllCaptions() {
		currentCaptions.clear();
		current.clear();
		secondaryCurrent.clear();
		secondaryCurrentCaptions.clear();
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onRenderDebugInfo(RenderGameOverlayEvent.Text event) {
		if(Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			event.getLeft().add("");
			event.getLeft().add("Caption: " + getAllCaptions(Minecraft.getMinecraft().player.getName()));
			if(hasCurrentCaption()) {
				Caption.Element current = getCurrentCaption();
				event.getLeft().add("  Main: " + current);
				event.getLeft().add("    Delay: " + current.delay.toStringTime(false));
				event.getLeft().add("     Time: " + current.displayTime.toStringTime(false));
			}
			if(hasCurrentSecondaryCaption()) {
				Caption.Element current = getCurrentSecondaryCaption();
				event.getLeft().add("  Secondary: " + current);
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
			if(hasCurrentCaption()) {
				Caption.Element current = getCurrentCaption();
				
				if(current.delay.isDone() && !current.show_pre_delay.isDone()) {
					current.preDraw(sr, mc, mc.ingameGUI, mc.fontRenderer);
				}else if(current.show_pre_delay.isDone() && !current.displayTime.isDone()) {
					current.draw(sr, mc, mc.ingameGUI, mc.fontRenderer);
				}else if(current.displayTime.isDone() && !current.show_post_delay.isDone()) {
					current.postDraw(sr, mc, mc.ingameGUI, mc.fontRenderer);
				}
			}
			if(hasCurrentSecondaryCaption()) {
				Caption.Element current = getCurrentSecondaryCaption();
				
				if(current.delay.isDone() && !current.show_pre_delay.isDone()) {
					current.preDraw(sr, mc, mc.ingameGUI, mc.fontRenderer);
				}else if(current.show_pre_delay.isDone() && !current.displayTime.isDone()) {
					current.draw(sr, mc, mc.ingameGUI, mc.fontRenderer);
				}else if(current.displayTime.isDone() && !current.show_post_delay.isDone()) {
					current.postDraw(sr, mc, mc.ingameGUI, mc.fontRenderer);
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
		/** show post transition time */
		protected final ICaptionTime show_post_delay;
		/** caption time of display */
		protected final ICaptionTime displayTime;
		/** caption display side */
		protected DisplaySideType side;
		/** true if you need black background */
		protected boolean needBg;
		/** speaker image, can be null, the size must be 100x60 */
		protected List<ResourceLocation> displayImg;
		/** image delay, like mcmeta {@code frametime} */
		protected final ICaptionTime imgDelay;
		/** speak sound, can be null */
		protected final CaptionSoundEvent sound;
		
		public Element(CaptionType type, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, boolean needBG, @Nullable ResourceLocation displayImg, @Nullable CaptionSoundEvent sound) {
			this(type, displayName, nameArg, displayText, textArg, displayTime, displaySide, displayDelay, needBG, displayImg == null ? null : Lists.newArrayList(displayImg), 0, sound);
		}
		
		public Element(CaptionType type, String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, boolean needBG, @Nullable List<ResourceLocation> displayImgs, long delayTicks, @Nullable CaptionSoundEvent sound) {
			this.type = type;
			this.displayName = displayName;
			this.nameArg = nameArg == null ? EMPTY_ARGS : nameArg;
			this.displayText = displayText;
			this.textArg = textArg == null ? EMPTY_ARGS : textArg;
			this.displayTime = displayTime;
			this.delay = displayDelay;
			this.needBg = needBG;
			this.displayImg = displayImgs;
			this.imgDelay = new CaptionTime(delayTicks);
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
			if(!this.type.isMainCaption() && this.displayName.isEmpty()) return;
			int x = sr.getScaledHeight() - 16 - 3 - 73;
			int y = sr.getScaledHeight() - 16 - 3 - 65;
			
			String name = I18n.format(this.displayName, this.nameArg);
	        List<String> texts = this.splitString(I18n.format(this.displayText, this.textArg), fr);
			int height = 16 + (texts.size() * 10);
			
			int mainY = y;
			if(this.type.isMainCaption() && hasCurrentSecondaryCaption()) {
				if(!getCurrentSecondaryCaption().displayName.isEmpty() && getCurrentSecondaryCaption().delay.isDone()) {
					mainY = y - height - 5;
				}
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
					if(this.displayImg != null) {
						if(this.displayImg.size()>1 && this.imgDelay.isDone()) {
							this.displayImgSerial++;
							this.imgDelay.reset();
							if(this.displayImgSerial >= this.displayImg.size()) {
								this.displayImgSerial = 0;
							}
						}
						GlStateManager.pushMatrix();
						GlStateManager.color(1,1,1,1);
						mc.getTextureManager().bindTexture(this.displayImg.get(this.displayImgSerial));
						Gui.drawModalRectWithCustomSizedTexture(0, textureY, 0, 0, 95, 59, 100, 60);
						GlStateManager.popMatrix();
					}else {
						mc.getTextureManager().bindTexture(default_img);
						Gui.drawModalRectWithCustomSizedTexture(0, textureY, 0, 0, 95, 59, 100, 60);
					}
					fr.drawString(name, 2, textureY + 49, Color.YELLOW.getRGB(), false);
					for(int i = 0; i < texts.size(); i++) {
						fr.drawString(texts.get(i), 2, textureY + 61 + (i * 10), 16777215);
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
							Gui.drawModalRectWithCustomSizedTexture(sr.getScaledWidth(), textureY + 59, 0, 0, (int)-pre_w, (int) -pre_h, -pre_w, pre_h);
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
					if(this.displayImg != null) {
						if(this.displayImg.size()>1 && this.imgDelay.isDone()) {
							this.displayImgSerial++;
							this.imgDelay.reset();
							if(this.displayImgSerial >= this.displayImg.size()) {
								this.displayImgSerial = 0;
							}
						}
						GlStateManager.pushMatrix();
						GlStateManager.color(1,1,1,1);
						mc.getTextureManager().bindTexture(this.displayImg.get(this.displayImgSerial));
						Gui.drawModalRectWithCustomSizedTexture(sr.getScaledWidth() - 95, textureY, 0, 0, 95, 59, 100, 60);
						GlStateManager.popMatrix();
					}else {
						mc.getTextureManager().bindTexture(default_img);
						Gui.drawModalRectWithCustomSizedTexture(sr.getScaledWidth() - 95, textureY, 0, 0, 95, 59, 100, 60);
					}
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
							Gui.drawModalRectWithCustomSizedTexture(sr.getScaledWidth(), textureY + 59, 0, 0, (int) -post_w, (int) -post_h, post_w, post_h);
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
			return new Element(type, displayName, nameArg, displayText, textArg, displayTime, side, delay, true, displayImg, imgDelay.getAllTicks(), sound);
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
		public List<ResourceLocation> getDisplayImg() {return displayImg;}
		public CaptionSoundEvent getSound() {return sound;}
		
		public void setDisplayName(String displayName) {this.displayName = displayName;}
		public void setDisplayText(String displayText) {this.displayText = displayText;}
		public void setDisplayImg(List<ResourceLocation> displayImg) {this.displayImg = displayImg;}

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
			
			if(this.displayImg!=null) {
				NBTTagCompound imgs = new NBTTagCompound();
				for(int i = 0; i < this.displayImg.size(); i++) {
					imgs.setString(String.valueOf(i), this.displayImg.get(i).toString());
				}
				imgs.setLong("delay", this.imgDelay.getAllTicks());
				nbt.setTag("imgs", imgs);
			}
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
			
			List<ResourceLocation> imgs = null;
			long imgDelay = 0;
			if(nbt.hasKey("imgs")) {
				imgs = Lists.newArrayList();
				NBTTagCompound imgsTag = nbt.getCompoundTag("imgs");
				imgDelay = imgsTag.getLong("delay");
				for(String key : imgsTag.getKeySet()) {
					if(!key.equals("delay")) {
						imgs.add(new ResourceLocation(imgsTag.getString(key)));
					}
				}
			}
			
			CaptionSoundEvent sound = nbt.hasKey("sound") ? CaptionSoundEvent.fromNBT(nbt.getCompoundTag("sound")) : null;
			return new Element(type, talkEntityName, nameArg, text, textArg, talkTime, side, delay, nbt.getBoolean("needBG"), imgs, imgDelay, sound);
		}
		
		@Override
		public String toString() {
			return "Caption@" + Integer.toHexString(hashCode());
		}
	}
	
	static class MsgCaption implements IMessage {
		protected Caption.Element element;
		public MsgCaption() {}
		public MsgCaption(Caption.Element e) {
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
			if(this.element.type.isMainCaption()) {
				if(!currentCaptions.containsKey(name)) {
					currentCaptions.put(name, Lists.newArrayList());
				}
				currentCaptions.get(name).add(element);
			}else {
				if(!secondaryCurrentCaptions.containsKey(name)) {
					secondaryCurrentCaptions.put(name, Lists.newArrayList());
				}
				secondaryCurrentCaptions.get(name).add(element);
			}
			
			return null;
		}
	}
	
	public static class CaptionSoundEvent {
		protected boolean played = false;
		protected final SoundEvent sound;
		protected final float volume;
		protected final float pitch;
		protected final boolean isFollowPlayer;
		protected final BlockPos pos;
		protected final SoundCategory category;
		
		public CaptionSoundEvent(SoundEvent sound, SoundCategory category, float volume, float pitch) {
			this(BlockPos.ORIGIN, true, sound, category, pitch, pitch);
		}
		public CaptionSoundEvent(BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
			this(pos, false, sound, category, pitch, pitch);
		}
		
		protected CaptionSoundEvent(BlockPos pos, boolean followPlayer, SoundEvent sound, SoundCategory category, float volume, float pitch) {
			this.sound = sound;
			this.category = category;
			this.volume = volume;
			this.pitch = pitch;
			this.pos = pos;
			this.isFollowPlayer = followPlayer;
		}

		public SoundEvent getSound() {return sound;}
		public BlockPos getPos() {return pos;}
		public SoundCategory getSoundCategory() {return category;}
		public float getSoundVolume() {return volume;}
		public float getSoundPitch() {return pitch;}
		public boolean isFollowPlayer() {return isFollowPlayer;}
		public boolean isPlayed() {return played;}
		public void setPlayed() {played = true;}
		
		public NBTTagCompound toNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("sound", SoundEvent.REGISTRY.getIDForObject(this.sound));
			nbt.setString("category", this.category.getName());
			nbt.setFloat("volume", volume);
			nbt.setFloat("pitch", pitch);
			nbt.setTag("pos", toNBT(pos));
			nbt.setBoolean("likeRecord", this.isFollowPlayer);
			return nbt;
		}
		
		public static CaptionSoundEvent fromNBT(NBTTagCompound nbt) {
			if(nbt!=null) {
				return new CaptionSoundEvent(toPos(nbt.getCompoundTag("pos")), nbt.getBoolean("likeRecord"), SoundEvent.REGISTRY.getObjectById(nbt.getInteger("sound")), SoundCategory.getByName(nbt.getString("category")), nbt.getFloat("volume"), nbt.getFloat("pitch"));
			}
			return null;
		}
		private static BlockPos toPos(NBTTagCompound nbt) {
			int x = nbt.getInteger("x");
			int y = nbt.getInteger("y");
			int z = nbt.getInteger("z");
			if(x == BlockPos.ORIGIN.getX()
			&& y == BlockPos.ORIGIN.getY()
			&& z == BlockPos.ORIGIN.getZ()) {
				return BlockPos.ORIGIN;
			}
			return new BlockPos(x, y, z);
		}
		private static NBTTagCompound toNBT(BlockPos pos) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("x", pos.getX());
			nbt.setInteger("y", pos.getY());
			nbt.setInteger("z", pos.getZ());
			return nbt;
		}
	}
}
