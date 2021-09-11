package bowling.domain;

import java.util.Objects;

public class Frame {

    private final int round;

    private final Score score;

    private final boolean isSecondTry;

    public Frame(int round, Score score, boolean isSecondTry) {
        this.round = round;
        this.score = score;
        this.isSecondTry = isSecondTry;
    }

    public static Frame start(int score) {
        return of(1, Score.first(score), false);
    }

    protected static Frame of(int round, Score score, boolean isSecondTry) {
        return new Frame(round, score, isSecondTry);
    }

    private static Frame next(int round, Score score, boolean isSecondTry) {
        return new Frame(round, score, isSecondTry);
    }

    public Frame next(int score) {

        if (isSecondTry) {
            return next(round + 1, Score.first(score), false);
        }

        if (this.score.isStrike()) {
            return next(round + 1, Score.first(score), false);
        }

        return next(round, this.score.withSecond(score), true);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Frame frame = (Frame) o;
        return round == frame.round && isSecondTry == frame.isSecondTry && Objects.equals(score, frame.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(round, score, isSecondTry);
    }
}
