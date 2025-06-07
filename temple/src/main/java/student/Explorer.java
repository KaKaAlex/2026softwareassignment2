package student;

import game.EscapeState;
import game.ExplorationState;
import game.Node;
import game.Edge;
import game.NodeStatus;

import java.util.*;

public class Explorer {

    /**
     * Explore the cavern and find the Orb using DFS.
     */
    public void explore(ExplorationState state) {
        Set<Long> visited = new HashSet<>();
        dfs(state, visited);
    }

    private boolean dfs(ExplorationState state, Set<Long> visited) {
        long current = state.getCurrentLocation();
        visited.add(current);

        if (state.getDistanceToTarget() == 0) {
            return true;  // Found the orb!
        }

        List<NodeStatus> neighbors = new ArrayList<>(state.getNeighbours());
        neighbors.sort(Comparator.comparingInt(NodeStatus::getDistanceToTarget));  // ✅ Correct method

        for (NodeStatus neighbor : neighbors) {
            long id = neighbor.getId();  // ✅ Correct method
            if (!visited.contains(id)) {
                state.moveTo(id);
                if (dfs(state, visited)) {
                    return true;
                }
                state.moveTo(current);  // Backtrack
            }
        }
        return false;
    }

    /**
     * Escape the cavern using Dijkstra’s algorithm to ensure safety,
     * and pick up gold along the way.
     */
    public void escape(EscapeState state) {
        Node start = state.getCurrentNode();
        Node exit = state.getExit();

        Map<Node, Node> path = dijkstra(state.getVertices(), start);

        // Reconstruct path from start to exit
        List<Node> route = new ArrayList<>();
        for (Node at = exit; at != null && !at.equals(start); at = path.get(at)) {
            route.add(at);
        }
        Collections.reverse(route);

        // Follow path and pick up gold
        for (Node step : route) {
            state.moveTo(step);
            if (step.getTile().getGold() > 0) {
                state.pickUpGold();
            }
        }
    }

    /**
     * Dijkstra’s algorithm to find the shortest path from start to all nodes.
     */
    private Map<Node, Node> dijkstra(Collection<Node> graph, Node start) {
        Map<Node, Integer> dist = new HashMap<>();
        Map<Node, Node> prev = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(dist::get));

        for (Node node : graph) {
            dist.put(node, Integer.MAX_VALUE);
        }
        dist.put(start, 0);
        pq.add(start);

        while (!pq.isEmpty()) {
            Node u = pq.poll();
            for (Edge e : u.getExits()) {
                Node v = e.getOther(u);
                int alt = dist.get(u) + e.length();
                if (alt < dist.get(v)) {
                    dist.put(v, alt);
                    prev.put(v, u);
                    pq.remove(v);
                    pq.add(v);
                }
            }
        }
        return prev;
    }
}
