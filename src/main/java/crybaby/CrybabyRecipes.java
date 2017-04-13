package crybaby;

import crybaby.items.ItemSaltBottle;
import crybaby.items.ItemTearBottle;
import crybaby.recipe.ShapelessOreOutRecipe;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CrybabyRecipes
{
    public static void init()
    {
        CrybabyRecipes.registerCrafting();
        CrybabyRecipes.registerSmelting();
    }
    
    private static void registerCrafting()
    {
        GameRegistry.addShapedRecipe(ItemTearBottle.getInstance().getDefaultInstance(), "X", "Y", 'X', Items.LEATHER, 'Y', Items.GLASS_BOTTLE);
        GameRegistry.addRecipe(new ShapelessOreOutRecipe("itemSalt", 8, new Object[] { ItemSaltBottle.getInstance() }));
    }
    
    private static void registerSmelting()
    {
        GameRegistry.addSmelting(new ItemStack(ItemTearBottle.getInstance(), 1, 0), new ItemStack(ItemSaltBottle.getInstance(), 1, 0), Crybaby.getInstance().getSmeltExperience());
    }
}
