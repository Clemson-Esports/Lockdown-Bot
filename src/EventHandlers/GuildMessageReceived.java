package EventHandlers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import Bot.*;
import MessageCommands.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import Interfaces.Command;

public class GuildMessageReceived {

	private ArrayList<Command> commands = new ArrayList<Command>();
	
	private GuildMessageReceivedEvent event;

	public GuildMessageReceived(GuildMessageReceivedEvent event) {
		this.event = event;
		logMessage();
		//Requirements: 
		//MANAGE_ROLES permission
		
		//string version of message
		String msg = event.getMessage().getContentDisplay();
		
		//checks if this is an attepted command
		if(!Driver.prefix.matcher(msg).find() && event.getMessage().getAttachments().size() == 0) {
			return;
		}
		//has perms
		if(!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
			return;
		}
		
		//add all bot commands to commands list
		//lockdown command
		LockdownCommand lockdownCommand = new LockdownCommand(event);
		//no new perms
		commands.add(lockdownCommand);
		EndLockdownCommand endLockdownCommand = new EndLockdownCommand(event);
		//no new perms
		commands.add(endLockdownCommand);
		UpdatePermsCommand updatePermsCommand = new UpdatePermsCommand(event);
		//no new perms
		commands.add(updatePermsCommand);
		
		//check message
		if(lockdownCommand.check())
			lockdownCommand.run();
		else if(endLockdownCommand.check())
			endLockdownCommand.run();
		else if(updatePermsCommand.check())
			updatePermsCommand.run();
		
		//help command
		if(event.getMessage().getContentDisplay().equalsIgnoreCase(Driver.prefix.toString() + "help"))
			new HelpCommand(commands,event);
	}
	
	private void logMessage() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		System.out.printf("[%s](%s,%s)%s: %s\n", dtf.format(LocalDateTime.now()),
				event.getGuild().getName(),event.getChannel().getName(),
				event.getMember().getEffectiveName(),event.getMessage().getContentDisplay());
	}
	
}