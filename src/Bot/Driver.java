package Bot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

import EventHandlers.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


//https://discord.com/oauth2/authorize?client_id=860979122301698068&scope=bot&permissions=2148006976
public class Driver extends ListenerAdapter{
	
	public static String token;
	public static Pattern prefix;
	public static JdaMaker jdaMaker;
	public static String location = 
			System.getProperty("user.dir").equals("D:\\Eclipse Upgrade\\workspace\\Lockdown Bot")
			? "D:\\Eclipse Upgrade\\Lockdown Bot\\" : "";
	public static String version = "1.0.0";
	public static String guildID = "512859462307151872"; //clemson id: 215845807801237514
	public static ArrayList<PermissionsSave> permSaves = new ArrayList<PermissionsSave>();

	public static void main(String[] args) {

		//load startup data
		if(!getData()){
			System.out.println("Failed to load token, prefix, or website");
			return;
		}
		//bot maker class
		jdaMaker = new JdaMaker(token,prefix.toString() + "help");
		//create bot
		jdaMaker.constructJda();
		//load permSave data
		if(!getPermSave()){
			System.out.println("Failed to load perm saves");
			return;
		}
		
		System.out.println("Location var set to " + location);
		System.out.println("Current version:" + version);
		
	}
	
	//////////////////////// Events ///////////////////////////////////////////////////
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        new GuildMessageReceived(event);
    }//ends message received event
	
	//////////////////////// Methods //////////////////////////////////////////////////
	
	//loads startup data and returns true if was successful
	private static boolean getData() {
		File file = new File(location + "startup.txt");
		try {
			Scanner scnr = new Scanner(file);
			prefix = Pattern.compile(scnr.nextLine(),Pattern.CASE_INSENSITIVE);
			token = scnr.nextLine();
			scnr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private static boolean getPermSave() {
		File file = new File(location + "permsSave.txt");
		try {
			Scanner scnr = new Scanner(file);
			if(!scnr.hasNext()) {
				scnr.close();
				return true;
			}
			while(scnr.hasNext()) {
				String role = scnr.nextLine();
				ArrayList<Permission> tmpPerm = new ArrayList<Permission>();
				String tmp = scnr.nextLine();
				while(!tmp.startsWith("-")) {
					tmpPerm.add(Permission.valueOf(tmp));
					tmp = scnr.nextLine();
				}
				permSaves.add(new PermissionsSave(tmpPerm,jdaMaker.getJda().getGuildById(guildID).getRoleById(role)));
			}
			scnr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}