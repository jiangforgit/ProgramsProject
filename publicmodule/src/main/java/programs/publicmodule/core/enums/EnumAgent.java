package programs.publicmodule.core.enums;

/**
 * Created by Jiang on 2016/4/8.
 */
public enum EnumAgent {

    studyprogram("ctgz","study");

    private String agentName = "";
    private String agentDesc = "";

    private EnumAgent (String agent,String desc){
        this.agentName = agent;
        this.agentDesc = desc;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentDesc() {
        return agentDesc;
    }

    public void setAgentDesc(String agentDesc) {
        this.agentDesc = agentDesc;
    }
}
