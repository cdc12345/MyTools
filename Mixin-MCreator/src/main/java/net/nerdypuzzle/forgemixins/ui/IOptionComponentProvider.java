package net.nerdypuzzle.forgemixins.ui;

import java.awt.*;
import java.util.HashMap;

public interface IOptionComponentProvider {
	Component getComponent(String exposedName, String description,HashMap<String,Object> properties);
}
