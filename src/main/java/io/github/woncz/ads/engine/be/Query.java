package io.github.woncz.ads.engine.be;

import java.util.List;

/**
 * @author woncz
 * @date 2019/5/5.
 */
public class Query extends Conjunction {
    public Query(Assignment... assignments) {
        super(assignments);
    }

    public Query(List<Assignment> assignments) {
        super(assignments);
    }

    public Query(Conjunction conjunction) {
        this(conjunction.getAssignments());
    }
}
