package student;

import game.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.mockito.Mockito.*;

public class EscapeTest {
    private EscapeState state;
    private Node start;
    private Node exit;
    private Node intermediate;
    private Edge edge1;
    private Edge edge2;

    @BeforeEach
    public void setUp() {
        state = mock(EscapeState.class);
        start = mock(Node.class);
        exit = mock(Node.class);
        intermediate = mock(Node.class);
        edge1 = mock(Edge.class);
        edge2 = mock(Edge.class);
        
        when(edge1.length()).thenReturn(1);
        when(edge2.length()).thenReturn(1);
        when(start.getEdge(intermediate)).thenReturn(edge1);
        when(intermediate.getEdge(exit)).thenReturn(edge2);
    }

    @Test
    public void testEscape_DirectPathToExit() {
        when(state.getCurrentNode()).thenReturn(start);
        when(state.getExit()).thenReturn(exit);
        when(state.getTimeRemaining()).thenReturn(10);
        when(state.getVertices()).thenReturn(Set.of(start, exit));
        when(start.getNeighbours()).thenReturn(Set.of(exit));

        new Explorer().escape(state);
        
        verify(state).moveTo(exit);
    }

    @Test
    public void testEscape_AlreadyAtExit() {
        when(state.getCurrentNode()).thenReturn(exit);
        when(state.getExit()).thenReturn(exit);

        new Explorer().escape(state);
        
        verify(state, never()).moveTo(any());
    }

    @Test
    public void testEscape_NotEnoughTime() {
        when(state.getCurrentNode()).thenReturn(start);
        when(state.getExit()).thenReturn(exit);
        when(state.getTimeRemaining()).thenReturn(1); // Not enough time
        when(state.getVertices()).thenReturn(Set.of(start, exit));
        when(start.getNeighbours()).thenReturn(Set.of(exit));
        when(start.getEdge(exit)).thenReturn(edge1);
        when(edge1.length()).thenReturn(2); // Requires 2 time units

        new Explorer().escape(state);
        
        verify(state, never()).moveTo(any());
    }

    @Test
    public void testEscape_MultiNodePath() {
        when(state.getCurrentNode()).thenReturn(start).thenReturn(intermediate);
        when(state.getExit()).thenReturn(exit);
        when(state.getTimeRemaining()).thenReturn(20);
        when(state.getVertices()).thenReturn(Set.of(start, intermediate, exit));
        when(start.getNeighbours()).thenReturn(Set.of(intermediate));
        when(intermediate.getNeighbours()).thenReturn(Set.of(exit));

        new Explorer().escape(state);
        
        verify(state).moveTo(intermediate);
        verify(state).moveTo(exit);
    }

    @Test
    public void testEscape_WithGoldPickup() {
        Node goldNode = mock(Node.class);
        Edge goldEdge = mock(Edge.class);
        
        when(goldEdge.length()).thenReturn(1);
        when(start.getEdge(goldNode)).thenReturn(goldEdge);
        when(goldNode.getEdge(exit)).thenReturn(edge1);
        
        when(state.getCurrentNode()).thenReturn(start).thenReturn(goldNode);
        when(state.getExit()).thenReturn(exit);
        when(state.getTimeRemaining()).thenReturn(20);
        when(state.getVertices()).thenReturn(Set.of(start, goldNode, exit));
        when(start.getNeighbours()).thenReturn(Set.of(goldNode, exit));
        when(goldNode.getTile().getGold()).thenReturn(100); // Gold present
        when(goldNode.getNeighbours()).thenReturn(Set.of(exit));

        new Explorer().escape(state);
        
        verify(state).moveTo(goldNode); // Should pick up gold first
        verify(state).moveTo(exit);
    }
}