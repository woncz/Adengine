package io.github.woncz.ads.engine.be.notation;


import java.util.List;

import io.github.woncz.ads.engine.be.Assignment;
import io.github.woncz.ads.engine.be.Conjunction;

/**
 * @author woncz
 * @date 2019/5/7.
 */
public class ConjunctionNotation implements Notational<Conjunction> {

    private AssignmentNotation assignmentNotation = new AssignmentNotation();

    @Override
    public String notate(Conjunction conjunction) {
        StringBuilder sb = new StringBuilder(NotationDef.CONJUNCTION_BEGIN);
        List<Assignment> assignments = conjunction.getAssignments();
        assignments.forEach(s -> sb.append(assignmentNotation.notate(s)).append(NotationDef.AND));
        if (assignments.size() > 0) {
            sb.delete(sb.length() - NotationDef.AND.length(), sb.length());
        }
        sb.append(NotationDef.CONJUNCTION_ENDED);
        return sb.toString();
    }

    @Override
    public Conjunction denotate(String notation) {
        Conjunction conjunction = new Conjunction();
        String ts = notation.trim().replaceAll(" ", "");
        if (ts.startsWith(NotationDef.CONJUNCTION_BEGIN) && ts.endsWith(NotationDef.CONJUNCTION_ENDED)) {
            ts = ts.substring(NotationDef.CONJUNCTION_BEGIN.length(), ts.length() - NotationDef.CONJUNCTION_ENDED.length());
        }
        String[] assignments = ts.split("\\" + NotationDef.AND);
        for (String s : assignments) {
            conjunction.and(assignmentNotation.denotate(s));
        }
        return conjunction;
    }
}
