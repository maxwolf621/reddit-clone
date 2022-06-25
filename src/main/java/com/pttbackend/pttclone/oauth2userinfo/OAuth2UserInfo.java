package com.pttbackend.pttclone.oauth2userinfo;

import java.util.Map;

import com.pttbackend.pttclone.model.AuthProviderType;

/**
 * Store the Information(Attribute) of third party account
 * <strong> for example GITHUB, GOOGLE </strong>
 */
public abstract class OAuth2UserInfo {
    
    // claims
    protected Map<String, Object> attributes;

    /**
     * consturctor that stores the attributes/claims
     * @param attributes from 3rd party account
     */  
    public OAuth2UserInfo( Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    
    /**
     * @return attributes from third party
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public abstract AuthProviderType getAuthProvider();

    public abstract String getId();

    public abstract String getUsername();

    public abstract String getEmail();

    //public abstract void printString();
    //public abstract String getImageUrl();
}
