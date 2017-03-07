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

	@Column(name = "userName")
	private String userName;

	@Column(name = "passwordEnc")
	private String passwordEnc;

	@Column(name = "salt")
	private String salt;

	@Column(name = "email")
	private String email;

	@Column(name = "tempToken")
	private String tempToken;

	@Column(name = "tempTokenExpiresAt")
	private java.sql.Date tempTokenExpiresAt;

	public Long getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPasswordEnc() {
		return passwordEnc;
	}

	public void setPasswordEnc(String passwordEnc) {
		this.passwordEnc = passwordEnc;
	}

	public String getSalt() {
		return salt;
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
		if (!email.endsWith(StudentsEmailPostfix) && !email.endsWith(AdmEmailPostfix)) {
			throw new IllegalArgumentException("email");
		}
		if (email.endsWith(StudentsEmailPostfix)) {
			this.passwordEnc = PasswordHandler.STUDENT_PLACEHOLDER_PREFIX;
		}
		else if (email.endsWith(AdmEmailPostfix))  {
			this.passwordEnc = PasswordHandler.ADM_PLACEHOLDER_PREFIX;
		}
	}

	public User(String email, String userName, String passwordEnc, String salt, String tempToken,
			java.sql.Date tempTokenExpiresAt) {
		this.userName = userName;
		this.passwordEnc = passwordEnc;
		this.salt = salt;
		this.tempToken = tempToken;
		this.tempTokenExpiresAt = tempTokenExpiresAt;
	}

	protected User(User copyUser) {
		this.userId = copyUser.userId;
		this.email = copyUser.email;
		this.passwordEnc = copyUser.passwordEnc;
		this.salt = copyUser.salt;
		this.tempToken = copyUser.tempToken;
		this.tempTokenExpiresAt = copyUser.tempTokenExpiresAt;
		this.userName = copyUser.userName;
	}

}