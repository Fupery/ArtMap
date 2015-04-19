package me.Fupery.Artiste;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import me.Fupery.Artiste.IO.Artist;
import me.Fupery.Artiste.IO.CanvasLocation;
import me.Fupery.Artiste.MapArt.Buffer;
import me.Fupery.Artiste.Tasks.ClaimTimer;
import me.Fupery.Artiste.Tasks.TimeRemaining;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** Represents 3d position of the ArtMap canvas, contains
 * static methods to interact with the canvas in-game. */
public class Canvas implements Serializable{
//TODO - Fix buffer system, clean this class up, Replace Canvas list with static reference
	private static final long serialVersionUID = -1867329677480940178L;
	
	public static Canvas canvas;
	
	private UUID owner;
	private ArrayList<UUID> member;
	
	private int size;
	private boolean canClaim;
	
	private CanvasLocation pos1;
	private CanvasLocation pos2;
	public String worldname;
	
	public Canvas(Location position1, Location position2, int size) {
		
		setOwner(null);
		member = new ArrayList<UUID>();
		this.size = size;
		setCanClaim(false);
		setPos1(position1);
		setPos2(position2);
		
		worldname = position1.getWorld().getName(); // much hacks wow
	}
	
	
	//Admin Commands
	public static boolean defineCanvas(CommandSender sender, 
	double xpos, double ypos, double zpos) {
		
			if(StartClass.canvas.size() == 0){
				
				int size = StartClass.config.getInt("canvasSize");
				if(size == 0) size = 64;
				
				if(!evalSize(size)){
					
					sender.sendMessage(ChatColor.DARK_RED + "Invalid map size set in config!");
					return false;
				}
				
				int mapSize = (size-1);
				Player player = (Player) sender;
				World w = player.getWorld();
				WorldBorder wb = w.getWorldBorder();
				
				Location canvasPos = new Location(w, xpos, ypos, zpos);
				
				if(wb == null){
					sender.sendMessage(ChatColor.DARK_RED + "No world border found!");
					return false;
				}
				
				// Check if world border interferes with canvas
				if ((wb.getSize() - (canvasPos.getBlockX() + size)) <= 0
				|| (wb.getSize() - (canvasPos.getBlockZ() + size) <= 0)) {
					sender.sendMessage(ChatColor.DARK_RED + "Canvas obstructed by World Border"); 
					return false; 
				}	
					// For loops check if area is obstructed
					Location endPos = new Location(w, xpos + mapSize, ypos, zpos + mapSize);
					Location l = canvasPos.clone();
					
					for (; l.getBlockX() <= (endPos.getBlockX()); l.add(1, 0, 0)) {
						
						for (; l.getBlockZ() <= endPos.getBlockZ(); l.add(0, 0, 1)) {
							
							if (w.getHighestBlockYAt(l) > canvasPos.getBlockY()
								&& w.getHighestBlockYAt(l) < (canvasPos.getBlockY() + 2) ){
								
								Material m = w.getHighestBlockAt(l).getType();
								
								if(m != Material.WOOL && m != Material.WATER){
					
									Integer x = l.getBlockX(), y = l.getBlockY(), z = l.getBlockZ();
									
									sender.sendMessage(ChatColor.DARK_RED +"Canvas obstructed at "+ 
									ChatColor.RED + x+", "+y+", "+z+".");

									return false; 
								}
							}
						}
					}//end for loops
					sender.sendMessage(ChatColor.GOLD + "No Obstruction found");
						StartClass.canvas.add(new Canvas(canvasPos, endPos, size) );
						printPos(sender, canvasPos, endPos);
						return true;
			}else {
			sender.sendMessage(ChatColor.DARK_RED + "Error : Multiple canvases detected"); 
			}
			return false;
	}
	
	
	public void getCanvasData(CommandSender sender){
		
		if(StartClass.canvas.size() > 0) {

			Canvas c = StartClass.canvas.get(0);
			
			sender.sendMessage( new String[]{
			ChatColor.GOLD + "Canvas Pos1 - " + ChatColor.DARK_AQUA + c.getPos1().toString(),
			ChatColor.GOLD + "Canvas Pos2 - " + ChatColor.DARK_AQUA + c.getPos2().toString(),
			});
			
			if(this.owner != null) sender.sendMessage(
			ChatColor.GOLD + "Owner - " + ChatColor.DARK_AQUA + c.getOwner().getName() );
		}
	}
	
	
	public void removeCanvas(CommandSender sender) {
		
			setOwner(null);
			member.clear();
			setPos1(null);
			setPos2(null);
			StartClass.canvas.clear();
			
			sender.sendMessage(ChatColor.DARK_AQUA + "Canvas removed successfully!");
	}
	
	
	public void clear(CommandSender sender) {
		
		this.setOwner(null);
		this.member.clear();
		this.reset(sender, DyeColor.WHITE);
	}
	
	
	//Player Commands
	public void claim(CommandSender sender, StartClass plugin) {
		
		if (getOwner() == (Player) sender) {
			sender.sendMessage(ChatColor.DARK_RED + "You have already claimed the canvas!");
			
		} else if (this.owner == null) {
			
			int claimTime = StartClass.config.getInt("claimTime");
			
			this.setOwner( (Player) sender );
			Player player = (Player) sender;
			UUID uuid = player.getUniqueId();
			Artist a = StartClass.artistList.get(uuid);
			
			Buffer m = a.getBuffer();
			
			// if sender has a map in their buffer, loads map to the canvas,
			// then clears the buffer
			
			if (m != null) {
				
				m.edit();
				a.clearBuffer();
			}
			if(claimTime > 0){
			
			sender.sendMessage(ChatColor.GOLD + "Canvas claimed for " + 
			ChatColor.YELLOW + claimTime + ChatColor.GOLD + " minutes!");
			
			int ticks = claimTime * 60 * 20;
	
			if(claimTime > 5){
				int warning = ticks - (5 * 60 * 20);
				new TimeRemaining(sender).runTaskLater(plugin, warning);
				
			}
			
			new ClaimTimer(sender, plugin).runTaskLater(plugin, ticks);
			
			}else sender.sendMessage(ChatColor.GOLD + "Canvas claimed!");
			
		} else {
			sender.sendMessage(ChatColor.DARK_RED + "Someone else is using the canvas!");
		}
	}
	
	
	@SuppressWarnings("deprecation")
	public void addMember(CommandSender sender, String mem) {
		
		Server s = sender.getServer();
		
		if (s.getOnlinePlayers().contains(mem)) {
			
			this.member.add( (s.getPlayer(mem)).getUniqueId() );
			
			sender.sendMessage(ChatColor.GOLD + mem + "has been added as an artist!");
			
		} else {
			sender.sendMessage(ChatColor.DARK_RED + "That player isn't online!");
		}

	}
	
	
	public void unclaim(CommandSender sender, StartClass plugin) {
		if(getOwner() == null) return;
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		Artist a = StartClass.artistList.get(uuid);
			if(a.getBuffer() != null){
				
				a.clearBuffer();
			}
			a.setBuffer(new Buffer(sender));
			this.clear(sender);
			Bukkit.getServer().getScheduler().cancelTasks(plugin);
			
			sender.sendMessage(ChatColor.GOLD + "You have unclaimed the canvas, "
					+ "your work will be saved for later!");

	}
	
	
	//TODO - implement task scheduling
	
	@SuppressWarnings("deprecation")
	public void reset(CommandSender sender, DyeColor colour) {
		
		sender.sendMessage(ChatColor.GOLD + "Resetting Canvas to " + 
		ChatColor.AQUA + colour.toString() + ChatColor.GOLD + " wool");
		
		Location f = this.getPos2().clone(); //end position
		Location l = this.getPos1().clone();
		
		for (int x = this.getPos1().getBlockX(); x <= (f.getBlockX()); x++ ) {
			
			for (int z = this.getPos1().getBlockZ(); z <= (f.getBlockZ()); z++ ) {
				
				l.setX(x); l.setZ(z);
				Block b = l.getBlock();
				
				if (b.getType() != Material.WOOL ) b.setType(Material.WOOL);
				
				if (b.getData() != colour.getData())
					b.setData(colour.getData());
				};
			//Scheduler s = new Scheduler();
			//s.initializeLoop(sender,this, x, l, f);
			//s.setProcess("reset");
			//s.runTaskLater(plugin, 50);
		}
	}
	
	
	public static void printPos(CommandSender sender, Location pos1, Location pos2){
		
		Integer x1 = pos1.getBlockX(), y1 = pos1.getBlockY(), z1 = pos1.getBlockZ();
		Integer x2 = pos2.getBlockX(), y2 = pos2.getBlockY(), z2 = pos2.getBlockZ();
		
		sender.sendMessage(ChatColor.GOLD + "Canvas defined between "+
		ChatColor.AQUA +x1.toString() + ", "+y1.toString() + ", "+z1.toString() + ChatColor.GOLD + ", and "+
		ChatColor.AQUA +x2.toString() + ", "+y2.toString() + ", "+z2.toString());
	}
	
	
	//Utility methods
	public static Canvas findCanvas(){
		if(StartClass.canvas != null){
			if(StartClass.canvas.size() == 0) {
				return null;
			}else return (StartClass.canvas.get(0));
		}else return null;
	}
	
	
	public static boolean evalSize(int mapSize){
		switch(mapSize){
			case 16 : return true;
			case 32 : return true;
			case 64 : return true;
			case 128 : return true;
			default : return false;
		}
	}

	public Location getPos1() {
		Location l = this.pos1.getLocation();
		return l;
	}

	public void setPos1(Location pos1) {
		this.pos1 = (pos1 == null) ?
		null:
		new CanvasLocation(pos1);
	}

	public Location getPos2() {
		Location l = this.pos2.getLocation();
		return l;
	}
	public void setPos2(Location pos2) {
		this.pos2 = (pos2 == null) ?
		null:
		new CanvasLocation(pos2);
	}

	public Player getOwner() {
		Player p = Bukkit.getPlayer(this.owner);
		return p;
	}

	public void setOwner(Player owner) {
		UUID id;
		id = (owner == null) ?
			null : owner.getUniqueId();
		this.owner = id;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public ArrayList<UUID> getMembers(){
		return this.member;
	}


	public boolean canClaim() {
		return canClaim;
	}


	public void setCanClaim(boolean canClaim) {
		this.canClaim = canClaim;
	}
}
