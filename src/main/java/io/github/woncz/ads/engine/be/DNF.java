package io.github.woncz.ads.engine.be;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.woncz.ads.engine.be.notation.DNFNotation;

/**
 * @author woncz
 * @date 2019/5/5.
 */
public class DNF {

    private List<Conjunction> conjunctions = new ArrayList<>();

    public DNF(Conjunction... conjunctions) {
        this.conjunctions.addAll(Arrays.asList(conjunctions));
    }

    public DNF(List<Conjunction> conjunctions) {
        this.conjunctions = conjunctions;
    }

    public int size() {
        return conjunctions.size();
    }

    public DNF or(Conjunction conjunction) {
        this.conjunctions.add(conjunction);
        return this;
    }

    public List<Conjunction> getConjunctions() {
        return conjunctions;
    }

    @Override
    public String toString() {
        return new DNFNotation().notate(this);
    }
}

