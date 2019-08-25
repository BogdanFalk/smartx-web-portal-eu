package smartx.data.user;

import javax.persistence.*;

import java.util.Objects;

@Entity
public class User {

    @Id
    @GeneratedValue
    @Column(unique = true)
    private int id;

    @Column(unique = true)
    private String username;
    private String password;
    private String email;
    private int role;
    private String companyName;
    private String companyDotNumber;
    private String companyAddress;
    @Lob
    private byte[] picture;
    
    @Transient
    private String password_2;   
    
    public User() {
    	this.username = "";
		this.password = "";
		this.email = "";
		this.companyName = "";
		this.companyDotNumber = "";
		this.companyAddress = "";
		this.password_2 = "";
    }

    public User(String username, String password, String email, int role) {
        this.setUsername(username);
        this.setPassword(password);
        this.setEmail(email);
        this.setRole(role);
        this.companyName = "";
		this.companyDotNumber = "";
		this.companyAddress = "";
		this.password_2 = "";
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword_2() {
        return password_2;
    }
    public void setPassword_2(String password_2) {
        this.password_2 = password_2;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public int getRole() {
        return role;
    }
    public void setRole(int role) {
        this.role = role;
    }

	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyDotNumber() {
		return companyDotNumber;
	}
	public void setCompanyDotNumber(String companyDotNumber) {
		this.companyDotNumber = companyDotNumber;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}
	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public byte[] getPicture() {
		return picture;
	}
	public void setPicture(byte[] picture) {
		this.picture = picture;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return (id == user.id // &&
//                Objects.equals(username, user.username) &&
//                Objects.equals(password, user.password) &&
//                Objects.equals(password_2, user.password_2) &&
//                Objects.equals(email, user.email) &&
//                Objects.equals(role, user.role)
        		);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, username, password, password_2, email, role, companyName, companyDotNumber, companyAddress);
    }
}
