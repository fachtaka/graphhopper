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

    @Test
    public void testCalcPathsReturnsNonEmptyList() {
        // BaseGraph requis par QueryGraph
        BaseGraph base = new BaseGraph.Builder().create();

        QueryGraph queryGraph = QueryGraph.create(base, Collections.emptyList());

        // Mock factory + algo
        RoutingAlgorithmFactory factory = mock(RoutingAlgorithmFactory.class);
        RoutingAlgorithm algo = mock(RoutingAlgorithm.class);
        when(factory.createAlgo(any(), any(), any())).thenReturn(algo);

        // Mock weighting
        Weighting weighting = mock(Weighting.class);

        // AlgorithmOptions dans ta version
        AlgorithmOptions opts = new AlgorithmOptions();

        // Mock d'un Path
        Path mockPath = mock(Path.class);
        when(algo.calcPaths(anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(mockPath));

        // EdgeRestrictions : constructeur avec ANY_EDGE
        EdgeRestrictions restrictions = new EdgeRestrictions(
                EdgeIterator.ANY_EDGE,
                EdgeIterator.ANY_EDGE,
                Collections.emptyList()
        );

        FlexiblePathCalculator calc =
                new FlexiblePathCalculator(queryGraph, factory, weighting, opts);

        List<Path> result = calc.calcPaths(0, 5, restrictions);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testGetVisitedNodes() {
        BaseGraph base = new BaseGraph.Builder().create();
        QueryGraph qg = QueryGraph.create(base, Collections.emptyList());

        RoutingAlgorithm algo = mock(RoutingAlgorithm.class);
        when(algo.getVisitedNodes()).thenReturn(42);

        RoutingAlgorithmFactory factory = mock(RoutingAlgorithmFactory.class);
        when(factory.createAlgo(any(), any(), any())).thenReturn(algo);

        AlgorithmOptions opts = new AlgorithmOptions();

        EdgeRestrictions restrictions = new EdgeRestrictions(
                EdgeIterator.ANY_EDGE,
                EdgeIterator.ANY_EDGE,
                Collections.emptyList()
        );

        FlexiblePathCalculator calc =
                new FlexiblePathCalculator(qg, factory, mock(Weighting.class), opts);

        calc.calcPaths(1, 2, restrictions);

        assertEquals(42, calc.getVisitedNodes());
    }
}

