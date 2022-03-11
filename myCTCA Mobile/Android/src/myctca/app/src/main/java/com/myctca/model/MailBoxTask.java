package com.myctca.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by tomackb on 1/22/18.
 */

public class MailBoxTask {

    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({ARCHIVE, MARK_AS_READ, SEND_NEW})
    // Create an interface for validating int types
    public @interface MailBoxTypeDef {}

    public static final int ARCHIVE = 0;
    public static final int MARK_AS_READ = 1;
    public static final int SEND_NEW = 2;

    public final int taskType;

    public MailBoxTask(@MailBoxTypeDef int taskType) {
        this.taskType = taskType;
    }

}
