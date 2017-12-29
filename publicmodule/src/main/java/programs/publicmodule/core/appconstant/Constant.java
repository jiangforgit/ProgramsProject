package programs.publicmodule.core.appconstant;


import programs.publicmodule.core.enums.EnumAgent;

/**
 * Created by Jiang on 2016/4/8.
 */
public class Constant {

    public final static EnumAgent agent = EnumAgent.studyprogram;//项目代号

    //初始登录默认连接服务器url 多域名路由机制
    public static String[] InitValueUrls = new String[]{"http://app101.ceeker.cn/auth.aspx","http://220.162.239.101:918/Auth.aspx",
            "http://app60.ceeker.cn:9108/Auth.aspx","http://app235.ceeker.cn/auth.aspx",
            "http://121.101.215.252/Auth.aspx"};

}
