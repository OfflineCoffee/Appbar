/* Copyright 2020 Braden Farmer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.farmerbb.taskbar.service;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.farmerbb.taskbar.util.U;

import static com.farmerbb.taskbar.util.Constants.*;

@TargetApi(Build.VERSION_CODES.N)
public class NotificationCountService extends NotificationListenerService {

    private BroadcastReceiver requestCountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            broadcastNotificationCount();
        }
    };

    @Override
    public void onListenerConnected() {
        U.registerReceiver(this, requestCountReceiver, ACTION_REQUEST_NOTIFICATION_COUNT);
        broadcastNotificationCount();
    }

    @Override
    public void onListenerDisconnected() {
        U.unregisterReceiver(this, requestCountReceiver);
        broadcastNotificationCount(0);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        broadcastNotificationCount();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        broadcastNotificationCount();
    }

    private void broadcastNotificationCount() {
        int count = 0;

        for(StatusBarNotification notification : getActiveNotifications()) {
            if(notification.isClearable()) count++;
        }

        broadcastNotificationCount(count);
    }

    private void broadcastNotificationCount(int count) {
        Intent intent = new Intent(ACTION_NOTIFICATION_COUNT_CHANGED);
        intent.putExtra("count", Math.min(count, 99));
        U.sendBroadcast(this, intent);
    }
}