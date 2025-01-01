package com.thomsonreuters.ado.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thomsonreuters.ado.Model.ActivityRecord;

public interface ActivityRecordRepository extends JpaRepository<ActivityRecord, Long> {

    @Query(value = "SELECT * FROM activity_records WHERE status = :status ORDER BY id ASC LIMIT 20" , nativeQuery = true)
    List<ActivityRecord> findTop20ByStatus(@Param("status") int status);

    @Query(value = "SELECT * FROM activity_records WHERE status = :status AND user_id = :userId", nativeQuery = true)
    List<ActivityRecord> findByStatusAndUserId(@Param("status") int status, @Param("userId") Long userId);

    @Query(value = "SELECT * FROM activity_records WHERE status = 0 AND user_id = :userId AND DATE(start_time AT TIME ZONE 'UTC') = TO_DATE(:date, 'YYYY-MM-DD')", nativeQuery = true)
    List<ActivityRecord> findByDate(@Param("userId") Long userId, @Param("date") String date);

    @Query(value = "SELECT * FROM activity_records WHERE status = 0 AND user_id = :userId AND work_item_id = :workItemId", nativeQuery = true)
    List<ActivityRecord> findByWorkItemId(@Param("userId")Long userId, @Param("workItemId") int workItemID);
}
