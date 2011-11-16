package net.codjo.dataprocess.gui.util.sqleditor.components;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
/**
 *
 */
public class NavigationPanelGui extends JPanel {

    private JButton firstButton = new JButton("<<");
    private JButton previousButton = new JButton("<");
    private JButton nextButton = new JButton(">");
    private JButton lastButton = new JButton(">>");
    private JLabel positionLabel = new JLabel("1 / 1");


    public NavigationPanelGui() {
        buildGui();
    }


    private void buildGui() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createHorizontalGlue());
        add(firstButton);
        add(Box.createHorizontalStrut(3));
        add(previousButton);
        add(Box.createHorizontalStrut(5));
        add(positionLabel);
        positionLabel.setText("1 / 1");
        add(Box.createHorizontalStrut(5));
        add(nextButton);
        add(Box.createHorizontalStrut(3));
        add(lastButton);
        add(Box.createHorizontalGlue());
    }


    public JButton getFirstButton() {
        return firstButton;
    }


    public JButton getPreviousButton() {
        return previousButton;
    }


    public JButton getNextButton() {
        return nextButton;
    }


    public JButton getLastButton() {
        return lastButton;
    }


    public JLabel getPositionLabel() {
        return positionLabel;
    }


    public void setPositionLabel(int currentPage, int nbOfPage) {
        positionLabel.setText(String.valueOf(currentPage) + " / " + nbOfPage);
    }


    public void manageEnablingButtons(int currentPosition, int nbOfPage) {
        firstButton.setEnabled(currentPosition > 1);
        previousButton.setEnabled(currentPosition > 1);
        nextButton.setEnabled(currentPosition < nbOfPage);
        lastButton.setEnabled(currentPosition < nbOfPage);
        positionLabel.setText(String.valueOf(currentPosition) + " / " + nbOfPage);
    }
}
