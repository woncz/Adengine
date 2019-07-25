package io.github.woncz.ads.engine.be.notation;

/**
 * 符合表示
 *
 * @author woncz
 * @date 2019/5/7.
 */
public interface Notational<T> {

    /**
     * 符号化
     *
     * @param t
     * @return
     */
    String notate(T t);

    /**
     * 去符号化
     *
     * @param notation
     * @return
     */
    T denotate(String notation);
}
