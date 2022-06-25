package com.pttbackend.pttclone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;



import java.util.List;
import java.time.Instant;

/**
 * <p> Model Authentication User </p>
 * @see <a href="https://springbootdev.com/2018/03/13/spring-data-jpa-auditing-with-createdby-createddate-lastmodifiedby-and-lastmodifieddate/"> Annotation </a>
 * @see <a href="https://stackoverflow.com/questions/17445657/hibernate-onetomany-java-lang-stackoverflowerror"> JPA stackoverflow</a>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user", uniqueConstraints = {
    @UniqueConstraint(columnNames = "mail")
})
@ApiModel( value = "User Model", description = "To store the user information") 
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id", unique = true, nullable =false)
    private long userId ;
    
    @NotBlank(message="UserName required")
    @Column(name = "user_name", unique = true)
    private String username ;
    
    //@NotBlank(message = "Password required")
    @JsonIgnore
    @Column(name = "password")
    private String password;
    
    @Email
    @Column(name = "mail")
    private String mail;

    @Column(name = "created_date")
    private Instant createdDate;
    
    @Column(name = "legit")
    private boolean legit;

    @Column(nullable = true, length = 64)
    private String avatar;

    @Column(name = "auth_provider")
    private AuthProviderType authProvider;

    @Column(name = "about_me")
    private String aboutMe;

    /**
     * Bidirectional
     * A user can create many subs
     */
    @OneToMany(fetch = LAZY,
               cascade = ALL,
               mappedBy = "user",
               orphanRemoval = true)
    private List<Sub> subs;

    /**
     * Bidirectional
     * A user can post many posts
     */
    @OneToMany(fetch = LAZY,
               cascade = ALL,
               mappedBy = "user",
               orphanRemoval = true)
    private List<Post> posts; 

    /**
     * Bidirectional
     * A user can have many votes 
     * on many posts
     */
    @OneToMany(fetch = LAZY,
              cascade = ALL,
              mappedBy = "user")
    private List<Vote> votes;

    @Override
    public String toString(){
        return this.username + this.mail + this.createdDate ;
    }
}

