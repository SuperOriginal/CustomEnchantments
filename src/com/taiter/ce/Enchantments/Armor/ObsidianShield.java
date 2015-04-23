package com.taiter.ce.Enchantments.Armor;

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



import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.taiter.ce.Main;
import com.taiter.ce.Tools;
import com.taiter.ce.Enchantments.CEnchantment;



public class ObsidianShield extends CEnchantment {


	public ObsidianShield(String originalName, Application app, int enchantProbability, int occurrenceChance) {
		super(originalName,  app, enchantProbability, occurrenceChance);
		triggers.add(Trigger.MOVE);
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		PlayerMoveEvent event = (PlayerMoveEvent) e;
		if(Main.repeatPotionEffects)
			Tools.repeatPotionEffect(item, event.getPlayer(), PotionEffectType.FIRE_RESISTANCE, 10, true, this);
		else {
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 600, 10), true);
			generateCooldown(event.getPlayer(), 400l);	
		}
	}

	@Override
	public void initConfigEntries() {
	}
}
