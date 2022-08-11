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
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
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
		CaptionMain.net.sendMessageToPlayer(new MsgCaption(e), (EntityPlayerMP) player);
	}
	
	public static void add(EntityPlayer player, String displayName, String displayText, ICaptionTime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, @Nullable ResourceLocation displayImg, @Nullable Sound sound) {
		add(player, new Element(displayName, displayText, displayTime, displaySide, displayDelay, displayImg, sound));
	}

	@Optional.Method(modid = "jiucore")
	public static void add(EntityPlayer player, String displayName, String displayText, cat.jiu.core.api.ITime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, @Nullable ResourceLocation displayImg, @Nullable Sound sound) {
		add(player, new Element(displayName, displayText, ICaptionTime.fromCoreTime(displayTime), displaySide, displayDelay, displayImg, sound));
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
	public static boolean hasCurrentCaption(String name) {return current.containsKey(name);}
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
		return !currentCaptions.get(name).isEmpty();
	}
	
	private static void next(String name) {
		if(hasNextCaption(name) && !hasCurrentCaption(name)) {
			current.put(name, currentCaptions.get(name).remove(0));
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
					if(current.sound != null && !current.sound.isPlayed()) {
						current.sound.setPlayed();
						if(current.sound.isLikeRecord()) {
							event.player.world.playRecord(current.sound.pos, current.sound.sound);
						}else {
							event.player.world.playSound(null, current.sound.pos, current.sound.sound, SoundCategory.PLAYERS, 1F, 1F);
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
	
	// Test
	@SubscribeEvent
	public static void onPlayerBreakBlock(BlockEvent.BreakEvent event) {
		if(event.getState().getBlock() == Blocks.DIAMOND_BLOCK) {
//			for(int i = 0; i < 20; i++) {
				Caption.add(event.getPlayer(), "钻石块sdfgdsfgsdfgdsfg", "你为什么要这样对我！asdgvasdvasdgasdvasdvasdrg", new CaptionTime(0, 5, 0), DisplaySideType.DOWN, new CaptionTime(1, 0), null, new Caption.Sound(SoundEvents.BLOCK_GLASS_PLACE, event.getPos(), false));
//			}
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	public static class Element {
		protected static Random rand = new Random();
		protected static ICaptionTime SHOW_PRE_DELAY = new CaptionTime(16);
		protected static ICaptionTime SHOW_POST_DELAY = new CaptionTime(16);
		protected static ResourceLocation bg_texture = new ResourceLocation("caption:textures/gui/bg.png");
		protected static ResourceLocation down_texture = new ResourceLocation("caption:textures/gui/down.png");
		protected static ResourceLocation side_texture = new ResourceLocation("caption:textures/gui/side.png");
		
		private String displayName;
		private String displayText;
		protected final ICaptionTime delay;
		protected final ICaptionTime show_pre_delay;
		protected final ICaptionTime show_post_delay;
		protected final ICaptionTime displayTime;
		protected final DisplaySideType side;
		protected ResourceLocation displayImg;
		protected Sound sound;
		
		public Element(String displayName, String displayText, ICaptionTime displayTime, DisplaySideType displaySide, @Nonnull ICaptionTime displayDelay, @Nullable ResourceLocation displayImg, @Nullable Sound sound) {
			this.displayName = displayName;
			this.displayText = displayText;
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
	        
	        String name = I18n.format(this.displayName);
	        String text = I18n.format(this.displayText);
	        
	        switch(this.side) {
				case DOWN:
					this.drawDown(DrawStage.DRAW, name, text, mc, gui, fr, sr, centerX, centerY);
					break;
				case LEFT:
					this.drawLeft(DrawStage.DRAW, name, text, mc, gui, fr, sr, centerX, centerY);
					break;
				case RIGHT:
					this.drawRight(DrawStage.DRAW, name, text, mc, gui, fr, sr, centerX, centerY);
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
					this.drawDown(DrawStage.PRE, null, null, mc, gui, fr, sr, centerX, centerY);
					break;
				case LEFT:
					this.drawLeft(DrawStage.PRE, null, null, mc, gui, fr, sr, centerX, centerY);
					break;
				case RIGHT:
					this.drawRight(DrawStage.PRE, null, null, mc, gui, fr, sr, centerX, centerY);
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
					this.drawDown(DrawStage.POST, null, null, mc, gui, fr, sr, centerX, centerY);
					break;
				case LEFT:
					this.drawLeft(DrawStage.POST, null, null, mc, gui, fr, sr, centerX, centerY);
					break;
				case RIGHT:
					this.drawRight(DrawStage.POST, null, null, mc, gui, fr, sr, centerX, centerY);
					break;
			}
		}
		
		protected void drawDown(DrawStage stage, String name, String text, Minecraft mc, GuiIngame gui, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
			int x = sr.getScaledHeight() - 16 - 3 - 73;
			int y = sr.getScaledHeight() - 16 - 3 - 73;
			switch(stage) {
				case PRE:
					int pre_part = this.show_pre_delay.getPart(16);
					if(pre_part != -1) {
						mc.getTextureManager().bindTexture(down_texture);
						Gui.drawModalRectWithCustomSizedTexture(x - 55, y - 3, 0, 0, 256, 36 / pre_part, 256, 36 / pre_part);
					}
					break;
				case DRAW:
					mc.getTextureManager().bindTexture(down_texture);
					Gui.drawModalRectWithCustomSizedTexture(x - 55, y - 3, 0, 0, 256, 36, 256, 36);
					fr.drawString(name, x - 55 + 5, y + 1, Color.YELLOW.getRGB(), false);
					fr.drawString(text, x - 55 + 5, y + 1 + 10, 16777215);
					break;
				case POST:
					int post_part = 17 - this.show_post_delay.getPart(16);
					if(post_part > 0) {
						mc.getTextureManager().bindTexture(down_texture);
						Gui.drawModalRectWithCustomSizedTexture(x - 55, y - 3, 0, 0, 256, 36 / post_part, 256, 36 / post_part);
					}
					break;
			}
		}
		
		protected void drawLeft(DrawStage stage, String name, String text, Minecraft mc, GuiIngame gui, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
			switch(stage) {
				case PRE:
					
					break;
				case DRAW:
					
					break;
					
				case POST:
					break;
			}
		}
		
		protected void drawRight(DrawStage stage, String name, String text, Minecraft mc, GuiIngame gui, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
			switch(stage) {
				case PRE:
					
					break;
				case DRAW:
					
					break;
					
				case POST:
					break;
			}
		}

		public Element copy() {
			return new Element(displayName, displayText, displayTime, side, delay, displayImg, sound);
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
			nbt.setString("text", this.displayText);
			nbt.setTag("time", this.displayTime.writeToNBT(new NBTTagCompound(), false));
			nbt.setTag("delay", this.delay.writeToNBT(new NBTTagCompound(), false));
			nbt.setInteger("side", this.side.getID());
			if(this.displayImg!=null)nbt.setString("img", this.displayImg.toString());
			if(this.sound!=null)nbt.setTag("sound", this.sound.toNBT());
			
			return nbt;
		}
		
		public static Element fromNBT(NBTTagCompound nbt) {
			String talkEntityName = nbt.getString("name");
			String text = nbt.getString("text");
			ICaptionTime talkTime = ICaptionTime.from(nbt.getCompoundTag("time"));
			ICaptionTime delay = ICaptionTime.from(nbt.getCompoundTag("delay"));
			
			DisplaySideType side = DisplaySideType.getType(nbt.getInteger("side"));
			
			ResourceLocation img = nbt.hasKey("img") ? new ResourceLocation(nbt.getString("img")) : null;
			Sound sound = nbt.hasKey("sound") ? Sound.fromNBT(nbt.getCompoundTag("sound")) : null;
			
			return new Element(talkEntityName, text, talkTime, side, delay, img, sound);
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
		Caption.Element element;
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
		protected final boolean likeRecord;
		protected final BlockPos pos;
		
		public Sound(ResourceLocation sound, BlockPos pos, boolean likeRecord) {
			this(new SoundEvent(sound), pos, likeRecord);
		}
		
		public Sound(SoundEvent sound, BlockPos pos, boolean likeRecord) {
			this.sound = sound;
			this.pos = pos;
			this.likeRecord = likeRecord;
		}

		public SoundEvent getSound() {return sound;}
		public BlockPos getPos() {return pos;}
		public boolean isLikeRecord() {return likeRecord;}
		public boolean isPlayed() {return played;}
		public void setPlayed() {played = true;}
		
		public NBTTagCompound toNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("sound", SoundEvent.REGISTRY.getIDForObject(this.sound));
			nbt.setTag("pos", toNBT(pos));
			nbt.setBoolean("likeRecord", this.likeRecord);
			return nbt;
		}
		
		public static Sound fromNBT(NBTTagCompound nbt) {
			if(nbt!=null) {
				return new Sound(SoundEvent.REGISTRY.getObjectById(nbt.getInteger("sound")), toPos(nbt.getCompoundTag("pos")), nbt.getBoolean("likeRecord"));
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
