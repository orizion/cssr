package ch.fhnw.cssr.domain;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "users")
public class User implements Serializable {

	private static final long serialVersionUID = 10023987L;

	public static final String StudentsEmailPostfix = "@students.fhnw.ch";
	public static final String AdmEmailPostfix = "@fhnw.ch";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "userId")
	private Long userId;

	@Column(name = "displayName")
	private String displayName;

	@Column(name = "passwordEnc")
	private String passwordEnc;

	@Column(name = "email")
	private String email;

	@Column(name = "tempToken")
	private String tempToken;

	@Column(name = "tempTokenExpiresAt")
	private java.sql.Date tempTokenExpiresAt;

	public Long getUserId() {
		return userId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getPasswordEnc() {
		return passwordEnc;
	}

	public void setPasswordEnc(String passwordEnc) {
		this.passwordEnc = passwordEnc;
	}

	public String getEmail() {
		return email;
	}

	public String getTempToken() {
		return tempToken;
	}

	public void setTempToken(String tempToken, java.sql.Date expiresAt) {
		this.tempToken = tempToken;
		this.tempTokenExpiresAt = expiresAt;
	}

	public java.sql.Date getTempTokenExpiresAt() {
		return tempTokenExpiresAt;
	}

	public User() {

	}

	/**
	 * This is a constructor for students or other AD Users only
	 */
	protected User(long userId, String email, String userName) {
		this.email = email;
		if (email.endsWith(StudentsEmailPostfix)) {
			this.passwordEnc = PasswordHandler.STUDENT_PLACEHOLDER_PREFIX;
		}
		else if (email.endsWith(AdmEmailPostfix))  {
			this.passwordEnc = PasswordHandler.ADM_PLACEHOLDER_PREFIX;
		}
		else {
			throw new IllegalArgumentException("email");	
		}
	}

	public User(String email, String displayName, String passwordEnc, String tempToken,
			java.sql.Date tempTokenExpiresAt) {
		this.displayName = displayName;
		this.passwordEnc = passwordEnc;
		this.tempToken = tempToken;
		this.tempTokenExpiresAt = tempTokenExpiresAt;
	}

	protected User(User copyUser) {
		this.userId = copyUser.userId;
		this.email = copyUser.email;
		this.passwordEnc = copyUser.passwordEnc;
		this.tempToken = copyUser.tempToken;
		this.tempTokenExpiresAt = copyUser.tempTokenExpiresAt;
		this.displayName = copyUser.displayName;
	}

}