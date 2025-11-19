package com.graphhopper.routing;

import com.carrotsearch.hppc.IntArrayList;
import com.graphhopper.routing.querygraph.QueryGraph;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.util.EdgeIterator;
import com.graphhopper.util.Parameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlexiblePathCalculatorTest {

    private QueryGraph queryGraph;
    private RoutingAlgorithmFactory algoFactory;
    private Weighting weighting;
    private AlgorithmOptions algoOptions;
    private RoutingAlgorithm algo;
    private Path path;

    @BeforeEach
    void setup() {
        queryGraph = Mockito.mock(QueryGraph.class);
        algoFactory = Mockito.mock(RoutingAlgorithmFactory.class);
        weighting = Mockito.mock(Weighting.class);
        algoOptions = Mockito.mock(AlgorithmOptions.class);

        // Le Path retourné par l'algo
        path = Mockito.mock(Path.class);

        // Algo factice retournant une liste contenant un seul Path
        algo = Mockito.mock(RoutingAlgorithm.class);
        Mockito.when(algo.calcPaths(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Collections.singletonList(path));
        Mockito.when(algo.getVisitedNodes()).thenReturn(3);

        Mockito.when(algoFactory.createAlgo(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(algo);

        // Max visited nodes pour contrôler exceptions
        Mockito.when(algoOptions.getMaxVisitedNodes()).thenReturn(50);

        // Pas d'arêtes "unfavored"
        Mockito.when(queryGraph.unfavorVirtualEdge(Mockito.anyInt())).thenReturn(true);
        Mockito.when(queryGraph.clearUnfavoredStatus()).thenReturn(true);
    }

    @Test
    void testCalcPaths_returnsOnePath() {
        FlexiblePathCalculator calc = new FlexiblePathCalculator(
                queryGraph, algoFactory, weighting, algoOptions);

        EdgeRestrictions restrictions = new EdgeRestrictions(
                EdgeIterator.ANY_EDGE,
                EdgeIterator.ANY_EDGE,
                new IntArrayList()
        );

        List<Path> result = calc.calcPaths(1, 5, restrictions);

        assertEquals(1, result.size(), "FlexiblePathCalculator doit retourner exactement un Path");
        assertEquals(path, result.get(0), "Le Path retourné doit être celui produit par l'algo mocké");
    }

    @Test
    void testCalcPaths_storesVisitedNodes() {
        FlexiblePathCalculator calc = new FlexiblePathCalculator(
                queryGraph, algoFactory, weighting, algoOptions);

        EdgeRestrictions restrictions = new EdgeRestrictions(
                EdgeIterator.ANY_EDGE,
                EdgeIterator.ANY_EDGE,
                new IntArrayList()
        );

        calc.calcPaths(1, 5, restrictions);

        assertEquals(3, calc.getVisitedNodes(),
                "Le nombre de nœuds visités doit provenir de algo.getVisitedNodes()");
    }

    @Test
    void testCalcPaths_throwsIfListEmpty() {
        // algo retourne une liste vide
        Mockito.when(algo.calcPaths(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Collections.emptyList());

        FlexiblePathCalculator calc = new FlexiblePathCalculator(
                queryGraph, algoFactory, weighting, algoOptions);

        EdgeRestrictions restrictions = new EdgeRestrictions(
                EdgeIterator.ANY_EDGE,
                EdgeIterator.ANY_EDGE,
                new IntArrayList()
        );

        assertThrows(IllegalStateException.class,
                () -> calc.calcPaths(1, 5, restrictions),
                "Une liste vide doit générer une IllegalStateException");
    }
}

