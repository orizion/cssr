package ch.fhnw.cssr.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
public class Role implements Serializable {

    private static final long serialVersionUID = 10123987L;

    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_SGL = 2;
    public static final int ROLE_COORD = 3;

    @Id
    @Column(name = "roleId")
    private Long roleId;

    @Column(name = "roleName")
    private String roleName;

    public Long getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Role() {

    }

    /**
     * This return the names for all roles that can be used by spring security.
     * 
     * @param id The id (1, 2 or 3)
     * @return The role name as string, or null
     */
    public static String getDefaultRoleName(int id) {
        if (id == ROLE_ADMIN) {
            return "ROLE_ADMIN";
        } else if (id == ROLE_SGL) {
            return "ROLE_SGL";
        } else if (id == ROLE_COORD) {
            return "ROLE_COORD";
        }
        return null;
    }

}
