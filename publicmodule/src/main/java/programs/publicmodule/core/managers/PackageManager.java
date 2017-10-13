package programs.publicmodule.core.managers;

import programs.publicmodule.core.entity.HeartBeatPackEntity;

/**
 * Created by Administrator on 2017/5/13 0013.
 */

public class PackageManager {

    private static PackageManager instance;

    private PackageManager(){

    }

    public PackageManager getInstance(){
        if(null == instance){
            instance = new PackageManager();
        }
        return instance;
    }

    public String heartBeatPack(boolean isPermitLocation,HeartBeatPackEntity entity) {
        StringBuilder sb = new StringBuilder();
        sb.append("<T>" + "<T_H>" + "<requester>" + entity.getRequester()+ "</requester>" + "<version>1.4</version>"
                + "<category>" + entity.getCategory() + "</category>"+ "<mac />" + "<packetid>" + entity.getPacketid()+ "</packetid>" + "</T_H>");

        sb.append("<T_B>" + "<item cmd=" + "'"+ entity.getCmd() + "'" + " user_id=" + "'"+ entity.getUserid() + "'"
                +" mobile=" + "'"+ entity.getMobile() + "'");

        if(isPermitLocation){//允许定位
            sb.append(" lat=" + "'" + entity.getLat()+"'" +" lng=" + "'" + entity.getLng() + "'" + " loc="+ "'" + entity.getLoc() + "'" + " radius="+"'"+entity.getRadius()+ "'" + " flag="+"'"+entity.getFlag()+"'" );
        }else{
            sb.append(" lat=" + "'" + ""+"'" +" lng=" + "'" + "" + "'" + " loc="+ "'" + "" + "'" + " radius="+"'"+""+ "'" + " flag="+"'"+"keep_live"+"'" );
        }

        sb.append(" udid=" + "'"+ entity.getImei() + "'" +  " cversion=" + "'" + entity.getVersion() + "'" + " time=" + "'" + entity.getTime()+ "'" + "/>"
                + "</T_B>" + "</T>");
        return sb.toString();
    }
}
