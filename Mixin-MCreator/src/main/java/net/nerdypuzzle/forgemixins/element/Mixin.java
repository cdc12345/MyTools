package net.nerdypuzzle.forgemixins.element;

import net.mcreator.element.GeneratableElement;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.elements.ModElement;

public class Mixin extends GeneratableElement {

    public String mixins = getModElement().getName() + "Mixin";
    public boolean isClient = false;
    public String mixinClass = getModElement().getName();

	public String mixinBody = "";

    public Mixin(ModElement element) {
        super(element);
    }
    
    public String t(String key){
        return L10N.t(key);
    }
}