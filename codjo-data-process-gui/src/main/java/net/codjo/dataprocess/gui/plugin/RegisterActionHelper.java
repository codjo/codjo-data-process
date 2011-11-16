package net.codjo.dataprocess.gui.plugin;
import net.codjo.dataprocess.gui.launcher.NewExecutionListLauncherAction;
import net.codjo.dataprocess.gui.param.ExecutionListParamAction;
import net.codjo.dataprocess.gui.param.ExecutionListPriorityAction;
import net.codjo.dataprocess.gui.repository.RepositoryAction;
import net.codjo.dataprocess.gui.tables.ContextTableAction;
import net.codjo.dataprocess.gui.tables.DependencyTableAction;
import net.codjo.dataprocess.gui.tables.ExecutionListStatusTableAction;
import net.codjo.dataprocess.gui.tables.ExecutionListTableAction;
import net.codjo.dataprocess.gui.tables.FamilyTableAction;
import net.codjo.dataprocess.gui.tables.PmDpConfigTableAction;
import net.codjo.dataprocess.gui.tables.RepositoryContentTableAction;
import net.codjo.dataprocess.gui.tables.RepositoryTableAction;
import net.codjo.dataprocess.gui.tables.TreatmentStatusTableAction;
import net.codjo.dataprocess.gui.tables.TreatmentTableAction;
import net.codjo.dataprocess.gui.tables.UserListTableAction;
import net.codjo.dataprocess.gui.treatmenthelper.TreatmentHelperGuiAction;
import net.codjo.dataprocess.gui.util.ClearMapServerAction;
import net.codjo.dataprocess.gui.util.LoginTracker.LoginTrackerGuiAction;
import net.codjo.dataprocess.gui.util.ReinitialiseUserImportAction;
import net.codjo.dataprocess.gui.util.action.RegisterActionProvider;
import net.codjo.dataprocess.gui.util.fexplorer.FExplorerAction;
import net.codjo.dataprocess.gui.util.sqleditor.SqlEditorAction;
import net.codjo.dataprocess.gui.util.sqleditor.loglist.DirectSqlLogAction;
import net.codjo.dataprocess.gui.util.tableexplorer.TableExploratorGuiAction;
/**
 *
 */
class RegisterActionHelper {
    private RegisterActionHelper() {
    }


    static void registerAction(RegisterActionProvider register) {
        register.register(ExecutionListParamAction.class);
        register.register(ExecutionListPriorityAction.class);
        register.register(net.codjo.dataprocess.gui.dependency.DependencyAction.class);
        register.register(RepositoryAction.class);
        register.register(net.codjo.dataprocess.gui.family.FamilyAction.class);
        register.register(NewExecutionListLauncherAction.class);
        register.register(TreatmentHelperGuiAction.class);

        register.register(ContextTableAction.class);
        register.register(FamilyTableAction.class);
        register.register(RepositoryTableAction.class);
        register.register(ExecutionListTableAction.class);
        register.register(ExecutionListStatusTableAction.class);
        register.register(TreatmentTableAction.class);
        register.register(DependencyTableAction.class);
        register.register(RepositoryContentTableAction.class);
        register.register(TreatmentStatusTableAction.class);

        register.register(UserListTableAction.class);

        register.register(ClearMapServerAction.class);
        register.register(ReinitialiseUserImportAction.class);
        register.register(DirectSqlLogAction.class);
        register.register(SqlEditorAction.class);
        register.register(FExplorerAction.class);
        register.register(TableExploratorGuiAction.class);
        register.register(PmDpConfigTableAction.class);
        register.register(LoginTrackerGuiAction.class);
    }
}
