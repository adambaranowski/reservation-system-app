package pl.adambaranowski.rsbackend.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;
    private String userNick;
    private String profileImageUrl;
    private LocalDateTime lastLoginDateTime;
    private LocalDateTime joinDateTime;

    @Singular
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "AUTHORITY", referencedColumnName = "ID")})
    private Set<Authority> authorities = new HashSet<>();

    @Builder.Default
    private Boolean accountNonExpired = true;
    @Builder.Default
    private Boolean accountNonLocked = true;
    @Builder.Default
    private Boolean credentialsNonExpired = true;

    public void addAuthorities(Set<Authority> authorities) {
        authorities.forEach(authority -> {
            authority.getUsers().add(this);
        });

        this.authorities.addAll(authorities);
    }

    public void removeAuthorities() {
        authorities.forEach(authority -> {
            authority.getUsers().remove(this);
        });

        this.authorities.clear();
    }
}
