package programs.studyprogram.proxys.proxys;

import programs.studyprogram.proxys.impls.Subject;

/**
 * Created by Administrator on 2017/3/31 0031.
 */

public class SubjectProxy extends Subject {

    private Subject mSubject;

    public SubjectProxy(Subject st){
        this.mSubject = st;
    }

    public void request(){
        preRequest();
        mSubject.request();
        endRequest();
    }

    private void preRequest(){
        System.out.println("SubjectProxy-preRequest");
    }

    private void endRequest(){
        System.out.println("SubjectProxy-endRequest");
    }
}
