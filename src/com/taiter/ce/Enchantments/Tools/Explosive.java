package com.taiter.ce.Enchantments.Tools;

/*
* This file is part of Custom Enchantments
* Copyright (C) Taiterio 2015
*
* This program is free software: you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as published by the
* Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
* for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.massivecore.ps.PS;
import com.taiter.ce.CEListener;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.Tools;
import com.taiter.ce.Enchantments.CEnchantment;



public class Explosive extends CEnchantment {

	int Radius;
	boolean LargerRadius;
	
	public Explosive(String originalName, Application app, int enchantProbability, int occurrenceChance) {
		super(originalName, app, enchantProbability, occurrenceChance);
		configEntries.add("Radius: 3");
		configEntries.add("LargerRadius: true");
		triggers.add(Trigger.BLOCK_BROKEN);
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		BlockBreakEvent event = (BlockBreakEvent) e;
		Player player = event.getPlayer();
			if(player.isSneaking()) return;
			if(!isUsable(player.getItemInHand().getType().toString(), event.getBlock().getType().toString()))
				return;
				
			
			List<Location> locations = new ArrayList<Location>();
			
			int locRad = Radius;
			if(LargerRadius && Tools.random.nextInt(100) < level * 5)
				locRad += 2;
			int r = locRad-1;
			int start = r/2;
			
			Location   sL		= event.getBlock().getLocation();
			
			player.getWorld().createExplosion(sL, 0f); //Create a fake explosion
			
			sL.setX(sL.getX() - start);
			sL.setY(sL.getY() - start);
			sL.setZ(sL.getZ()-start);

			//for(int x = 0; x < locRad; x++)
					//for(int y = 0; y < locRad; y++)
					//for(int z = 0; z < locRad; z++)
					/*if ((!(x == 0 && y == 0 && z == 0)) &&
							(!(x == r && y == 0 && z == 0)) &&
							(!(x == 0 && y == r && z == 0)) &&
							(!(x == 0 && y == 0 && z == r)) &&
							(!(x == r && y == r && z == 0)) &&
							(!(x == 0 && y == r && z == r)) &&
							(!(x == r && y == 0 && z == r)) &&
							(!(x == r && y == r && z == r))) {*/
					//Location toBreak = new Location(sL.getWorld(), sL.getX() + x, sL.getY() + y, sL.getZ() + z);
		for(Block b : getSurroundingBlocks(CEListener.faces.get(player.getName()), ((BlockBreakEvent) e).getBlock())) {
			Location toBreak = b.getLocation();
			Faction territory = BoardColls.get().getFactionAt(PS.valueOf(toBreak));
			UPlayer up = UPlayer.get(player);
			if (territory.getName().equalsIgnoreCase(up.getFactionName()) || ChatColor.stripColor(territory.getName()).equalsIgnoreCase("Wilderness"))
				locations.add(toBreak);
		}


			
			for(Location loc : locations) {
				String iMat = item.getType().toString();
				Block b = loc.getBlock();
				String bMat = b.getType().toString();

				if(isUsable(iMat, bMat))
					if(!loc.getBlock().getDrops(item).isEmpty())
						if(Tools.checkWorldGuard(loc, player, "BUILD")) 
							loc.getBlock().breakNaturally(item);
			}
			
					
		
	}
	
	//Checks if the Material of the block (bMat) is intended to be mined by the item's Material (iMat)
	private boolean isUsable(String iMat, String bMat) {
		if(	(iMat.endsWith("PICKAXE") 				&& 
				(bMat.contains("ORE") 				||
				(!bMat.contains("STAIRS") 			&&
				bMat.contains("STONE"))				||
				bMat.equals("STAINED_CLAY")			||
				bMat.equals("NETHERRACK")))  		||
			(iMat.endsWith("SPADE") 				&& 
				(bMat.contains("SAND") 				||
				bMat.equals("DIRT") 				||
				bMat.equals("SNOW_BLOCK")			||
				bMat.equals("SNOW")					||
				bMat.equals("MYCEL")				||
				bMat.equals("CLAY")					||
				bMat.equals("GRAVEL")				||
				bMat.equals("GRASS")))       		||
			(iMat.endsWith("_AXE") 					&& 
				bMat.contains("LOG")        		||
				bMat.contains("PLANKS"))       		||
			(iMat.endsWith("HOE") 					&& 
				(bMat.equals("CROPS")				||
				bMat.equals("POTATO")				||
				bMat.equals("CARROT")))       
			)
			return true;
		return false;
	}
	
	@Override
	public void initConfigEntries() {
		Radius = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Radius"));
		if(Radius % 2 == 0)
			Radius += 1;
		LargerRadius = Boolean.parseBoolean(getConfig().getString("Enchantments." + getOriginalName() + ".LargerRadius"));
	}

	public static ArrayList<Block> getSurroundingBlocks(BlockFace blockFace, Block targetBlock) {
		ArrayList<Block> blocks = new ArrayList<Block>();
		World world = targetBlock.getWorld();

		int x, y, z;
		x = targetBlock.getX();
		y = targetBlock.getY();
		z = targetBlock.getZ();

		// Check the block face from which the block is being broken in order to get the correct surrounding blocks
		switch(blockFace) {
			case UP:
			case DOWN:
				blocks.add(world.getBlockAt(x+1, y, z));
				blocks.add(world.getBlockAt(x-1, y, z));
				blocks.add(world.getBlockAt(x, y, z+1));
				blocks.add(world.getBlockAt(x, y, z-1));
				blocks.add(world.getBlockAt(x+1, y, z+1));
				blocks.add(world.getBlockAt(x-1, y, z-1));
				blocks.add(world.getBlockAt(x+1, y, z-1));
				blocks.add(world.getBlockAt(x-1, y, z+1));
				break;
			case EAST:
			case WEST:
				blocks.add(world.getBlockAt(x, y, z+1));
				blocks.add(world.getBlockAt(x, y, z-1));
				blocks.add(world.getBlockAt(x, y+1, z));
				blocks.add(world.getBlockAt(x, y-1, z));
				blocks.add(world.getBlockAt(x, y+1, z+1));
				blocks.add(world.getBlockAt(x, y-1, z-1));
				blocks.add(world.getBlockAt(x, y-1, z+1));
				blocks.add(world.getBlockAt(x, y+1, z-1));
				break;
			case NORTH:
			case SOUTH:
				blocks.add(world.getBlockAt(x+1, y, z));
				blocks.add(world.getBlockAt(x-1, y, z));
				blocks.add(world.getBlockAt(x, y+1, z));
				blocks.add(world.getBlockAt(x, y-1, z));
				blocks.add(world.getBlockAt(x+1, y+1, z));
				blocks.add(world.getBlockAt(x-1, y-1, z));
				blocks.add(world.getBlockAt(x+1, y-1, z));
				blocks.add(world.getBlockAt(x-1, y+1, z));
				break;
			default:
				break;
		}

		// Trim the nulls from the list
		blocks.removeAll(Collections.singleton(null));
		return blocks;
	}
	
}
