package com.graphhopper.routing;

import com.graphhopper.routing.util.FastestWeighting;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.storage.BaseGraph;
import com.graphhopper.storage.Graph;
import com.graphhopper.routing.querygraph.QueryGraph;
import com.graphhopper.util.EdgeIterator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static com.graphhopper.routing.EdgeRestrictions.noRestrictions;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FlexiblePathCalculatorTest {

    @Test
    public void testCalcPathsReturnsNonEmptyList() {
        // Mock du graph de base
        Graph mockGraph = mock(Graph.class);

        // QueryGraph.wrap() transforme le graph en QueryGraph
        QueryGraph queryGraph = QueryGraph.create(mockGraph);

        // Mock du RoutingAlgorithmFactory
        RoutingAlgorithm mockAlgo = mock(RoutingAlgorithm.class);
        RoutingAlgorithmFactory factory = mock(RoutingAlgorithmFactory.class);
        when(factory.createAlgo(any(), any(), any())).thenReturn(mockAlgo);

        // Mock du weighting
        Weighting weighting = mock(Weighting.class);

        // Options algorithme
        AlgorithmOptions opts = AlgorithmOptions.start().build();

        // Mock du Path
        Path mockPath = mock(Path.class);
        when(mockAlgo.calcPaths(anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(mockPath));

        // Création de notre calculator
        FlexiblePathCalculator calc = new FlexiblePathCalculator(queryGraph, factory, weighting, opts);

        // Appel
        List<Path> result = calc.calcPaths(0, 5, noRestrictions());

        // Vérifications
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testGetVisitedNodes() {
        Graph mockGraph = mock(Graph.class);
        QueryGraph qg = QueryGraph.create(mockGraph);

        RoutingAlgorithm mockAlgo = mock(RoutingAlgorithm.class);
        when(mockAlgo.getVisitedNodes()).thenReturn(42);

        RoutingAlgorithmFactory factory = mock(RoutingAlgorithmFactory.class);
        when(factory.createAlgo(any(), any(), any())).thenReturn(mockAlgo);

        FlexiblePathCalculator calc =
                new FlexiblePathCalculator(qg, factory, mock(Weighting.class), AlgorithmOptions.start().build());

        calc.calcPaths(1, 2, noRestrictions());
        assertEquals(42, calc.getVisitedNodes());
    }
}

