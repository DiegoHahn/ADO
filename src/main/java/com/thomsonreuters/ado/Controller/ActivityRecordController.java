package com.thomsonreuters.ado.Controller;

import com.thomsonreuters.ado.Model.ActivityRecord;
import com.thomsonreuters.ado.Model.ActivityRecordDTO;
import com.thomsonreuters.ado.Service.ActivityRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/activityrecord")
public class ActivityRecordController {

    private final ActivityRecordService activityRecordService;

    @Autowired
    public ActivityRecordController(ActivityRecordService activityRecordService) {
        this.activityRecordService = activityRecordService;
    }

    @PostMapping
    public ResponseEntity<ActivityRecord> createActivityRecord(@RequestBody ActivityRecordDTO activityRecordDTO) throws Exception {
        ActivityRecord savedRecord = activityRecordService.saveActivityRecord(activityRecordDTO);
        return new ResponseEntity<>(savedRecord, HttpStatus.CREATED);
    }
}

