package MessageCommands;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class EndLockdownCommand implements Command{

	private GuildMessageReceivedEvent event;
	private String msg;
	private User user;
	private Guild guild;
	private JDA jda;
	private TextChannel channel;
	private Pattern p;
	private Matcher m;
	Message message;
	
	public EndLockdownCommand(GuildMessageReceivedEvent event) {
		msg = event.getMessage().getContentDisplay();
		this.event = event;
	}
	
	@Override
	public void run() {
		getInfo();
		for(PermissionsSave i : Driver.permSaves) {
			try {
				if(guild.getMember(jda.getSelfUser()).canInteract(guild.getRoleById(i.role.getId())))
					i.role.getManager().givePermissions(i.perms).queue();
			}catch(IllegalArgumentException e) {
				channel.sendMessage("The lockdown was not started in this guild").queue();
				e.printStackTrace();
			}
		}
		Driver.permSaves = new ArrayList<PermissionsSave>();
		savePerms();
		channel.sendMessage("Lockdown is over and all permissions have been reset").queue();
	}

	@Override
	public boolean check() {
		p = Pattern.compile(Driver.prefix.toString() + "endlockdown",Pattern.CASE_INSENSITIVE);
		m = p.matcher(msg);
		return m.find();
	}

	@Override
	public String getName() {
		return Driver.prefix.toString() + "endLockdown";
	}

	@Override
	public String getDescription() {
		return "ends lockdown and resets all permissions";
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