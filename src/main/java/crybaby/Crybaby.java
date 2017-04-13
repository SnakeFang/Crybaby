package crybaby;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import crybaby.items.ItemSaltBottle;
import crybaby.items.ItemTearBottle;
import crybaby.recipe.ShapelessOreOutRecipe;
import crybaby.sounds.CrybabySounds;
import lombok.Getter;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

@Mod(modid = "crybaby")
public class Crybaby
{
    @Instance
    @Getter
    private static Crybaby instance;
    
    @Getter
    private CreativeTabs creativeTab = new CreativeTabs("crybaby")
    {
        @Override
        public ItemStack getTabIconItem()
        {
            return ItemTearBottle.getInstance().getDefaultInstance();
        }
    };
    
    private Configuration config;
    
    @Getter
    private int smeltExperience;
    
    @Getter
    private int cryingTime;
    
    @Getter
    private int debuffTime;
    
    @Getter
    private float cryingVolume;
    
    @Getter
    private List<String> debuffs;
    
    private void loadConfig()
    {
        smeltExperience = config.getInt("smeltExperience", "general", 5, 0, Integer.MAX_VALUE, "Amount of experience from smelting one bottle of tears into a bottle of salt");
        cryingTime = config.getInt("cryingTime", "general", 400, 1, Integer.MAX_VALUE, "Time, in ticks, to fully fill a bottle with tears (Note: this will affect the meta value of the empty bottle, but not the filled one)");
        debuffTime = config.getInt("debuffTime", "general", 400, 0, Integer.MAX_VALUE, "Time, int ticks, of the debuffs a player gets from crying");
        debuffs = ImmutableList.copyOf(config.getStringList("debuffs", "general", new String[] { "minecraft:slowness", "minecraft:weakness" }, "List of debuffs a player gets from crying"));
        cryingVolume = config.getFloat("cryingVolume", "general", 0.5F, 0.0F, 1.0F, "Volume of crying sound");
        
        if (config.hasChanged())
        {
            config.save();
        }
    }
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        config = new Configuration(event.getSuggestedConfigurationFile());
        loadConfig();
        
        ItemTearBottle.getInstance();
        ItemSaltBottle.getInstance();
        CrybabySounds.init();
        MinecraftForge.EVENT_BUS.register(this);
        
        RecipeSorter.register("crybaby:shapelessoreout", ShapelessOreOutRecipe.class, Category.SHAPELESS, "after:forge:shapelessore");
    }
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        CrybabyRecipes.init();
    }
    
    private Map<UUID, Long> lastCries = Maps.newHashMap();
    
    public void startCrying(ItemStack stack)
    {
        if (stack != null && stack.getItem() == ItemTearBottle.getInstance())
        {
            ItemTearBottle.getInstance().initStack(stack).getTagCompound().setBoolean("crying", true);
        }
    }
    
    public void stopCrying(ItemStack stack)
    {
        if (stack != null && stack.getItem() == ItemTearBottle.getInstance())
        {
            if (stack.hasTagCompound())
            {
                stack.getTagCompound().setBoolean("crying", false);
            }
            else
            {
                ItemTearBottle.getInstance().initStack(stack).getTagCompound().setBoolean("crying", false);
            }
        }
    }
    
    public boolean isCrying(EntityPlayer player)
    {
        for (ItemStack stack : player.inventoryContainer.inventoryItemStacks)
        {
            if (isCrying(stack))
            {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean isDoubleCrying(EntityPlayer player)
    {
        boolean single = false;
        
        for (ItemStack stack : player.inventoryContainer.inventoryItemStacks)
        {
            if (isCrying(stack))
            {
                if (single)
                {
                    return true;
                }
                else
                {
                    single = true;
                }
            }
        }
        
        return false;
    }
    
    public boolean isCrying(ItemStack stack)
    {
        if (stack != null && stack.getItem() == ItemTearBottle.getInstance())
        {
            return ItemTearBottle.getInstance().initStack(stack).getTagCompound().getBoolean("crying");
        }
        else
        {
            return false;
        }
    }
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onSound(PlaySoundAtEntityEvent event)
    {
        if (!event.isCanceled() && (event.getEntity() instanceof EntityPlayer))
        {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            
            if (event.getSound().equals(SoundEvents.ENTITY_GENERIC_DRINK) && player.getActiveItemStack().getItem().equals(ItemTearBottle.getInstance()))
            {
                long lastCry = lastCries.getOrDefault(player.getUniqueID(), 0L);
                
                if (System.currentTimeMillis() - lastCry >= 3000)
                {
                    lastCries.put(player.getUniqueID(), System.currentTimeMillis());
                    event.setSound(CrybabySounds.crying);
                    event.setVolume(getCryingVolume());
                }
                else
                {
                    event.setCanceled(true);
                }
            }
        }
    }
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDamage(LivingAttackEvent event)
    {
        if (!event.isCanceled() && (event.getEntity() instanceof EntityPlayer))
        {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            
            for (Slot slot : player.inventoryContainer.inventorySlots)
            {
                ItemStack stack = slot.getStack();
                
                if (stack != null && stack.getItem().equals(ItemTearBottle.getInstance()) && stack.getItemDamage() > 0)
                {
                    stack.setItemDamage(stack.getItemDamage() - 1);
                    player.inventory.setInventorySlotContents(slot.getSlotIndex(), stack);
                    
                    break;
                }
            }
        }
    }
}
