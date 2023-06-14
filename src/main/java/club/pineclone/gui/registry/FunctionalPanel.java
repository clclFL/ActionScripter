package club.pineclone.gui.registry;

import club.pineclone.gui.MainFrame;
import club.pineclone.gui.api.Registrable;

import javax.swing.*;

public abstract class FunctionalPanel extends JPanel implements Registrable {

    protected final MainFrame mainFrame;

    public FunctionalPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public abstract ImageIcon getIcon();

    public abstract String getTip();

    public void onCutIn(){
        //void
    };

    public void onCutOut(){
        //void
    };

    @Override
    public void register() {
        mainFrame.addFunctionalPanel(this);
    }
}
