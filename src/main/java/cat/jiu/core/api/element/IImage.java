package cat.jiu.core.api.element;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cat.jiu.core.api.ITimer;
import cat.jiu.core.api.handler.ISerializable;
import cat.jiu.core.util.element.Image;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;

public interface IImage extends ISerializable {
	public static final IImage DEFAULT_IMAGE = new Image(new ResourceLocation("textures/blocks/bedrock.png")) {
		public Image setUseMillisToTiming(boolean useMillisToTiming) {
			return this;
		}
	};
	
	void startTiming();
	
	int[] getOrder();
	IImage setOrder(int[] order);
	
	ITimer getDelay();
	long getDelayMillis();
	IImage setDelay(long delay);
	
	boolean isUseMillisToTiming();
	IImage setUseMillisToTiming(boolean useMillisToTiming);
	
	ResourceLocation getImage();
	IImage setImg(ResourceLocation img);
	
	List<ResourceLocation> getImages();
	IImage setImgs(List<ResourceLocation> imgs);
	IImage setImg(int index, ResourceLocation img);
	
	IImage setDonePlay(boolean isDonePlaying);
	
	boolean hasMoreFrame();
	
	void update();
	boolean isDonePlay();
	IImage resetDelay();
	
	IImage copy();
	
	default NBTTagCompound write(NBTTagCompound nbt) {
		if(nbt==null) nbt = new NBTTagCompound();
		
		if(this.hasMoreFrame()) {
			NBTTagList imgsTag = new NBTTagList();
			
			for(int i = 0; i < this.getImages().size(); i++) {
				imgsTag.appendTag(new NBTTagString(this.getImages().get(i).toString()));
			}
			nbt.setTag("imgs", imgsTag);
			nbt.setTag("order", new NBTTagIntArray(this.getOrder()));
			nbt.setBoolean("useMillis", this.isUseMillisToTiming());
			nbt.setLong("delay", this.isUseMillisToTiming() ? this.getDelayMillis() : this.getDelay().getAllTicks());
		}else {
			nbt.setString("img", this.getImage().toString());
		}
		return nbt;
	}
	default void read(NBTTagCompound nbt) {
		if(nbt.hasKey("imgs")) {
			List<ResourceLocation> imgs = Lists.newArrayList();
			NBTTagList imgsTag = nbt.getTagList("imgs", 8);
			for(int i = 0; i < imgsTag.tagCount(); i++) {
				imgs.add(new ResourceLocation(imgsTag.getStringTagAt(i)));
			}
			this.setImgs(imgs);
			this.setOrder(nbt.getIntArray("order"));
			
			this.setUseMillisToTiming(nbt.getBoolean("useMillis"));
			this.setDelay(nbt.getLong("delay"));
		}else {
			if(nbt.hasKey("img") && !nbt.getString("img").equalsIgnoreCase(DEFAULT_IMAGE.getImage().toString())) {
				this.setImg(new ResourceLocation(nbt.getString("img")));
			}
		}
	}
	
	@Override
	default JsonObject write(JsonObject json) {
		if(json==null) json = new JsonObject();
		
		if(this.hasMoreFrame()) {
			JsonArray imgsArray = new JsonArray();
			
			for(int i = 0; i < this.getImages().size(); i++) {
				imgsArray.add(this.getImages().get(i).toString());
			}
			json.add("imgs", imgsArray);
			
			JsonArray orderArray = new JsonArray();
			for(int i = 0; i < this.getOrder().length; i++) {
				orderArray.add(this.getOrder()[i]);
			}
			json.add("order", orderArray);
			
			json.addProperty("useMillis", this.isUseMillisToTiming());
			json.addProperty("delay", this.isUseMillisToTiming() ? this.getDelayMillis() : this.getDelay().getAllTicks());
		}else {
			json.addProperty("img", this.getImage().toString());
		}
		
		return json;
	}
	@Override
	default void read(JsonObject json) {
		if(json.has("imgs")) {
			List<ResourceLocation> imgs = Lists.newArrayList();
			
			JsonArray imgsArray = json.getAsJsonArray("imgs");
			for(int i = 0; i < imgsArray.size(); i++) {
				imgs.add(new ResourceLocation(imgsArray.get(i).getAsString()));
			}
			this.setImgs(imgs);

			JsonArray orderArray = json.getAsJsonArray("order");
			int[] order = new int[orderArray.size()];
			for(int i = 0; i < order.length; i++) {
				order[i] = orderArray.get(i).getAsInt();
			}
			this.setOrder(order);
			
			this.setUseMillisToTiming(json.get("useMillis").getAsBoolean());
			this.setDelay(json.get("delay").getAsLong());
		}else {
			if(json.has("img") && !json.get("img").getAsString().equalsIgnoreCase(DEFAULT_IMAGE.getImage().toString())) {
				this.setImg(new ResourceLocation(json.get("img").getAsString()));
				this.setUseMillisToTiming(true);
				this.setDelay(0);
			}
		}
	}
}
