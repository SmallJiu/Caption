package cat.jiu.caption.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cat.jiu.caption.ModMain;
import cat.jiu.caption.element.Caption;
import cat.jiu.caption.event.CaptionDrawEvent;
import cat.jiu.caption.iface.ICaption;
import cat.jiu.caption.type.CaptionType;
import cat.jiu.caption.type.DrawState;
import cat.jiu.core.api.ITimer;
import cat.jiu.core.api.element.ISound;
import cat.jiu.core.util.timer.Timer;

import io.netty.buffer.ByteBuf;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
public final class CaptionImp {
	private static final ITimer NO_DELAY = new Timer();
	public static ITimer noDelay() {return NO_DELAY.copy();}
	public static final Logger log = LogManager.getLogger("Caption");
	
	static final Map<CaptionType, Map<String, ICaption>> currentCaptions = Maps.newHashMap();
	static final Map<CaptionType, Map<String, List<ICaption>>> alternativeCaptions = Maps.newHashMap();
	static final Map<CaptionType, Map<String, ICaption>> lastCurrentCaptions = Maps.newHashMap();
	
	public static void addCaption(EntityPlayer player, ICaption caption) {
		addCaption(player.getName(), caption);
	}
	static void addCaption(String name, ICaption caption) {
		if(!alternativeCaptions.containsKey(caption.getType())) {
			alternativeCaptions.put(caption.getType(), Maps.newHashMap());
		}
		if(!alternativeCaptions.get(caption.getType()).containsKey(name)) {
			alternativeCaptions.get(caption.getType()).put(name, Lists.newLinkedList());
		}
		
		alternativeCaptions.get(caption.getType()).get(name).add(caption);
	}
	
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
	public static ICaption getCurrentCaption(CaptionType type) {return getCurrentCaption(type, Minecraft.getMinecraft().player.getName());}
	@SideOnly(Side.CLIENT)
	public static boolean hasCurrentCaption() {String name = Minecraft.getMinecraft().player.getName(); return hasCurrentCaption(CaptionType.Main, name) || hasCurrentCaption(CaptionType.Secondary, name);}
	@SideOnly(Side.CLIENT)
	public static boolean hasCurrentCaption(CaptionType type) {return hasCurrentCaption(type, Minecraft.getMinecraft().player.getName());}
	@SideOnly(Side.CLIENT)
	public static ICaption getNextCaption(CaptionType type) {return getNextCaption(type, Minecraft.getMinecraft().player.getName());}
	@SideOnly(Side.CLIENT)
	public static boolean hasNextCaption(CaptionType type) {return hasNextCaption(type, Minecraft.getMinecraft().player.getName());}
	
	public static boolean hasCurrentCaption(CaptionType type, String name) {return getCurrentCaption(type, name) != null;}
	public static ICaption getCurrentCaption(CaptionType type, String name) {
		if(!currentCaptions.containsKey(type)) return null;
		return currentCaptions.get(type).get(name);
	}
	public static ICaption getNextCaption(CaptionType type, String name) {
		if(hasNextCaption(type, name)) {
			return alternativeCaptions.get(type).get(name).get(0);
		}
		return null;
	}
	public static boolean hasNextCaption(CaptionType type, String name) {
		if(!alternativeCaptions.containsKey(type)) {
			return false;
		}
		List<ICaption> e = alternativeCaptions.get(type).get(name);
		return e!=null && !e.isEmpty();
	}
	public static ICaption getLastCaption(CaptionType type, String name) {
		if(lastCurrentCaptions.containsKey(type)) {
			return lastCurrentCaptions.get(type).get(name);
		}
		return null;
	}
	
	static {
		new Thread(()->{
			while(true) {
				try {Thread.sleep(50);}catch(InterruptedException e) { e.printStackTrace();}
				
				if(ModMain.proxy.isClient() && Minecraft.getMinecraft().isGamePaused()) continue;
				
				if(!alternativeCaptions.isEmpty()) {
					for(Entry<CaptionType, Map<String, List<ICaption>>> currents : Sets.newHashSet(alternativeCaptions.entrySet())) {
						Map<String, List<ICaption>> values = alternativeCaptions.get(currents.getKey());
						if(values==null || values.isEmpty()) {
							continue;
						}
						for(Entry<String, List<ICaption>> elements : Sets.newHashSet(values.entrySet())) {
							List<ICaption> element = values.get(elements.getKey());
							if(element==null || element.isEmpty()) values.remove(elements.getKey());
							for(int i = 0; i < element.size(); i++) {
								if(element.get(i).getTalkTime().isDone()) element.remove(i);
							}
						}
					}
				}
				
				if(!currentCaptions.isEmpty()) {
					for(Entry<CaptionType, Map<String, ICaption>> allCurrents : currentCaptions.entrySet()) {
						for(Entry<String, ICaption> currents : allCurrents.getValue().entrySet()) {
							try {
								currents.getValue().updataTime();
							}catch(Exception e) {
								e.printStackTrace();
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
	
	@SubscribeEvent
	public static void onPlayerLeaveServer(PlayerLoggedOutEvent event) {
		for(Entry<CaptionType, Map<String, ICaption>> currents : Sets.newHashSet(CaptionImp.currentCaptions.entrySet())) {
			CaptionImp.currentCaptions.get(currents.getKey()).remove(event.player.getName());
		}
		for(Entry<CaptionType, Map<String, List<ICaption>>> currents : Sets.newHashSet(CaptionImp.alternativeCaptions.entrySet())) {
			CaptionImp.alternativeCaptions.get(currents.getKey()).remove(event.player.getName());
		}
	}
	
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if(event.phase == Phase.START) {
			String name = event.player.getName();
			try {
				next(name);
			}catch(Exception e) {
				e.printStackTrace();
			}
			//处理已完成的字幕
			for(CaptionType type : CaptionType.VALUES) {
				if(CaptionImp.hasCurrentCaption(type, name)) {
					ICaption current = CaptionImp.getCurrentCaption(type, name);
					if(current.getShowPostDelay().isDone()) {
						if(current.getDisplayImg()!=null) current.getDisplayImg().setDonePlay(true);// 处理字幕的图片
						if(!CaptionImp.lastCurrentCaptions.containsKey(type)) CaptionImp.lastCurrentCaptions.put(type, Maps.newHashMap());
						CaptionImp.lastCurrentCaptions.get(type).put(name, current);
						CaptionImp.currentCaptions.get(type).remove(name);
						return;
					}
				}
			}
		}
	}
	private static void next(String playerName) {
		for(Entry<CaptionType, Map<String, List<ICaption>>> currents : CaptionImp.alternativeCaptions.entrySet()) {
			CaptionType type = currents.getKey();
			
			if(CaptionImp.hasNextCaption(type, playerName)) {
				List<ICaption> elements = currents.getValue().get(playerName);
				if(!CaptionImp.hasCurrentCaption(type, playerName)) {
					if(!CaptionImp.currentCaptions.containsKey(type)) CaptionImp.currentCaptions.put(type, Maps.newHashMap());
					CaptionImp.currentCaptions.get(type).put(playerName, elements.remove(0));
					CaptionImp.getCurrentCaption(type, playerName).getDisplayImg().startTiming();
				}else if(CaptionImp.hasCurrentCaption(type, playerName) && CaptionImp.getCurrentCaption(type, playerName).getTalkTime().isDone()) {
					if(CaptionImp.getCurrentCaption(type, playerName).getDisplayName().getText().equalsIgnoreCase(CaptionImp.getNextCaption(type, playerName).getDisplayName().getText())) {
						CaptionImp.getCurrentCaption(type, playerName).changeTo(elements.remove(0));
					}
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onClientPlayerTick(TickEvent.PlayerTickEvent event) {
		if(event.phase == Phase.START) {
			String name = event.player.getName();
			for(CaptionType type : CaptionType.VALUES) {
				if(CaptionImp.hasCurrentCaption(type, name)) {
					ICaption current = CaptionImp.getCurrentCaption(type, name);
					if(current.getShowPreDelay().isDone() && current.getSound() != null && !current.getSound().isPlayed()) {
						current.getSound().setPlayed(true);
						CapitonSndSound soundSnd = type.isMainCaption() ? new CapitonSndSound(event.player, current.getSound(), current.getTalkTime()) : new SecondarySndSound(event.player, current.getSound(), current.getTalkTime());
						Minecraft.getMinecraft().getSoundHandler().playSound(soundSnd);
					}
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	private static class SecondarySndSound extends CapitonSndSound {
		public SecondarySndSound(EntityPlayer player, ISound sound, ITimer playTime) {
			super(player, sound, playTime);
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onRenderDebugInfo(RenderGameOverlayEvent.Text event) {
		if(Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			event.getLeft().add("");
			event.getLeft().add("Caption: " + CaptionImp.getAllCaptionsSize(Minecraft.getMinecraft().player.getName()));
			if(CaptionImp.hasCurrentCaption(CaptionType.Main)) {
				ICaption current = CaptionImp.getCurrentCaption(CaptionType.Main);
				event.getLeft().add("  Current: " + current);
				event.getLeft().add("  Next: " + CaptionImp.getNextCaption(CaptionType.Main));
				event.getLeft().add("    Delay: " + current.getDelay().toStringTime(false));
				event.getLeft().add("     Time: " + current.getTalkTime().toStringTime(false));
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
				if(CaptionImp.hasCurrentCaption(type)) {
					ICaption current = CaptionImp.getCurrentCaption(type);

					GlStateManager.pushMatrix();
					GlStateManager.color(1,1,1,1);
					
					try {
						if(current.getDelay().isDone() && !current.getShowPreDelay().isDone()) {
							if(MinecraftForge.EVENT_BUS.post(new CaptionDrawEvent.Pre(mc.ingameGUI, current, DrawState.PRE))) {GlStateManager.popMatrix(); continue;}
							current.preDraw(sr, mc, mc.ingameGUI, mc.fontRenderer);
							MinecraftForge.EVENT_BUS.post(new CaptionDrawEvent.Post(mc.ingameGUI, current, DrawState.PRE));
						}else if(current.getShowPreDelay().isDone() && !current.getTalkTime().isDone()) {
							if(MinecraftForge.EVENT_BUS.post(new CaptionDrawEvent.Pre(mc.ingameGUI, current, DrawState.DRAW))) {GlStateManager.popMatrix(); continue;}
							current.draw(sr, mc, mc.ingameGUI, mc.fontRenderer);
							MinecraftForge.EVENT_BUS.post(new CaptionDrawEvent.Post(mc.ingameGUI, current, DrawState.DRAW));
						}else if(current.getTalkTime().isDone() && !current.getShowPostDelay().isDone()) {
							if(MinecraftForge.EVENT_BUS.post(new CaptionDrawEvent.Pre(mc.ingameGUI, current, DrawState.POST))) {GlStateManager.popMatrix(); continue;}
							current.postDraw(sr, mc, mc.ingameGUI, mc.fontRenderer);
							MinecraftForge.EVENT_BUS.post(new CaptionDrawEvent.Post(mc.ingameGUI, current, DrawState.POST));
						}
					}catch(Throwable e) {
						e.printStackTrace();
					}
					GlStateManager.popMatrix();
				}
			}
		}
	}
	
	public static final class MsgCaption implements IMessage {
		private ICaption element;
		public MsgCaption() {}
		public MsgCaption(ICaption e) {
			this.element = e;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			try {
				this.element = Caption.fromNBT(new PacketBuffer(buf).readCompoundTag());
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void toBytes(ByteBuf buf) {
			new PacketBuffer(buf).writeCompoundTag(this.element.toNBT());
		}
		
		public IMessage handler(MessageContext ctx) {
			String player;
			if(ctx.side.isClient()) {
				player = Minecraft.getMinecraft().player.getName();
			}else {
				player=	ctx.getServerHandler().player.getName();
			}
			addCaption(player, this.element);
			return null;
		}
	}
}
