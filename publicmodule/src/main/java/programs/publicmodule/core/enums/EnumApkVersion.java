package programs.publicmodule.core.enums;

/**
 * Created by Jiang on 2016/4/18.
 *
 * 用于保存各个apk已发布的版本信息
 *
 */
public enum EnumApkVersion {

    studyprogramApkVs("1.1",1);

    private String versionName;
    private int versionCode;

    private EnumApkVersion(String vName,int vCode){
        versionName = vName;
        versionCode = vCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }


}
