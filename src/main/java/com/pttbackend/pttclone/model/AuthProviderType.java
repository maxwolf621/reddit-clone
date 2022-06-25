package com.pttbackend.pttclone.model;

/**
 * <p> Via 3rd party application login or Local Login</p>
 * @author Maxowlf 
 */
public enum AuthProviderType {
    // login via LOCAL (our spring application)
    LOCAL, 
    // login via Google 
    GOOGLE,
    // login via Github 
    GITHUB
}
