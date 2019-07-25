package io.github.woncz.ads.engine.be.notation;

import org.junit.Assert;
import org.junit.Test;

import io.github.woncz.ads.engine.be.Assignment;
import io.github.woncz.ads.engine.be.Conjunction;

/**
 * @author woncz
 * @date 2019/5/8.
 */
public class ConjunctionNotationTest {

    @Test
    public void testNotate() {
        Assignment s1 = new Assignment("location", true, "北京");
        Assignment s2 = new Assignment("age", true, "30|40");

        Conjunction conjunction = new Conjunction(s1, s2);

        Assert.assertEquals("(location∈{北京}∧age∈{30|40})", new ConjunctionNotation().notate(conjunction));
    }

    @Test
    public void testDenotate() {
        String ts = "(location∈{北京}∧age∈{30|40})";
        Conjunction conjunction = new ConjunctionNotation().denotate(ts);

        Assert.assertEquals(conjunction.getAssignments().size(), 2);

        Assert.assertEquals(conjunction.getAssignments().get(0).getAttribute(), "location");
        Assert.assertEquals(conjunction.getAssignments().get(1).getAttribute(), "age");
    }

}
