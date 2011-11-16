package net.codjo.dataprocess.common.eventsbinder.reflect;
import java.lang.reflect.Method;
import junit.framework.Assert;
import junit.framework.TestCase;
/**
 * Classe de test de {@link GetterHelper}.
 */
public class GetterHelperTest extends TestCase {
    private TestA testA;


    @Override
    protected void setUp() throws Exception {
        testA = new TestA();
    }


    public void test_simple() throws Exception {
        Method toFind = testA.getClass().getMethod("getMethodA");
        Assert.assertEquals(toFind.invoke(testA), GetterHelper.getProperty("methodA", testA));

        try {
            GetterHelper.getProperty("mEthodA", testA);
        }
        catch (GetterHelperException ghex) {
            assertEquals(NoSuchMethodException.class, ghex.getCause().getClass());
        }
    }


    public void test_multiple() throws Exception {
        assertEquals(testA.getTestB(), GetterHelper.getProperty("testB", testA));
        assertEquals(testA.getTestB().getMethodB(), GetterHelper.getProperty("testB.methodB", testA));
        assertEquals(testA.getTestB().getTestC().getMethodC(),
                     GetterHelper.getProperty("testB.testC.methodC", testA));

        try {
            GetterHelper.getProperty("testB.testC.methodX", testA);
        }
        catch (GetterHelperException ghex) {
            assertEquals(NoSuchMethodException.class, ghex.getCause().getClass());
            assertEquals("Invalid property 'testB.testC[.methodX]'", ghex.getMessage());
        }

        try {
            GetterHelper.getProperty("testB.testX.methodC", testA);
        }
        catch (GetterHelperException ghex) {
            assertEquals(NoSuchMethodException.class, ghex.getCause().getClass());
            assertEquals("Invalid property 'testB[.testX.methodC]'", ghex.getMessage());
        }
    }


    public static class TestA {
        private TestB testB = new TestB();


        public String getMethodA() {
            return "test";
        }


        public TestB getTestB() {
            return testB;
        }
    }

    public static class TestB {
        private TestC testC = new TestC();
        private TestC testC2 = new TestC();

        private TestC[] testCs = new TestC[]{testC, testC2};


        public String getMethodB() {
            return "test2";
        }


        public TestC getTestC() {
            return testC;
        }


        public TestC getTestC2() {
            return testC;
        }


        public TestC[] getTestCs() {
            return testCs;
        }
    }

    public static class TestC {
        public String getMethodC() {
            return "test3";
        }
    }
}
