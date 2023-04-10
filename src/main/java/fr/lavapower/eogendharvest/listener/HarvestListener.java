package fr.lavapower.eogendharvest.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class HarvestListener implements Listener {
    @EventHandler
    public void onBreakCrop(PlayerInteractEvent event) {
        if(event.getAction() == Action.PHYSICAL) {
            try {
                if(event.getClickedBlock().getType() == Material.FARMLAND)
                    event.setCancelled(true);
            }
            catch (NullPointerException ignored) {
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasBlock()) {
            Player p = event.getPlayer();
            Block b = event.getClickedBlock();
            if(b == null)
                return;
            switch(b.getType()) {
                case WHEAT -> harvestCrop(p, b, Material.WHEAT, Material.WHEAT, Material.WHEAT_SEEDS, ((Ageable)b.getBlockData()).getMaximumAge());
                case CARROTS -> harvestCrop(p, b, Material.CARROT, Material.CARROTS, Material.CARROT, ((Ageable)b.getBlockData()).getMaximumAge());
                case POTATOES -> harvestCrop(p, b, Material.POTATO, Material.POTATOES, Material.POTATO, ((Ageable)b.getBlockData()).getMaximumAge());
                case BEETROOTS -> harvestCrop(p, b, Material.BEETROOT, Material.BEETROOTS, Material.BEETROOT_SEEDS, ((Ageable)b.getBlockData()).getMaximumAge());
                case NETHER_WART -> harvestCrop(p, b, Material.NETHER_WART, Material.NETHER_WART, Material.NETHER_WART, ((Ageable)b.getBlockData()).getMaximumAge());
                case COCOA -> harvestCocoa(p, b);
                case MELON -> harvestStem(p, b, Material.ATTACHED_MELON_STEM, Material.MELON, Material.MELON_SEEDS);
                case PUMPKIN -> harvestStem(p, b, Material.ATTACHED_PUMPKIN_STEM, Material.PUMPKIN, Material.PUMPKIN_SEEDS);
            }
        }
    }

    private void harvestCrop(Player p, Block b, Material output, Material seeds, Material droppedSeeds, int maximumAge) {
        if(b.getType() == seeds && ((Ageable)b.getBlockData()).getAge() == maximumAge) {
            b.setType(seeds);
            Ageable bd = (Ageable) b.getBlockData();
            bd.setAge(0);
            b.setBlockData(bd);

            int rand = random(1, 2);
            b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(output, rand));
            if(random(0, 100) < 25)
                b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(droppedSeeds, 1));
        }
    }

    private void harvestStem(Player p, Block b, Material stem, Material block, Material seeds) {
        if(b.getType() == block) {
            Location loc = b.getLocation();
            World w = b.getWorld();
            Location[] dir = {
                    new Location(w, loc.getX() - 1D, loc.getY(), loc.getZ()),
                    new Location(w, loc.getX() + 1D, loc.getY(), loc.getZ()),
                    new Location(w, loc.getX(), loc.getY(), loc.getZ() - 1D),
                    new Location(w, loc.getX(), loc.getY(), loc.getZ() + 1D)
            };
            for (Location l: dir) {
                Block sided = w.getBlockAt(l);
                if(sided.getType() == stem) {
                    if(((Directional)sided.getBlockData()).getFacing() == sided.getFace(b))
                        b.breakNaturally();
                }
            }
        }
    }

    private void harvestCocoa(Player p, Block b) {
        if(b.getType() == Material.COCOA) {
            int full = b.getData() / 4;
            int face = b.getData() - 8;
            if(full == 2) {
                b.breakNaturally();
                b.setType(Material.COCOA);
                BlockState bs = b.getState();
                bs.setRawData((byte)face);
                b.setBlockData(bs.getBlockData());
            }
        }
    }

    private int random(int min, int max) {
        Random rand = new Random();
        return rand.nextInt(max - min + 1) + min;
    }
}
