package io.github.woncz.ads.engine.be.notation;

import java.util.Arrays;
import java.util.List;

import io.github.woncz.ads.engine.be.Assignment;

/**
 * @author woncz
 * @date 2019/4/29.
 */
public class AssignmentNotation implements Notational<Assignment> {

    @Override
    public String notate(Assignment assignment) {
        String attribute = assignment.getAttribute();
        boolean belong = assignment.getBelong();
        List<Object> multiValues = assignment.getMultiValues();
        StringBuilder sb = new StringBuilder();
        sb.append(attribute).append(belong ? NotationDef.INCLUSIVE : NotationDef.EXCLUSIVE);
        sb.append(NotationDef.ASSIGNMENT_BEGIN);
        multiValues.forEach(v -> sb.append(v).append(NotationDef.OR));
        if (multiValues.size() > 0) {
            sb.delete(sb.length() - NotationDef.OR.length(), sb.length());
        }
        sb.append(NotationDef.ASSIGNMENT_ENDED);
        return sb.toString();
    }

    @Override
    public Assignment denotate(String notation) {
        int inclusiveIdx = notation.indexOf(NotationDef.INCLUSIVE);
        if (inclusiveIdx != -1) {
            String attribute = notation.substring(0, inclusiveIdx).trim();
            String values = notation.substring(inclusiveIdx + NotationDef.INCLUSIVE.length()).trim();
            return this.build(attribute, true, values);
        }

        int exclusiveIdx = notation.indexOf(NotationDef.EXCLUSIVE);
        if (exclusiveIdx != -1) {
            String attribute = notation.substring(0, exclusiveIdx).trim();
            String values = notation.substring(exclusiveIdx + NotationDef.INCLUSIVE.length()).trim();
            return this.build(attribute, false, values);
        }

        throw new IllegalArgumentException("bad notation for assignment : " + notation);
    }

    protected Assignment build(String attribute, boolean belong, String values) {
        List<Object> multiValues = Arrays.asList(denotateMultiValues(values));
        return new Assignment(attribute, belong, multiValues);
    }

    protected Object[] denotateMultiValues(String values) {
        values = values.replace(NotationDef.ASSIGNMENT_BEGIN, "").replace(NotationDef.ASSIGNMENT_ENDED, "");
        String[] ts = values.split("\\" + NotationDef.OR);
        Object[] multiValues = new Object[ts.length];
        for (int i = 0; i < multiValues.length; i++) {
            multiValues[i] = ts[i];
        }
        return multiValues;
    }
}
