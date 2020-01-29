package com.smiletic.royalur.XML;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement(namespace = "com.smiletic.royalur.XML")
public class ActionList {
    @XmlElementWrapper(name = "listOfActions")
    @XmlElement(name="Action")
    private ArrayList<Action> listOfActions;


    public ArrayList<Action> getActionList() {
        return listOfActions;
    }

    public void setActionList(ArrayList<Action> listOfActions) {
        this.listOfActions = listOfActions;
    }
}
