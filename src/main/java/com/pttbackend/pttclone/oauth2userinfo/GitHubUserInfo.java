package com.pttbackend.pttclone.oauth2userinfo;

import java.util.Map;

import com.pttbackend.pttclone.model.AuthProviderType;


    /** 
     * <p> **** Example for Attributes in GITHUB ****
     *   login=xxx123, 
     *   id= .... , 
     *   node_id= ... , 
     *   avatar_url=https://avatars.githubusercontent.com/ .... , 
     *   gravatar_id=, 
     *   url=https://api.github.com/users/xxx123, 
     *   html_url=https://github.com/xxx123, 
     *   followers_url=https://api.github.com/users/xxx123/followers, 
     *   following_url=https://api.github.com/users/xxx123/following{/other_user}, 
     *   gists_url=https://api.github.com/users/xxx123/gists{/gist_id}, 
     *   starred_url=https://api.github.com/users/xxx123/starred{/owner}{/repo}, 
     *   subscriptions_url=https://api.github.com/users/xxx123/subscriptions, 
     *   organizations_url=https://api.github.com/users/xxx123/orgs, 
     *   repos_url=https://api.github.com/users/xxx123/repos, 
     *   events_url=https://api.github.com/users/xxx123/events{/privacy}, 
     *   received_events_url=https://api.github.com/users/xxx123/received_events, 
     *   type=User, 
     *   site_admin=false, 
     *   name=xxx123, 
     *   company=null, 
     *   blog=, 
     *   location=null, 
     *   email=null, 
     *   hireable=null, 
     *   bio=null, 
     *   twitter_username=null, 
     *   public_repos=13, 
     *   public_gists=0, 
     *   followers=0, 
     *   following=0, 
     *   created_at=2020-07-22T05:37:14Z, 
     *   updated_at=2021-06-30T05:45:40Z, 
     *   private_gists=1, 
     *   total_private_repos=4, 
     *   owned_private_repos=4, 
     *   disk_usage=8989, 
     *   collaborators=0, 
     *   two_factor_authentication=false, 
     *   plan={name=free, space=1234568, collaborators=0, private_repos=1234567}
     */


public class GitHubUserInfo extends OAuth2UserInfo {

    
    public GitHubUserInfo(Map<String, Object> claims){
        super(claims);
    }

    private static final AuthProviderType authProvider = AuthProviderType.GITHUB;

    /**
     * @return AuthProviderTYpe.GITHUB
     */
    @Override
    public AuthProviderType getAuthProvider(){
        return authProvider;
    } 

    @Override
    public String getId() {
        return ((Integer) attributes.get("id")).toString();
    }

    @Override
    public String getUsername() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        String email = (String) attributes.get("email");
        return email;
    }
}
