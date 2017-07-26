// IDayTask.aidl
package com.ut_sdk.day_task;
import com.ut_sdk.day_task.DayTaskEntity;

interface IDayTaskInterface {
    void loadDayTaskEntity(inout DayTaskEntity dayTaskEntity);
}
