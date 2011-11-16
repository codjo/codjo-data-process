/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.kernel;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.codec.ListCodec;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ArgModel;
import net.codjo.dataprocess.common.model.ArgModelHelper;
import net.codjo.dataprocess.common.util.CommonUtils;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
/**
 *
 */
class Argument {
    static final String PACKAGE_ARGUMENT_MODIFIER_ERROR = "PACKAGE_ARGUMENT_MODIFIER_NOT_INITIALIZED";
    private ArgModel argModel;
    private String computedValue;


    Argument(ArgModel argModel) {
        this.argModel = argModel;
        computedValue = argModel.getValue();
    }


    public String getValue() {
        return computedValue;
    }


    public String getName() {
        return argModel.getName();
    }


    public int getPosition() {
        return argModel.getPosition();
    }


    public int getType() {
        return argModel.getType();
    }


    public void computeValue(Connection con,
                             DataProcessContext context,
                             int repositoryId,
                             String executionListName) throws TreatmentException {
        if (argModel.isFunctionValue()) {
            processContext(con, context, repositoryId, executionListName);
        }
        else {
            processContext(context, repositoryId, executionListName);
        }
    }


    private void processContext(Connection con,
                                DataProcessContext context,
                                int repositoryId,
                                String executionListName) throws TreatmentException {
        String className;
        String argModifierClass = argModel.getValue().substring(0, argModel.getValue().indexOf('('));
        if (argModifierClass.indexOf('.') == -1) {
            String packageArgumentModifier = getPackageArgumentModifier(context);
            className = packageArgumentModifier + argModifierClass;
        }
        else {
            className = argModifierClass;
        }
        try {
            ArgumentModifier argumentModifier = (ArgumentModifier)Class.forName(className).newInstance();
            List<String> parametersList = proceedParameters(context, repositoryId, executionListName);
            computedValue = argumentModifier.proceed(con, parametersList);
            String params = argModel.getValue().substring(argModel.getValue().indexOf('(') + 1,
                                                          argModel.getValue().length() - 1);
            if (Log.isDebugEnabled()) {
                Log.debug(getClass(), String.format("Résolution des arguments: %s(%s) --> %s(%s) --> %s",
                                                    className,
                                                    params,
                                                    className,
                                                    new ListCodec().encode(parametersList),
                                                    computedValue));
            }
        }
        catch (InstantiationException ex) {
            computedValue = argModel.getValue();
            String errorMessage =
                  String.format("Problème d'instanciation de la classe de modification d'arguments '%s'",
                                className);
            Log.error(getClass(), errorMessage, ex);
            throw new TreatmentException(errorMessage, ex);
        }
        catch (IllegalAccessException ex) {
            computedValue = argModel.getValue();
            String errorMessage =
                  String.format("Problème d'accès à la classe de modification d'arguments '%s'", className);
            Log.error(getClass(), errorMessage, ex);
            throw new TreatmentException(errorMessage, ex);
        }
        catch (ClassNotFoundException ex) {
            computedValue = argModel.getValue();
            String errorMessage =
                  String.format("Classe de modification d'arguments '%s' inexistante", className);
            Log.error(getClass(), errorMessage, ex);
            throw new TreatmentException(errorMessage, ex);
        }
        catch (TreatmentException ex) {
            computedValue = argModel.getValue();
            Log.error(getClass(),
                      String.format("Erreur dans la classe de modification d'arguments '%s'", className), ex);
            throw ex;
        }
        catch (Exception ex) {
            computedValue = argModel.getValue();
            String errorMessage =
                  String.format("Erreur lors de l'appel et/ou exécution de la classe de modification "
                                + "d'arguments '%s' \nPACKAGE-ARGUMENT-MODIFIER a t-il été renseigné correctement "
                                + "dans le projet qui utilise la librairie codjo-data-process ?",
                                className);
            Log.error(getClass(), errorMessage, ex);
            throw new TreatmentException(errorMessage, ex);
        }
    }


    private void processContext(DataProcessContext context,
                                int repositoryId,
                                String executionListName) throws TreatmentException {
        if (argModel.isGlobalValue()) {
            computedValue = context.getProperty(argModel.getGlobalValue());
            if (Log.isDebugEnabled()) {
                Log.debug(getClass(), String.format("Résolution des arguments: %s --> %s",
                                                    argModel.getValue(),
                                                    computedValue));
            }
        }
        else if (argModel.isLocalValue()) {
            if (executionListName != null && repositoryId != 0) {
                String key = CommonUtils.localify(repositoryId, executionListName, argModel.getLocalValue());
                computedValue = context.getProperty(key);
                if (Log.isDebugEnabled()) {
                    Log.debug(getClass(), String.format("Résolution des arguments: %s --> %s",
                                                        argModel.getValue(), computedValue));
                }
            }
            else {
                throw new TreatmentException(impossibleResolveErrorMessage());
            }
        }
        else {
            computedValue = argModel.getValue();
        }
    }


    public List<String> getNotResolvableValue(Connection con,
                                              DataProcessContext context,
                                              int repositoryId,
                                              String executionListName) {
        if (argModel.isFunctionValue()) {
            return getNotResolvableValueWithArgumentModifier(con, context, repositoryId, executionListName);
        }
        else {
            return getNotResolvableValueWithoutArgumentModifier(context, executionListName, repositoryId);
        }
    }


    private List<String> getNotResolvableValueWithoutArgumentModifier(DataProcessContext context,
                                                                      String executionListName,
                                                                      int repositoryId) {
        String message = null;
        if (argModel.isGlobalValue()) {
            if (context.getProperty(argModel.getGlobalValue()) == null) {
                message = getNotResolvedMessage(argModel.getGlobalValue());
            }
        }
        else if (argModel.isLocalValue()) {
            if (executionListName != null && repositoryId != 0) {
                String key = CommonUtils.localify(repositoryId, executionListName, argModel.getLocalValue());
                if (context.getProperty(key) == null) {
                    message = getNotResolvedMessage(argModel.getLocalValue());
                }
            }
            else {
                message = impossibleResolveErrorMessage();
            }
        }
        List<String> parametersList = new ArrayList<String>();
        if (message != null) {
            parametersList.add(message);
        }
        return parametersList;
    }


    private List<String> getNotResolvableValueWithArgumentModifier(Connection con,
                                                                   DataProcessContext context,
                                                                   int repositoryId,
                                                                   String executionListName) {
        String packageArgumentModifier = getPackageArgumentModifier(context);
        String treatmentArgModifierClassName =
              argModel.getValue().substring(0, argModel.getValue().indexOf('('));
        List<String> paramNotResolvableAndError = getNotResolvableParameters(context,
                                                                             repositoryId,
                                                                             executionListName);
        String className;
        if (treatmentArgModifierClassName.indexOf('.') == -1) {
            className = packageArgumentModifier + treatmentArgModifierClassName;
        }
        else {
            className = treatmentArgModifierClassName;
        }
        try {
            ArgumentModifier argumentModifier = (ArgumentModifier)Class.forName(className).newInstance();
            if (paramNotResolvableAndError.isEmpty()) {
                List<String> parametersList = proceedParameters(context, repositoryId, executionListName);
                argumentModifier.proceed(con, parametersList);
            }
        }
        catch (InstantiationException ex) {
            paramNotResolvableAndError.add(
                  String.format("Problème d'instanciation de la classe de modification d'arguments '%s'",
                                className));
        }
        catch (IllegalAccessException ex) {
            paramNotResolvableAndError.add(
                  String.format("Problème d'accès à la classe de modification d'arguments '%s'", className));
        }
        catch (ClassNotFoundException ex) {
            paramNotResolvableAndError.add(String.format(
                  "Classe de modification d'arguments '%s' inexistante.", className));
        }
        catch (TreatmentException ex) {
            paramNotResolvableAndError.add(String.format(
                  "Erreur dans la classe de modification d'arguments '%s' : %s",
                  className,
                  ex.getLocalizedMessage()));
        }
        catch (Exception ex) {
            paramNotResolvableAndError.add(
                  String.format(
                        "Erreur lors de l'appel et/ou exécution de la classe de modification d'arguments '%s' "
                        + "\nPACKAGE-ARGUMENT-MODIFIER a t-il été renseigné correctement dans le projet qui "
                        + "utilise la librairie codjo-data-process ? :\n%s",
                        className,
                        ex.getLocalizedMessage()));
        }
        return paramNotResolvableAndError;
    }


    private List<String> proceedParameters(DataProcessContext context,
                                           int repositoryId,
                                           String executionListName) throws TreatmentException {
        String parameters = argModel.getValue().substring(argModel.getValue().indexOf('(') + 1,
                                                          argModel.getValue().length() - 1);
        List<String> parametersList = new ArrayList<String>();
        List<String> parametersTemp = new ListCodec().decode(parameters);
        for (String param : parametersTemp) {
            param = param.trim();
            if (ArgModelHelper.isGlobalValue(param)) {
                param = context.getProperty(ArgModelHelper.getGlobalValue(param));
            }
            else if (ArgModelHelper.isLocalValue(param)) {
                if (executionListName != null && repositoryId != 0) {
                    String key = CommonUtils.localify(repositoryId,
                                                      executionListName,
                                                      ArgModelHelper.getLocalValue(param));
                    param = context.getProperty(key);
                }
                else {
                    throw new TreatmentException(impossibleResolveErrorMessage());
                }
            }
            parametersList.add(param);
        }
        return parametersList;
    }


    private List<String> getNotResolvableParameters(DataProcessContext context,
                                                    int repositoryId,
                                                    String executionListName) {
        List<String> parametersList = new ArrayList<String>();
        String parameters = argModel.getValue().substring(argModel.getValue().indexOf('(') + 1,
                                                          argModel.getValue().length() - 1);
        List<String> parametersTemp = new ListCodec().decode(parameters);
        for (String aParametersTemp : parametersTemp) {
            String param = aParametersTemp.trim();
            if (ArgModelHelper.isGlobalValue(param)) {
                if (context.getProperty(ArgModelHelper.getGlobalValue(param)) == null) {
                    parametersList.add(getNotResolvedMessage(ArgModelHelper.getGlobalValue(param)));
                }
            }

            else if (ArgModelHelper.isLocalValue(param)) {
                if (executionListName != null && repositoryId != 0) {
                    String key = CommonUtils.localify(repositoryId,
                                                      executionListName,
                                                      ArgModelHelper.getLocalValue(param));
                    if (context.getProperty(key) == null) {
                        parametersList.add(getNotResolvedMessage(ArgModelHelper.getLocalValue(param)));
                    }
                }
                else {
                    parametersList.add(impossibleResolveErrorMessage());
                }
            }
        }
        return parametersList;
    }


    static String getPackageArgumentModifier(DataProcessContext context) {
        String packageArgumentModifier = context.getProperty(DataProcessContext.PACKAGE_ARGUMENT_MODIFIER);
        if (!"".equals(packageArgumentModifier) && packageArgumentModifier != null) {
            packageArgumentModifier = packageArgumentModifier + ".";
        }
        if (packageArgumentModifier == null) {
            packageArgumentModifier = PACKAGE_ARGUMENT_MODIFIER_ERROR + ".";
        }
        return packageArgumentModifier;
    }


    public static String getNotResolvedMessage(String value) {
        return String.format("Le paramètre '%s' n'est pas configuré.", value);
    }


    private String impossibleResolveErrorMessage() {
        return String.format("La résolution d'un argument local (%s) est impossible car le traitement "
                             + "est utilisé en dehors d'un repository et/ou d'une liste d'exécution",
                             argModel.getValue());
    }


    public static Comparator<Argument> getPositionComparator() {
        return new ArgumentPositionComparator();
    }


    private static class ArgumentPositionComparator implements Comparator<Argument> {
        public int compare(Argument arg1, Argument arg2) {
            if (arg1.getPosition() == arg2.getPosition()) {
                return 0;
            }
            else if (arg1.getPosition() > arg2.getPosition()) {
                return 1;
            }
            else {
                return -1;
            }
        }
    }
}
