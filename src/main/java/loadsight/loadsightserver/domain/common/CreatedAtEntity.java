package loadsight.loadsightserver.domain.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Getter
@MappedSuperclass
public class CreatedAtEntity {

    @CreationTimestamp
    @Column(name="created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
