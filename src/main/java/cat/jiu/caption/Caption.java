package cat.jiu.caption;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import cat.jiu.caption.jiucore.time.ICaptionTime;
import cat.jiu.caption.jiucore.time.CaptionTime;
import cat.jiu.caption.type.DisplaySideType;

import io.netty.buffer.ByteBuf;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
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
public class Caption {
	public static final ICaptionTime NO_DELAY = new CaptionTime();
	
	public static void add(EntityPlayer player, Caption.Element e) {
		if(!player.world.isRemote) {
			CaptionMain.net.sendMessageToPlayer(new MsgCaption(e), (EntityPlayerMP) player);
		}else {
			currentCaptions.add(e);
		}
	}
	
	public static void add(EntityPlayer player, String displayName, String displayText, ICaptionTime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, @Nullable ResourceLocation displayImg, @Nullable Sound sound) {
		add(player, new Element(displayName, displayText, displayTime, displaySide, displayDelay, displayImg, sound));
	}

	@Optional.Method(modid = "jiucore")
	public static void add(EntityPlayer player, String displayName, String displayText, cat.jiu.core.api.ITime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, @Nullable ResourceLocation displayImg, @Nullable Sound sound) {
		add(player, new Element(displayName, displayText, ICaptionTime.fromCoreTime(displayTime), displaySide, displayDelay, displayImg, sound));
	}
	
	private static List<Caption.Element> currentCaptions = Lists.newArrayList();
	private static Caption.Element current = null;
	public static Caption.Element getCurrentCaption() {return current;}
	public static boolean hasCurrentCaption() {return current != null;}
	public static Caption.Element getNextCaption() {
		if(hasNextCaption()) {
			return currentCaptions.get(0);
		}
		return null;
	}
	public static boolean hasNextCaption() {return !currentCaptions.isEmpty();}
	
	private static void next() {
		if(hasNextCaption() && !hasCurrentCaption()) {
			current = currentCaptions.get(0).copy();
			currentCaptions.remove(0);
		}
	}
	
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if(event.phase == Phase.END) {
			next();
			
			if(hasCurrentCaption()) {
				if(current.displayTime.isDone()) {
					current = null;
					return;
				}
				
				boolean flag = false;
				
				if(current.show_delay != null && current.show_delay.isDone()) {
					if(current.sound != null && !current.sound.isPlayed()) {
						flag = true;
					}
				}else if(current.delay.isDone()) {
					if(current.sound != null && !current.sound.isPlayed()) {
						flag = true;
					}
				}
				
				if(flag) {
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
	
	static {
		new Thread(()->{
			while(true) {
				try {Thread.sleep(50);}catch(InterruptedException e) { e.printStackTrace();}
				if(CaptionMain.proxy.isClient()) {
					if(Minecraft.getMinecraft().isGamePaused()) {
						continue;
					}
				}
				if(hasCurrentCaption()) {
					if(!current.delay.isDone()) {
						current.delay.update();
					}else if(current.show_delay != null && !current.show_delay.isDone()) {
						current.show_delay.update();
					}else {
						current.displayTime.update();
					}
				}
			}
		}).start();
	}
	
	static void clearCaptions() {
		currentCaptions.clear();
		current = null;
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onRenderDebugInfo(RenderGameOverlayEvent.Text event) {
		if(Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			event.getLeft().add("");
			event.getLeft().add("Caption:");
			event.getLeft().add("  Surpluses: " + currentCaptions.size());
			if(hasCurrentCaption()) {
				event.getLeft().add("  Current:");
				event.getLeft().add("    Delay: " + current.delay.toStringTime(false));
				event.getLeft().add("     Time: " + current.displayTime.toStringTime(false));
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
		if(event.getType() == ElementType.ALL && hasCurrentCaption()) {
			if(!current.displayTime.isDone()) {
				Minecraft mc = Minecraft.getMinecraft();
				
				boolean lag = current.show_delay != null ? current.show_delay.isDone() : current.delay.isDone();
				if(lag && !mc.gameSettings.hideGUI) {
					current.draw(mc, mc.ingameGUI, mc.fontRenderer);
				}
			}
		}
	}
	
	// Test
	@SubscribeEvent
	public static void onPlayerBreakBlock(BlockEvent.BreakEvent event) {
		if(event.getState().getBlock() == Blocks.DIAMOND_BLOCK) {
			for(int i = 0; i < 5; i++) {
				Caption.add(event.getPlayer(), "钻石块", "你为什么要这样对我！", new CaptionTime(0, 5, 0), DisplaySideType.DOWN, new CaptionTime(1, 0), null, new Caption.Sound(SoundEvents.BLOCK_GLASS_PLACE, event.getPos(), false));
			}
		}
	}
	
	public static class Element {
		protected static Random rand = new Random();
		protected static ICaptionTime SHOW_DELAY = new CaptionTime(1,0);
		private String displayName;
		private String displayText;
		protected final ICaptionTime delay;
		protected final ICaptionTime show_delay;
		protected final ICaptionTime displayTime;
		protected final DisplaySideType side;
		protected ResourceLocation displayImg;
		protected Sound sound;
		
		public Element(String displayName, String displayText, ICaptionTime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, @Nullable ResourceLocation displayImg, @Nullable Sound sound) {
			this.displayName = displayName;
			this.displayText = displayText;
			this.displayTime = displayTime;
			this.delay = displayDelay;
			this.displayImg = displayImg;
			this.sound = sound;
			this.side = DisplaySideType.rand(displaySide);
			this.show_delay = this.side != DisplaySideType.DOWN ? SHOW_DELAY.copy() : null;
		}
		
		final static ITextComponent EMPTY_TEXT = new TextComponentString("");
		
		@SuppressWarnings("incomplete-switch")
		@SideOnly(Side.CLIENT)
		public void draw(Minecraft mc, GuiIngame gui, FontRenderer fr) {
			ScaledResolution sr = new ScaledResolution(mc);
			int width = sr.getScaledWidth();
	        int height = sr.getScaledHeight();
	        int centerX = width / 2 -1;
	        int centerY = height / 2 - 4;
	        
	        String name = I18n.format(this.displayName);
	        String text = I18n.format(this.displayText);
	        
	        switch(this.side) {
				case DOWN:
					this.drawDown(name, text, fr, sr, centerX, centerY);
					break;
				case LEFT:
					this.drawLeft(name, text, fr, sr, centerX, centerY);
					break;
				case RIGHT:
					this.drawRight(name, text, fr, sr, centerX, centerY);
					break;
			}
		}
		
		protected void drawDown(String name, String text, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
			int y = sr.getScaledHeight() - 16 - 3 - 73;
			fr.drawString(name, centerX - fr.getStringWidth(name) / 2, y, Color.YELLOW.getRGB(), false);
			fr.drawString(text, centerX - fr.getStringWidth(text) / 2, y + 11, 16777215);
		}
		
		protected void drawLeft(String name, String text, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
			
		}
		
		protected void drawRight(String name, String text, FontRenderer fr, ScaledResolution sr, int centerX, int centerY) {
			
		}

		public Element copy() {
			return new Element(displayName, displayText, displayTime, side, delay, displayImg, sound);
		}

		public String getDisplayName() {return displayName;}
		public String getDisplayText() {return displayText;}
		public ICaptionTime getTalkTime() {return displayTime;}
		public ICaptionTime getDelay() {return delay;}
		public ICaptionTime getShowDelay() {return show_delay;}
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
			currentCaptions.add(element);
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
