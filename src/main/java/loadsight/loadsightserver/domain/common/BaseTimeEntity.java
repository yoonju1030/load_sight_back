package loadsight.loadsightserver.domain.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Getter
@MappedSuperclass
public class BaseTimeEntity extends CreatedAtEntity {

    @UpdateTimestamp
    @Column(name="updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
