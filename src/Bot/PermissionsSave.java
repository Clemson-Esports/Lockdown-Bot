package Bot;

import java.util.List;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;

public class PermissionsSave {

	public List<Permission> perms;
	public Role role;
	
	private PermissionsSave() {}
	
	public PermissionsSave(List<Permission> perms, Role role) {
		this.perms = perms;
		this.role = role;
	}
	
}