package crybaby.sounds;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class CrybabySounds
{
    public static SoundEvent crying;
    
    private static int id;
    
    public static void init()
    {
        id = SoundEvent.REGISTRY.getKeys().size();
        
        crying = register("crying");
    }
    
    public static SoundEvent register(String name)
    {
        ResourceLocation location = new ResourceLocation("crybaby", name);
        SoundEvent sound = new SoundEvent(location);
        SoundEvent.REGISTRY.register(id++, location, sound);
        
        return sound;
    }
}
