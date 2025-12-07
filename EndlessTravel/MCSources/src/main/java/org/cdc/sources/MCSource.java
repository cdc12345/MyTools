package org.cdc.sources;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.cdc.sources.annotation.EnumLabel;
import org.cdc.sources.annotation.Field;
import org.cdc.sources.annotation.Input;
import org.cdc.sources.annotation.ItemStackCount;

public class MCSource {
	private void creativetab_insertafter(Event event,@Input @ItemStackCount ItemStack after,@Input @ItemStackCount ItemStack item,
			@Field @EnumLabel CreativeModeTab.TabVisibility tabvisible) {
		if (event instanceof BuildCreativeModeTabContentsEvent _event) {
			_event.insertAfter(after, item, tabvisible);
		}
	}
}
