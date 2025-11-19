package com.graphhopper.routing;

import com.graphhopper.routing.querygraph.QueryGraph;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.util.EdgeIterator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for FlexiblePathCalculator
 */
public class FlexiblePathCalculatorTest {

    @Test
    public void testCalcPathsReturnsPath() {
        // Mock graph + queryGraph
        Graph graph = mock(Graph.class);
        QueryGraph queryGraph = mock(QueryGraph.class);

        // Mock weighting
        Weighting weighting = mock(Weighting.class);

        // Mock algo options
        AlgorithmOptions algoOpts = mock(AlgorithmOptions.class);
        when(algoOpts.getMaxVisitedNodes()).thenReturn(1000);

        // Mock algorithm factory
        RoutingAlgorithmFactory algoFactory = mock(RoutingAlgorithmFactory.class);

        // Mock routing algorithm
        RoutingAlgorithm algo = mock(RoutingAlgorithm.class);

        // When factory creates algo → return our algo mock
        when(algoFactory.createAlgo(queryGraph, weighting, algoOpts)).thenReturn(algo);

        // Mock returned path
        Path path = mock(Path.class);
        when(algo.calcPaths(1, 2)).thenReturn(Collections.singletonList(path));
        when(algo.getVisitedNodes()).thenReturn(5);

        // No restrictions
        EdgeRestrictions restrictions = EdgeRestrictions.noRestrictions();

        // Create instance under test
        FlexiblePathCalculator calc = new FlexiblePathCalculator(queryGraph, algoFactory, weighting, algoOpts);

        List<Path> result = calc.calcPaths(1, 2, restrictions);

        // Validate
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(path, result.get(0));
        assertEquals(5, calc.getVisitedNodes());
    }

    @Test
    public void testCalcPathsThrowsIfEmpty() {
        Graph graph = mock(Graph.class);
        QueryGraph queryGraph = mock(QueryGraph.class);
        Weighting weighting = mock(Weighting.class);
        AlgorithmOptions algoOpts = mock(AlgorithmOptions.class);
        RoutingAlgorithmFactory algoFactory = mock(RoutingAlgorithmFactory.class);
        RoutingAlgorithm algo = mock(RoutingAlgorithm.class);

        when(algoFactory.createAlgo(queryGraph, weighting, algoOpts)).thenReturn(algo);
        when(algo.calcPaths(1, 2)).thenReturn(Collections.emptyList());

        FlexiblePathCalculator calc = new FlexiblePathCalculator(queryGraph, algoFactory, weighting, algoOpts);

        assertThrows(IllegalStateException.class,
                () -> calc.calcPaths(1, 2, EdgeRestrictions.noRestrictions()));
    }

}

