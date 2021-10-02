package bowling.domain.frame;

import bowling.domain.score.FinalScore;
import bowling.domain.score.Score;

import java.util.List;
import java.util.Objects;

public class FinalFrame extends BaseFrame {

    private static final int IDX = 10;

    private FinalFrameStatus status;

    private FinalFrame(Score score, int trial, FinalFrameStatus status, Frame prevFrame) {
        super(trial, score, prevFrame);
        this.status = status;
    }

    protected static FinalFrame of(Score score, int trial, boolean isThirdAvailable, boolean isDone, Frame prevFrame) {
        return new FinalFrame(score, trial, FinalFrameStatus.of(isThirdAvailable, isDone), prevFrame);
    }

    protected static FinalFrame of(Score score, int trial, FinalFrameStatus status, Frame prevFrame) {
        return new FinalFrame(score, trial, status, prevFrame);
    }

    public static FinalFrame start(int score) {
        return of(FinalScore.first(score), 1, false, false, null);
    }

    public static FinalFrame start(int score, Frame prevFrame) {
        return of(FinalScore.first(score), 1, false, false, prevFrame);
    }

    @Override
    public int nextIdx() {
        return IDX;
    }

    @Override
    public List<Integer> getAllScores() {
        return score.getAll();
    }

    @Override
    public BaseFrame bowl(int score) {
        if (isNowFirstTry()) {
            return bowlSecondTry(score);
        }
        return bowlThirdTry(score);
    }

    private FinalFrame bowlSecondTry(int score) {
        this.score = this.score.accumulate(score);
        increaseTrial();
        if (isThirdAvailable()) {
            this.status = FinalFrameStatus.of(true, false);
            return this;
        }
        this.status = FinalFrameStatus.of(false, true);
        return this;
    }

    private boolean isThirdAvailable() {
        return this.score.isStrike() || this.score.isSpare();
    }

    private FinalFrame bowlThirdTry(int score) {
        this.score = this.score.accumulate(score);
        increaseTrial();
        this.status = FinalFrameStatus.of(false, true);
        return this;
    }

    @Override
    public boolean isLast() {
        return !status.isThirdAvailable() && status.isDone();
    }

    @Override
    public int addWithFirstScore(int score) {
        return this.score.getFirst() + score;
    }

    @Override
    public BaseFrame next() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FinalFrame that = (FinalFrame) o;
        return Objects.equals(score, that.score) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(score, status);
    }
}
