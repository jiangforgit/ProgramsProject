package programs.studyprogram.core.businessdeal;

/**
 * Created by Administrator on 2017/3/4 0004.
 */

public class JniBusi {
    static{
        System.loadLibrary("libjnibusi");
    }
    public native int versionCode();
}
