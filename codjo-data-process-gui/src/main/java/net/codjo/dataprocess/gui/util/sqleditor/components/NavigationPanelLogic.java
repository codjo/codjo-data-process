/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util.sqleditor.components;
import net.codjo.dataprocess.common.eventsbinder.EventBinderException;
import net.codjo.dataprocess.common.eventsbinder.EventsBinder;
import net.codjo.dataprocess.common.eventsbinder.annotations.OnError;
import net.codjo.dataprocess.common.eventsbinder.annotations.events.OnAction;
import net.codjo.dataprocess.gui.util.ErrorDialog;
/**
 *
 */
public abstract class NavigationPanelLogic {
    private int nbOfPage;
    private int currentPosition;
    private NavigationPanelGui navigationPanelGui;


    protected NavigationPanelLogic(EventsBinder eventsBinder) throws EventBinderException {
        currentPosition = 1;
        navigationPanelGui = new NavigationPanelGui();
        eventsBinder.bind(this, navigationPanelGui);
    }


    public int getCurrentPosition() {
        return currentPosition;
    }


    public void setNbOfPage(int nbOfPage) {
        this.nbOfPage = nbOfPage;
        getNavigationPanelGui().manageEnablingButtons(currentPosition, nbOfPage);
    }


    public NavigationPanelGui getNavigationPanelGui() {
        return navigationPanelGui;
    }


    @OnAction(propertiesBound = "firstButton")
    public void goFirst() {
        int oldPos = currentPosition;
        currentPosition = 1;
        getNavigationPanelGui().manageEnablingButtons(currentPosition, nbOfPage);
        onPageChanged(oldPos, currentPosition);
    }


    @OnAction(propertiesBound = "previousButton")
    public void goPrevious() {
        int oldPos = currentPosition;
        currentPosition--;
        getNavigationPanelGui().manageEnablingButtons(currentPosition, nbOfPage);
        onPageChanged(oldPos, currentPosition);
    }


    @OnAction(propertiesBound = "nextButton")
    public void goNext() {
        int oldPos = currentPosition;
        currentPosition++;
        getNavigationPanelGui().manageEnablingButtons(currentPosition, nbOfPage);
        onPageChanged(oldPos, currentPosition);
    }


    @OnAction(propertiesBound = "lastButton")
    public void goLast() {
        int oldPos = currentPosition;
        currentPosition = nbOfPage;
        getNavigationPanelGui().manageEnablingButtons(currentPosition, nbOfPage);
        onPageChanged(oldPos, currentPosition);
    }


    public abstract void onPageChanged(int from, int to);


    @OnError
    public void exceptionRaisedWhileInvokingMethod(Throwable ex) {
        ErrorDialog.show(null, "Error", ex);
    }
}
