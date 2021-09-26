package bowling.domain.frame;

import bowling.domain.score.BaseScore;
import bowling.domain.score.NormalScore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NormalFrame extends BaseFrame {

    private static final int FIRST_IDX = 1;

    private static final int LAST = 9;

    private final int index;

    private NormalScore score;

    private Frame nextFrame;

    private NormalFrame(int index, NormalScore score, int trial, int totalScore, Frame prevFrame, Frame nextFrame) {
        super(trial, totalScore, prevFrame);
        this.index = index;
        this.score = score;
        this.nextFrame = nextFrame;
    }

    public static NormalFrame start(int score) {
        return of(FIRST_IDX, NormalScore.first(score), FIRST_TRIAL);
    }

    protected static NormalFrame of(int index, NormalScore score, int trial) {
        return new NormalFrame(index, score, trial, -1, null, null);
    }

    protected static NormalFrame of(int index, NormalScore score, int trial, int totalScore) {
        return new NormalFrame(index, score, trial, totalScore, null, null);
    }

    protected static NormalFrame of(int index, NormalScore score, int trial, BaseFrame prevFrame, BaseFrame nextFrame) {
        return new NormalFrame(index, score, trial, -1, prevFrame, nextFrame);
    }

    public BaseScore score() {
        return score;
    }

    @Override
    public int nextIdx() {
        if (isNowFirstTry() && !this.score.isStrike()) {
            return index;
        }
        return index + 1;
    }

    @Override
    public List<Integer> getAllScores() {
        return score.getAll();
    }

    protected void calculateWith(int baseScore) {
        if (endsWithNeitherSpareNorStrike()) {
            setTotalScore(baseScore + score.getFirst() + score.getSecond());
        }
        calculateWhenSpareOrStrike(baseScore);
    }

    private void calculateWhenSpareOrStrike(int baseScore) {
        if (nextFrame == null) {
            return;
        }

        calculateTotalScoreIfSpare(baseScore);
        calculateTotalScoreIfStrike(baseScore);
    }

    private void calculateTotalScoreIfSpare(int baseScore) {
        if (!score.isSpare()) {
            return;
        }
        setTotalScore(baseScore + nextFrame.addWithFirstScore(score.sum()));
    }

    private void calculateTotalScoreIfStrike(int baseScore) {
        if (!score.isStrike()) {
            return;
        }
        calculateTotalScoreIfNextScoresMoreThanTwo(baseScore, nextFramesScores());
    }

    private void calculateTotalScoreIfNextScoresMoreThanTwo(int baseScore, List<Integer> nextScores) {
        if (nextScores.size() > 1) {
            setTotalScore(baseScore + score.getFirst() + nextScores.get(0) + nextScores.get(1));
        }
    }

    private List<Integer> nextFramesScores() {

        List<Integer> nextFrameAllScores = nextFrame.getAllScores();

        List<Integer> nextNextFrameAllScores =
                Optional.ofNullable(this.nextFrame.getNextFrame()).map(Frame::getAllScores).orElse(new ArrayList<>());

        return Stream.concat(nextFrameAllScores.stream(), nextNextFrameAllScores.stream())
                .filter(score -> score != NONE_SCORE)
                .collect(Collectors.toList());
    }

    private boolean endsWithNeitherSpareNorStrike() {
        return !score.isSpare() && !score.isStrike() && score.isDone();
    }

    @Override
    public boolean isLast() {
        if (index == LAST) {
            return score.isStrike() || isNowSecondTry();
        }
        return index > LAST;
    }

    @Override
    public int addWithFirstScore(int score) {
        return this.score.getFirst() + score;
    }

    @Override
    public Frame getNextFrame() {
        return nextFrame;
    }

    @Override
    public BaseFrame bowl(int score) {
        if (isNowFirstTry() && !this.score.isStrike()) {
            return bowlSecondTry(score);
        }
        return bowlFirstTry(index + 1, score);
    }

    private BaseFrame bowlFirstTry(int index, int score) {
        if (index > LAST) {
            BaseFrame nextFrame = FinalFrame.start(score, this);
            this.nextFrame = nextFrame;
            return nextFrame;
        }
        BaseFrame nextFrame = of(index, NormalScore.first(score), FIRST_TRIAL, this, null);
        this.nextFrame = nextFrame;
        return nextFrame;
    }

    private BaseFrame bowlSecondTry(int score) {
        this.score = this.score.second(score);
        increaseTrial();
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NormalFrame that = (NormalFrame) o;
        return index == that.index && Objects.equals(score, that.score) && Objects.equals(nextFrame, that.nextFrame);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, score, nextFrame);
    }
}
