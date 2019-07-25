package io.github.woncz.ads.engine.be.notation;

import java.util.Arrays;
import java.util.List;

import io.github.woncz.ads.engine.be.Conjunction;
import io.github.woncz.ads.engine.be.DNF;

/**
 * @author woncz
 * @date 2019/5/7.
 */
public class DNFNotation implements Notational<DNF> {

    /**
     * 分隔符)|(
     */
    private static final String SEPARATOR =("\\" + NotationDef.CONJUNCTION_ENDED) + ("\\" + NotationDef.OR)
            + ("\\" + NotationDef.CONJUNCTION_BEGIN);

    private ConjunctionNotation conjunctionNotation = new ConjunctionNotation();

    @Override
    public String notate(DNF dnf) {
        StringBuilder sb = new StringBuilder();
        List<Conjunction> conjunctions = dnf.getConjunctions();
        conjunctions.forEach(c -> sb.append(conjunctionNotation.notate(c)).append(NotationDef.OR));
        if (conjunctions.size() > 0) {
            sb.delete(sb.length() - NotationDef.OR.length(), sb.length());
        }
        return sb.toString();
    }

    @Override
    public DNF denotate(String notation) {
        DNF dnf = new DNF();
        String ts = notation.replaceAll(" ", "");
        Arrays.asList(ts.split(SEPARATOR)).forEach(c -> {
            if (c.startsWith(NotationDef.CONJUNCTION_BEGIN) && !c.endsWith(NotationDef.CONJUNCTION_ENDED)) {
                c = c + NotationDef.CONJUNCTION_ENDED;
            }
            if (!c.startsWith(NotationDef.CONJUNCTION_BEGIN) && c.endsWith(NotationDef.CONJUNCTION_ENDED)) {
                c = NotationDef.CONJUNCTION_BEGIN + c;
            }
            dnf.or(conjunctionNotation.denotate(c));
        });
        return dnf;
    }
}
