package cat.jiu.caption;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import cat.jiu.caption.jiucore.time.ICaptionTime;
import cat.jiu.caption.jiucore.time.CaptionTime;
import cat.jiu.caption.type.DisplaySideType;
import io.netty.buffer.ByteBuf;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

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
 * 1.添加延迟
 * 2.实现渲染
 */
@Mod.EventBusSubscriber
public class Caption {
	public static final ICaptionTime NO_DELAY = new CaptionTime();
	
	public static void add(EntityPlayer player, BlockPos pos, String displayName, String displayText, ICaptionTime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, @Nullable ResourceLocation displayImg, @Nullable Sound sound) {
		if(!player.world.isRemote) {
			CaptionMain.net.sendMessageToPlayer(new MsgCaption(new Element(pos, displayName, displayText, displayTime, displaySide, displayDelay, displayImg, sound)), (EntityPlayerMP) player);
		}else {
			currentCaptions.add(new Element(pos, displayName, displayText, displayTime, displaySide, displayDelay, displayImg, sound));
		}
	}
	
	public static void add(EntityPlayer player, EntityLiving entity, String displayName, String displayText, ICaptionTime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, @Nullable ResourceLocation displayImg, @Nullable Sound sound) {
		add(player, entity.getPosition(), displayName, displayText, displayTime, displaySide, displayDelay, displayImg, sound);
	}

	@Optional.Method(modid = "jiucore")
	public static void add(EntityPlayer player, BlockPos pos, String displayName, String displayText, cat.jiu.core.api.ITime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, @Nullable ResourceLocation displayImg, @Nullable Sound sound) {
		add(player, pos, displayName, displayText, ICaptionTime.fromCoreTime(displayTime), displaySide, displayDelay, displayImg, sound);
	}
	@Optional.Method(modid = "jiucore")
	public static void add(EntityPlayer player, EntityLiving entity, String displayName, String displayText, cat.jiu.core.api.ITime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, @Nullable ResourceLocation displayImg, @Nullable Sound sound) {
		add(player, entity, displayName, displayText, ICaptionTime.fromCoreTime(displayTime), displaySide, displayDelay, displayImg, sound);
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
	
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if(event.phase == Phase.END && !event.player.world.isRemote) {
			if(!currentCaptions.isEmpty() && !hasCurrentCaption()) {
				current = currentCaptions.get(0).copy();
				currentCaptions.remove(0);
			}
			if(hasCurrentCaption()) {
				if(current.displayTime.isDone()) {
					current = null;
					return;
				}
				
				if(current.delay.isDone()) {
					if(!current.isPlaySound() && current.sound != null) {
						if(current.sound.isLikeRecord()) {
							event.player.world.playSound((double)current.pos.getX(), (double)current.pos.getY(), (double)current.pos.getZ(), current.sound.sound, SoundCategory.PLAYERS, 1F, 1F, false);
						}else {
							event.player.world.playSound(Minecraft.getMinecraft().player, current.pos, current.sound.sound, SoundCategory.PLAYERS, 1F, 1F);
						}
						current.playSound = true;
					}
				}
			}
		}
	}
	
	static void clearCaptions() {
		currentCaptions.clear();
		current = null;
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onRenderTick(TickEvent.RenderTickEvent event) {
		if(event.phase == Phase.END && hasCurrentCaption()) {
			if(current.displayTime.isDone()) {
				current = null;
			}else {
				if(current.delay.isDone() && Minecraft.getMinecraft().player.world.isRemote) {
					current.draw(Minecraft.getMinecraft().ingameGUI, Minecraft.getMinecraft().fontRenderer, event.renderTickTime);
				}
			}
		}
	}
	
	static {
		new Thread(()->{
			while(true) {
				try {Thread.sleep(50);}catch(InterruptedException e) { e.printStackTrace();}
				if(hasCurrentCaption()) {
					if(!current.delay.isDone()) {
						current.delay.update();
					}else {
						current.displayTime.update();
					}
				}
			}
		}, "Draw Caption Thread").start();
	}
	
	// Test
	@SubscribeEvent
	public static void onPlayerBreakBlock(BlockEvent.BreakEvent event) {
		if(event.getState().getBlock() == Blocks.DIAMOND_BLOCK) {
			Caption.add(event.getPlayer(), event.getPos(), "钻石块: " + event.getPos(), "你为什么要这样对我！", new CaptionTime(5, 0), DisplaySideType.UP, new CaptionTime(1, 0), null, null);
		}
	}
	
	public static class Element {
		final BlockPos pos;
		String displayName;
		String displayText;
		final ICaptionTime displayTime;
		final ICaptionTime delay; 
		final DisplaySideType side;
		ResourceLocation displayImg;
		final Sound sound;
		
		public Element(BlockPos pos, String displayName, String displayText, ICaptionTime displayTime, DisplaySideType displaySide, ICaptionTime displayDelay, @Nullable ResourceLocation displayImg, @Nullable Sound sound) {
			this.pos = pos;
			this.displayName = displayName;
			this.displayText = displayText;
			this.displayTime = displayTime;
			this.side = displaySide;
			this.delay = displayDelay;
			this.displayImg = displayImg;
			this.sound = sound;
		}
		
		private boolean playSound = false;
		
		@SideOnly(Side.CLIENT)
		public void draw(GuiIngame gui, FontRenderer fr, float renderTickTime) {
			gui.drawString(fr, this.displayName, 22, 0, Color.YELLOW.getRGB());
			gui.drawString(fr, this.displayText, 22, 10, Color.GRAY.getRGB());
		}

		public Element copy() {
			return new Element(pos, displayName, displayText, displayTime, side, delay, displayImg, sound);
		}

		public BlockPos getPos() {return pos;}
		public String getDisplayName() {return displayName;}
		public String getDisplayText() {return displayText;}
		public ICaptionTime getTalkTime() {return displayTime;}
		public ICaptionTime getDelay() {return delay;}
		public DisplaySideType getDisplaySide() {return side;}
		public ResourceLocation getDisplayImg() {return displayImg;}
		public Sound getSound() {return sound;}
		public boolean isPlaySound() {return playSound;}
		
		public void setDisplayName(String displayName) {this.displayName = displayName;}
		public void setDisplayText(String displayText) {this.displayText = displayText;}
		public void setDisplayImg(ResourceLocation displayImg) {this.displayImg = displayImg;}

		public NBTTagCompound toNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			
			nbt.setTag("pos", toNBT(pos));
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
			BlockPos pos = toPos(nbt.getCompoundTag("pos"));
			String talkEntityName = nbt.getString("name");
			String text = nbt.getString("text");
			ICaptionTime talkTime = ICaptionTime.from(nbt.getCompoundTag("time"));
			ICaptionTime delay = ICaptionTime.from(nbt.getCompoundTag("delay"));
			
			DisplaySideType side = DisplaySideType.getType(nbt.getInteger("side"));
			
			ResourceLocation img = nbt.hasKey("img") ? new ResourceLocation(nbt.getString("img")) : null;
			Sound sound = nbt.hasKey("sound") ? Sound.fromNBT(nbt.getCompoundTag("sound")) : null;
			
			return new Element(pos, talkEntityName, text, talkTime, side, delay, img, sound);
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
	
	public static class MsgCaption implements IMessage {
		Caption.Element type;
		public MsgCaption() {}
		public MsgCaption(Caption.Element type) {
			this.type = type;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			try {
				this.type = Caption.Element.fromNBT(new PacketBuffer(buf).readCompoundTag());
			}catch(IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void toBytes(ByteBuf buf) {
			new PacketBuffer(buf).writeCompoundTag(this.type.toNBT());
		}
		
		public IMessage handler(MessageContext ctx) {
			if(ctx.side.isClient()) {
				currentCaptions.add(type);
			}
			return null;
		}
	}
	
	public static class Sound {
		final SoundEvent sound;
		final boolean likeRecord;
		
		public Sound(ResourceLocation sound, boolean likeRecord) {
			this(new SoundEvent(sound), likeRecord);
		}
		public Sound(SoundEvent sound, boolean likeRecord) {
			this.sound = sound;
			this.likeRecord = likeRecord;
		}

		public SoundEvent getSound() {return sound;}
		public boolean isLikeRecord() {return likeRecord;}
		
		public NBTTagCompound toNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("sound", SoundEvent.REGISTRY.getIDForObject(this.sound));
			nbt.setBoolean("likeRecord", this.likeRecord);
			return nbt;
		}
		
		public static Sound fromNBT(NBTTagCompound nbt) {
			if(nbt!=null) {
				return new Sound(new ResourceLocation(nbt.getString("sound")), nbt.getBoolean("likeRecord"));
			}
			return null;
		}
	}
}
