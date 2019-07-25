package io.github.woncz.ads.engine.be.notation;

import org.junit.Assert;
import org.junit.Test;

import io.github.woncz.ads.engine.be.Conjunction;
import io.github.woncz.ads.engine.be.DNF;

/**
 * @author woncz
 * @date 2019/5/8.
 */
public class DNFNotationTest {

    @Test
    public void testNotate() {
        Conjunction c1 = new ConjunctionNotation().denotate("location∈{北京}∧gender∈{男}");
        Conjunction c2 = new ConjunctionNotation().denotate("location∈{深圳}∧gender∈{女}∧age∉{30|40}");

        DNF dnf = new DNF(c1, c2);

        Assert.assertEquals("(location∈{北京}∧gender∈{男})" + "|" + "(location∈{深圳}∧gender∈{女}∧age∉{30|40})", new DNFNotation().notate(dnf));
    }

    @Test
    public void testDenotate() {
        String ts = "(location∈{北京}∧gender∈{男})" + "|" + "(location∈{深圳}∧gender∈{女}∧age∉{30|40})";

        DNF dnf = new DNFNotation().denotate(ts);
        Assert.assertEquals(dnf.getConjunctions().size(), 2);
    }
}
