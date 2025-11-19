package com.graphhopper.routing.weighting;

import com.graphhopper.routing.ev.DecimalEncodedValue;
import com.graphhopper.util.EdgeIteratorState;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SpeedWeightingTest {

    @Test
    public void testCalcEdgeWeightForward() {
        DecimalEncodedValue speedEnc = mock(DecimalEncodedValue.class);
        EdgeIteratorState edge = mock(EdgeIteratorState.class);

        when(edge.get(speedEnc)).thenReturn(60.0);
        when(edge.getDistance()).thenReturn(120.0);

        SpeedWeighting weighting = new SpeedWeighting(speedEnc);

        double weight = weighting.calcEdgeWeight(edge, false);

        // 120m / 60kmh = 120 / 60 = 2s => 2 seconds = 2 units (SpeedWeighting returns distance/speed)
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
    public void testZeroSpeedGivesInfiniteWeight() {
        DecimalEncodedValue speedEnc = mock(DecimalEncodedValue.class);
        EdgeIteratorState edge = mock(EdgeIteratorState.class);

        when(edge.get(speedEnc)).thenReturn(0.0);
        when(edge.getDistance()).thenReturn(100.0);

        SpeedWeighting weighting = new SpeedWeighting(speedEnc);

        assertTrue(Double.isInfinite(weighting.calcEdgeWeight(edge, false)));
    }

    @Test
    public void testCalcEdgeMillis() {
        DecimalEncodedValue speedEnc = mock(DecimalEncodedValue.class);
        EdgeIteratorState edge = mock(EdgeIteratorState.class);

        when(edge.get(speedEnc)).thenReturn(50.0);
        when(edge.getDistance()).thenReturn(100.0);

        SpeedWeighting weighting = new SpeedWeighting(speedEnc);

        long millis = weighting.calcEdgeMillis(edge, false);

        // weight = 100 / 50 = 2 → 2s → 2000ms
        assertEquals(2000, millis);
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

    @Test
    public void testHasTurnCosts() {
        DecimalEncodedValue speedEnc = mock(DecimalEncodedValue.class);

        TurnCostProvider provider = mock(TurnCostProvider.class);
        SpeedWeighting w1 = new SpeedWeighting(speedEnc, provider);

        SpeedWeighting w2 = new SpeedWeighting(speedEnc);

        assertTrue(w1.hasTurnCosts());
        assertFalse(w2.hasTurnCosts());
    }
}

