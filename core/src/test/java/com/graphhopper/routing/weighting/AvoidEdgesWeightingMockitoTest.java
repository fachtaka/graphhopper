package com.graphhopper.routing.weighting;

import com.carrotsearch.hppc.IntSet;
import com.graphhopper.coll.GHIntHashSet;
import com.graphhopper.util.EdgeIteratorState;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class AvoidEdgesWeightingMockitoTest {

    @Test
    void testAvoidedEdgeReceivesPenalty() {

        // Base weighting (mocked)
        Weighting base = Mockito.mock(Weighting.class);

        // Mock an edge
        EdgeIteratorState edge = Mockito.mock(EdgeIteratorState.class);
        Mockito.when(edge.getEdge()).thenReturn(5);

        // Base weight for the edge
        Mockito.when(base.calcEdgeWeight(edge, false)).thenReturn(10.0);

        // Create AvoidEdgesWeighting
        AvoidEdgesWeighting w = new AvoidEdgesWeighting(base);

        // Avoid edge 5
        IntSet avoided = new GHIntHashSet();
        avoided.add(5);
        w.setAvoidedEdges(avoided);

        double weight = w.calcEdgeWeight(edge, false);

        // Because edge 5 is avoided, weight = baseWeight * penaltyFactor (default = 5.0)
        assertEquals(50.0, weight, 0.0001);
    }

    @Test
    void testNormalEdgeUsesBaseWeight() {

        Weighting base = Mockito.mock(Weighting.class);

        // Mock a normal edge
        EdgeIteratorState edge = Mockito.mock(EdgeIteratorState.class);
        Mockito.when(edge.getEdge()).thenReturn(7);

        Mockito.when(base.calcEdgeWeight(edge, false)).thenReturn(3.5);

        AvoidEdgesWeighting w = new AvoidEdgesWeighting(base);

        // No avoided edges → weight = base weight
        double weight = w.calcEdgeWeight(edge, false);

        assertEquals(3.5, weight, 0.0001);
    }
}

