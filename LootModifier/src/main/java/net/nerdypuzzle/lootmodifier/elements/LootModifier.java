package net.nerdypuzzle.lootmodifier.elements;

import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.elements.ModElement;

public class LootModifier extends GeneratableElement {
    public String modifiedTable;
    public String modifierTable;
    public String modifierMethod;
    public LootModifier(ModElement element) {
        super(element);
    }

}
