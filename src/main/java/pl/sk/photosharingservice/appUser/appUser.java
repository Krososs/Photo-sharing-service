package pl.sk.photosharingservice.appUser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.*;
import org.hibernate.annotations.Formula;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.sk.photosharingservice.support.language.English;
import pl.sk.photosharingservice.support.language.Language;
import pl.sk.photosharingservice.support.language.Polish;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Entity
@Table(name = "users")
@Data
public class appUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private String username;
    @NonNull
    private String password;
    @NonNull
    private String email;
    @NonNull
    private Date joiningDate;
    @NonNull
    @Formula("(SELECT COUNT(1) FROM followers f WHERE f.target_id = id)")
    private Integer followers;
    @NonNull
    @Formula("(SELECT COUNT(1) FROM followers f WHERE f.user_id = id)")
    private Integer following;
    @NonNull
    @Formula("(SELECT COUNT(1) FROM images i WHERE i.owner_id = id)")
    private Integer posts;
    private String role;
    private String profilePicture;
    private String description;
    private String language;

    public appUser() {

    }

    public appUser(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.joiningDate = new Date();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

    public Language getUserLanguage() {
        return switch (this.language) {
            case "English" -> new English();
            case "Polish" -> new Polish();
            default -> new English();
        };
    }

    public ObjectNode toJson() {
        return new ObjectMapper().createObjectNode()
                .put("id", this.id)
                .put("username", this.username)
                .put("description", this.description == null ? "" : this.description)
                .put("profilePicture", this.profilePicture == null ? null : this.profilePicture);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
