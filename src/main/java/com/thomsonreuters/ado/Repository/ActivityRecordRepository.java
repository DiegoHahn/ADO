package com.thomsonreuters.ado.Repository;

import com.thomsonreuters.ado.Model.ActivityRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRecordRepository extends JpaRepository<ActivityRecord, Long> {

    List<ActivityRecord> findByStatus(int status);
}
