package crybaby.items;

import crybaby.Crybaby;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
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
    
    public ItemStack initStack(ItemStack stack)
    {
        if(!stack.hasTagCompound())
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        
        if(!stack.getTagCompound().hasKey("crying", 1))
        {
            stack.getTagCompound().setBoolean("crying", false);
        }
        
        return stack;
    }
    
    @Override
    public ItemStack getDefaultInstance()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("crying", true);
        
        ItemStack stack = new ItemStack(getInstance(), 1, Crybaby.getInstance().getCryingTime());
        stack.setTagCompound(nbt);
        
        return stack;
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
    public void getSubItems(Item item, CreativeTabs tabs, NonNullList<ItemStack> list)
    {
        list.add(initStack(new ItemStack(item, 1, 0)));
        list.add(getDefaultInstance());
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        
        if (stack.getItemDamage() > 0 && !Crybaby.getInstance().isCrying(player))
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
        return stack.getItemDamage() == 0 || Crybaby.getInstance().isCrying(stack) ? EnumAction.NONE : EnumAction.DRINK;
    }
    
    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 32;
    }
    
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entity)
    {
        if (stack.getItemDamage() > 0 && entity instanceof EntityPlayer && !Crybaby.getInstance().isCrying((EntityPlayer) entity))
        {
            Crybaby.getInstance().startCrying(stack);
            
            int debuffTime = Crybaby.getInstance().getDebuffTime();
            
            for (String potionName : Crybaby.getInstance().getDebuffs())
            {
                Potion potion = Potion.REGISTRY.getObject(new ResourceLocation(potionName));
                PotionEffect effect = entity.getActivePotionEffect(potion);
                
                if (effect != null)
                {
                    entity.addPotionEffect(new PotionEffect(potion, effect.getDuration() + debuffTime, effect.getAmplifier(), effect.getIsAmbient(), effect.doesShowParticles()));
                }
                else
                {
                    entity.addPotionEffect(new PotionEffect(potion, debuffTime));
                }
            }
        }
        
        return stack;
    }
    
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
    {
        if (entity instanceof EntityPlayer && Crybaby.getInstance().isCrying(stack))
        {
            EntityPlayer player = (EntityPlayer) entity;
            
            if (!Crybaby.getInstance().isDoubleCrying(player))
            {
                if (stack != null && stack.getItem().equals(ItemTearBottle.getInstance()) && stack.getItemDamage() > 0)
                {
                    stack.setItemDamage(stack.getItemDamage() - 1);
                    
                    if (stack.getItemDamage() <= 0)
                    {
                        stack.setItemDamage(0);
                        Crybaby.getInstance().stopCrying(stack);
                    }
                }
            }
            else
            {
                Crybaby.getInstance().stopCrying(stack);
            }
        }
    }
}
