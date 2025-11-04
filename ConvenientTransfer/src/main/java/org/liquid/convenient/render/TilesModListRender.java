/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.liquid.convenient.render;

import net.mcreator.ui.MCreator;
import net.mcreator.workspace.elements.IElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.liquid.convenient.TransferMain;

import javax.swing.*;
import java.awt.*;

public class TilesModListRender extends JPanel implements ListCellRenderer<IElement> {

	private static final Logger LOG = LogManager.getLogger(TilesModListRender.class);

	public static void updateRenderer(MCreator mcreator, TransferMain transferMain) {
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends IElement> list, IElement value, int index,
			boolean isSelected, boolean cellHasFocus) {
		return null;
	}
}
