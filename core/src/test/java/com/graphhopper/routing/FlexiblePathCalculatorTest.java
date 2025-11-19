package com.graphhopper.routing;

import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.storage.BaseGraph;
import com.graphhopper.storage.GraphBuilder;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.storage.index.QueryGraph;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.querygraph.Snap;
import com.graphhopper.routing.ev.DecimalEncodedValue;
import com.graphhopper.routing.ev.Speed;
import com.graphhopper.routing.ev.RoadEnvironment;
import com.graphhopper.routing.ev.RoadClass;
import com.graphhopper.routing.ev.BooleanEncodedValue;
import com.graphhopper.storage.RoutingCHGraph;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.querygraph.EdgeRestrictions;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class FlexiblePathCalculatorTest {

    private BaseGraph newGraph() {
        return new GraphBuilder(EncodingManager.create("car")).create();
    }

    @Test
    public void testCalcPaths() {
        BaseGraph base = newGraph();

        // Construire un QueryGraph correctement
        QueryGraph qg = QueryGraph.create(base, Collections.emptyList());

        // Mock du RoutingAlgorithm
        RoutingAlgorithm algo = mock(RoutingAlgorithm.class);

        // Simuler un Path retourné
        Path path = mock(Path.class);
        when(algo.calcPaths(anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(path));

        // Mock du factory
        RoutingAlgorithmFactory factory = mock(RoutingAlgorithmFactory.class);
        when(factory.createAlgo(any(), any(), any())).thenReturn(algo);

        AlgorithmOptions opts = new AlgorithmOptions();
        Weighting weighting = mock(Weighting.class);

        EdgeRestrictions restrictions = new EdgeRestrictions();

        FlexiblePathCalculator calc =
                new FlexiblePathCalculator(qg, factory, weighting, opts);

        List<Path> result = calc.calcPaths(1, 2, restrictions);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetVisitedNodes() {
        BaseGraph base = newGraph();

        QueryGraph qg = QueryGraph.create(base, Collections.emptyList());

        RoutingAlgorithm algo = mock(RoutingAlgorithm.class);

        // Important : Simuler un Path pour éviter l’erreur "Path list was empty"
        Path path = mock(Path.class);
        when(algo.calcPaths(anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(path));

        // Simuler 42 nœuds visités
        when(algo.getVisitedNodes()).thenReturn(42);

        RoutingAlgorithmFactory factory = mock(RoutingAlgorithmFactory.class);
        when(factory.createAlgo(any(), any(), any())).thenReturn(algo);

        AlgorithmOptions opts = new AlgorithmOptions();
        Weighting weighting = mock(Weighting.class);

        EdgeRestrictions restrictions = new EdgeRestrictions();

        FlexiblePathCalculator calc =
                new FlexiblePathCalculator(qg, factory, weighting, opts);

        calc.calcPaths(1, 2, restrictions);

        assertEquals(42, calc.getVisitedNodes());
    }
}

