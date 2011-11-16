package net.codjo.dataprocess.common.table.annotations;
import java.sql.Types;
/**
 *
 */
public enum Type {
    BIGINT(Types.BIGINT),
    DECIMAL(Types.DECIMAL),
    DOUBLE(Types.DOUBLE),
    FLOAT(Types.FLOAT),
    NUMERIC(Types.NUMERIC),
    REAL(Types.REAL),
    BINARY(Types.BINARY),
    BIT(Types.BIT),
    BOOLEAN(Types.BOOLEAN),
    CHAR(Types.CHAR),
    NULL(Types.NULL),
    LONGVARCHAR(Types.LONGVARCHAR),
    INTEGER(Types.INTEGER),
    SMALLINT(Types.SMALLINT),
    TINYINT(Types.TINYINT),
    VARCHAR(Types.VARCHAR),
    TIMESTAMP(Types.TIMESTAMP),
    DATETIME(93),
    DATE(Types.DATE),
    BLOB(Types.BLOB),
    UNDEFINED(-1);

    private int sqlType;


    Type(int sqlType) {
        this.sqlType = sqlType;
    }


    public int getSqlType() {
        return sqlType;
    }
}
