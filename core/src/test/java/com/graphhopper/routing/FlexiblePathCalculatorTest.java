package com.graphhopper.routing;

import com.graphhopper.routing.querygraph.QueryGraph;
import com.graphhopper.routing.util.TraversalMode;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.routing.weighting.FastestWeighting;
import com.graphhopper.storage.Graph;
import com.graphhopper.util.EdgeIterator;
import com.graphhopper.routing.AlgorithmOptions;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.PathCalculator.EdgeRestrictions;
import com.graphhopper.routing.ch.CHPreparationHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FlexiblePathCalculatorTest {

    @Test
    public void testCalcPathsBasic() {
        // Mock du graph
        Graph graph = mock(Graph.class);
        QueryGraph qGraph = mock(QueryGraph.class);
        when(qGraph.getBaseGraph()).thenReturn(graph);

        // Mock weighting
        Weighting weighting = mock(Weighting.class);

        // Options de l’algorithme
        AlgorithmOptions algoOpts = new AlgorithmOptions();

        // Mock factory -> retourne un algo mocké
        RoutingAlgorithm algo = mock(RoutingAlgorithm.class);
        RoutingAlgorithmFactory factory = mock(RoutingAlgorithmFactory.class);
        when(factory.createAlgo(qGraph, weighting, algoOpts)).thenReturn(algo);

        // Le calcul retournera une simple liste avec un Path mocké
        Path p = mock(Path.class);
        when(algo.calcPaths(0, 10)).thenReturn(Collections.singletonList(p));
        when(algo.getName()).thenReturn("mockAlgo");

        FlexiblePathCalculator calc =
                new FlexiblePathCalculator(qGraph, factory, weighting, algoOpts);

        EdgeRestrictions er = new EdgeRestrictions();

        List<Path> result = calc.calcPaths(0, 10, er);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(algo).calcPaths(0, 10);
    }

    @Test
    public void testSetGetWeighting() {

        QueryGraph qGraph = mock(QueryGraph.class);
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

