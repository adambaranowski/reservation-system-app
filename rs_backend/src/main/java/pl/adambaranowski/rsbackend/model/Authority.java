package pl.adambaranowski.rsbackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String role;

    @JsonBackReference
    @ManyToMany(mappedBy = "authorities", fetch = FetchType.EAGER)
    private Set<User> users = new HashSet<>();

    public Authority(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Authority{" +
                "id=" + id +
                ", role='" + role + '\'' +
                ", users=" + users.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Authority authority = (Authority) o;
        return Objects.equals(id, authority.id) && Objects.equals(role, authority.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, role);
    }
}
