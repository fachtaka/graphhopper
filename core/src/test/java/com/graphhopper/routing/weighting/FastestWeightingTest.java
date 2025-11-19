package com.graphhopper.routing.weighting;

import com.graphhopper.routing.ev.Speed;
import com.graphhopper.storage.IntsRef;
import com.graphhopper.util.EdgeIteratorState;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class FastestWeightingTest {

    @Test
    void testGetMinWeight() {
        // GIVEN
        var speedEnc = Mockito.mock(Speed.class);
        var weighting = new FastestWeighting(speedEnc);
        var edge = Mockito.mock(EdgeIteratorState.class);
        var ref = Mockito.mock(IntsRef.class);

        Mockito.when(speedEnc.getDouble(ref)).thenReturn(50.0); // km/h

        // WHEN
        double weight = weighting.getMinWeight(edge);

        // THEN
        assertTrue(weight > 0, "Weight must be positive");
    }

    @Test
    void testZeroSpeedReturnsInfinity() {
        // GIVEN
        var speedEnc = Mockito.mock(Speed.class);
        var weighting = new FastestWeighting(speedEnc);
        var ref = Mockito.mock(IntsRef.class);
        var edge = Mockito.mock(EdgeIteratorState.class);

        Mockito.when(speedEnc.getDouble(ref)).thenReturn(0.0);

        // WHEN
        double weight = weighting.getMinWeight(edge);

        // THEN
        assertEquals(Double.POSITIVE_INFINITY, weight);
    }
}

