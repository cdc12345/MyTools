package org.cdc.temp;

import net.mcreator.java.JavaConventions;
import net.mcreator.plugin.JavaPlugin;
import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.events.workspace.MCreatorLoadedEvent;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.StringUtils;
import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.builder.DataListBuilder;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class TempPluginMain extends JavaPlugin {
    public TempPluginMain(Plugin plugin) {
        super(plugin);

        String uuid = String.valueOf(System.currentTimeMillis());
        File pluginFolder = new File(System.getProperty("user.dir"), "plugins/tempplugin"+uuid);
        MCreatorPluginFactory mcr = new MCreatorPluginFactory(pluginFolder);

        this.addListener(MCreatorLoadedEvent.class, a -> {
            var mcreator = a.getMCreator();
            var mainMenuBar = mcreator.getMainMenuBar();

            JMenu temp = new JMenu("TempPlugin");
            mainMenuBar.add(temp);

            JMenuItem addTempItem = new JMenuItem(L10N.t("menubar.item.addtempitem"));
            final DataListBuilder[] dataListBuilder = {null};
            addTempItem.addActionListener(b -> {
                String readableName = JOptionPane.showInputDialog("input your item readable name");
                String type = JOptionPane.showInputDialog("input your item type (block or item)");
                String registryName = JOptionPane.showInputDialog("input your item registerId");

                if (dataListBuilder[0] == null){
                    dataListBuilder[0] = mcr.createDataList().setName("blocksitems");
                }
                dataListBuilder[0].appendElement(String.format("""
                        %s: 
                          readable_name: "%s"
                          type: %s""", StringUtils.uppercaseFirstLetter(type) + "s" + "." + JavaConventions.convertToValidClassName(readableName), readableName, type), String.format("""
                          
                          - Null
                          - "%s"
                        """, registryName)).initGenerator().buildAndOutput();


            });
            temp.add(addTempItem);

            JMenuItem export = new JMenuItem(L10N.t("menubar.item.build"));
            export.addActionListener(b->{
                mcr.initGenerator(mcreator.getGenerator().getGeneratorName(),true);
                pluginFolder.mkdirs();
                try {
                    String str = new String(Objects.requireNonNull(this.getClass().getResourceAsStream("/subplugin.json")).readAllBytes());
                    str = String.format(str, uuid);
                    Files.copy(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)), pluginFolder.toPath().resolve("plugin.json"), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                JOptionPane.showMessageDialog(mcreator,"build successful");
            });
            temp.add(export);

        });
    }
}
