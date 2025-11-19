package com.graphhopper.routing;

import com.graphhopper.routing.querygraph.QueryGraph;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.storage.BaseGraph;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.AlgorithmOptions;
import com.graphhopper.routing.PathCalculator.EdgeRestrictions;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FlexiblePathCalculatorTest {

    @Test
    public void testCalcPathsBasic() {
        // Mock BaseGraph (correct type)
        BaseGraph baseGraph = mock(BaseGraph.class);

        // Mock QueryGraph
        QueryGraph qGraph = mock(QueryGraph.class);
        when(qGraph.getBaseGraph()).thenReturn(baseGraph);

        // Mock weighting
        Weighting weighting = mock(Weighting.class);

        // Algo options
        AlgorithmOptions algoOpts = new AlgorithmOptions();

        // Mock routing algorithm
        RoutingAlgorithm algo = mock(RoutingAlgorithm.class);
        when(algo.calcPaths(0, 10)).thenReturn(
                Collections.singletonList(mock(Path.class))
        );
        when(algo.getName()).thenReturn("mockAlgo");

        // Mock factory
        RoutingAlgorithmFactory factory = mock(RoutingAlgorithmFactory.class);
        when(factory.createAlgo(qGraph, weighting, algoOpts)).thenReturn(algo);

        // Instance to test
        FlexiblePathCalculator calc =
                new FlexiblePathCalculator(qGraph, factory, weighting, algoOpts);

        // Restrictions
        EdgeRestrictions er = new EdgeRestrictions();

        List<Path> result = calc.calcPaths(0, 10, er);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(algo).calcPaths(0, 10);
    }

    @Test
    public void testSetGetWeighting() {
        BaseGraph baseGraph = mock(BaseGraph.class);
        QueryGraph qGraph = mock(QueryGraph.class);
        when(qGraph.getBaseGraph()).thenReturn(baseGraph);

        Weighting w1 = mock(Weighting.class);
        Weighting w2 = mock(Weighting.class);

        AlgorithmOptions opts = new AlgorithmOptions();
        RoutingAlgorithmFactory factory = mock(RoutingAlgorithmFactory.class);

        FlexiblePathCalculator calc =
                new FlexiblePathCalculator(qGraph, factory, w1, opts);

        assertEquals(w1, calc.getWeighting());
        calc.setWeighting(w2);
        assertEquals(w2, calc.getWeighting());
    }
}

