package student;

import game.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.mockito.Mockito.*;

public class ExplorerTest {
    private ExplorationState state;
    private Explorer explorer;

    @BeforeEach
    public void setUp() {
        state = mock(ExplorationState.class);
        explorer = new Explorer();
    }

    @Test
    public void testExplore_FindsOrbInOneMove() {
        NodeStatus neighbor = mock(NodeStatus.class);
        
        when(state.getCurrentLocation()).thenReturn(1L);
        when(state.getDistanceToTarget()).thenReturn(1);
        when(neighbor.getDistanceToTarget()).thenReturn(0);
        when(state.getNeighbours()).thenReturn(Set.of(neighbor));
        
        explorer.explore(state);
        
        verify(state).moveTo(neighbor);
    }

    @Test
    public void testExplore_AlreadyAtOrb() {
        when(state.getDistanceToTarget()).thenReturn(0);
        
        explorer.explore(state);
        
        verify(state, never()).moveTo(any());
    }

    @Test
    public void testExplore_ChoosesClosestNeighbor() {
        NodeStatus closer = mock(NodeStatus.class);
        NodeStatus farther = mock(NodeStatus.class);
        
        when(state.getCurrentLocation()).thenReturn(1L);
        when(state.getDistanceToTarget()).thenReturn(5);
        when(closer.getDistanceToTarget()).thenReturn(2);
        when(farther.getDistanceToTarget()).thenReturn(4);
        when(state.getNeighbours()).thenReturn(Set.of(farther, closer));
        
        explorer.explore(state);
        
        verify(state).moveTo(closer);
    }

    @Test
    public void testExplore_NoValidMoves() {
        when(state.getCurrentLocation()).thenReturn(1L);
        when(state.getDistanceToTarget()).thenReturn(5);
        when(state.getNeighbours()).thenReturn(Set.of());
        
        explorer.explore(state);
        
        verify(state, never()).moveTo(any());
    }

    @Test
    public void testExplore_EqualDistanceChoosesAny() {
        NodeStatus neighbor1 = mock(NodeStatus.class);
        NodeStatus neighbor2 = mock(NodeStatus.class);
        
        when(state.getCurrentLocation()).thenReturn(1L);
        when(state.getDistanceToTarget()).thenReturn(3);
        when(neighbor1.getDistanceToTarget()).thenReturn(2);
        when(neighbor2.getDistanceToTarget()).thenReturn(2);
        when(state.getNeighbours()).thenReturn(Set.of(neighbor1, neighbor2));
        
        explorer.explore(state);
        
        verify(state).moveTo(any(NodeStatus.class));
    }
}