package basemod;

import java.util.ArrayList;
import java.util.List;

public class ModRadioButtonGroup {
    private List<ModToggleButton> buttons;
    private ModToggleButton selected;

    public ModRadioButtonGroup(){
        this.buttons = new ArrayList<ModToggleButton>();
    }

    public ModRadioButtonGroup(ModToggleButton... buttons){
        this();
        for(ModToggleButton b : buttons){
            addButton(b);
        }
    }

    public boolean addButton(ModToggleButton b){
        b.addConsumer(
                button ->
                {
                    selectButton(button);
                });
        return buttons.add(b);
    }

    public boolean removeButton(ModToggleButton b){
        return buttons.remove(b);
    }

    public void selectButton(ModToggleButton b){
        this.selected=b;
        updateButtons();
    }

    public void updateButtons(){
        for(ModToggleButton b : buttons){
            b.enabled = selected.equals(b);
        }
    }
}