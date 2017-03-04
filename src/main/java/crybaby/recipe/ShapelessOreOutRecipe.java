package crybaby.recipe;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ShapelessOreOutRecipe extends ShapelessOreRecipe
{
    protected String outputOre;
    protected int outputCount;
    
    public ShapelessOreOutRecipe(String outputOre, int outputCount, Object... inputs)
    {
        super(new ItemStack(Blocks.STONE), inputs);
        super.output = null;
        
        this.outputOre = outputOre;
        this.outputCount = outputCount;
    }
    
    public ShapelessOreOutRecipe(String outputOre, Object... inputs)
    {
        this(outputOre, 1, inputs);
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        if(OreDictionary.doesOreNameExist(outputOre))
        {
            ItemStack stack = OreDictionary.getOres(outputOre).get(0).copy();
            stack.stackSize = outputCount;
            
            if(stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
            {
                stack.setItemDamage(0);
            }
            
            return stack;
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        if(OreDictionary.doesOreNameExist(outputOre))
        {
            ItemStack stack = OreDictionary.getOres(outputOre).get(0).copy();
            stack.stackSize = outputCount;
            
            if(stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
            {
                stack.setItemDamage(0);
            }
            
            return stack;
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public boolean matches(InventoryCrafting inv, World world)
    {
        return OreDictionary.doesOreNameExist(outputOre) && super.matches(inv, world);
    }
}
