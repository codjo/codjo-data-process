/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.model;
import java.util.List;
/**
 *
 */
public class ArgModel {
    private String name;
    private String value;
    private int position;
    private int type;


    public ArgModel(String name, String value, int position, int type) {
        this.name = name;
        this.value = value;
        this.position = position;
        this.type = type;
    }


    public ArgModel(String name, String value) {
        this.name = name;
        this.value = value;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }


    public int getPosition() {
        return position;
    }


    public void setPosition(int position) {
        this.position = position;
    }


    public int getType() {
        return type;
    }


    public void setType(int type) {
        this.type = type;
    }


    public String getLocalValue() {
        return ArgModelHelper.getLocalValue(value);
    }


    public String getGlobalValue() {
        return ArgModelHelper.getGlobalValue(value);
    }


    public boolean isGlobalValue() {
        return ArgModelHelper.isGlobalValue(value);
    }


    public boolean isLocalValue() {
        return ArgModelHelper.isLocalValue(value);
    }


    public boolean isFunctionValue() {
        return ArgModelHelper.isFunctionValue(value);
    }


    public List<String> getFunctionParams() {
        return ArgModelHelper.getFunctionParams(value);
    }


    @Override
    public String toString() {
        return "name = " + name + ", value = " + value + ", position = " + position
               + ", type = " + type;
    }
}
