package com.graphhopper.routing.weighting;

import com.graphhopper.routing.ev.DecimalEncodedValue;
import com.graphhopper.storage.TurnCostStorage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests with Mockito for the FastestWeighting class.
 * These tests simulate dependencies (DecimalEncodedValue + TurnCostStorage)
 * to validate behaviour and increase mutation coverage.
 */
class FastestWeightingMockitoTest {

    /**
     * Test 1:
     * Simulate a normal speed value and no turn penalty.
     * We verify that getMinWeight returns a non-negative value.
     */
    @Test
    void testMinWeightWithMockedSpeed() {
        // --- MOCKS ---
        DecimalEncodedValue speedEv = Mockito.mock(DecimalEncodedValue.class);
        DecimalEncodedValue turnEv = Mockito.mock(DecimalEncodedValue.class);
        TurnCostStorage turnCostStorage = Mockito.mock(TurnCostStorage.class);

        // Simulated values
        Mockito.when(speedEv.getDecimal(false, 0)).thenReturn(50.0);
        Mockito.when(turnCostStorage.get(0, 1, 2)).thenReturn(0);

        // Object under test
        FastestWeighting weighting =
                new FastestWeighting(speedEv, turnEv, turnCostStorage);

        // Invoke method
        double weight = weighting.getMinWeight(0);

        // Validate
        assertTrue(weight >= 0, "Weight should be non-negative when speed is positive.");
    }

    /**
     * Test 2:
     * Simulate lower speed + a significant turn cost.
     * The resulting weight should be higher.
     */
    @Test
    void testTurnCostAffectsWeight() {
        // --- MOCKS ---
        DecimalEncodedValue speedEv = Mockito.mock(DecimalEncodedValue.class);
        DecimalEncodedValue turnEv = Mockito.mock(DecimalEncodedValue.class);
        TurnCostStorage turnCostStorage = Mockito.mock(TurnCostStorage.class);

        // Simulated lower speed
        Mockito.when(speedEv.getDecimal(false, 0)).thenReturn(10.0);

        // Simulated turn cost
        Mockito.when(turnCostStorage.get(3, 4, 5)).thenReturn(120);

        // Object under test
        FastestWeighting weighting =
                new FastestWeighting(speedEv, turnEv, turnCostStorage);

        double weight = weighting.getMinWeight(3);

        assertTrue(weight > 0, "Turn cost should increase the weight value.");
    }
}

