package kr.co.simplesns.controller.response;


import kr.co.simplesns.model.Alarm;
import kr.co.simplesns.model.AlarmArgs;
import kr.co.simplesns.model.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class AlarmResponse {
    private Integer id;
    private String text;
    private AlarmType alarmType;
    private AlarmArgs args;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp removedAt;

    public static AlarmResponse fromAlarm(Alarm alarm) {
        return new AlarmResponse(
                alarm.getId(),
                alarm.getAlarmType().getAlarmText(),
                alarm.getAlarmType(),
                alarm.getAlarmArgs(),
                alarm.getRegisteredAt(),
                alarm.getUpdatedAt(),
                alarm.getDeletedAt()
        );
    }
}