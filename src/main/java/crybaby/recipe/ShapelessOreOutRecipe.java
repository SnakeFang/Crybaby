package crybaby.recipe;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ShapelessOreOutRecipe extends ShapelessOreRecipe
{
    protected String outputOre;
    protected ItemStack outputDefault;
    protected int outputCount;
    
    public ShapelessOreOutRecipe(String outputOre, int outputCount, Object... inputs)
    {
        super(new ItemStack(Blocks.STONE), inputs);
        output = null;
        
        this.outputOre = outputOre;
        this.outputCount = outputCount;
        
        NBTTagCompound nbt = new NBTTagCompound();
        
        nbt.setTag("display", new NBTTagCompound());
        nbt.getCompoundTag("display").setString("Name", TextFormatting.RESET + "IOU");
        nbt.getCompoundTag("display").setTag("Lore", new NBTTagList());
        nbt.getCompoundTag("display").getTagList("Lore", 8).appendTag(new NBTTagString("" + TextFormatting.RESET + TextFormatting.RED + "IOU: " + outputCount + " * " + outputOre));
        
        outputDefault = new ItemStack(Items.PAPER, 1, 0);
        outputDefault.setTagCompound(nbt);
    }
    
    public ShapelessOreOutRecipe(String outputOre, Object... inputs)
    {
        this(outputOre, 1, inputs);
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        return getRecipeOutput();
    }
    
    @Override
    public ItemStack getRecipeOutput()
    {
        if (OreDictionary.doesOreNameExist(outputOre))
        {
            ItemStack stack = OreDictionary.getOres(outputOre).get(0).copy();
            stack.stackSize = outputCount;
            
            if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
            {
                stack.setItemDamage(0);
            }
            
            return stack;
        }
        else
        {
            return outputDefault.copy();
        }
    }
    
    @Override
    public boolean matches(InventoryCrafting inv, World world)
    {
        return super.matches(inv, world);
    }
}
