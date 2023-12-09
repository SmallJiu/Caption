package cat.jiu.caption.util;

import java.util.List;

import com.google.common.collect.Lists;

import cat.jiu.caption.CaptionAPI;
import cat.jiu.caption.element.style.UpToDownStyle;
import cat.jiu.caption.type.CaptionType;
import cat.jiu.caption.type.DisplaySideType;
import cat.jiu.core.util.element.Image;
import cat.jiu.core.util.element.Sound;
import cat.jiu.core.util.element.Text;
import cat.jiu.core.util.timer.Timer;

import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber
public class Tests {
	
	// Test
	private static final SoundEvent DEV_SOT_SeaLord_Last_Calipso = new SoundEvent(new ResourceLocation("caption:dev_sound")).setRegistryName(new ResourceLocation("caption:dev_sound"));
	@SubscribeEvent
	public static void onPlayerBreakBlock(BlockEvent.BreakEvent event) {
		if(event.getState().getBlock() == Blocks.DIAMOND_BLOCK) {
			if(event.getPlayer().isSneaking()) {
				List<ResourceLocation> imgs = Lists.newArrayList();
				//imgs.add(new ResourceLocation("caption:textures/gui/dev/bat.png"));
				for(int i = 0; i < 13; i++) {
					imgs.add(new ResourceLocation("caption:textures/gui/dev/dev-gif/dev-gif_" + i + ".png"));
				}
				CaptionAPI.add(event.getPlayer(),
						CaptionType.Main, new Text(TextFormatting.RED + "森林蝙蝠"),
						new Text("不是很喜欢加速火把嘛，把火把插进你PY让你好好加速"),
						UpToDownStyle.instance, new Timer(0, 1, 0),
						DisplaySideType.LEFT, new Timer(1, 0), true,
						new Image(imgs, true, 4), null
					);
			}else {
				CaptionAPI.add(event.getPlayer(),
						CaptionType.Main,
						new Text("caption.dev.name"),
						new Text("caption.dev.msg.0"),
						UpToDownStyle.instance, new Timer(0, 4, 0),
						DisplaySideType.RIGHT, new Timer(1, 0),
						true, new Image(new ResourceLocation("caption:textures/gui/dev/dev.png")),
						new Sound(Sound.EMPTY_PLAY_TYIME, DEV_SOT_SeaLord_Last_Calipso, 1F, 1F, SoundCategory.PLAYERS).setPlayPosition(event.getPos())
					);
				CaptionAPI.add(event.getPlayer(), 
						CaptionType.Main,
						new Text("caption.dev.name"),
						new Text("caption.dev.msg.1"),
						UpToDownStyle.instance, new Timer(0, 8, 0),
						DisplaySideType.RIGHT, new Timer(1, 0),
						true, new Image(new ResourceLocation("caption:textures/gui/dev/dev.png")), null
					);
			}
		}
		if(event.getState().getBlock() == Blocks.GOLD_BLOCK) {
			CaptionAPI.add(event.getPlayer(), 
					CaptionType.Main,
					new Text("caption.dev.name"),
					new Text("caption.dev.msg"),
					UpToDownStyle.instance, new Timer(0, 12, 0),
					DisplaySideType.RIGHT, new Timer(1, 0),
					true, new Image(new ResourceLocation("caption:textures/gui/dev/dev.png")),
					new Sound(Sound.EMPTY_PLAY_TYIME, DEV_SOT_SeaLord_Last_Calipso, 1F, 1F, SoundCategory.PLAYERS).setPlayPosition(event.getPos())
				);
		}
	}
	@SubscribeEvent
	public static void onSoundEvenrRegistration(RegistryEvent.Register<SoundEvent> event) {
	    event.getRegistry().register(DEV_SOT_SeaLord_Last_Calipso);
	}
}
