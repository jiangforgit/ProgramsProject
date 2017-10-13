package programs.publicmodule.core.enums;

/**
 * Created by Administrator on 2017/4/27 0027.
 */

public enum EnumMainServiceCmd {

    Default(-1,"无指令"),Logout(-2,"退出解绑指令");

    private int cmd = -1;
    private String des = "";
    private EnumMainServiceCmd(int command,String ds){
        this.cmd = command;
        this.des = ds;
    }

    public int getCmd() {
        return cmd;
    }

    public String getDes() {
        return des;
    }
}
