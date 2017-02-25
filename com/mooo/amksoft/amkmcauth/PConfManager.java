package com.mooo.amksoft.amkmcauth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.google.common.io.PatternFilenameFilter;

public class PConfManager extends YamlConfiguration {

    private static final Map<UUID, PConfManager> pcms = new HashMap<>();
    private final Object saveLock = new Object();
    private File pconfl = null;

    /**
     * Player configuration manager
     *
     * @param p Player to manage
     */
    PConfManager(OfflinePlayer p) {
        super();
        File dataFolder = AmkMcAuth.dataFolder;
        this.pconfl = new File(dataFolder + File.separator + "userdata" + File.separator + p.getUniqueId() + ".yml");
        try {
            load(this.pconfl);
        } catch (Exception ignored) {
        }
    }

    /**
     * Player configuration manager.
     *
     * @param u Player to manage
     */
    PConfManager(UUID u) {
        super();
        File dataFolder = AmkMcAuth.dataFolder;
        this.pconfl = new File(dataFolder + File.separator + "userdata" + File.separator + u + ".yml");
        try {
            load(this.pconfl);
        } catch (Exception ignored) {
        }
    }

    /**
     * No outside construction, please.
     */
    //@SuppressWarnings("unused")
    PConfManager() {
    }

    public static PConfManager getPConfManager(Player p) {
        return PConfManager.getPConfManager(p.getUniqueId());
    }

    public static PConfManager getPConfManager(UUID u) {
        synchronized (PConfManager.pcms) {
            if (PConfManager.pcms.containsKey(u)) return PConfManager.pcms.get(u);
            final PConfManager pcm = new PConfManager(u);
            PConfManager.pcms.put(u, pcm);
            return pcm;
        }
    }

    public static void saveAllManagers() {
        synchronized (PConfManager.pcms) {
        	// pcm only exists if player has joined.. 
            for (PConfManager pcm : PConfManager.pcms.values()) {
            	// Skip Save if "login.password" NOT set (=null/removed)
            	if(pcm.isSet("login.password")) {
            		pcm.forceSave();
            		// Bukkit.getLogger for Debugging 
            		// Bukkit.getLogger().log(Level.INFO, "Saving: " + pcm.getString("login.username") + ":"); // Debug
            	}
            }
        }
    }

    public void forceSave() {
        synchronized (this.saveLock) {
            try {
                save(this.pconfl);
            } catch (IOException ignored) {
            }
        }
    }

    public static int countPlayersFromIp(String IpAddress) {    	
    	int counted=0;
    	// First get Count from Current Players.
        //Bukkit.getLogger().log(Level.INFO, "[IpAddress] :" + IpAddress + ":"); // Debug
    	
    	String CurrentPlayers = " "; // spaces around playername
        synchronized (PConfManager.pcms) {
            for (PConfManager pcm : PConfManager.pcms.values()) {
            	// Count number Playernames logged in from same Ip-Adress
            	if(pcm.getString("login.ipaddress")!=null){
            		if(pcm.getString("login.ipaddress").equals(IpAddress)) counted++;
            	}
            	CurrentPlayers = CurrentPlayers + pcm.getString("login.username") + " ";
            }
        }

    	// First get Count from Inactive Players excluding Current Players.    	
		boolean Aanwezig=false;
		String InactivePlayer;
		String InactiveIpAddress;
        final File userdataFolder = new File(AmkMcAuth.dataFolder, "userdata");
        if (!userdataFolder.exists() || !userdataFolder.isDirectory()) return counted;
        for (String fileName : userdataFolder.list(new PatternFilenameFilter("(?i)^.+\\.yml$"))) {
        	Scanner in;
        	Aanwezig=false;
			InactivePlayer="";
			try {
				in = new Scanner(new File(userdataFolder + File.separator + fileName));
	        	//while (in.hasNextLine()) { // iterates each line in the file
		        while (in.hasNext()) { // 1 more character?: iterates each line in the file
	        	    String line = in.nextLine();
	        	    if(line.contains("username:")) {
	        	    	InactivePlayer = " " + line.substring(line.lastIndexOf(" ")+1) + " ";
	        	    	if(CurrentPlayers.contains(InactivePlayer)) Aanwezig=true;
	        	    }
	        	    if(!Aanwezig && line.contains("ipaddress:")){
	        	    	InactiveIpAddress = line.substring(line.lastIndexOf(" ")+1);
	        	    	if(InactiveIpAddress!=null){
	        	    		if(InactiveIpAddress.equals(IpAddress)) counted++;
	        	    	}
	        	    }
	        	}
	        	in.close(); // don't forget to close resource leaks
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        return counted;
    }

    public static String getVipPlayers() {    	
    	String VipPlayers = "";  // space separated
    	// First get Count from Current Players.
    	
    	String CurrentPlayers = " "; // spaces around playername
        synchronized (PConfManager.pcms) {
            for (PConfManager pcm : PConfManager.pcms.values()) {
            	// Count number Playernames logged in from same Ip-Adress
            	if(pcm.getString("login.vip")!=null){
            		if(pcm.getBoolean("login.vip")) VipPlayers = VipPlayers + pcm.getString("login.username") + " ";
            	}
            	CurrentPlayers = CurrentPlayers + pcm.getString("login.username") + " ";
            }
        }

    	// First get Count from Inactive Players excluding Current Players.    	
		boolean Aanwezig=false;
		String InactivePlayer;
		String InactiveIpAddress;
        final File userdataFolder = new File(AmkMcAuth.dataFolder, "userdata");
        if (!userdataFolder.exists() || !userdataFolder.isDirectory()) return VipPlayers;
        for (String fileName : userdataFolder.list(new PatternFilenameFilter("(?i)^.+\\.yml$"))) {
        	Scanner in;
        	Aanwezig=false;
			InactivePlayer="";
			try {
				in = new Scanner(new File(userdataFolder + File.separator + fileName));
	        	//while (in.hasNextLine()) { // iterates each line in the file
		        while (in.hasNext()) { // 1 more character?: iterates each line in the file
	        	    String line = in.nextLine();
	        	    if(line.contains("username:")) {
	        	    	InactivePlayer = " " + line.substring(line.lastIndexOf(" ")+1) + " ";
	        	    	if(CurrentPlayers.contains(InactivePlayer)) Aanwezig=true;
	        	    }
	        	    if(!Aanwezig && line.contains("vip:")){
	        	    	InactiveIpAddress = line.substring(line.lastIndexOf(" ")+1);
	        	    	if(InactiveIpAddress!=null){
	        	    		if(InactiveIpAddress.equals("true")) VipPlayers = VipPlayers + InactivePlayer+ " ";
	        	    	}
	        	    }
	        	}
	        	in.close(); // don't forget to close resource leaks
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return VipPlayers;
    }

    public static void purge() {
        synchronized (PConfManager.pcms) {
            PConfManager.pcms.clear();
        }
    }

    public boolean exists() {
        return this.pconfl.exists();
    }

    public boolean createFile() {
        try {
            return this.pconfl.createNewFile();
        } catch (IOException ignored) {
            return false;
        }
    }

    public static void removePlayer(UUID u) {
        synchronized (PConfManager.pcms) {
            if (PConfManager.pcms.containsKey(u)) {
            	PConfManager.pcms.clear();
            	File dataFolder = AmkMcAuth.dataFolder;
            	File rfile = new File(dataFolder + File.separator + "userdata" + File.separator + u + ".yml");
                if (rfile.exists()) rfile.delete();  // Als bestaat dan verwijderen..
            }            
        }
    }

    /**
     * Gets a Location from config
     * <p/>
     * This <strong>will</strong> throw an exception if the saved Location is invalid or has missing parts.
     *
     * @param path Path in the yml to fetch from
     * @return Location or null if path does not exist or if config doesn't exist
     */
    public Location getLocation(String path) {
        if (this.get(path) == null) return null;
        String world = this.getString(path + ".w");
        double x = this.getDouble(path + ".x");
        double y = this.getDouble(path + ".y");
        double z = this.getDouble(path + ".z");
        float pitch = this.getFloat(path + ".pitch");
        float yaw = this.getFloat(path + ".yaw");
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    /**
     * Sets a location in config
     *
     * @param value Location to set
     * @param path  Path in the yml to set
     */
    public void setLocation(String path, Location value) {
        this.set(path + ".w", value.getWorld().getName());
        this.set(path + ".x", value.getX());
        this.set(path + ".y", value.getY());
        this.set(path + ".z", value.getZ());
        this.set(path + ".pitch", value.getPitch());
        this.set(path + ".yaw", value.getYaw());
    }

    public float getFloat(String path) {
        return (float) this.getDouble(path);
    }
}
