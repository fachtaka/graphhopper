package com.graphhopper.routing.weighting;

import com.graphhopper.routing.ev.DecimalEncodedValue;
import com.graphhopper.storage.TurnCostStorage;
import com.graphhopper.util.EdgeIterator;
import com.graphhopper.util.EdgeIteratorState;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SpeedWeightingTest {

    // -------------------------------------------------------------
    // BASIC EDGE WEIGHT TESTS
    // -------------------------------------------------------------

    @Test
    public void testCalcEdgeWeightForward() {
        DecimalEncodedValue speedEnc = mock(DecimalEncodedValue.class);
        EdgeIteratorState edge = mock(EdgeIteratorState.class);

        when(edge.get(speedEnc)).thenReturn(60.0);
        when(edge.getDistance()).thenReturn(120.0);

        SpeedWeighting weighting = new SpeedWeighting(speedEnc);

        double weight = weighting.calcEdgeWeight(edge, false);
        assertEquals(120.0 / 60.0, weight, 1e-9);
    }

    @Test
    public void testCalcEdgeWeightReverse() {
        DecimalEncodedValue speedEnc = mock(DecimalEncodedValue.class);
        EdgeIteratorState edge = mock(EdgeIteratorState.class);

        when(edge.getReverse(speedEnc)).thenReturn(30.0);
        when(edge.getDistance()).thenReturn(90.0);

        SpeedWeighting weighting = new SpeedWeighting(speedEnc);

        double weight = weighting.calcEdgeWeight(edge, true);
        assertEquals(90.0 / 30.0, weight, 1e-9);
    }

    @Test
    public void testZeroSpeedForwardGivesInfiniteWeight() {
        DecimalEncodedValue speedEnc = mock(DecimalEncodedValue.class);
        EdgeIteratorState edge = mock(EdgeIteratorState.class);

        when(edge.get(speedEnc)).thenReturn(0.0);

        SpeedWeighting weighting = new SpeedWeighting(speedEnc);

        assertTrue(Double.isInfinite(weighting.calcEdgeWeight(edge, false)));
    }

    @Test
    public void testZeroSpeedReverseGivesInfiniteWeight() {
        DecimalEncodedValue speedEnc = mock(DecimalEncodedValue.class);
        EdgeIteratorState edge = mock(EdgeIteratorState.class);

        when(edge.getReverse(speedEnc)).thenReturn(0.0);

        SpeedWeighting weighting = new SpeedWeighting(speedEnc);

        assertTrue(Double.isInfinite(weighting.calcEdgeWeight(edge, true)));
    }

    @Test
    public void testCalcEdgeMillis() {
        DecimalEncodedValue speedEnc = mock(DecimalEncodedValue.class);
        EdgeIteratorState edge = mock(EdgeIteratorState.class);

        when(edge.get(speedEnc)).thenReturn(50.0);
        when(edge.getDistance()).thenReturn(100.0);

        SpeedWeighting weighting = new SpeedWeighting(speedEnc);

        long millis = weighting.calcEdgeMillis(edge, false);
        assertEquals(2000, millis); // 2 seconds → 2000ms
    }

    // -------------------------------------------------------------
    // TURN COSTS TESTS
    // -------------------------------------------------------------

    @Test
    public void testNoTurnCostsByDefault() {
        DecimalEncodedValue speedEnc = mock(DecimalEncodedValue.class);

        SpeedWeighting w = new SpeedWeighting(speedEnc);

        assertFalse(w.hasTurnCosts());
    }

    @Test
    public void testHasTurnCostsWhenProviderInjected() {
        DecimalEncodedValue speedEnc = mock(DecimalEncodedValue.class);
        TurnCostProvider provider = mock(TurnCostProvider.class);

        SpeedWeighting w = new SpeedWeighting(speedEnc, provider);

        assertTrue(w.hasTurnCosts());
    }

    @Test
    public void testTurnCostDelegation() {
        DecimalEncodedValue speedEnc = mock(DecimalEncodedValue.class);
        TurnCostProvider provider = mock(TurnCostProvider.class);

        when(provider.calcTurnWeight(1, 2, 3)).thenReturn(7.5);
        when(provider.calcTurnMillis(1, 2, 3)).thenReturn(7500L);

        SpeedWeighting weighting = new SpeedWeighting(speedEnc, provider);

        assertEquals(7.5, weighting.calcTurnWeight(1, 2, 3));
        assertEquals(7500L, weighting.calcTurnMillis(1, 2, 3));
    }

    // -------------------------------------------------------------
    // COMPLEX CONSTRUCTOR (with turnCostEnc + storage + uTurnCosts)
    // -------------------------------------------------------------

    @Test
    public void testUTurnCostAppliedWhenInEdgeEqualsOutEdge() {
        DecimalEncodedValue speedEnc = mock(DecimalEncodedValue.class);
        DecimalEncodedValue turnCostEnc = mock(DecimalEncodedValue.class);
        TurnCostStorage storage = mock(TurnCostStorage.class);

        // stored turn cost = 1
        when(storage.get(turnCostEnc, 1, 2, 1)).thenReturn(1.0);

        SpeedWeighting w = new SpeedWeighting(speedEnc, turnCostEnc, storage, 5.0);

        // Because inEdge == outEdge, uTurnCosts = 5 > stored(1) → result should be 5
        assertEquals(5.0, w.calcTurnWeight(1, 2, 1));
    }

    @Test
    public void testNormalTurnCostWhenEdgesDiffer() {
        DecimalEncodedValue speedEnc = mock(DecimalEncodedValue.class);
        DecimalEncodedValue turnCostEnc = mock(DecimalEncodedValue.class);
        TurnCostStorage storage = mock(TurnCostStorage.class);

        when(storage.get(turnCostEnc, 10, 2, 20)).thenReturn(3.5);

        SpeedWeighting w = new SpeedWeighting(speedEnc, turnCostEnc, storage, 10);

        assertEquals(3.5, w.calcTurnWeight(10, 2, 20));
    }

    @Test
    public void testInvalidEdgesReturnZeroTurnCost() {
        DecimalEncodedValue speedEnc = mock(DecimalEncodedValue.class);
        DecimalEncodedValue turnCostEnc = mock(DecimalEncodedValue.class);
        TurnCostStorage storage = mock(TurnCostStorage.class);

        SpeedWeighting w = new SpeedWeighting(speedEnc, turnCostEnc, storage, 4);

        // EdgeIterator.Edge.isValid(-1) → false
        assertEquals(0.0, w.calcTurnWeight(-1, 2, 1));
    }

    // -------------------------------------------------------------
    // MISC METHODS
    // -------------------------------------------------------------

    @Test
    public void testCalcMinWeightPerDistance() {
        DecimalEncodedValue speedEnc = mock(DecimalEncodedValue.class);
        when(speedEnc.getMaxStorableDecimal()).thenReturn(100.0);

        SpeedWeighting w = new SpeedWeighting(speedEnc);

        assertEquals(1.0 / 100.0, w.calcMinWeightPerDistance());
    }

    @Test
    public void testGetName() {
        DecimalEncodedValue speedEnc = mock(DecimalEncodedValue.class);
        SpeedWeighting w = new SpeedWeighting(speedEnc);

        assertEquals("speed", w.getName());
    }
}

