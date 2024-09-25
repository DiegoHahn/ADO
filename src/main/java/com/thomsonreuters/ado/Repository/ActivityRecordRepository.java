package com.thomsonreuters.ado.Repository;

import com.thomsonreuters.ado.Model.ActivityRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRecordRepository extends JpaRepository<ActivityRecord, Long> {
}
