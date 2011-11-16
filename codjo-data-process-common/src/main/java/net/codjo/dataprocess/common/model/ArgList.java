/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.model;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
public class ArgList {
    private List<ArgModel> args;


    public ArgList() {
    }


    public ArgList(ArgList argList) {
        args = new ArrayList<ArgModel>(argList.getArgs());
    }


    public List<ArgModel> getArgs() {
        if (args == null) {
            args = new ArrayList<ArgModel>();
        }
        return args;
    }


    public void setArgs(List<ArgModel> args) {
        this.args = args;
    }


    @Override
    public String toString() {
        if (args == null) {
            return "ArgsList(empty)";
        }
        StringBuilder str = new StringBuilder();
        for (ArgModel arg : args) {
            str.append(';').append(arg);
        }
        return str.substring(1);
    }
}
