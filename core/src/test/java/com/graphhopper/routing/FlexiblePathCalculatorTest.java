package com.graphhopper.routing;

import com.graphhopper.routing.querygraph.QueryGraph;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.storage.BaseGraph;
import com.graphhopper.util.EdgeIterator;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FlexiblePathCalculatorTest {

    private BaseGraph newGraph() {
        // Dans ta version : Builder exige un int
        return new BaseGraph.Builder(1).create();
    }

    @Test
    public void testCalcPathsReturnsNonEmptyList() {
        BaseGraph base = newGraph();

        QueryGraph queryGraph = QueryGraph.create(base, Collections.emptyList());

        RoutingAlgorithmFactory factory = mock(RoutingAlgorithmFactory.class);
        RoutingAlgorithm algo = mock(RoutingAlgorithm.class);
        when(factory.createAlgo(any(), any(), any())).thenReturn(algo);

        Weighting weighting = mock(Weighting.class);
        AlgorithmOptions opts = new AlgorithmOptions();

        Path mockPath = mock(Path.class);
        when(algo.calcPaths(anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(mockPath));

        // EdgeRestrictions sans arguments
        EdgeRestrictions restrictions = new EdgeRestrictions();

        FlexiblePathCalculator calc =
                new FlexiblePathCalculator(queryGraph, factory, weighting, opts);

        List<Path> result = calc.calcPaths(0, 5, restrictions);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testGetVisitedNodes() {
        BaseGraph base = newGraph();

        QueryGraph qg = QueryGraph.create(base, Collections.emptyList());

        RoutingAlgorithm algo = mock(RoutingAlgorithm.class);
        when(algo.getVisitedNodes()).thenReturn(42);

        RoutingAlgorithmFactory factory = mock(RoutingAlgorithmFactory.class);
        when(factory.createAlgo(any(), any(), any())).thenReturn(algo);

        AlgorithmOptions opts = new AlgorithmOptions();

        EdgeRestrictions restrictions = new EdgeRestrictions();

        FlexiblePathCalculator calc =
                new FlexiblePathCalculator(qg, factory, mock(Weighting.class), opts);

        calc.calcPaths(1, 2, restrictions);

        assertEquals(42, calc.getVisitedNodes());
    }
}

