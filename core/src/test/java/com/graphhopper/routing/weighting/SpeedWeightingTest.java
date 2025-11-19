package com.graphhopper.routing.weighting;

import com.graphhopper.routing.ev.BooleanEncodedValue;
import com.graphhopper.routing.ev.DecimalEncodedValue;
import com.graphhopper.routing.ev.Speed;
import com.graphhopper.util.EdgeIteratorState;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SpeedWeighting using Mockito to isolate dependencies.
 */
public class SpeedWeightingTest {

    private DecimalEncodedValue speedEnc;
    private BooleanEncodedValue accessEnc;
    private EdgeIteratorState edge;
    private SpeedWeighting weighting;

    @BeforeEach
    public void setup() {
        speedEnc = mock(DecimalEncodedValue.class);
        accessEnc = mock(BooleanEncodedValue.class);
        edge = mock(EdgeIteratorState.class);

        // Simule access autorisé
        when(accessEnc.getBool(edge, false)).thenReturn(true);

        // SpeedWeighting a besoin d’un *Speed*, mocké proprement :
        Speed speedEV = mock(Speed.class);
        when(speedEV.getDecimal(false)).thenReturn(speedEnc);
        when(speedEV.getAccess()).thenReturn(accessEnc);

        // construction de SpeedWeighting
        weighting = new SpeedWeighting(speedEV, 1);  // 1 = default minimum speed
    }

    @Test
    public void testWeightFormula() {
        // speed = 50 km/h
        when(speedEnc.getDecimal(edge, false)).thenReturn(50.0);

        double w = weighting.calcEdgeWeight(edge, false);

        // SpeedWeighting = distance(=1m) / speed
        assertEquals(1.0 / 50.0, w, 1e-9);
    }

    @Test
    public void testZeroSpeedMeansInfiniteWeight() {
        when(speedEnc.getDecimal(edge, false)).thenReturn(0.0);

        double w = weighting.calcEdgeWeight(edge, false);

        assertTrue(Double.isInfinite(w));
    }

    @Test
    public void testEdgeNotAccessible() {
        when(accessEnc.getBool(edge, false)).thenReturn(false);

        double w = weighting.calcEdgeWeight(edge, false);

        assertTrue(Double.isInfinite(w), "Weight must be infinite when access is denied");
    }
}

