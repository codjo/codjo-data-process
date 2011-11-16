package net.codjo.dataprocess.common.table.model;
import net.codjo.dataprocess.common.table.annotations.PrimaryKey;
import net.codjo.dataprocess.common.table.annotations.Table;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
public class TableModel {
    private Class<?> clazz;
    private List<FieldModel> fields;
    private HandlerIdProvider handlerIdProvider;


    public TableModel(Class<?> clazz) {
        this(clazz, new DefaultHandlerIdProvider(clazz));
    }


    public TableModel(Class<?> clazz, HandlerIdProvider handlerIdProvider) {
        this.clazz = clazz;
        this.handlerIdProvider = handlerIdProvider;
    }


    public String getName() {
        Table table = clazz.getAnnotation(Table.class);
        return table.name();
    }


    public List<FieldModel> getFields() {
        if (fields == null) {
            fields = new ArrayList<FieldModel>();
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(net.codjo.dataprocess.common.table.annotations.Field.class)) {
                    net.codjo.dataprocess.common.table.annotations.Field fieldAnno
                          = field.getAnnotation(net.codjo.dataprocess.common.table.annotations.Field.class);
                    String name = fieldAnno.name().length() == 0 ? field.getName() : fieldAnno.name();
                    FieldModel fieldModel = new FieldModel(name, field.getType(), fieldAnno.type());
                    fieldModel.setDescription(fieldAnno.description());
                    if (field.isAnnotationPresent(PrimaryKey.class)) {
                        fieldModel.setPk(true);
                    }
                    fields.add(fieldModel);
                }
            }
        }
        return fields;
    }


    public List<FieldModel> getPks() {
        List<FieldModel> pks = new ArrayList<FieldModel>();
        for (FieldModel fieldModel : getFields()) {
            if (fieldModel.isPk()) {
                pks.add(fieldModel);
            }
        }
        return pks;
    }


    public List<FieldModel> getNotPks() {
        List<FieldModel> pks = new ArrayList<FieldModel>();
        for (FieldModel fieldModel : getFields()) {
            if (!fieldModel.isPk()) {
                pks.add(fieldModel);
            }
        }
        return pks;
    }


    public String[] getPkAsStrArray() {
        List<String> pks = new ArrayList<String>();
        for (FieldModel fieldModel : getFields()) {
            if (fieldModel.isPk()) {
                pks.add(fieldModel.getName());
            }
        }
        return pks.toArray(new String[pks.size()]);
    }


    public HandlerIdProvider getHandlerIdProvider() {
        return handlerIdProvider;
    }
}
