package io.github.some_example_name.model.costumActors;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PauseModel extends Modal{
    public PauseModel(){
        super();
        TextButton resumeBtn=new TextButton("Resume",skin);
        TextButton settingsBtn=new TextButton("Settings",skin);
        TextButton guideBtn=new TextButton("Guide",skin);
        TextButton exitBtn=new TextButton("exit",skin);


        defaults().space(5);
        add(resumeBtn).width(100).row();
        add(settingsBtn).width(100).row();
        add(guideBtn).width(100).row();
        add(exitBtn).width(100);

        resumeBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onResume();
            }
        });
        exitBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onExit();
            }
        });

    }
    public void onResume(){}
    public void onExit(){}

}
