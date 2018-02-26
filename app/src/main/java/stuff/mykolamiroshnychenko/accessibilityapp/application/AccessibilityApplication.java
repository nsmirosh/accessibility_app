package stuff.mykolamiroshnychenko.accessibilityapp.application;

import android.app.Application;
import android.content.Context;

import timber.log.Timber;

/**
 * Created by mykolamiroshnychenko on 2/20/18.
 */

public class AccessibilityApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
    }
}
