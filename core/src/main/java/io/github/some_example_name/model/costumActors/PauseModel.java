package io.github.some_example_name.model.costumActors;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PauseModel extends Modal{
    public PauseModel(){
        super();
        TextButton resumeBtn=new TextButton("Resume",textButtonStyle);
        TextButton saveBtn=new TextButton("Save",textButtonStyle);
        TextButton settingsBtn=new TextButton("Settings",textButtonStyle);
        TextButton guideBtn=new TextButton("Guide",textButtonStyle);
        TextButton exitBtn=new TextButton("exit",textButtonStyle);


        defaults().space(5);
        add(resumeBtn).width(100).row();
        add(saveBtn).width(100).row();
        add(settingsBtn).width(100).row();
        add(guideBtn).width(100).row();
        add(exitBtn).width(100);

        resumeBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onResume();
            }
        });
        saveBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onSave();
            }
        });
        exitBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onExit();
            }
        });
        settingsBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onSettings();
            }
        });
        guideBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onGuide();
            }
        });

    }
    public void onResume(){}
    public void onSave(){}
    public void onExit(){}
    public void onSettings(){}
    public void onGuide(){}

}
