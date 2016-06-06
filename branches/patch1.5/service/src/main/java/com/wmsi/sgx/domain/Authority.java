package com.wmsi.sgx.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Immutable;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.common.base.Objects;

@Entity(name = "authorities")
@Table(name="authorities")
@Immutable
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Authority.class)
public class Authority {

	public Authority(){}
	
	public Authority(User u, Role r){
		user = u;
		authority = r;
	}
	
	@Id
	@GeneratedValue(generator = "authorityGenerator")
	@GenericGenerator(name = "authorityGenerator", strategy = "com.wmsi.sgx.generator.IDGenerator")
	private Long id;
	
	@ManyToOne(optional=true)
	private User user;

	@Column(name = "authority")
	@Enumerated(EnumType.STRING)
	private Role authority;

	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public Role getAuthority() {
		return authority;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setAuthority(Role authority) {
		this.authority = authority;
	}

	/***
	 * WARNING: Note the lack of the ID field in the hashCode and equals methods. With persistent objects we don't want to 
	 * use id for comparison as objects loaded from the database will not equal an object with all the same 
	 * properties yet has not been persisted because ID is null. 
	 */
	
	@Override
	public int hashCode(){
		return Objects.hashCode(user, authority);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof Authority) {
			Authority that = (Authority) object;
			return Objects.equal(this.user, that.user)
				&& Objects.equal(this.authority, that.authority);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("id", id)
			.add("user", user)
			.add("authority", authority)
			.toString();
	}
	
}
