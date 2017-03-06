package ch.fhnw.cssr.domain;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "userRoles")
public class UserRole implements Serializable {

	private static final long serialVersionUID = 10023988L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "userRoleId")
	private long userRoleId;

	@Column(name = "userId")
	private long userId;

	@Column(name = "roleId")
	private int roleId;

	public UserRole() {

	}

	public UserRole(long userId, int roleId) {
		this.userId = userId;
		this.roleId = roleId;
	}

	public long getUserId() {
		return userId;
	}

	public int getRoleId() {
		return roleId;
	}
}