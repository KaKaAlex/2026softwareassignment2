package student;

import game.EscapeState;
import game.ExplorationState;
import game.NodeStatus;
import game.Node;

import java.util.*;

public class Explorer {

    /**
     * Explore the cavern, trying to find the orb in as few steps as possible.
     * Once you find the orb, you must return from the function in order to pick
     * it up. If you continue to move after finding the orb rather
     * than returning, it will not count.
     * If you return from this function while not standing on top of the orb,
     * it will count as a failure.
     * <p>
     * There is no limit to how many steps you can take, but you will receive
     * a score bonus multiplier for finding the orb in fewer steps.
     * <p>
     * At every step, you only know your current tile's ID and the ID of all
     * open neighbor tiles, as well as the distance to the orb at each of these tiles
     * (ignoring walls and obstacles).
     * <p>
     * To get information about the current state, use functions
     * getCurrentLocation(),
     * getNeighbours(), and
     * getDistanceToTarget()
     * in ExplorationState.
     * You know you are standing on the orb when getDistanceToTarget() is 0.
     * <p>
     * Use function moveTo(long id) in ExplorationState to move to a neighboring
     * tile by its ID. Doing this will change state to reflect your new position.
     * <p>
     * A suggested first implementation that will always find the orb, but likely won't
     * receive a large bonus multiplier, is a depth-first search.
     *
     * @param state the information available at the current state
     */
    public void explore(ExplorationState state) {
        exploreByAStar(state);
    }

    private void exploreByDepthFirstSearch(ExplorationState state) {
        Set<Long> visitedNodes = new HashSet<>();
        Stack<Long> nodesToExplore = new Stack<>();
        Map<Long, Long> parentNodes = new HashMap<>();
        
        long startingNode = state.getCurrentLocation();
        nodesToExplore.push(startingNode);
        visitedNodes.add(startingNode);
        
        while (!nodesToExplore.isEmpty() && state.getDistanceToTarget() != 0) {
            long currentNode = nodesToExplore.peek();
            
            if (currentNode != state.getCurrentLocation()) {
                moveToTargetNode(state, currentNode, parentNodes);
            }
            
            boolean foundUnvisitedNeighbor = false;
            for (NodeStatus neighborNode : state.getNeighbours()) {
                long neighborNodeId = neighborNode.nodeID();
                if (!visitedNodes.contains(neighborNodeId)) {
                    visitedNodes.add(neighborNodeId);
                    parentNodes.put(neighborNodeId, currentNode);
                    nodesToExplore.push(neighborNodeId);
                    foundUnvisitedNeighbor = true;
                    break;
                }
            }
            
            if (!foundUnvisitedNeighbor) {
                nodesToExplore.pop();
            }
        }
    }
    
    private void moveToTargetNode(ExplorationState state, long targetNodeId, Map<Long, Long> parentNodes) {
        List<Long> pathToTarget = new ArrayList<>();
        long currentNodeInPath = targetNodeId;
        
        while (currentNodeInPath != state.getCurrentLocation() && parentNodes.containsKey(currentNodeInPath)) {
            pathToTarget.add(currentNodeInPath);
            currentNodeInPath = parentNodes.get(currentNodeInPath);
        }
        
        Collections.reverse(pathToTarget);
        
        for (long nodeIdInPath : pathToTarget) {
            for (NodeStatus neighbor : state.getNeighbours()) {
                if (neighbor.nodeID() == nodeIdInPath) {
                    state.moveTo(nodeIdInPath);
                    break;
                }
            }
        }
    }

    private void exploreByAStar(ExplorationState state) {
        Set<Long> visitedNodesAStar = new HashSet<>();
        Stack<Long> nodesToExploreAStar = new Stack<>();
        Map<Long, Long> parentNodesAStar = new HashMap<>();
        
        long startingNodeAStar = state.getCurrentLocation();
        nodesToExploreAStar.push(startingNodeAStar);
        visitedNodesAStar.add(startingNodeAStar);
        
        while (!nodesToExploreAStar.isEmpty() && state.getDistanceToTarget() != 0) {
            long currentNodeAStar = nodesToExploreAStar.peek();
            
            if (currentNodeAStar != state.getCurrentLocation()) {
                moveToTargetNodeAStar(state, currentNodeAStar, parentNodesAStar);
            }
            
            NodeStatus bestUnvisitedNeighbor = null;
            int shortestDistanceToOrb = Integer.MAX_VALUE;
            
            for (NodeStatus neighborNode : state.getNeighbours()) {
                long neighborNodeId = neighborNode.nodeID();
                if (!visitedNodesAStar.contains(neighborNodeId)) {
                    int distanceToOrb = neighborNode.distanceToTarget();
                    if (distanceToOrb < shortestDistanceToOrb) {
                        shortestDistanceToOrb = distanceToOrb;
                        bestUnvisitedNeighbor = neighborNode;
                    }
                }
            }
            
            if (bestUnvisitedNeighbor != null) {
                long bestNeighborId = bestUnvisitedNeighbor.nodeID();
                visitedNodesAStar.add(bestNeighborId);
                parentNodesAStar.put(bestNeighborId, currentNodeAStar);
                nodesToExploreAStar.push(bestNeighborId);
            } else {
                nodesToExploreAStar.pop();
            }
        }
    }
    
    private void moveToTargetNodeAStar(ExplorationState state, long targetNodeId, Map<Long, Long> parentNodesAStar) {
        List<Long> pathToTargetAStar = new ArrayList<>();
        long currentNodeInPathAStar = targetNodeId;
        
        while (currentNodeInPathAStar != state.getCurrentLocation() && parentNodesAStar.containsKey(currentNodeInPathAStar)) {
            pathToTargetAStar.add(currentNodeInPathAStar);
            currentNodeInPathAStar = parentNodesAStar.get(currentNodeInPathAStar);
        }
        
        Collections.reverse(pathToTargetAStar);
        
        for (long nodeIdInPathAStar : pathToTargetAStar) {
            for (NodeStatus neighbor : state.getNeighbours()) {
                if (neighbor.nodeID() == nodeIdInPathAStar) {
                    state.moveTo(nodeIdInPathAStar);
                    break;
                }
            }
        }
    }

    /**
     * Escape from the cavern before the ceiling collapses, trying to collect as much
     * gold as possible along the way. Your solution must ALWAYS escape before time runs
     * out, and this should be prioritized above collecting gold.
     * <p>
     * You now have access to the entire underlying graph, which can be accessed through EscapeState.
     * getCurrentNode() and getExit() will return you Node objects of interest, and getVertices()
     * will return a collection of all nodes on the graph.
     * <p>
     * Note that time is measured entirely in the number of steps taken, and for each step
     * the time remaining is decremented by the weight of the edge taken. You can use
     * getTimeRemaining() to get the time still remaining, pickUpGold() to pick up any gold
     * on your current tile (this will fail if no such gold exists), and moveTo() to move
     * to a destination node adjacent to your current node.
     * <p>
     * You must return from this function while standing at the exit. Failing to do so before time
     * runs out or returning from the wrong location will be considered a failed run.
     * <p>
     * You will always have enough time to escape using the shortest path from the starting
     * position to the exit, although this will not collect much gold.
     *
     * @param state the information available at the current state
     */
    public void escape(EscapeState state) {
        Map<Node, Integer> distancesToExit = new HashMap<>();
        Map<Node, Node> previousNode = new HashMap<>();
        Set<Node> visitedNodes = new HashSet<>();
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>((a, b) ->
            distancesToExit.getOrDefault(a, Integer.MAX_VALUE) - distancesToExit.getOrDefault(b, Integer.MAX_VALUE));
        
        Node start = state.getCurrentNode();
        Node exit = state.getExit();
        
        distancesToExit.put(start, 0);
        priorityQueue.add(start);
        
        while (!priorityQueue.isEmpty()) {
            Node current = priorityQueue.poll();
            
            if (visitedNodes.contains(current)) {
                continue;
            }
            visitedNodes.add(current);
            
            if (current.equals(exit)) {
                break;
            }
            
            for (Node neighbor : current.getNeighbours()) {
                if (!visitedNodes.contains(neighbor)) {
                    int newDistance = distancesToExit.get(current) + current.getEdge(neighbor).length();
                    
                    if (newDistance < distancesToExit.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                        distancesToExit.put(neighbor, newDistance);
                        previousNode.put(neighbor, current);
                        priorityQueue.add(neighbor);
                    }
                }
            }
        }
        
        List<Node> pathToExit = new ArrayList<>();
        Node current = exit;
        while (current != null) {
            pathToExit.add(current);
            current = previousNode.get(current);
        }
        Collections.reverse(pathToExit);
        
        for (int i = 1; i < pathToExit.size(); i++) {
            Node nextNodeOnPath = pathToExit.get(i);
            
            if (state.getCurrentNode().getTile().getGold() > 0) {
                state.pickUpGold();
            }
            
            state.moveTo(nextNodeOnPath);
        }
        
        if (state.getCurrentNode().getTile().getGold() > 0) {
            state.pickUpGold();
        }
    }
}
