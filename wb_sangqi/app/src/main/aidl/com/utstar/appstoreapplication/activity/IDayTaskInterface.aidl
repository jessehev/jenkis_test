// IDayTask.aidl
package com.utstar.appstoreapplication.activity;
import com.utstar.appstoreapplication.activity.DayTaskEntity;

interface IDayTaskInterface {
    void loadDayTaskEntity(inout DayTaskEntity dayTaskEntity);
}
