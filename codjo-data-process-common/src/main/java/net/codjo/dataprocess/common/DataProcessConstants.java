/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common;
/**
 *
 */
public interface DataProcessConstants {

    enum MapCommand {
        PUT,
        GET,
        REMOVE,
        CONTAINS_KEY,
        CONTAINS_VALUE,
        GET_SIZE,
        CLEAR,
        GET_MAP;
    }

    enum ListCommand {
        ADD,
        GET,
        REMOVE,
        GET_SIZE,
        CLEAR,
        GET_LIST;
    }

    enum ImportRepoCommand {
        BEGIN_INSERT,
        INSERT_PART,
        END_INSERT,
        UPDATE_IMPORT_DATE;
    }

    enum Command {
        CREATE,
        READ,
        UPDATE,
        DELETE,
        COPY,
        IS_EXIST
    }

    String JAVA_TYPE = "java";
    String JAVA_TYPE_WITH_RESULT = "java_with_result";

    String BSH_TYPE_WITH_RESULT = "bsh_with_result";
    String BSH_TYPE = "bsh";

    String SQL_QUERY_TYPE = "sql";
    String SQL_QUERY_TYPE_WITH_RESULT = "sql_with_result";
    String STORED_PROC_TYPE = "stored_procedure";
    String STORED_PROC_TYPE_WITH_RESULT = "stored_procedure_with_result";

    String LOCAL_VISIBILITY = "[local] ";

    String SPECIAL_CHAR_REPLACER_N = "[@]";
    String SPECIAL_CHAR_REPLACER_R = "[$]";

    int TO_DO = 0;
    int DONE = 1;
    int FAILED = 2;
    int FAILED_DEPENDENCY = 3;

    String TRT_WARNING = "IS_WARNING";
    String TRT_OK = "OK";

    String CONTEXT_SAVE_FAILED = "CONTEXT_SAVE_FAILED";
    String CONTEXT_SAVE_OK = "CONTEXT_SAVE_OK";

    String REPOSITORY_ALREADY_EXISTS = "REPOSITORY_ALREADY_EXISTS";
    String FAMILY_ALREADY_EXISTS = "FAMILY_ALREADY_EXISTS";

    String TREATMENT_ENTITY_XML = "treatment";

    String SEPARATOR = "[separator]";
    String QUOTE = "[quote]";
    String COLUMN = "[with_column_header]";

    String STATUS_ERROR = "ERROR";
    String STATUS_NO_ERROR = "NO_ERROR";
    String STATUS_INFORMATION = "INFO";

    String MESSAGE_PROP_TREATMENT_ID = "treatmentId";
    String MESSAGE_PROP_STATUS = "status";
    String MESSAGE_PROP_ERROR = "error";
    String MESSAGE_INFORMATION = "info";
    String MESSAGE_PROP_GUI_CLASS_NAME = "targetGuiClassName";
    String MESSAGE_PROP_GUI_CLASS_PARAM = "targetGuiClassParameters";
    String OPERATION_REPORT = "operationReport";

    String USER_COMMAND_IS_EXIST = "isExist";
    String USER_COMMAND_SAVE = "save";
    String USER_COMMAND_LOAD = "load";
    String USER_COMMAND_CREATE = "create";

    String CURRENT_REPOSITORY_PROP = "CURRENT_REPOSITORY_PROP";

    String NO_RESULT = "NO_RESULT";

    String EXECUTION_LIST_JOB_TYPE = "EXECUTION_LIST_JOB_TYPE";
    String BATCH_JOB_TYPE = "BATCH_JOB_TYPE";

    String TABLE_NAME_KEY = "tableNameKey";
    String WHERE_CLAUSE_KEY = "WHERE_CLAUSE_KEY";

    String SHOW_TABLE_NAME_IN_MENU = "SHOW_TABLE_NAME_IN_MENU";
    String TABLE_EXPLORATOR = "TABLE_EXPLORATOR";

    String KEY_DATE_LAST_IMPORT_REPOSITORY = "key-dateOfLastRepositoryImport";
    String KEY_CONFIRMATION_OF_TREATMENT_EXEC = "key-askConfirmationOfTreatmentExecution";
}
