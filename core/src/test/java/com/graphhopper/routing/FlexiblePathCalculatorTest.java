package com.graphhopper.routing;

import com.graphhopper.routing.querygraph.QueryGraph;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.storage.Graph;
import com.graphhopper.util.EdgeIterator;
import com.graphhopper.util.GHUtility;
import com.graphhopper.routing.util.TraversalMode;
import com.graphhopper.util.Parameters;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FlexiblePathCalculatorTest {

    @Test
    void testCalcPathsSuccessful() {
        // Mock du graph + queryGraph
        Graph graph = mock(Graph.class);
        QueryGraph queryGraph = mock(QueryGraph.class);

        // Mock weighting
        Weighting weighting = mock(Weighting.class);

        // Algo options
        AlgorithmOptions algoOpts = AlgorithmOptions.start().build();

        // Fake algo factory
        RoutingAlgorithm algo = mock(RoutingAlgorithm.class);

        RoutingAlgorithmFactory factory = mock(RoutingAlgorithmFactory.class);
        when(factory.createAlgo(queryGraph, weighting, algoOpts)).thenReturn(algo);

        // Fake path
        Path mockPath = mock(Path.class);
        when(algo.calcPaths(0, 5)).thenReturn(List.of(mockPath));
        when(algo.getVisitedNodes()).thenReturn(10);

        FlexiblePathCalculator calc =
                new FlexiblePathCalculator(queryGraph, factory, weighting, algoOpts);

        // EdgeRestrictions valide
        EdgeRestrictions restrictions = new EdgeRestrictions();
        restrictions.getUnfavoredEdges().add(12);  // autorisé ✔

        List<Path> result = calc.calcPaths(0, 5, restrictions);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(factory).createAlgo(queryGraph, weighting, algoOpts);
    }

    @Test
    void testThrowsIfEmptyPaths() {
        Graph graph = mock(Graph.class);
        QueryGraph queryGraph = mock(QueryGraph.class);
        Weighting weighting = mock(Weighting.class);
        AlgorithmOptions algoOpts = AlgorithmOptions.start().build();

        RoutingAlgorithm algo = mock(RoutingAlgorithm.class);
        when(algo.calcPaths(0, 5)).thenReturn(List.of()); // Aucun path

        RoutingAlgorithmFactory factory = mock(RoutingAlgorithmFactory.class);
        when(factory.createAlgo(queryGraph, weighting, algoOpts)).thenReturn(algo);

        FlexiblePathCalculator calc =
                new FlexiblePathCalculator(queryGraph, factory, weighting, algoOpts);

        EdgeRestrictions restrictions = new EdgeRestrictions();

        assertThrows(IllegalStateException.class, () -> {
            calc.calcPaths(0, 5, restrictions);
        });
    }
}

