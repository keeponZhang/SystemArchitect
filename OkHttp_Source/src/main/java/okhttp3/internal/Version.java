package okhttp3.internal;

/**
 * Created by hcDarren on 2017/11/19.
 */

public final class Version {
    public static String userAgent() {
        return "okhttp/${project.version}";
    }

    private Version() {
    }
}
