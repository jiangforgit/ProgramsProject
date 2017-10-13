package programs.studyprogram.mvp.model;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import javax.inject.Inject;

import programs.publicmodule.core.appconstant.ProgramsApplication;
import programs.publicmodule.retrofit2.apiservices.NetApiService;
import programs.publicmodule.retrofit2.responsepack.RequestResPack;
import programs.studyprogram.core.businessdeal.JniBusi;
import programs.studyprogram.proxys.impls.Subject;
import programs.studyprogram.proxys.impls.SubjectImpl;
import programs.studyprogram.proxys.interfaces.ISubject;
import programs.studyprogram.proxys.proxys.DynamicSubject;
import programs.studyprogram.proxys.proxys.SubjectProxy;
import programs.studyprogram.retrofit2.apiservices.ConfigObtainService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/2/15 0015.
 */

public class StudyFunctionModel implements IStudyFunctionModel {

    private String showValue;
    private NetApiService netApiService;
    private ConfigObtainService configObtainService;

    public StudyFunctionModel(){

    }

    @Override
    public String getShowValue() {
        return showValue;
    }

    @Override
    public NetApiService getNetApiService() {
        return netApiService;
    }

    @Override
    public ConfigObtainService getConfigObtainService() {
        return configObtainService;
    }

    public void setConfigObtainService(ConfigObtainService configObtainService) {
        this.configObtainService = configObtainService;
    }

    public void setShowValue(String showValue) {
        this.showValue = showValue;
    }

    public void setNetApiService(NetApiService netApiService) {
        this.netApiService = netApiService;
    }

    @Override
    public int getVersionCode() {
        JniBusi busi = new JniBusi();
        return busi.versionCode();
    }

    @Override
    public void myTest() {
//        SubjectProxy proxy = new SubjectProxy(new Subject());
//        proxy.request();
        SubjectImpl subject = new SubjectImpl();
        InvocationHandler handler = new DynamicSubject(subject);
        Class cls = subject.getClass();
        ISubject isubject = (ISubject) Proxy.newProxyInstance(cls.getClassLoader(), cls.getInterfaces(),handler);
        isubject.request();
    }
}
