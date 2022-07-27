package cat.jiu.dialog;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import cat.jiu.dialog.jiucore.time.BigTime;
import cat.jiu.dialog.jiucore.time.ITime;
import cat.jiu.dialog.jiucore.time.Time;
import cat.jiu.dialog.type.ShowPosType;

import io.netty.buffer.ByteBuf;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class Dialog {
	public static void dialog(BlockPos pos, String talkEntityName, String text, ITime talkTime, ShowPosType side) {
		dialog(pos, talkEntityName, text, talkTime, side, null, null);
	}
	public static void dialog(EntityLiving entity, String talkEntityName, String text, ITime talkTime, ShowPosType side) {
		dialog(entity.getPosition(), talkEntityName, text, talkTime, side);
	}
	public static void dialog(BlockPos pos, String talkEntityName, String text, ITime talkTime, ShowPosType side, @Nullable ResourceLocation img, @Nullable Sound sound) {
		currentDialogs.add(new Type(pos, talkEntityName, text, talkTime, side, img, sound));
	}
	public static void dialog(EntityLiving entity, String talkEntityName, String text, ITime talkTime, ShowPosType side, @Nullable ResourceLocation img, @Nullable Sound sound) {
		dialog(entity.getPosition(), talkEntityName, text, talkTime, side, img, sound);
	}

	@Optional.Method(modid = "jiucore")
	public static void dialog(BlockPos pos, String talkEntityName, String text, cat.jiu.core.api.ITime talkTime, ShowPosType side) {
		dialog(pos, talkEntityName, text, ITime.fromCoreTime(talkTime), side);
	}
	@Optional.Method(modid = "jiucore")
	public static void dialog(EntityLiving entity, String talkEntityName, String text, cat.jiu.core.api.ITime talkTime, ShowPosType side) {
		dialog(entity, talkEntityName, text, ITime.fromCoreTime(talkTime), side);
	}
	@Optional.Method(modid = "jiucore")
	public static void dialog(BlockPos pos, String talkEntityName, String text, cat.jiu.core.api.ITime talkTime, ShowPosType side, @Nullable ResourceLocation img, @Nullable Sound sound) {
		dialog(pos, talkEntityName, text, ITime.fromCoreTime(talkTime), side, img, sound);
	}
	@Optional.Method(modid = "jiucore")
	public static void dialog(EntityLiving entity, String talkEntityName, String text, cat.jiu.core.api.ITime talkTime, ShowPosType side, @Nullable ResourceLocation img, @Nullable Sound sound) {
		dialog(entity, talkEntityName, text, ITime.fromCoreTime(talkTime), side, img, sound);
	}
	
	private static List<Dialog.Type> currentDialogs = Lists.newArrayList();
	private static Dialog.Type current = null;
	public static Dialog.Type getCurrentDialog() {return current.copy();}
	public static boolean hasCurrentDialog() {return current != null;}
	
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if(event.phase == Phase.END) {
			if(!currentDialogs.isEmpty() && !hasCurrentDialog()) {
				current = currentDialogs.get(0).copy();
				currentDialogs.remove(0);
			}
			if(hasCurrentDialog()) {
				if(current.talkTime.isDone()) {
					current = null;
					return;
				}
				
				if(!current.sendToClient && event.side.isServer()) {
					current.sendToClient = true;
					DialogMain.net.sendMessageToPlayer(new MsgDialogue(current), (EntityPlayerMP) event.player);
				}
				
				current.talkTime.update();
				if(!current.isPlaySound() && current.sound != null) {
					if(current.sound.isLikeRecord()) {
						event.player.world.playSound((double)current.pos.getX(), (double)current.pos.getY(), (double)current.pos.getZ(), current.sound.sound, SoundCategory.PLAYERS, 1F, 1F, false);
					}else {
						event.player.world.playSound(event.player, current.pos, current.sound.sound, SoundCategory.PLAYERS, 1F, 1F);
					}
					current.playSound = true;
				}
				if(event.side.isClient()) {
					current.render(Minecraft.getMinecraft());
				}
			}
		}
	}
	
	public static class Type {
		final BlockPos pos;
		final String talkName;
		final String text;
		final ITime talkTime; 
		final ShowPosType side;
		final ResourceLocation img;
		final Sound sound;
		
		public Type(BlockPos pos, String talkName, String text, ITime talkTime, ShowPosType side) {
			this(pos, talkName, text, talkTime, side, null, null);
		}
		
		public Type(BlockPos pos, String talkEntityName, String text, ITime talkTime, ShowPosType side, @Nullable ResourceLocation img, @Nullable Sound sound) {
			this.pos = pos;
			this.talkName = talkEntityName;
			this.text = text;
			this.talkTime = talkTime;
			this.side = side;
			this.img = img;
			this.sound = sound;
		}
		boolean playSound = false;
		boolean sendToClient = false;
		
		@SideOnly(Side.CLIENT)
		public void render(Minecraft mc) {
			
		}

		public Type copy() {
			return new Type(pos, talkName, text, talkTime, side, img, sound);
		}

		public BlockPos getPos() {return pos;}
		public String getName() {return talkName;}
		public String getText() {return text;}
		public ITime getTalkTime() {return talkTime;}
		public ShowPosType getSide() {return side;}
		public ResourceLocation getImg() {return img;}
		public Sound getSound() {return sound;}
		public boolean isPlaySound() {return playSound;}

		public NBTTagCompound toNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			
			nbt.setTag("pos", toNBT(pos));
			nbt.setString("name", this.talkName);
			nbt.setString("text", this.text);
			
			NBTTagCompound time = this.talkTime.writeToNBT(new NBTTagCompound(), false);
			time.setBoolean("isBig", this.talkTime instanceof BigTime);
			nbt.setTag("time", time);
			
			nbt.setInteger("side", this.side.getID());
			if(this.img!=null)nbt.setString("img", this.img.toString());
			if(this.sound!=null)nbt.setTag("sound", this.sound.toNBT());
			
			return nbt;
		}
		
		public static Type fromNBT(NBTTagCompound nbt) {
			BlockPos pos = toPos(nbt.getCompoundTag("pos"));
			String talkEntityName = nbt.getString("name");
			String text = nbt.getString("text");
			ITime talkTime = null;
				NBTTagCompound timeNBT = nbt.getCompoundTag("time");
				talkTime = timeNBT.getBoolean("isBig") ? new BigTime() : new Time();
				talkTime.readFromNBT(timeNBT);
			
			ShowPosType side = ShowPosType.getType(nbt.getInteger("side"));
			ResourceLocation img = nbt.hasKey("img") ? new ResourceLocation(nbt.getString("img")) : null;
			Sound sound = nbt.hasKey("sound") ? Sound.fromNBT(nbt.getCompoundTag("sound")) : null;
			
			return new Type(pos, talkEntityName, text, talkTime, side, img, sound);
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
	
	public static class MsgDialogue implements IMessage {
		Dialog.Type type;
		public MsgDialogue() {}
		public MsgDialogue(Dialog.Type type) {
			this.type = type;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			try {
				this.type = Dialog.Type.fromNBT(new PacketBuffer(buf).readCompoundTag());
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
				current = type;
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
