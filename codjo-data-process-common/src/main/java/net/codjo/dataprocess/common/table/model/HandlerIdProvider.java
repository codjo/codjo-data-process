package net.codjo.dataprocess.common.table.model;
/**
 *
 */
public interface HandlerIdProvider {

    String getSelectAllHandlerId();


    String getSelectHandlerById();


    String getInsertHandlerId();


    String getDeleteHandlerId();


    String getUpdateHandlerId();


    String getSelectRequetorHandlerId();
}
