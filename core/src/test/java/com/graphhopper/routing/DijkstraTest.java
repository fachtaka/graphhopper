package com.graphhopper.routing;

import com.graphhopper.routing.util.TraversalMode;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.storage.Graph;
import com.graphhopper.util.EdgeExplorer;
import com.graphhopper.util.EdgeIterator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires simples pour l'algorithme Dijkstra.
 * On utilise des mocks pour Graph, EdgeExplorer, EdgeIterator et Weighting.
 */
class DijkstraTest {

    private Graph graph;
    private Weighting weighting;
    private EdgeExplorer edgeExplorer;
    private EdgeIterator edgeIterator;

    @BeforeEach
    void setUp() {
        // Mocks de base
        graph = Mockito.mock(Graph.class);
        weighting = Mockito.mock(Weighting.class);
        edgeExplorer = Mockito.mock(EdgeExplorer.class);
        edgeIterator = Mockito.mock(EdgeIterator.class);

        // Le constructeur de Dijkstra utilise getNodes() pour dimensionner les structures
        Mockito.when(graph.getNodes()).thenReturn(10);

        // AbstractRoutingAlgorithm récupère un EdgeExplorer via graph.createEdgeExplorer()
        Mockito.when(graph.createEdgeExplorer()).thenReturn(edgeExplorer);

        // Quand l'algo demande les arêtes sortantes d'un nœud,
        // on lui donne un itérateur sans arêtes (next() retourne false)
        Mockito.when(edgeExplorer.setBaseNode(Mockito.anyInt())).thenReturn(edgeIterator);
        Mockito.when(edgeIterator.next()).thenReturn(false);
    }

    @Test
    void calcPath_withoutEdges_returnsEmptyPath() {
        // GIVEN
        Dijkstra algo = new Dijkstra(graph, weighting, TraversalMode.NODE_BASED);

        // WHEN : aucun edge, donc pas de chemin possible
        Path path = algo.calcPath(1, 5);

        // THEN : le Path est "vide" pour GraphHopper => isFound() = false
        assertNotNull(path, "Le Path ne doit pas être null");
        assertFalse(path.isFound(), "Aucun chemin ne devrait être trouvé sans arêtes");
        assertEquals(1, algo.getVisitedNodes(),
                "On s'attend à ce que le nœud de départ ait été visité exactement une fois");
    }

    @Test
    void calcPath_startsFromSourceNode() {
        // GIVEN
        Dijkstra algo = new Dijkstra(graph, weighting, TraversalMode.NODE_BASED);

        // WHEN
        algo.calcPath(3, 7);

        // THEN : l'algo doit commencer par explorer le nœud source (3)
        Mockito.verify(edgeExplorer, Mockito.atLeastOnce()).setBaseNode(3);
    }
}

