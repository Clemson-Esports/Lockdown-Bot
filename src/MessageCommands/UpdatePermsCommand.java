package MessageCommands;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import Bot.Driver;
import Bot.PermissionsSave;
import Interfaces.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class UpdatePermsCommand implements Command{

	private GuildMessageReceivedEvent event;
	@SuppressWarnings("unused")
	private String msg;
	private User user;
	private Guild guild;
	private JDA jda;
	private TextChannel channel;
	Message message;
	
	public UpdatePermsCommand(GuildMessageReceivedEvent event) {
		msg = event.getMessage().getContentDisplay();
		this.event = event;
	}
	
	@Override
	public void run() {
		if(Driver.permSaves.size() > 0) {
			channel.sendMessage("A lockdown is currently in process. End lockdown to update perms").queue();
			return;
		}
		File file = null;
		try {
			file = message.getAttachments().get(0).downloadToFile(new File(Driver.location + "newPerms.txt")).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		try {
			Scanner scnr = new Scanner(file);
			if(!scnr.hasNext()) {
				scnr.close();
			}
			boolean notDone = true;
			while(scnr.hasNextLine()) {
				String role = scnr.nextLine();
				ArrayList<Permission> tmpPerm = new ArrayList<Permission>();
				String tmp = scnr.nextLine();
				while(!tmp.startsWith("-") && scnr.hasNextLine()) {
					try {
						tmpPerm.add(Permission.valueOf(tmp));
						tmp = scnr.nextLine();
					}catch(IllegalArgumentException e) {
						notDone = false;
					}
				}
				if(notDone) {
					if(!role.equals(""))
						Driver.permSaves.add(new PermissionsSave(tmpPerm,Driver.jdaMaker.getJda().getRoleById(role)));
				}else{
					scnr.close();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for(PermissionsSave i : Driver.permSaves) {
			try {
				if(guild.getMember(jda.getSelfUser()).canInteract(guild.getRoleById(i.role.getId())))
					i.role.getManager().givePermissions(i.perms).queue();
			}catch(IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		Driver.permSaves = new ArrayList<PermissionsSave>();
		channel.sendMessage("Permissions have been updated").queue();
		file.delete();
	}

	@Override
	public boolean check() {
		getInfo();
		if(message.getAttachments().size() > 0 && !user.isBot()) {
			if(message.getAttachments().get(0).getFileName().equals("permsSave.txt")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getName() {
		return Driver.prefix + "send perms file save with name permsSave";
	}

	@Override
	public String getDescription() {
		return "updates a servers perms from a lockdown file";
	}

	@Override
	public void getInfo() {
		msg = event.getMessage().getContentDisplay();
		user = event.getAuthor();
		guild = event.getGuild();
		jda = event.getJDA();
		jda.getGuildById(guild.getId()).retrieveMember(user).complete();
		channel = event.getChannel();
		message = event.getMessage();
	}

}
