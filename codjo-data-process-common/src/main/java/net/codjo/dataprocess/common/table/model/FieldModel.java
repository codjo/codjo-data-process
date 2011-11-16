package net.codjo.dataprocess.common.table.model;
import net.codjo.dataprocess.common.table.annotations.Type;
import net.codjo.util.string.StringUtil;
/**
 *
 */
public class FieldModel {
    private String name;
    private Class<?> javaType;
    private Type type;
    private int precision;
    private boolean isPk;
    private boolean isRequired;
    private String defaultValue;
    private String description;


    public FieldModel(String name, Class javaType, Type type) {
        this.name = name;
        this.javaType = javaType;
        this.type = type;
    }


    public String getName() {
        return name;
    }


    public String getSqlName() {
        return StringUtil.javaToSqlName(name);
    }


    public Class<?> getJavaType() {
        return javaType;
    }


    public Type getType() {
        return type;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public int getPrecision() {
        return precision;
    }


    public void setPrecision(int precision) {
        this.precision = precision;
    }


    public boolean isPk() {
        return isPk;
    }


    public void setPk(boolean pk) {
        isPk = pk;
    }


    public boolean isRequired() {
        return isRequired;
    }


    public void setRequired(boolean required) {
        isRequired = required;
    }


    public String getDefaultValue() {
        return defaultValue;
    }


    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
