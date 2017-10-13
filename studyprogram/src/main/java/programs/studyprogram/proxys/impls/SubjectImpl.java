package programs.studyprogram.proxys.impls;

import programs.studyprogram.proxys.interfaces.ISubject;

/**
 * Created by Administrator on 2017/3/31 0031.
 */

public class SubjectImpl implements ISubject {
    @Override
    public void request() {
        System.out.println("SubjectImpl-request");
    }

    @Override
    public void getValue() {
        System.out.println("SubjectImpl-getValue");
    }
}
