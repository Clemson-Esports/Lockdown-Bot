package EventHandlers;

import java.util.ArrayList;

import Bot.*;
import MessageCommands.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import Interfaces.Command;

public class GuildMessageReceived {

	private static ArrayList<Command> commands = new ArrayList<Command>();

	public GuildMessageReceived(GuildMessageReceivedEvent event) {
		//Requirements: 
		//MANAGE_ROLES permission
		
		//string version of message
		String msg = event.getMessage().getContentDisplay();
		
		//checks if this is an attepted command
		if(!Driver.prefix.matcher(msg).find()) {
			return;
		}
		//has perms
		if(!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
			return;
		}
		
		//add all bot commands to commands list
		LockdownCommand lockdownCommand = new LockdownCommand(event);
		//no knew perms
		commands.add(lockdownCommand);
		EndLockdownCommand endLockdownCommand = new EndLockdownCommand(event);
		//no new perms
		commands.add(endLockdownCommand);
		
		//check message
		if(lockdownCommand.check())
			lockdownCommand.run();
		else if(endLockdownCommand.check())
			endLockdownCommand.run();
		
		//help command
		if(event.getMessage().getContentDisplay().equalsIgnoreCase(Driver.prefix.toString() + "help"))
			new HelpCommand(commands,event);
	}
	
}