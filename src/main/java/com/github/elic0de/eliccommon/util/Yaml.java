package com.github.elic0de.eliccommon.util;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.logging.Level;

public class Yaml extends YamlConfiguration {

    public final JavaPlugin plugin;
    public final File file;
    public final String name;
    public final String resourceFileName;

    public Yaml(JavaPlugin plugin, String fileName){
        this(plugin, new File(plugin.getDataFolder(), fileName));
    }

    public Yaml(JavaPlugin plugin, File file){
        this(plugin, file, file.getName());
    }

    public Yaml(JavaPlugin plugin, File file, String resourceFileName){
        this.plugin = plugin;
        this.file = file;
        this.resourceFileName = resourceFileName.replace('\\', '/');

        String fileName = file.getName();
        name = fileName.substring(0, fileName.length() - 4);

        saveResource(resourceFileName);

        reload();
    }

    public void save(){
        try {
            save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload(){
        super.map.clear();

        try {
            load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        InputStream input = plugin.getResource(resourceFileName);
        if(input != null)
            setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(input, StandardCharsets.UTF_8)));
    }

    public void update(){
        save();
        reload();
    }

    private void saveResource(String resourceFileName){
        if(file.exists()) return;

        InputStream in = plugin.getResource(resourceFileName);
        if(in == null)
            throw new IllegalArgumentException("The embedded resource '" + resourceFileName + "' cannot be found in " + file);

        String path = file.getPath();
        int lastIndex = path.lastIndexOf(47);
        File outDir = new File(file.getParent(), path.substring(0, Math.max(lastIndex, 0)));

        if (outDir.exists()) outDir.mkdirs();

        OutputStream output;

        try{
            output = Files.newOutputStream(file.toPath());
            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) output.write(buf, 0, length);
            output.close();
            in.close();
        }catch(IOException ex){
            plugin.getLogger().log(Level.SEVERE, "Cound not save " + file.getName() + " to " + file, ex);
        }
    }

}
