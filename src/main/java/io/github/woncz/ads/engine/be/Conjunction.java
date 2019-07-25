package io.github.woncz.ads.engine.be;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.woncz.ads.engine.be.notation.ConjunctionNotation;

/**
 * @author woncz
 * @date 2019/5/5.
 */
public class Conjunction {

    private List<Assignment> assignments;

    public Conjunction(Assignment... assignments) {
        this.assignments = new ArrayList<>();
        this.assignments.addAll(Arrays.asList(assignments));
    }

    public Conjunction(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public Conjunction and(Assignment assignment) {
        this.assignments.add(assignment);
        return this;
    }

    public int size() {
        return (int) assignments.stream().filter(Assignment::isInclusive).count();
    }

    public List<Key> keys() {
        List<Key> keys = new ArrayList<>();
        assignments.forEach(s -> keys.add(s.key()));
        return keys;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    @Override
    public String toString() {
        return new ConjunctionNotation().notate(this);
    }
}
