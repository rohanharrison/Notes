package com.rohanharrison.notes;

import android.app.RemoteInput;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Rohan on 7/14/2016.
 */
public class Reply {

    private static final java.lang.String KEY_TEXT_REPLY = "";

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_TEXT_REPLY);
        }
        return null;
    }
}
