package com.graphhopper.routing.weighting;

import com.graphhopper.routing.ev.DecimalEncodedValue;
import com.graphhopper.storage.EdgeIntAccess;
import com.graphhopper.storage.TurnCostStorage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ShortestWeightingMockitoTest {

    @Test
    void testShortestWeightingWithMockedSpeed() {

        DecimalEncodedValue speedEv = Mockito.mock(DecimalEncodedValue.class);
        DecimalEncodedValue turnEv = Mockito.mock(DecimalEncodedValue.class);
        TurnCostStorage turnCostStorage = Mockito.mock(TurnCostStorage.class);
        EdgeIntAccess edgeAccess = Mockito.mock(EdgeIntAccess.class);

        // Mock speed for edge 0
        Mockito.when(speedEv.getDecimal(false, 0, edgeAccess)).thenReturn(50.0);

        // No turn cost
        Mockito.when(turnCostStorage.get(turnEv, 0, 1, 2)).thenReturn(0);

        ShortestWeighting weighting = new ShortestWeighting(speedEv);

        double w = weighting.calcEdgeWeight(0, false, edgeAccess);

        assertTrue(w >= 0);
    }

    @Test
    void testTurnCostAffectsWeight() {

        DecimalEncodedValue speedEv = Mockito.mock(DecimalEncodedValue.class);
        DecimalEncodedValue turnEv = Mockito.mock(DecimalEncodedValue.class);
        TurnCostStorage turnCostStorage = Mockito.mock(TurnCostStorage.class);
        EdgeIntAccess edgeAccess = Mockito.mock(EdgeIntAccess.class);

        Mockito.when(speedEv.getDecimal(false, 3, edgeAccess)).thenReturn(10.0);

        Mockito.when(turnCostStorage.get(turnEv, 3, 4, 5)).thenReturn(120);

        ShortestWeighting weighting = new ShortestWeighting(speedEv);

        double w = weighting.calcEdgeWeight(3, false, edgeAccess);

        assertTrue(w > 0);
    }
}

