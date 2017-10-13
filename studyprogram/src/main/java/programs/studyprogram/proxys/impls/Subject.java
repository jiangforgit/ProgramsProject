package programs.studyprogram.proxys.impls;

import programs.studyprogram.proxys.interfaces.ASubject;

/**
 * Created by Administrator on 2017/3/31 0031.
 */

public class Subject extends ASubject {
    @Override
    public void request() {
        System.out.println("Subject-request");
    }
}
