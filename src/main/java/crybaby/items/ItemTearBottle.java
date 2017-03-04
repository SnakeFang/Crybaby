package crybaby.items;

import java.util.List;

import crybaby.Crybaby;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTearBottle extends Item
{
    private static ItemTearBottle instance;
    
    public static ItemTearBottle getInstance()
    {
        return instance == null ? (instance = new ItemTearBottle()) : instance;
    }
    
    public ItemTearBottle()
    {
        setCreativeTab(Crybaby.getInstance().getCreativeTab());
        setHasSubtypes(true);
        setMaxStackSize(1);
        
        setUnlocalizedName("tear_bottle");
        this.setRegistryName(new ResourceLocation("crybaby:tear_bottle"));
        GameRegistry.register(this);
        
        if (FMLCommonHandler.instance().getSide().isClient())
        {
            registerModels();
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void registerModels()
    {
        int cryingTime = Crybaby.getInstance().getCryingTime();
        
        for (int meta = 0; meta <= cryingTime; meta++)
        {
            int tears = (6 * (cryingTime - meta)) / cryingTime;
            
            ModelLoader.setCustomModelResourceLocation(this, meta, new ModelResourceLocation("crybaby:tear_bottle_" + tears));
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list)
    {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, Crybaby.getInstance().getCryingTime()));
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {
        if ((stack.getMetadata() > 0) && !Crybaby.getInstance().isCrying(player))
        {
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        else
        {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
    }
    
    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return stack.getItemDamage() == 0 ? EnumAction.NONE : EnumAction.DRINK;
    }
    
    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 32;
    }
    
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entity)
    {
        if ((entity instanceof EntityPlayer) && (stack.getItemDamage() > 0))
        {
            Crybaby.getInstance().startCrying((EntityPlayer) entity);
            
            stack.setItemDamage(stack.getItemDamage() - 1);
            
            int debuffTime = Crybaby.getInstance().getDebuffTime();
            
            for (String potion : Crybaby.getInstance().getDebuffs())
            {
                addToPotionEffect(entity, Potion.REGISTRY.getObject(new ResourceLocation(potion)), debuffTime);
            }
        }
        
        return stack;
    }
    
    private void addToPotionEffect(EntityLivingBase entity, Potion potion, int time)
    {
        PotionEffect effect = entity.getActivePotionEffect(potion);
        
        if (effect != null)
        {
            entity.addPotionEffect(new PotionEffect(potion, effect.getDuration() + time, effect.getAmplifier(), effect.getIsAmbient(), effect.doesShowParticles()));
        }
        else
        {
            entity.addPotionEffect(new PotionEffect(potion, time));
        }
    }
}
