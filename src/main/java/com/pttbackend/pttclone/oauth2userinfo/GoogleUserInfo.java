package com.pttbackend.pttclone.oauth2userinfo;

import java.util.Map;

import com.pttbackend.pttclone.model.AuthProviderType;

/** 
* <p> **** EXAMPLE for attributes in GOOGLE***** </p>
* <p>1. Name: [10052435 .... 382622370], </p>
* <p>2. Granted Authorities: [
*    [ROLE_USER, 
*     SCOPE_https://www.googleapis.com/auth/userinfo.email, 
*     SCOPE_https://www.googleapis.com/auth/userinfo.profile, 
*     SCOPE_openid]
*   ], 
* </p>
* <p> 3. User Attributes: [                  <--- get userinfo from this header
*   {sub=10052435 .... 382622370, 
*    name=xxxx, 
*    given_name=xx, 
*    family_name=xxx, 
*    picture=https://lh3.googleusercontent.com/......, 
*    email=xxxx@nkust.edu.tw, 
*    email_verified=true, 
*    locale=zh-TW, 
*    hd=nkust.edu.tw}
*   ] 
* </p>
*/

/**
 * Store The Attribute From Google
 */
public class GoogleUserInfo extends OAuth2UserInfo {

    public GoogleUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    private final AuthProviderType authProvider = AuthProviderType.GOOGLE;

    @Override
    public AuthProviderType getAuthProvider(){
        return authProvider;
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getUsername() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }


}
