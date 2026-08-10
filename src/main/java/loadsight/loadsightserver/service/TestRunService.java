package loadsight.loadsightserver.service;

import loadsight.loadsightserver.domain.loadtest.enums.RunStatus;
import loadsight.loadsightserver.dto.PageResponse;
import loadsight.loadsightserver.dto.RecentRunResponse;
import loadsight.loadsightserver.repository.TestRunRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class TestRunService {

    private final TestRunRepository repository;

    public TestRunService(TestRunRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public PageResponse<RecentRunResponse> getRuns(
            UUID ownerId,
            int page,
            int size,
            String search,
            String status,
            Integer days
    ) {
        String normalizedSearch = normalizeFilter(search);
        UUID runId = parseUuid(normalizedSearch);
        Set<RunStatus> statuses = parseStatuses(status);
        if (statuses != null && statuses.isEmpty()) {
            return PageResponse.of(List.of(), page, size, 0);
        }
        OffsetDateTime createdAfter = days == null || days <= 0
                ? null
                : OffsetDateTime.now().minusDays(days);

        List<RecentRunResponse> content = repository.findPageByOwner(
                        ownerId, normalizedSearch, runId, statuses, createdAfter, page, size
                ).stream()
                .map(RecentRunResponse::from)
                .toList();
        long totalElements = repository.countByOwner(
                ownerId, normalizedSearch, runId, statuses, createdAfter
        );

        return PageResponse.of(content, page, size, totalElements);
    }

    private String normalizeFilter(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private UUID parseUuid(String value) {
        if (value == null) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private Set<RunStatus> parseStatuses(String value) {
        String normalized = normalizeFilter(value);
        if (normalized == null || normalized.equalsIgnoreCase("ALL")) {
            return null;
        }

        return switch (normalized.toUpperCase(Locale.ROOT)) {
            case "PASS", "COMPLETED" -> Set.of(RunStatus.COMPLETED);
            case "FAIL", "FAILED" -> Set.of(RunStatus.FAILED);
            case "CANCELLED", "CANCELED", "STOPPED" -> Set.of(RunStatus.CANCELLED);
            case "RUNNING" -> Set.of(
                    RunStatus.CREATED,
                    RunStatus.STARTING,
                    RunStatus.RUNNING,
                    RunStatus.STOPPING
            );
            case "WARNING" -> Set.of();
            default -> throw new IllegalArgumentException("Unsupported run status: " + value);
        };
    }
}
