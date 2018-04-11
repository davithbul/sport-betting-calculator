package com.el.betting.sdk;

import com.el.betting.sdk.v2.Team;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Ignore
public class TeamEqualityTest {

    @Test
    public void teamNamesBlank() {
        Team team1 = new Team(null, null);
        Team team2 = new Team(null, null);
        assertFalse(team1.equals(team2));
        assertFalse(team2.equals(team1));
    }

    @Test
    public void teamNameBlank() {
        Team team1 = new Team(null, null);
        Team team2 = new Team("Manchester United", "Manchester United", Team.Side.HOME);
        assertFalse(team1.equals(team2));
        assertFalse(team2.equals(team1));
    }

    @Test
    public void teamEqualNames() {
        Team team1 = new Team("Manchester United", null);
        Team team2 = new Team("Manchester United", null);
        assertTrue(team1.equals(team2));
        assertTrue(team2.equals(team1));
    }

    @Test
    public void teamEqualShortNames() {
        Team team1 = new Team(null, "Man. United", Team.Side.HOME);
        Team team2 = new Team(null, "Man. United", Team.Side.HOME);
        assertTrue(team1.equals(team2));
        assertTrue(team2.equals(team1));
    }

    @Test
    public void teamDifferentSides() {
        Team team1 = new Team(null, "Man. United", Team.Side.HOME);
        Team team2 = new Team(null, "Man. United", Team.Side.AWAY);
        assertFalse(team1.equals(team2));
        assertFalse(team2.equals(team1));
    }

    @Test
    public void teamEqualNameContainsShortName() {
        Team team1 = new Team("Manchester United", null);
        Team team2 = new Team(null, "Man. United", null);
        assertTrue(team1.equals(team2));
        assertTrue(team2.equals(team1));
    }

    @Test
    public void teamEqualNameToShortName() {
        Team team1 = new Team("Manchester United", null);
        Team team2 = new Team(null, "Man. United", null);
        assertTrue(team1.equals(team2));
        assertTrue(team2.equals(team1));
    }

    @Test
    public void teamNamesContains() {
        Team team1 = new Team("Manchester United", null);
        Team team2 = new Team("Man. United", null);
        assertTrue(team1.equals(team2));
        assertTrue(team2.equals(team1));
    }

    @Test
    public void teamShortNamesContains() {
        Team team1 = new Team(null, "Manchester United", null);
        Team team2 = new Team(null, "Man. United", null);
        assertTrue(team1.equals(team2));
        assertTrue(team2.equals(team1));
    }

    @Test
    public void teamPathShortNamesContainsCorrect() {
        Team team1 = new Team(null, "Manchester United", null);
        Team team2 = new Team(null, "Man. Unt.", null);
        assertTrue(team1.equals(team2));
        assertTrue(team2.equals(team1));
    }


    @Test
    public void teamPathShortNamesContains() {
        Team team1 = new Team(null, "Manchester United", null);
        Team team2 = new Team(null, "Man. Unit.", null);
        assertTrue(team1.equals(team2));
        assertTrue(team2.equals(team1));
    }

    @Test
    public void teamOnePartShortName() {
        Team team1 = new Team(null, "Manchester United", null);
        Team team2 = new Team(null, "Man", null);
        assertTrue(team1.equals(team2));
        assertTrue(team2.equals(team1));
    }


    @Test
    public void teamWrongShortNames() {
        Team team1 = new Team(null, "Man. United", null);
        Team team2 = new Team(null, "Manchester", null);
        assertTrue(team1.equals(team2));
        assertTrue(team2.equals(team1));
    }


    @Test
    public void teamPartialShortName() {
        Team team1 = new Team("Man. United", null);
        Team team2 = new Team(null, "Manchester", null);
        assertTrue(team1.equals(team2));
        assertTrue(team2.equals(team1));
    }

    @Test
    public void teamRightNameWrongShortName() {
        Team team1 = new Team("Manchester United", "Man. city", null);
        Team team2 = new Team("Manchester United", "Manchester", null);
        assertTrue(team1.equals(team2));
        assertTrue(team2.equals(team1));
    }

    @Test
    public void teamWithEmptyParts() {
        Team team1 = new Team("", " ", null);
        Team team2 = new Team("Manchester United", "Manchester", null);
        assertFalse(team1.equals(team2));
        assertFalse(team2.equals(team1));
    }


    @Test
    public void teamWithWrongPartsDifferentPlace() {
        Team team1 = new Team("Manchester United", " ", null);
        Team team2 = new Team("", "Manchester city", null);
        assertFalse(team1.equals(team2));
        assertFalse(team2.equals(team1));
    }


    @Test
    public void teamWithWrongParts() {
        Team team1 = new Team("Manchester United", " ", null);
        Team team2 = new Team("Manchester city", "", null);
        assertFalse(team1.equals(team2));
        assertFalse(team2.equals(team1));
    }

    @Test
    public void teamNameWithDots() {
        Team team1 = new Team(null, "Southampto", null);
        Team team2 = new Team("Southampton", "Southampton", null);
        assertTrue(team1.equals(team2));
        assertTrue(team2.equals(team1));
    }

    @Test
    public void teamNameWithOnePart() {
        Team team1 = new Team(null, "Hull City", null);
        Team team2 = new Team("Hull", "Hull", null);
        assertTrue(team1.equals(team2));
        assertTrue(team2.equals(team1));
    }


    @Test
    public void teamNameWithOnePartWithoutOneName() {
        Team team1 = new Team(null, "Wigan Athletic", null);
        Team team2 = new Team("Wigan", "Wigan", null);
        assertTrue(team1.equals(team2));
        assertTrue(team2.equals(team1));
    }

    @Test
    public void teamNameKnownAbbreviation() {
        Team team1 = new Team(null, "Sutton Utd", null);
        Team team2 = new Team("Sutton United", "Sutton United", null);
        assertTrue(team1.equals(team2));
        assertTrue(team2.equals(team1));
    }

    @Test
    public void teamNameWithParentheses() {
        Team team1 = new Team(null, "FC Chelsea (U21)", null);
        Team team2 = new Team("Chelsea U21", "Chelsea U21", null);
        assertTrue(team1.equals(team2));
        assertTrue(team2.equals(team1));
    }

    @Test
    public void teamNamesContainsEachOther() {
        Team team1 = new Team(null, "FC Halifax Town", null);
        Team team2 = new Team("Halifax", "Halifax", null);
        assertTrue(team1.equals(team2));
        assertTrue(team2.equals(team1));
    }

    @Test
    public void teamNamesContainsLine() {
        Team team1 = new Team(null, "AS Saint-Etienne", null);
        Team team2 = new Team("St Etienne", "St Etienne", null);
        assertTrue(team1.equals(team2));
        assertTrue(team2.equals(team1));
    }

    @Test
    public void peopleNamesEquality() {
        Team team1 = new Team("Gasquet, Richard", null);
        Team team2 = new Team("Richard Gasquet", null);
        assertTrue(team1.equals(team2));
        assertTrue(team2.equals(team1));
    }

    @Test
    public void teamNamesMoreDetails() {
        Team team1 = new Team("Maccabi Electra Tel Avi", null);
        Team team2 = new Team("Maccabi Tel Aviv.", null);
        assertTrue(team1.equals(team2));
        assertTrue(team2.equals(team1));
    }

    @Test
    public void teamNamesWithMoreDetails() {
        Team team1 = new Team("Standard Liege", null);
        Team team2 = new Team("Standard de Liege", null);
        assertTrue(team1.equals(team2));
        assertTrue(team2.equals(team1));
    }

    @Test
    public void teamShortButWrong() {
        Team team1 = new Team("USA", null);
        Team team2 = new Team("OSA", null);
        assertFalse(team1.equals(team2));
        assertFalse(team2.equals(team1));
    }

    @Test
    public void wrongAttletics() {
        Team team1 = new Team("Attletico madrid", null);
        Team team2 = new Team("Attletic bilbao", null);
        assertFalse(team1.equals(team2));
        assertFalse(team2.equals(team1));
    }
}

