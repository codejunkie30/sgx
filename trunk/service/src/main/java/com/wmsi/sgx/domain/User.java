package com.wmsi.sgx.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.common.base.Objects;

@Entity(name = "Users")
@Table(name="users", uniqueConstraints= {@UniqueConstraint(columnNames={"username"})})
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = User.class)
public class User extends AbstractAuditable{

	@Id
	@GeneratedValue(generator = "userGenerator")
	@GenericGenerator(name = "userGenerator", strategy = "com.wmsi.sgx.generator.IDGenerator")
	private Long id;

	@Column(name = "username", nullable = false, unique = true)	
	private String username;
	
	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "enabled", nullable = false)
	private Boolean enabled = false;

	@Column(name = "contact_opt_in", nullable = false)
	private Boolean contactOptIn = false;

	@OneToMany( 
		mappedBy = "user",
		fetch = FetchType.EAGER,
		cascade={CascadeType.ALL},
		orphanRemoval=true)
	private Set<Authority> authorities;	
	
	/**
	 * Add a Role type for this user
	 * @param role
	 * @return true if successfully added
	 */
	public Boolean addRole(Role role){
	
		if(authorities == null)
			authorities = new HashSet<Authority>();
		
		Boolean added = false;
		Authority auth = new Authority(this, role);
		
		if(!authorities.contains(auth))
			added = authorities.add(auth);
		
		return added;
	}
	/**
	 * Remove a role type for this user
	 * @param role
	 * @return true if successfully removed
	 */
	public Boolean removeRole(Role role){
		
		Boolean removed = false;
		
		if(authorities != null){
			Authority authority = new Authority(this, role);
			removed = authorities.remove(authority);
		}
		
		return removed;
	}
	
	/**
	 * Determine 
	 * @param role
	 * @return true if user has role
	 */
	public Boolean hasRole(Role role){
		return authorities != null && authorities.contains(new Authority(this,role));
	}
	
	public Set<Authority> getAuthorities() {
		return authorities;
	}
	
	public void setAuthorities(Set<Authority> authorities) {
		this.authorities = authorities;
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public Boolean getContactOptIn() {
		return contactOptIn;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}


	public void setContactOptIn(Boolean contactOptIn) {
		this.contactOptIn = contactOptIn;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(username, password, enabled, contactOptIn);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof User) {
			User that = (User) object;
			return Objects.equal(this.username, that.username)
				&& Objects.equal(this.password, that.password)
				&& Objects.equal(this.enabled, that.enabled)
				&& Objects.equal(this.contactOptIn, that.contactOptIn);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("super", super.toString())
			.add("id", id)
			.add("username", username)
			.add("password", password)
			.add("enabled", enabled)
			.add("contactOptIn", contactOptIn)
			.toString();
	}
		
}
