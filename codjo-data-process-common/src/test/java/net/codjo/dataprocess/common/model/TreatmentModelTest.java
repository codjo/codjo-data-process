package net.codjo.dataprocess.common.model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class TreatmentModelTest {
    private TreatmentModel treatmentModel;


    @Before
    public void before() {
        treatmentModel = new TreatmentModel();
    }


    @Test
    public void content() {
        treatmentModel.setId("id");
        treatmentModel.setTitle("title");
        treatmentModel.setType("type");
        treatmentModel.setComment("comment");
        treatmentModel.setResultTable(new ResultTable("T_TABLE", "selectAllPeriod"));
        treatmentModel.setTarget("net.codjo.xxx");
        treatmentModel.setGuiTarget("net.codjo.yyy(aaa)");
        ArgList arglist = new ArgList();
        arglist.setArgs(Arrays.asList(new ArgModel("name1", "value1", 1, 1),
                                      new ArgModel("name2", "value2", 2, 2),
                                      new ArgModel("name3", "value3", 3, 3)));
        treatmentModel.setArguments(arglist);

        assertThat(treatmentModel.getId(), equalTo("id"));
        assertThat(treatmentModel.getTitle(), equalTo("title"));
        assertThat(treatmentModel.getType(), equalTo("type"));
        assertThat(treatmentModel.getComment(), equalTo("comment"));
        assertThat(treatmentModel.getResultTable().getTable(), equalTo("T_TABLE"));
        assertThat(treatmentModel.getResultTable().getSelectAllHandler(), equalTo("selectAllPeriod"));
        assertThat(treatmentModel.getTarget(), equalTo("net.codjo.xxx"));
        assertThat(treatmentModel.getTargetGuiClassName(), equalTo("net.codjo.yyy"));
        assertThat(treatmentModel.getArguments().getArgs().size(), equalTo(3));
    }


    @Test
    public void isConfigurable() {
        List<String> exclude = new ArrayList<String>();
        exclude.add("$periode$");

        ArgList arglist = new ArgList();
        arglist.setArgs(Arrays.asList(new ArgModel("name1", "$value1$", 1, 1),
                                      new ArgModel("name2", "value2", 2, 2),
                                      new ArgModel("name3", "value3", 3, 3)));
        treatmentModel.setArguments(arglist);
        assertThat(treatmentModel.isConfigurable(exclude), equalTo(true));

        arglist.setArgs(Arrays.asList(new ArgModel("name1", "value1", 1, 1),
                                      new ArgModel("name2", "value2", 2, 2),
                                      new ArgModel("name3", "value3", 3, 3)));
        treatmentModel.setArguments(arglist);
        assertThat(treatmentModel.isConfigurable(exclude), equalTo(false));

        arglist.setArgs(Arrays.asList(new ArgModel("name1", "$periode$", 1, 1),
                                      new ArgModel("name2", "value2", 2, 2),
                                      new ArgModel("name3", "value3", 3, 3)));
        treatmentModel.setArguments(arglist);
        assertThat(treatmentModel.isConfigurable(exclude), equalTo(false));
    }
}
