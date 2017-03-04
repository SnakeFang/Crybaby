package crybaby.items;

import crybaby.Crybaby;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSaltBottle extends Item
{
    private static ItemSaltBottle instance;
    
    public static ItemSaltBottle getInstance()
    {
        return instance == null ? (instance = new ItemSaltBottle()) : instance;
    }
    
    public ItemSaltBottle()
    {
        setCreativeTab(Crybaby.getInstance().getCreativeTab());
        setHasSubtypes(true);
        setMaxStackSize(64);
        
        setUnlocalizedName("salt_bottle");
        this.setRegistryName(new ResourceLocation("crybaby:salt_bottle"));
        GameRegistry.register(this);
        
        if (FMLCommonHandler.instance().getSide().isClient())
        {
            registerModels();
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void registerModels()
    {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation("crybaby:salt_bottle"));
    }
    
    @Override
    public boolean hasContainerItem()
    {
        return true;
    }
    
    @Override
    public ItemStack getContainerItem(ItemStack stack)
    {
        return new ItemStack(ItemTearBottle.getInstance(), 1, Crybaby.getInstance().getCryingTime());
    }
}
