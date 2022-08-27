package cat.jiu.caption;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cat.jiu.caption.jiucore.time.ICaptionTime;
import cat.jiu.caption.jiucore.time.CaptionTime;
import cat.jiu.caption.type.DisplaySideType;

import io.netty.buffer.ByteBuf;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
	
	public static void add(EntityPlayer player, Caption.Element e) {
		if(player instanceof EntityPlayerMP) {
			CaptionMain.net.sendMessageToPlayer(new MsgCaption(e), (EntityPlayerMP) player);
		}else {
			CaptionMain.net.sendMessageToServer(new MsgCaption(e));
		}
	}
	
	public static void add(EntityPlayer player, String displayName, Object[] nameArg, String displayText, Object[] textArg, ICaptionTime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, @Nullable ResourceLocation displayImg, @Nullable Sound sound) {
		add(player, new Element(displayName, nameArg, displayText, textArg, displayTime, displaySide, displayDelay, displayImg, sound));
	}

	@Optional.Method(modid = "jiucore")
	public static void add(EntityPlayer player, String displayName, Object[] nameArg, String displayText, Object[] textArg, cat.jiu.core.api.ITime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, @Nullable ResourceLocation displayImg, @Nullable Sound sound) {
		add(player, new Element(displayName, nameArg, displayText, textArg, ICaptionTime.fromCoreTime(displayTime), displaySide, displayDelay, displayImg, sound));
	}
	
	
	
	// Test
	public static final SoundEvent DEV_SOT_SeaLord_Last_Calipso = new SoundEvent(new ResourceLocation("caption:dev_sound"));
	private static boolean test() {return true;}
	@SubscribeEvent
	public static void onPlayerBreakBlock(BlockEvent.BreakEvent event) {
		if(event.getState().getBlock() == Blocks.DIAMOND_BLOCK) {
			if(test()) {
//				Caption.add(event.getPlayer(), "caption.dev.name", "caption.dev.msg.0", new CaptionTime(0, 4, 0), DisplaySideType.DOWN, new CaptionTime(1, 0), null, new Caption.Sound(DEV_SOT_SeaLord_Last_Calipso, event.getPos(), false));
//				Caption.add(event.getPlayer(), "caption.dev.name", "caption.dev.msg.1", new CaptionTime(0, 8, 0), DisplaySideType.DOWN, new CaptionTime(1, 0), null, null);
				Caption.add(event.getPlayer(), "caption.dev.name", new Object[] {System.currentTimeMillis()}, "caption.dev.msg.1", null, new CaptionTime(0, 1, 0), DisplaySideType.DOWN, new CaptionTime(1, 0), null, null);
			}else {
				Caption.add(event.getPlayer(), "caption.dev.name", new Object[] {System.currentTimeMillis()}, "caption.dev.msg", null, new CaptionTime(0, 12, 0), DisplaySideType.DOWN, new CaptionTime(1, 0), null, new Caption.Sound(DEV_SOT_SeaLord_Last_Calipso, event.getPos(), false, 1F, 1F));
			}
		}
	}
	@SubscribeEvent
	public static void onSoundEvenrRegistration(RegistryEvent.Register<SoundEvent> event) {
	    event.getRegistry().register(DEV_SOT_SeaLord_Last_Calipso.setRegistryName(DEV_SOT_SeaLord_Last_Calipso.getSoundName()));
	}
	
	
	
	
	private static final Map<String, List<Caption.Element>> currentCaptions = Maps.newHashMap();
	private static final Map<String, Caption.Element> current = Maps.newHashMap();
	
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
	
	private static void next(String playerName) {
		if(hasNextCaption(playerName) && !hasCurrentCaption(playerName)) {
			current.put(playerName, currentCaptions.get(playerName).remove(0));
		}else if(hasNextCaption(playerName) && hasCurrentCaption(playerName) && getCurrentCaption(playerName).displayTime.isDone()) {
			if(getCurrentCaption(playerName).displayName.equalsIgnoreCase(getNextCaption(playerName).displayName)) {
				getCurrentCaption(playerName).changeTo(currentCaptions.get(playerName).remove(0));
			}
		}
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
				if(current.show_pre_delay.isDone()) {
					if(event.player.world.isRemote && current.sound != null && !current.sound.isPlayed()) {
						current.sound.setPlayed();
						if(current.sound.isLikeRecord()) {
							event.player.world.playRecord(current.sound.pos, current.sound.sound);
						}else {
							event.player.world.playSound(event.player, current.sound.pos, current.sound.sound, SoundCategory.PLAYERS, current.sound.volume, current.sound.pitch);
						}
					}
				}
			}
		}
	}
	
	static {
		new Thread(()->{
			while(true) {
				try {Thread.sleep(50);}catch(InterruptedException e) { e.printStackTrace();}
				if(CaptionMain.proxy.isClient() && Minecraft.getMinecraft().isGamePaused()) continue;
				
				for(Entry<String, Caption.Element> currents : Maps.newHashMap(current).entrySet()) {
					if(currents.getValue() == null) {
						current.remove(currents.getKey());
					}
				}
				for(Entry<String, List<Caption.Element>> currents : Maps.newHashMap(currentCaptions).entrySet()) {
					if(currents.getValue() == null || currents.getValue().isEmpty()) {
						currentCaptions.remove(currents.getKey());
					}
				}
				
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
				}
			}
		}).start();
	}
	
	static void clearCaptions() {
		currentCaptions.clear();
		current.clear();
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onRenderDebugInfo(RenderGameOverlayEvent.Text event) {
		if(Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			event.getLeft().add("");
			event.getLeft().add("Caption:");
			event.getLeft().add("  Surpluses: " + (currentCaptions.containsKey(Minecraft.getMinecraft().player.getName()) ? currentCaptions.get(Minecraft.getMinecraft().player.getName()).size() : 0));
			if(hasCurrentCaption()) {
				Caption.Element current = getCurrentCaption();
				event.getLeft().add("  Current: " + current);
				event.getLeft().add("     Next: " + getNextCaption());
				event.getLeft().add("    Delay: " + current.delay.toStringTime(false));
				event.getLeft().add("     Time: " + current.displayTime.toStringTime(false));
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
		if(event.getType() == ElementType.CHAT && hasCurrentCaption() && !Minecraft.getMinecraft().gameSettings.hideGUI) {
			Caption.Element current = getCurrentCaption();
			Minecraft mc = Minecraft.getMinecraft();
			ScaledResolution sr = new ScaledResolution(mc);
			if(current.delay.isDone() && !current.show_pre_delay.isDone()) {
				current.preDraw(sr, mc, mc.ingameGUI, mc.fontRenderer);
			}else if(current.show_pre_delay.isDone() && !current.displayTime.isDone()) {
				current.draw(sr, mc, mc.ingameGUI, mc.fontRenderer);
			}else if(current.displayTime.isDone() && !current.show_post_delay.isDone()) {
				current.postDraw(sr, mc, mc.ingameGUI, mc.fontRenderer);
			}
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	public static class Element {
		protected static final Random rand = new Random();
		protected static final ICaptionTime SHOW_PRE_DELAY = new CaptionTime(10);
		protected static final ICaptionTime SHOW_POST_DELAY = new CaptionTime(10);
		protected static final ResourceLocation bg_texture = new ResourceLocation("caption:textures/gui/bg.png");
		protected static final ResourceLocation down_texture = new ResourceLocation("caption:textures/gui/down.png");
		protected static final ResourceLocation side_texture = new ResourceLocation("caption:textures/gui/side.png");
		private static final Object[] EMPTY_ARGS = new Object[0];
		
		protected String displayName;
		protected Object[] nameArg;
		protected String displayText;
		protected Object[] textArg;
		protected final ICaptionTime delay;
		protected final ICaptionTime show_pre_delay;
		protected final ICaptionTime show_post_delay;
		protected final ICaptionTime displayTime;
		protected final DisplaySideType side;
		protected ResourceLocation displayImg;
		protected Sound sound;
		
		public Element(String displayName, @Nullable Object[] nameArg, String displayText, @Nullable Object[] textArg, ICaptionTime displayTime, DisplaySideType displaySide, @Nonnull ICaptionTime displayDelay, @Nullable ResourceLocation displayImg, @Nullable Sound sound) {
			this.displayName = displayName;
			this.nameArg = nameArg == null ? EMPTY_ARGS : nameArg;
			this.displayText = displayText;
			this.textArg = textArg == null ? EMPTY_ARGS : textArg;
			this.displayTime = displayTime;
			this.delay = displayDelay;
			this.displayImg = displayImg;
			this.sound = sound;
			this.side = DisplaySideType.rand(displaySide);
			this.show_pre_delay = SHOW_PRE_DELAY.copy();
			this.show_post_delay = SHOW_POST_DELAY.copy();
		}
		
		final static ITextComponent EMPTY_TEXT = new TextComponentString("");
		
		@SideOnly(Side.CLIENT)
		public void draw(ScaledResolution sr, Minecraft mc, GuiIngame gui, FontRenderer fr) {
			int width = sr.getScaledWidth();
	        int height = sr.getScaledHeight();
	        int centerX = width / 2 -1;
	        int centerY = height / 2 - 4;
	        
	        switch(this.side) {
				case DOWN:
					this.drawDown(DrawStage.DRAW, mc, gui, fr, sr, centerX, centerY);
					break;
				case LEFT:
					this.drawLeft(DrawStage.DRAW, mc, gui, fr, sr, centerX, centerY);
					break;
				case RIGHT:
					this.drawRight(DrawStage.DRAW, mc, gui, fr, sr, centerX, centerY);
					break;
			}
		}
		
		@SideOnly(Side.CLIENT)
		public void preDraw(ScaledResolution sr, Minecraft mc, GuiIngame gui, FontRenderer fr) {
			int width = sr.getScaledWidth();
	        int height = sr.getScaledHeight();
	        int centerX = width / 2 -1;
	        int centerY = height / 2 - 4;
	        
	        switch(this.side) {
				case DOWN:
					this.drawDown(DrawStage.PRE, mc, gui, fr, sr, centerX, centerY);
					break;
				case LEFT:
					this.drawLeft(DrawStage.PRE, mc, gui, fr, sr, centerX, centerY);
					break;
				case RIGHT:
					this.drawRight(DrawStage.PRE, mc, gui, fr, sr, centerX, centerY);
					break;
			}
		}
		
		@SideOnly(Side.CLIENT)
		public void postDraw(ScaledResolution sr, Minecraft mc, GuiIngame gui, FontRenderer fr) {
			int width = sr.getScaledWidth();
	        int height = sr.getScaledHeight();
	        int centerX = width / 2 -1;
	        int centerY = height / 2 - 4;
	        
	        switch(this.side) {
				case DOWN:
					this.drawDown(DrawStage.POST, mc, gui, fr, sr, centerX, centerY);
					break;
				case LEFT:
					this.drawLeft(DrawStage.POST, mc, gui, fr, sr, centerX, centerY);
					break;
				case RIGHT:
					this.drawRight(DrawStage.POST, mc, gui, fr, sr, centerX, centerY);
					break;
			}
		}
		
		protected void drawDown(DrawStage stage, Minecraft mc, GuiIngame gui, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
			int x = sr.getScaledHeight() - 16 - 3 - 73;
			int y = sr.getScaledHeight() - 16 - 3 - 73;
			
			String name = I18n.format(this.displayName, this.nameArg);
	        List<String> texts = this.splitString(I18n.format(this.displayText, this.textArg), fr);
			int height = 16 + (texts.size() * 10);
			
			switch(stage) {
				case PRE:
					int pre_part = 11 - this.show_pre_delay.getPart(10);
					if(pre_part != -1) {
						mc.getTextureManager().bindTexture(down_texture);
						float pre_h = (height / 10.0F) * pre_part;
						Gui.drawModalRectWithCustomSizedTexture(x - 55, y - 3, 0, 0, 256, (int)pre_h, 256, (int)pre_h);
					}
					break;
				case DRAW:
					mc.getTextureManager().bindTexture(down_texture);
					Gui.drawModalRectWithCustomSizedTexture(x - 55, y - 3, 0, 0, 256, height, 256, height);
					fr.drawString(name, x - 55 + 5, y + 1, Color.YELLOW.getRGB(), false);
					for(int i = 0; i < texts.size(); i++) {
						fr.drawString(texts.get(i), x - 55 + 5, y + 1 + 10 + (i * 10), 16777215);
					}
					break;
				case POST:
					int post_part = this.show_post_delay.getPart(10);
					if(post_part > 0) {
						mc.getTextureManager().bindTexture(down_texture);
						float post_h = height / 10.0F * post_part;
						Gui.drawModalRectWithCustomSizedTexture(x - 55, y - 3, 0, 0, 256, (int)post_h, 256, (int)post_h);
					}
					break;
			}
		}
		
		protected void drawLeft(DrawStage stage, Minecraft mc, GuiIngame gui, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
			switch(stage) {
				case PRE:
					
					break;
				case DRAW:
					
					break;
					
				case POST:
					break;
			}
		}
		
		protected void drawRight(DrawStage stage, Minecraft mc, GuiIngame gui, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
			switch(stage) {
				case PRE:
					
					break;
				case DRAW:
					
					break;
					
				case POST:
					break;
			}
		}
		
		protected List<String> splitString(String text, FontRenderer fr) {
			List<String> texts = Lists.newArrayList();
			int sideLength = this.side == DisplaySideType.DOWN ? 246 : 256;
			if(fr.getStringWidth(text) >= sideLength) {
				
				char[] cache = text.toCharArray();
				StringBuilder s = new StringBuilder();
				
				for(int i = 0; i < cache.length; i++) {
					s.append(cache[i]);
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
			return new Element(displayName, nameArg, displayText, textArg, displayTime, side, delay, displayImg, sound);
		}
		
		public void changeTo(Element other) {
			this.nameArg = other.nameArg;
			this.displayText = other.displayText;
			this.textArg = other.textArg;
			this.displayTime.add(other.displayTime);
		}

		public String getDisplayName() {return displayName;}
		public String getDisplayText() {return displayText;}
		public ICaptionTime getTalkTime() {return displayTime;}
		public ICaptionTime getDelay() {return delay;}
		public ICaptionTime getShowDelay() {return show_pre_delay;}
		public DisplaySideType getDisplaySide() {return side;}
		public ResourceLocation getDisplayImg() {return displayImg;}
		public Sound getSound() {return sound;}
		
		public void setDisplayName(String displayName) {this.displayName = displayName;}
		public void setDisplayText(String displayText) {this.displayText = displayText;}
		public void setDisplayImg(ResourceLocation displayImg) {this.displayImg = displayImg;}

		public NBTTagCompound toNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			
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
			if(this.displayImg!=null)nbt.setString("img", this.displayImg.toString());
			if(this.sound!=null)nbt.setTag("sound", this.sound.toNBT());
			
			return nbt;
		}
		
		public static Element fromNBT(NBTTagCompound nbt) {
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
			
			ResourceLocation img = nbt.hasKey("img") ? new ResourceLocation(nbt.getString("img")) : null;
			Sound sound = nbt.hasKey("sound") ? Sound.fromNBT(nbt.getCompoundTag("sound")) : null;
			
			return new Element(talkEntityName, nameArg, text, textArg, talkTime, side, delay, img, sound);
		}
		
		@Override
		public String toString() {
			return "Caption@" + Integer.toHexString(hashCode());
		}
		public static enum DrawStage {
			PRE, DRAW, POST
		}
	}
	
	public static class MsgCaption implements IMessage {
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
			String name = "";
			if(ctx.side.isClient()) {
				name = Minecraft.getMinecraft().player.getName();
			}else {
				name = ctx.getServerHandler().player.getName();
			}
			if(!currentCaptions.containsKey(name)) {
				currentCaptions.put(name, Lists.newArrayList());
			}
			currentCaptions.get(name).add(element);
			return null;
		}
	}
	
	public static class Sound {
		private boolean played = false;
		protected final SoundEvent sound;
		protected final float volume;
		protected final float pitch;
		protected final boolean likeRecord;
		protected final BlockPos pos;
		
		public Sound(ResourceLocation sound, BlockPos pos, boolean likeRecord, float volume, float pitch) {
			this(new SoundEvent(sound), pos, likeRecord, volume, pitch);
		}
		
		public Sound(SoundEvent sound, BlockPos pos, boolean likeRecord, float volume, float pitch) {
			this.sound = sound;
			this.volume = volume;
			this.pitch = pitch;
			this.pos = pos;
			this.likeRecord = likeRecord;
		}

		public SoundEvent getSound() {return sound;}
		public BlockPos getPos() {return pos;}
		public float getSoundVolume() {return volume;}
		public float getSoundPitch() {return pitch;}
		public boolean isLikeRecord() {return likeRecord;}
		public boolean isPlayed() {return played;}
		public void setPlayed() {played = true;}
		
		public NBTTagCompound toNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("sound", SoundEvent.REGISTRY.getIDForObject(this.sound));
			nbt.setFloat("volume", volume);
			nbt.setFloat("pitch", pitch);
			nbt.setTag("pos", toNBT(pos));
			nbt.setBoolean("likeRecord", this.likeRecord);
			return nbt;
		}
		
		public static Sound fromNBT(NBTTagCompound nbt) {
			if(nbt!=null) {
				return new Sound(SoundEvent.REGISTRY.getObjectById(nbt.getInteger("sound")), toPos(nbt.getCompoundTag("pos")), nbt.getBoolean("likeRecord"), nbt.getFloat("volume"), nbt.getFloat("pitch"));
			}
			return null;
		}
		private static BlockPos toPos(NBTTagCompound nbt) {
			return new BlockPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
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
