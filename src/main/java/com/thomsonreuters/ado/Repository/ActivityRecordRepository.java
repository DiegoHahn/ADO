package com.thomsonreuters.ado.Repository;

import com.thomsonreuters.ado.Model.ActivityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityRecordRepository extends JpaRepository<ActivityRecord, Long> {

    @Query(value = "SELECT * FROM activity_records WHERE status = :status ORDER BY id ASC LIMIT 5" , nativeQuery = true)
    List<ActivityRecord> findTop20ByStatus(@Param("status") int status);

    @Query(value = "SELECT * FROM activity_records WHERE status = :status AND user_id = :userId", nativeQuery = true)
    List<ActivityRecord> findByStatusAndUserId(@Param("status") int status, @Param("userId") Long userId);
}
