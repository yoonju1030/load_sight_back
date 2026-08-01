package loadsight.loadsightserver.domain.auth.entity;

import jakarta.persistence.*;
import loadsight.loadsightserver.domain.auth.enums.RoleCode;
import loadsight.loadsightserver.domain.common.CreatedAtEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "role")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role extends CreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false, unique = true, length=30)
    private RoleCode code;

    @Column(name = "name", nullable = false, length = 80)
    private String name;

    @Column(name = "description", length = 300)
    private String description;

    @Column(name = "system_role", nullable = false)
    private boolean systemRole = true;

    @Builder
    private Role(RoleCode code, String name, String description, Boolean systemRole) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.systemRole = systemRole == null || systemRole;
    }

}
