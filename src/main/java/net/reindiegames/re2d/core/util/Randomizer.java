package net.reindiegames.re2d.core.util;

import java.util.Random;

public class Randomizer {
    public final Random random;

    public Randomizer() {
        this.random = new Random();
    }

    public Randomizer(long seed) {
        this.random = new Random(seed);
    }

    public <T> T pick(T[] items, double[] chances, T filler, float fillerChance, boolean copy, boolean normalize) {
        if (items.length != chances.length) throw new IllegalArgumentException("The two Arrays differ in length!");

        double[] work;
        if (copy) {
            work = new double[chances.length];
            System.arraycopy(chances, 0, work, 0, chances.length);
        } else {
            work = chances;
        }

        if (normalize) {
            double sum = MathUtil.sum(work);
            sum += fillerChance;

            MathUtil.multiply(work, 1.0D / sum);
            fillerChance *= (1.0D / sum);
        }

        final double[] lower = new double[work.length];
        final double[] upper = new double[work.length];

        double last = 0.0D;
        for (int i = 0; i < work.length; i++) {
            lower[i] = last;
            upper[i] = last + work[i];
            last = upper[i];
        }
        last += fillerChance;

        float pick = (float) (random.nextFloat() * last);
        for (int i = 0; i < work.length; i++) {
            if (pick >= lower[i] && pick <= upper[i]) {
                return items[i];
            }
        }

        return filler;
    }
}
