package com.graphhopper.routing;

import com.graphhopper.routing.util.Weighting;
import com.graphhopper.storage.GraphHopperStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class DijkstraTest {

    private GraphHopperStorage storage;
    private Weighting weighting;

    @BeforeEach
    void setup() {
        storage = Mockito.mock(GraphHopperStorage.class);
        weighting = Mockito.mock(Weighting.class);
    }

    @Test
    void testNoPathFound() {
        // GIVEN
        Dijkstra dijkstra = new Dijkstra(storage, weighting);

        // WHEN
        var result = dijkstra.calcPath(1, 999);

        // THEN
        assertTrue(result.isEmpty(), "Expected empty path when destination is unreachable");
    }

    @Test
    void testWeightingIsUsed() {
        // GIVEN
        Mockito.when(weighting.getMinWeight(1, 2)).thenReturn(10.0);
        Dijkstra dijkstra = new Dijkstra(storage, weighting);

        // WHEN
        dijkstra.calcPath(1, 2);

        // THEN
        Mockito.verify(weighting, Mockito.atLeastOnce()).getMinWeight(1, 2);
    }
}

