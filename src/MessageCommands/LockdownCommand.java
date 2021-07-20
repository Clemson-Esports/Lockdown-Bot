package MessageCommands;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Bot.Driver;
import Bot.PermissionsSave;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class LockdownCommand implements Interfaces.Command{

	private GuildMessageReceivedEvent event;
	private String msg;
	private User user;
	private Guild guild;
	private JDA jda;
	private TextChannel channel;
	private Pattern p;
	private Matcher m;
	Message message;
	
	public LockdownCommand(GuildMessageReceivedEvent event) {
		msg = event.getMessage().getContentDisplay();
		this.event = event;
	}
	
	@Override
	public void run() {
		getInfo();
		//check if user is mentioned
		ArrayList<Role> lockdownRoles = new ArrayList<Role>(message.getMentionedRoles());
		if(lockdownRoles.size() <= 0) {
			channel.sendMessage("No roles to exclude mentioned in command").queue();
			return;
		}
		for(Role i : guild.getRoles()) {
			boolean skipRole = false;
			//check if this is a lockdown role
			for(Role j : lockdownRoles) {
				if(i.equals(j)) {
					skipRole = true;
				}
			}
			//if not a lockdown role
			if(!skipRole) {
				//get perms
				ArrayList<Permission> perms = new ArrayList<Permission>(i.getPermissions());
				//save permissions
				Driver.permSaves.add(new PermissionsSave(perms,i));
				savePerms();
				if(guild.getMember(jda.getSelfUser()).canInteract(i))
					i.getManager().revokePermissions(perms).queue();
			}
		}
		channel.sendMessage("Lockdown Has Been Implemented").queue();
	}

	@Override
	public boolean check() {
		p = Pattern.compile(Driver.prefix.toString() + "lockdown",Pattern.CASE_INSENSITIVE);
		m = p.matcher(msg);
		return m.find();
	}

	
	
	@Override
	public String getName() {
		return Driver.prefix.toString() + "lockdown [@role @role etc...]";
	}

	@Override
	public String getDescription() {
		return "removes all permissions from all roles except those mentioned (must mention at least one role)";
	}

	@Override
	public void getInfo() {
		user = event.getAuthor();
		guild = event.getGuild();
		jda = event.getJDA();
		jda.getGuildById(guild.getId()).retrieveMember(user).complete();
		channel = event.getChannel();
		message = event.getMessage();
	}
	
	private void savePerms() {
		try {
			FileWriter file = new FileWriter(Driver.location + "permsSave.txt",false);
			for(PermissionsSave i : Driver.permSaves) {
				file.write(i.role.getId() + "\r\n");
				for(Permission j : i.perms) {
					file.write(j.toString() + "\r\n");
				}
				file.write("------------------------\r\n");
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
