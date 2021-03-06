package fr.black_eyes.lootchest;

import java.sql.Timestamp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

import lombok.Getter;
import lombok.Setter;
public class Lootchest extends Utils {
	@Getter @Setter String name;
	@Setter Location globalLoc;
	@Setter Location randomLoc;
	Inventory inv;
	@Getter @Setter Boolean fall;
	Integer[] chances;
	@Setter String direction;
	@Getter @Setter String holo;
	@Getter @Setter int time;
	@Getter @Setter long lastreset;
	@Getter @Setter String particle;
	@Getter @Setter Boolean respawn_cmd;
	@Getter @Setter Boolean respawn_natural;
	@Getter @Setter Boolean take_msg;
	@Getter @Setter int radius;
	@Getter @Setter String world;
	
	
	/*Function used in Main / reload for chest loading */
	public Lootchest(String naming) {
		name = naming;
		Integer[] chancesInit = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		chances = chancesInit;
		chancesInit = null;
		inv = Bukkit.createInventory(null, 27);
		globalLoc = getPosition(naming);
		if(config.getData().isSet("chests."+naming+".randomradius")) {
			radius = config.getData().getInt("chests."+naming+".randomradius");
			randomLoc = getRandomPosition(naming);
		}
		else {
			radius = 0;
			randomLoc = null;
		}
		holo = config.getData().getString("chests." + naming + ".holo");
		particle = config.getData().getString("chests." + naming + ".particle");
		time = config.getData().getInt("chests." + naming + ".time");
		fall =  config.getData().getBoolean("chests." + naming + ".fall");
		for(String keys : config.getData().getConfigurationSection("chests." + naming + ".inventory").getKeys(false)) {
			inv.setItem(Integer.parseInt(keys), config.getData().getItemStack("chests." + naming + ".inventory." + keys));
			chances[Integer.parseInt(keys)] = config.getData().getInt("chests." + naming + ".chance." + keys);
		}
		respawn_cmd =  config.getData().getBoolean("chests." + naming + ".respawn_cmd");
		respawn_natural =  config.getData().getBoolean("chests." + naming + ".respawn_natural");
		take_msg =  config.getData().getBoolean("chests." + naming + ".take_message");
		world = config.getData().getString("chests." + naming + ".position.world");
		direction = config.getData().getString("chests." + naming + ".direction");
		lastreset = config.getData().getLong("chests." + name + ".lastreset");
	}
	
	
	/*Function used for /lc create */
	public Lootchest(Block chest, String naming){

		name = naming;
		inv = Bukkit.createInventory(null, 27);
		Integer[] chancesInit = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		chances = chancesInit;
		Inventory inve = ((Chest) chest.getState()).getInventory();
		for(int i = 0 ; i < inve.getSize() ; i++) {
			if(inve.getItem(i) != null) {
				inv.setItem( i, inve.getItem(i));
				chances[i] =  config.getConfig().getInt("default_item_chance");
			}
		}
		fall =  config.getConfig().getBoolean("Fall_Effect.Enabled");
		respawn_cmd =  config.getConfig().getBoolean("respawn_notify.respawn_with_command.enabled");
		respawn_natural =  config.getConfig().getBoolean("respawn_notify.natural_respawn.enabled");
		take_msg =  config.getConfig().getBoolean("respawn_notify.message_on_chest_take");
		direction = getDirection(chest);
		holo = name;
		time =  config.getConfig().getInt("default_reset_time");
		globalLoc =  chest.getLocation();
		lastreset =  new Timestamp(System.currentTimeMillis()).getTime();
	   	particle =  config.getConfig().getString("Particles.default_particle");
	   	radius = 0;
	   	world = chest.getWorld().getName();
		((Chest) chest.getLocation().getBlock().getState()).getInventory().clear();
		chest.getLocation().getBlock().setType(Material.AIR);
		
	}
	
	/*Function used at defined time in config and at plugin stop for saving chests */
	void saveInConfig(){

			config.getData().set("chests." + name + ".inventory", null);
			for(int i = 0 ; i < inv.getSize() ; i++) {
				if(inv.getItem(i) != null && inv.getItem(i).getType() != Material.AIR) {
					config.getData().set("chests." + name + ".inventory." + i, inv.getItem(i));
					config.getData().set("chests." + name + ".chance." + i, chances[i]);
				}
			}
			config.getData().set("chests." + name + ".fall", fall);
			config.getData().set("chests." + name + ".respawn_cmd", respawn_cmd);
			config.getData().set("chests." + name + ".respawn_natural", respawn_natural);
			config.getData().set("chests." + name + ".take_message", take_msg);
			
			config.getData().set("chests." + name + ".direction", direction);
			config.getData().set("chests." + name + ".holo", holo);
			config.getData().set("chests." + name + ".time", time);
			setPosition(name, globalLoc);
			config.getData().set("chests." + name + ".lastreset", lastreset);
		   	config.getData().set("chests." +name+ ".particle", particle);
		   	config.getData().set("chests."+name+".randomradius", radius);
		   	if(randomLoc != null) {
		   		setRandomPosition(name, randomLoc);
		   	}

	}
	

	public Location getPosition() {			return globalLoc.clone();	}
	public Location getRandomPosition() {	return (randomLoc!=null)?randomLoc.clone():null;	}


	public Location getActualLocation() {
		return (radius!=0)?randomLoc.clone():globalLoc.clone();
	}
	
	public void setChance(int c, int v) {			chances[c] = v;		}


	public void setInventory(Inventory inve) {
		for(int i = 0 ; i < inve.getSize() ; i++) {
			if(inve.getItem(i) != null) {
				inv.setItem( i, inve.getItem(i));
				if(chances[i] ==0) {
					chances[i] =  config.getConfig().getInt("default_item_chance");
				}
			}
		}
	}



	
	
}