package com.graphhopper.routing.weighting;

import com.graphhopper.routing.ev.Distance;
import com.graphhopper.routing.ev.DecimalEncodedValue;
import com.graphhopper.routing.ev.EncodingManager;
import com.graphhopper.routing.ev.VehicleEncodedValues;
import com.graphhopper.storage.BaseGraph;
import com.graphhopper.storage.GraphBuilder;
import com.graphhopper.util.EdgeIteratorState;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ShortestWeightingTest {

    @Test
    public void testRealDistanceWeight() {
        EncodingManager em = EncodingManager.create("car");
        DecimalEncodedValue distEnc = em.getDecimalEncodedValue(Distance.KEY);

        BaseGraph graph = new GraphBuilder(em).create();
        EdgeIteratorState edge = graph.edge(0, 1).set(distEnc, 150.0);

        Weighting weighting = new ShortestWeighting(em.getEncoder("car"));

        double weight = weighting.calcEdgeWeight(edge, false);

        assertEquals(150.0, weight, 0.01, "Weight should equal the distance");
    }

    @Test
    public void testLongerEdgeHasHigherWeight() {
        EncodingManager em = EncodingManager.create("car");
        DecimalEncodedValue distEnc = em.getDecimalEncodedValue(Distance.KEY);

        BaseGraph graph = new GraphBuilder(em).create();
        EdgeIteratorState shortEdge = graph.edge(0, 1).set(distEnc, 50.0);
        EdgeIteratorState longEdge = graph.edge(1, 2).set(distEnc, 200.0);

        Weighting weighting = new ShortestWeighting(em.getEncoder("car"));

        double w1 = weighting.calcEdgeWeight(shortEdge, false);
        double w2 = weighting.calcEdgeWeight(longEdge, false);

        assertTrue(w2 > w1, "Longer edge should have higher weight");
    }

    @Test
    public void testWithMockito() {
        // Mock d'une arête
        EdgeIteratorState edge = mock(EdgeIteratorState.class);

        // Encodage véhicule + manager
        VehicleEncodedValues vev = VehicleEncodedValues.car();
        EncodingManager em = EncodingManager.start().add(vev).build();

        DecimalEncodedValue distEnc = em.getDecimalEncodedValue(Distance.KEY);

        // On simule une distance de 120m
        when(edge.get(distEnc)).thenReturn(120.0);

        Weighting weighting = new ShortestWeighting(vev);

        double weight = weighting.calcEdgeWeight(edge, false);

        assertEquals(120.0, weight, 0.01);
        verify(edge, times(1)).get(distEnc);
    }
}

