package net.codjo.dataprocess.common.table.model;
/**
 *
 */
class DefaultHandlerIdProvider implements HandlerIdProvider {
    private Class clazz;


    DefaultHandlerIdProvider(Class clazz) {
        this.clazz = clazz;
    }


    public String getSelectAllHandlerId() {
        return "selectAll" + clazz.getSimpleName();
    }


    public String getSelectHandlerById() {
        return "select" + clazz.getSimpleName() + "ById";
    }


    public String getInsertHandlerId() {
        return "new" + clazz.getSimpleName();
    }


    public String getDeleteHandlerId() {
        return "delete" + clazz.getSimpleName();
    }


    public String getUpdateHandlerId() {
        return "update" + clazz.getSimpleName();
    }


    public String getSelectRequetorHandlerId() {
        return clazz.getSimpleName() + "Requetor";
    }
}
