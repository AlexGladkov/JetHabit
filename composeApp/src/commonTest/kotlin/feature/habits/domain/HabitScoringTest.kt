package feature.habits.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val SHORT_STREAK_COMPLETED_DAYS: Int = 5
private const val SHORT_STREAK_TOTAL_DAYS: Int = 1_0
private const val SHORT_STREAK_SCORE: Double = 5_5.0
private const val LONG_STREAK_COMPLETED_DAYS: Int = 8
private const val LONG_STREAK_TOTAL_DAYS: Int = 2_0
private const val LONG_STREAK_SCORE: Double = 5_2.0
private const val MAXIMUM_COMPLETED_DAYS: Int = 1_0
private const val MAXIMUM_TOTAL_DAYS: Int = 1_0
private const val MAXIMUM_SCORE: Double = 1_00.0
private const val SINGLE_COMPLETED_DAY: Int = 1
private const val ZERO_TOTAL_DAYS: Int = 0
private const val NEGATIVE_TOTAL_DAYS: Int = -1
private const val ZERO_COMPLETED_DAYS: Int = 0
private const val ZERO_SCORE: Double = 0.0
private const val NOVICE_DAYS: Int = 6
private const val BEGINNER_DAYS: Int = 7
private const val INTERMEDIATE_DAYS: Int = 3_0
private const val EXPERT_DAYS: Int = 9_0
private const val MASTER_DAYS: Int = 3_65

class HabitScoringTest {
    private val scoring = HabitScoring()

    @Test
    fun calculateScoreKeepsExistingShortStreakFormula() {
        val score = scoring.calculateScore(SHORT_STREAK_COMPLETED_DAYS, SHORT_STREAK_TOTAL_DAYS)

        assertEquals(SHORT_STREAK_SCORE, score)
    }

    @Test
    fun calculateScoreAppliesLongStreakBonus() {
        val score = scoring.calculateScore(LONG_STREAK_COMPLETED_DAYS, LONG_STREAK_TOTAL_DAYS)

        assertEquals(LONG_STREAK_SCORE, score)
    }

    @Test
    fun calculateScoreIsCappedAtMaximum() {
        val score = scoring.calculateScore(MAXIMUM_COMPLETED_DAYS, MAXIMUM_TOTAL_DAYS)

        assertEquals(MAXIMUM_SCORE, score)
    }

    @Test
    fun calculateScoreReturnsZeroWhenTotalDaysIsZero() {
        val score = scoring.calculateScore(SINGLE_COMPLETED_DAY, ZERO_TOTAL_DAYS)

        assertEquals(ZERO_SCORE, score)
    }

    @Test
    fun calculateScoreReturnsZeroWhenTotalDaysIsNegative() {
        val score = scoring.calculateScore(SINGLE_COMPLETED_DAY, NEGATIVE_TOTAL_DAYS)

        assertEquals(ZERO_SCORE, score)
    }

    @Test
    fun calculateScoreDoesNotReturnNanForEmptyInput() {
        val score = scoring.calculateScore(ZERO_COMPLETED_DAYS, ZERO_TOTAL_DAYS)

        assertTrue(score.isFinite())
    }

    @Test
    fun getStreakLevelReturnsExpectedBoundaries() {
        assertEquals("Novice", scoring.getStreakLevel(NOVICE_DAYS))
        assertEquals("Beginner", scoring.getStreakLevel(BEGINNER_DAYS))
        assertEquals("Intermediate", scoring.getStreakLevel(INTERMEDIATE_DAYS))
        assertEquals("Expert", scoring.getStreakLevel(EXPERT_DAYS))
        assertEquals("Master", scoring.getStreakLevel(MASTER_DAYS))
    }
}
