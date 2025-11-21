package com.graphhopper.routing.weighting;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class AvoidEdgesWeightingMockitoTest {

    @Test
    void testAvoidedEdgeHasHugeWeight() {

        // --- MOCKS ---
        Weighting baseWeighting = Mockito.mock(Weighting.class);

        // simulate base weighting value
        Mockito.when(baseWeighting.calcEdgeWeight(5, false)).thenReturn(10.0);

        // test AvoidEdgesWeighting
        AvoidEdgesWeighting weighting = new AvoidEdgesWeighting(baseWeighting);
        weighting.addEdgeToAvoid(5); // edge 5 is avoided

        double w = weighting.calcEdgeWeight(5, false);

        assertTrue(w >= 1e6, "Avoided edge should have huge weight");
    }

    @Test
    void testNormalEdgeUsesBaseWeighting() {

        Weighting baseWeighting = Mockito.mock(Weighting.class);

        Mockito.when(baseWeighting.calcEdgeWeight(7, false)).thenReturn(3.5);

        AvoidEdgesWeighting weighting = new AvoidEdgesWeighting(baseWeighting);

        double w = weighting.calcEdgeWeight(7, false);

        assertEquals(3.5, w, 0.0001, "Non-avoided edge should use base weighting");
    }
}

