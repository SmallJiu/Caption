package cat.jiu.caption;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cat.jiu.caption.Caption.Element;
import cat.jiu.caption.element.CaptionImage;
import cat.jiu.caption.element.CaptionSound;
import cat.jiu.caption.element.CaptionText;
import cat.jiu.caption.element.style.StyleUpToDown;
import cat.jiu.caption.event.CaptionDrawEvent;
import cat.jiu.caption.jiucore.time.CaptionTime;
import cat.jiu.caption.jiucore.time.ICaptionTime;
import cat.jiu.caption.type.CaptionType;
import cat.jiu.caption.type.DisplaySideType;
import cat.jiu.caption.type.DrawState;
import cat.jiu.caption.util.CapitonSndSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class Events {
	
	// Test
	private static final SoundEvent DEV_SOT_SeaLord_Last_Calipso = new SoundEvent(new ResourceLocation("caption:dev_sound")).setRegistryName(new ResourceLocation("caption:dev_sound"));
	private static boolean test() {return false;}
	@SubscribeEvent
	public static void onPlayerBreakBlock(BlockEvent.BreakEvent event) {
		if(event.getState().getBlock() == Blocks.DIAMOND_BLOCK) {
			if(test()) {
				List<ResourceLocation> imgs = Lists.newArrayList();
				//imgs.add(new ResourceLocation("caption:textures/gui/dev/bat.png"));
				for(int i = 0; i < 13; i++) {
					imgs.add(new ResourceLocation("caption:textures/gui/dev/dev-gif/dev-gif_" + i + ".png"));
				}
				Caption.utils.add(event.getPlayer(), CaptionType.Main, new CaptionText(TextFormatting.RED + "森林蝙蝠"), new CaptionText("不是很喜欢加速火把嘛，把火把插进你PY让你好好加速"), StyleUpToDown.instance, new CaptionTime(0, 1, 0), DisplaySideType.RIGHT, new CaptionTime(1, 0), true, new CaptionImage(imgs, true, 4), null);
			}else {
				Caption.utils.add(event.getPlayer(), CaptionType.Main, new CaptionText("caption.dev.name"), new CaptionText("caption.dev.msg.0"), StyleUpToDown.instance, new CaptionTime(0, 4, 0), DisplaySideType.DOWN, new CaptionTime(1, 0), true, new CaptionImage(new ResourceLocation("caption:textures/gui/dev/dev.png")), new CaptionSound(DEV_SOT_SeaLord_Last_Calipso, SoundCategory.PLAYERS, 1F, 1F));
				Caption.utils.add(event.getPlayer(), CaptionType.Main, new CaptionText("caption.dev.name"), new CaptionText("caption.dev.msg.1"), StyleUpToDown.instance, new CaptionTime(0, 8, 0), DisplaySideType.DOWN, new CaptionTime(1, 0), true, new CaptionImage(new ResourceLocation("caption:textures/gui/dev/dev.png")), null);
			}
		}
		if(event.getState().getBlock() == Blocks.GOLD_BLOCK) {
			Caption.utils.add(event.getPlayer(), CaptionType.Main, new CaptionText("caption.dev.name"), new CaptionText("caption.dev.msg"), StyleUpToDown.instance, new CaptionTime(0, 12, 0), DisplaySideType.RIGHT, new CaptionTime(1, 0), true, new CaptionImage(new ResourceLocation("caption:textures/gui/dev/dev.png")), new CaptionSound(DEV_SOT_SeaLord_Last_Calipso, SoundCategory.PLAYERS, 1F, 1F));
		}
	}
	@SubscribeEvent
	public static void onSoundEvenrRegistration(RegistryEvent.Register<SoundEvent> event) {
	    event.getRegistry().register(DEV_SOT_SeaLord_Last_Calipso);
	}
	
	
	
	@SubscribeEvent
	public static void onPlayerLeaveServer(PlayerLoggedOutEvent event) {
		for(Entry<CaptionType, Map<String, Element>> currents : Sets.newHashSet(Caption.currentCaptions.entrySet())) {
			Caption.currentCaptions.get(currents.getKey()).remove(event.player.getName());
		}
		for(Entry<CaptionType, Map<String, List<Element>>> currents : Sets.newHashSet(Caption.alternativeCaptions.entrySet())) {
			Caption.alternativeCaptions.get(currents.getKey()).remove(event.player.getName());
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
			
			for(CaptionType type : CaptionType.VALUES) {
				if(Caption.hasCurrentCaption(type, name)) {
					Caption.Element current = Caption.getCurrentCaption(type, name);
					if(current.show_post_delay.isDone()) {
						current.img.setDonePlaying(true);
						if(!Caption.lastCurrentCaptions.containsKey(type)) Caption.lastCurrentCaptions.put(type, Maps.newHashMap());
						Caption.lastCurrentCaptions.get(type).put(name, current);
						Caption.currentCaptions.get(type).remove(name);
						return;
					}
				}
			}
		}
	}
	private static void next(String playerName) {
		for(Entry<CaptionType, Map<String, List<Element>>> currents : Caption.alternativeCaptions.entrySet()) {
			CaptionType type = currents.getKey();
			
			if(Caption.hasNextCaption(type, playerName)) {
				List<Element> elements = currents.getValue().get(playerName);
				if(!Caption.hasCurrentCaption(type, playerName)) {
					if(!Caption.currentCaptions.containsKey(type)) Caption.currentCaptions.put(type, Maps.newHashMap());
					Caption.currentCaptions.get(type).put(playerName, elements.remove(0));
					Caption.getCurrentCaption(type, playerName).img.startTiming();
				}else if(Caption.hasCurrentCaption(type, playerName) && Caption.getCurrentCaption(type, playerName).displayTime.isDone()) {
					if(Caption.getCurrentCaption(type, playerName).displayName.getText().equalsIgnoreCase(Caption.getNextCaption(type, playerName).displayName.getText())) {
						Caption.getCurrentCaption(type, playerName).changeTo(elements.remove(0));
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
				if(Caption.hasCurrentCaption(type, name)) {
					Caption.Element current = Caption.getCurrentCaption(type, name);
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
	}
	
	@SideOnly(Side.CLIENT)
	private static class SecondarySndSound extends CapitonSndSound {
		public SecondarySndSound(EntityPlayer player, CaptionSound sound, ICaptionTime playTime) {
			super(player, sound, playTime);
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onRenderDebugInfo(RenderGameOverlayEvent.Text event) {
		if(Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			event.getLeft().add("");
			event.getLeft().add("Caption: " + Caption.getAllCaptionsSize(Minecraft.getMinecraft().player.getName()));
			if(Caption.hasCurrentCaption(CaptionType.Main)) {
				Caption.Element current = Caption.getCurrentCaption(CaptionType.Main);
				event.getLeft().add("  Current: " + current);
				event.getLeft().add("  Next: " + Caption.getNextCaption(CaptionType.Main));
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
				if(Caption.hasCurrentCaption(type)) {
					Caption.Element current = Caption.getCurrentCaption(type);

					GlStateManager.pushMatrix();
					GlStateManager.color(1,1,1,1);
					
					try {
						if(current.delay.isDone() && !current.show_pre_delay.isDone()) {
							if(MinecraftForge.EVENT_BUS.post(new CaptionDrawEvent.Pre(mc.ingameGUI, current, DrawState.PRE))) continue;
							current.preDraw(sr, mc, mc.ingameGUI, mc.fontRenderer);
							MinecraftForge.EVENT_BUS.post(new CaptionDrawEvent.Post(mc.ingameGUI, current, DrawState.PRE));
						}else if(current.show_pre_delay.isDone() && !current.displayTime.isDone()) {
							if(MinecraftForge.EVENT_BUS.post(new CaptionDrawEvent.Pre(mc.ingameGUI, current, DrawState.DRAW))) continue;
							current.draw(sr, mc, mc.ingameGUI, mc.fontRenderer);
							MinecraftForge.EVENT_BUS.post(new CaptionDrawEvent.Post(mc.ingameGUI, current, DrawState.DRAW));
						}else if(current.displayTime.isDone() && !current.show_post_delay.isDone()) {
							if(MinecraftForge.EVENT_BUS.post(new CaptionDrawEvent.Pre(mc.ingameGUI, current, DrawState.POST))) continue;
							current.postDraw(sr, mc, mc.ingameGUI, mc.fontRenderer);
							MinecraftForge.EVENT_BUS.post(new CaptionDrawEvent.Post(mc.ingameGUI, current, DrawState.POST));
						}
					}catch(Throwable e) {
						Caption.log.error(e + ": " + e.getLocalizedMessage());
						e.printStackTrace();
					}
					GlStateManager.popMatrix();
				}
			}
		}
	}
}
