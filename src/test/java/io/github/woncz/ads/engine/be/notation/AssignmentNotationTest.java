package io.github.woncz.ads.engine.be.notation;

import org.junit.Assert;
import org.junit.Test;

import io.github.woncz.ads.engine.be.Assignment;


/**
 * @author woncz
 * @date 2019/5/7.
 */
public class AssignmentNotationTest {

    @Test
    public void testNotate() {
        Assignment singleAssignment = new Assignment("location", true, "北京");
        Assert.assertEquals("location∈{北京}", new AssignmentNotation().notate(singleAssignment));

        Assignment multiAssignment = new Assignment("location", true, "北京", "上海", "深圳");
        Assert.assertEquals("location∈{北京|上海|深圳}", new AssignmentNotation().notate(multiAssignment));

        Assignment exclusiveAssignment = new Assignment("location", false, "北京", "上海");
        Assert.assertEquals("location∉{北京|上海}", new AssignmentNotation().notate(exclusiveAssignment));
    }

    @Test
    public void testDenotate() {
        String s1 = "location∈{北京}";
        Assignment assignment = new AssignmentNotation().denotate(s1);

        Assert.assertEquals(assignment.getAttribute(), "location");
        Assert.assertEquals(assignment.getBelong(), Boolean.TRUE);
        Assert.assertEquals(assignment.getMultiValues().get(0), "北京");
    }

}
