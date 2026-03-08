package com.gfg.userservice.repository.specs;

import com.gfg.userservice.domain.entity.PostPackageMapEntity;
import com.gfg.userservice.domain.enums.PostPackageStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;

public class PostPackageMapSpecification {

    public static Specification<PostPackageMapEntity> hasStatus(PostPackageStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<PostPackageMapEntity> hasStartDate(LocalDate date) {
        return (root, query, cb) -> {
            if (date == null) {
                return null;
            }
            // 1. Lấy thời điểm bắt đầu ngày (00:00:00)
            LocalDateTime startOfDay = date.atStartOfDay();

            // 2. Lấy thời điểm kết thúc ngày (23:59:59.999999999)
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            // 3. Tạo query: startDate nằm GIỮA khoảng này
            return cb.between(root.get("startDate"), startOfDay, endOfDay);
        };
    }

    public static Specification<PostPackageMapEntity> hasPackageIds(Collection<Long> packageIds) {
        return (root, query, cb) -> root.get("packageId").in(packageIds);
    }

    public static Specification<PostPackageMapEntity> hasUserId(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("userId"), userId);
    }
}
