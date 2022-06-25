package com.pttbackend.pttclone.security;

import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.pttbackend.pttclone.oauth2userinfo.OAuth2UserInfo;
import com.pttbackend.pttclone.service.CustomOAuth2UserPrincipalService;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.Assert;


/**
 * <h3> A custom Oauth2User Principal </h3>
 * <p> This Model used by Custom Oauth2 Service to fetch Oauth2 UserDetails 
 * {@link CustomOAuth2UserPrincipalService#CustomOAuth2UserPrincipalService()} </p> 
 * @see <a href="https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/oauth2/core/user/OAuth2User.html">
 *      OAuth2User </a>
 * @see <a href="https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/oauth2/core/user/DefaultOAuth2User.html">
 *      DefaultOAuth2User implements OAuth2User </a>, a default Oauth2User principal 
 */
public class OAuth2UserPrincipal implements OAuth2User, Serializable{
    
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private final Set<GrantedAuthority> authorities;
	
	private final Map<String, Object> attributes;
	
	private final String nameAttributeKey;
    
	private final OAuth2UserInfo userInfo;

	/**
	 * <p> A Custom UserPrinciapl that contains </p>
	 * @param authorities role of the User
	 * @param attributes resoruce from 3rd party application
	 * @param nameAttributeKey key : "name" in {@code attributes}
	 * @param userInfo : see {@link OAuth2UserInfo#OAuth2UserInfo(Map)} 
	 */
    public OAuth2UserPrincipal(Collection<? extends GrantedAuthority> authorities, 
                               Map<String, Object> attributes,
                               String nameAttributeKey /* user name */,
                               OAuth2UserInfo userInfo) {
        /**
         * Assert 
         *   {@code attribute} 
         *   {@code nameAttributeKey} 
         */
        Assert.notEmpty(attributes, "attributes cannot be empty");
        Assert.hasText(nameAttributeKey, "nameAttributeKey cannot be empty");

		/**
		 * check if attribute contains 
		 * {@code nameAttributeKey}
		 */
        if (!attributes.containsKey(nameAttributeKey)) {
            throw new IllegalArgumentException("Missing attribute '" + nameAttributeKey + "' in attributes");
        }

		this.authorities = (authorities != null)
                ? Collections.unmodifiableSet(new LinkedHashSet<>(this.sortAuthorities(authorities)))
                : Collections.unmodifiableSet(new LinkedHashSet<>(AuthorityUtils.NO_AUTHORITIES));
        
        
		this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
        
		this.nameAttributeKey = nameAttributeKey;
        
		this.userInfo = userInfo;
    }


	/**
	 * Get value from key "name" from {@code nameAttributeKey}
	 * @return String
	 */
	@Override
	public String getName(){
			return this.getAttribute(this.nameAttributeKey).toString();
	
	}

	/** 
	 * Role of the user
	 * @return {@code Collection<? extends GrantedAuthority>}
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

    
	/** 
	 * <p> Get the OAuth 2.0 token attributes </p>
	 * @return {@code Map<String, Object>} attributes provided by 3rd party allication
	 */
	@Override
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	
	/** 
	 * <p> Remove the duplicate authorities in Colloection </p>
	 * @param authorities Roles might have duplicates in Collection
	 * @return {@code Set<GrantedAuthority>}
	 */
	private Set<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
		SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(
				Comparator.comparing(GrantedAuthority::getAuthority));
		sortedAuthorities.addAll(authorities);
		return sortedAuthorities;
	}

	
	/** 
	 * @param obj object of {@link OAuth2UserPrincipal}
	 * @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) { //same reference ?
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		OAuth2UserPrincipal that = (OAuth2UserPrincipal) obj;
		
		/**
		 * Compare the Content
		 */
		if (!this.getName().equals(that.getName())) {
			return false;
		}
		if (!this.getAuthorities().equals(that.getAuthorities())) {
			return false;
		}
		return this.getAttributes().equals(that.getAttributes());
	}

	
	/** 
	 * Hashcode of this Entity
	 * @return hashcode
	 */
	@Override
	public int hashCode() {
		int result = this.getName().hashCode();
		result = 31 * result + this.getAuthorities().hashCode();
		result = 31 * result + this.getAttributes().hashCode();
		return result;
	}

    
	/** 
	 * @return {@link OAuth2UserInfo}
	 */
	public OAuth2UserInfo getUserInfo(){
        return this.userInfo; 
    }

	
	/** 
	 * {@code ToString} name, Authorities, and attributes
	 * <pre> "name : [username], Granted Authorities: [ user_authorities ],
	 * 		User Attributes : [ attributes_from_3rdPartyApplication]" </pre>
	 * @return String 

	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Name: [");
		sb.append(this.getName());
		sb.append("], Granted Authorities: [");
		sb.append(getAuthorities());
		sb.append("], User Attributes: [");
		sb.append(getAttributes());
		sb.append("]");
		return sb.toString();
    }
}
