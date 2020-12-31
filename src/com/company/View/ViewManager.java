package com.company.View;

import com.company.Controller.ProgramManager;

public class ViewManager {

    private static ViewManager s_Instance;
    public static ViewManager getInstance(){
        if (s_Instance == null) s_Instance = new ViewManager();
        return s_Instance;
    }

    JoinView joinView;

    public void joinViewOpen(){
        if(joinView == null) joinView = new JoinView();
        joinView.drawView();
    }

}
