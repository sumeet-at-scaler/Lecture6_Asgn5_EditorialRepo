import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testNodeClassExists() {
        try {
            Class.forName("Node");
        } catch (ClassNotFoundException e) {
            fail("Node class not found");
        }
    }

    @Test
    public void testTreeSizeCalculatorClassExists() {
        try {
            Class.forName("TreeSizeCalculator");
        } catch (ClassNotFoundException e) {
            fail("TreeSizeCalculator class not found");
        }
    }

    @Test
    public void testLeftFieldInNode() throws ClassNotFoundException, NoSuchFieldException {
        Field field = Class.forName("Node").getDeclaredField("left");
        assertNotNull(field, "left field should be there");
        assertTrue(field.getType().equals(Class.forName("Node")), "left field should be of type Node");
    }

    @Test
    public void testRightFieldInNode() throws ClassNotFoundException, NoSuchFieldException {
        Field field = Class.forName("Node").getDeclaredField("right");
        assertNotNull(field, "right field should be there");
        assertTrue(field.getType().equals(Class.forName("Node")), "right field should be of type Node");
    }

    @Test
    public void testdataFieldInNode() throws ClassNotFoundException, NoSuchFieldException {
        Field field = Class.forName("Node").getDeclaredField("data");
        assertNotNull(field, "data field should be there");
        assertTrue(field.getType().equals(int.class), "data field should be of type int");
    }

    @Test
    public void testTreeSizeCalculatorCallableInterface() throws Exception {
        assertTrue(Callable.class.isAssignableFrom(Class.forName("TreeSizeCalculator")));
    }

    @Test
    public void testTreeSizeCalculatorConstructor() throws Exception {
        Constructor<?> constructor = Class.forName("TreeSizeCalculator").
                                                getDeclaredConstructor(Class.forName("Node"), ExecutorService.class);
        assertNotNull(constructor);
    }

    @Test
    public void testCalculateTreeSize() throws Exception {
        // Get Node class via reflection
        Class<?> nodeClass = Class.forName("Node");

        // Get Node constructor via reflection
        Constructor<?> nodeConstructor = nodeClass.getConstructor(int.class);

        Object rootNode = nodeConstructor.newInstance(1);

        Object leftNode1 = nodeConstructor.newInstance(2);
        Object rightNode1 = nodeConstructor.newInstance(3);
        setNodeFields(rootNode, leftNode1, rightNode1);

        Object leftNode2 = nodeConstructor.newInstance(4);
        Object rightNode2 = nodeConstructor.newInstance(5);
        setNodeFields(leftNode1, leftNode2, rightNode2);

        Object leftNode3 = nodeConstructor.newInstance(6);
        setNodeFields(leftNode2, leftNode3, null);

        // Create a new ExecutorService
        ExecutorService exs = Executors.newFixedThreadPool(20);

        // Create TreeSizeCalculator via reflection
        Class<?> calculatorClass = Class.forName("TreeSizeCalculator");
        Constructor<?> calculatorConstructor = calculatorClass.getConstructor(nodeClass, ExecutorService.class);
        Object calculator = calculatorConstructor.newInstance(rootNode, exs);

        // Invoke call method on TreeSizeCalculator via reflection
        Method callMethod = calculatorClass.getMethod("call");
        int result = (int) callMethod.invoke(calculator);

        // Check that the tree size is calculated correctly
        assertEquals(6, result);

        // Shutdown the ExecutorService
        exs.shutdown();
    }

    private void setNodeFields(Object node, Object left, Object right) throws Exception {
        Field leftField = node.getClass().getField("left");
        leftField.set(node, left);

        Field rightField = node.getClass().getField("right");
        rightField.set(node, right);
    }

}