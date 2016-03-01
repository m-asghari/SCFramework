package edu.usc.infolab.sc.Algorithms.Batched.FordFulkerson;

/*****************************************************************************
 * File: FlowNetwork.java
 * Author: Keith Schwarz (htiek@cs.stanford.edu)
 *
 * A class representing a flow network; a directed, capacitated graph with
 * integral capacities and a possible flow on each edge.
 */
import java.util.*; // For HashMap, ArrayList

public final class FlowNetwork<T> implements Iterable<T> {
    /**
     * A utility class representing an edge in the flow network.
     */
    public static final class Edge<T> {
        private final T start; // Node start point
        private final T end;   // Node endpoint
        private int capacity;  // The capacity of the edge
        private int flow;      // The flow across the edge.

        /**
         * Returns the start node of this edge.
         *
         * @return The start node of this edge.
         */
        public T getStart() {
            return start;
        }

        /**
         * Returns the end node of this edge.
         *
         * @return The end node of this edge.
         */
        public T getEnd() {
            return end;
        }

        /**
         * Returns the capacity of this edge.
         *
         * @return The capacity of this edge.

         */
        public int getCapacity() {
            return capacity;
        }

        /**
         * Sets the capacity of this edge.  If this value is negative or is
         * less than the flow currently across this edge, throws an
         * IllegalArgumentException.
         *
         * @param capacity The new capacity of this edge.
         */
        public void setCapacity(int capacity) {
            /* Check that this is a valid capacity. */
            if (capacity < 0)
                throw new IllegalArgumentException("Capacities must be non-negative.");

            /* Confirm that we aren't pushing more flow across this edge than
             * the new capacity.
             */
            if (flow > capacity)
                throw new IllegalArgumentException("Cannot decrease capacity below the edge's current flow.");

            this.capacity = capacity;
        }

        /**
         * Returns the amount of flow across this edge.
         *
         * @return The amount of flow across this edge.
         */
        public int getFlow() {
            return flow;
        }

        /**
         * Sets the amount of flow across this edge.  If this value is negative
         * or exceeds the capacity, throws an IllegalArgumentException.
         *
         * @param flow The new flow along this edge.
         */
        public void setFlow(int flow) {
            /* Check that the flow is nonnegative; it's not defined otherwise. */
            if (flow < 0)
                throw new IllegalArgumentException("Flow must be nonnegative.");

            /* Check that the flow doesn't exceed the capacity. */
            if (flow > capacity)
                throw new IllegalArgumentException("Cannot set flow along an edge of capacity " + capacity + " to " + flow);
            
            this.flow = flow;
        }

        /**
         * Given a start point, an endpoint, and a capacity, constructs an edge
         * between those edges that has the specified capacity.
         *
         * @param start The start node of the edge.
         * @param end The end node of the edge.
         * @param capacity The capacity of the edge.
         * @param flow The flow across the edge.
         */
        private Edge(T start, T end, int capacity, int flow) {
            this.start = start;
            this.end = end;
            this.capacity = capacity;
            this.flow = flow;
        }

    }

    /* A map from nodes to a secondary map associating other nodes in the graph
     * with the edge connecting the current node to the indicated node.
     */
    private final Map<T, Map<T, Edge<T>>> graph = new HashMap<T, Map<T, Edge<T>>>();

    /**
     * Adds a new node to the graph.  If the node already exists, this
     * function is a no-op.
     *
     * @param node The node to add.
     * @return Whether or not the node was added.
     */
    public boolean addNode(T node) {
        /* If the node already exists, don't do anything. */
        if (graph.containsKey(node))
            return false;

        /* Otherwise, add the node with an empty list of outgoing edges. */
        graph.put(node, new HashMap<T, Edge<T>>());
        return true;
    }

    /**
     * Given a start node and a destination, adds a new edge from the start
     * node to the destination, then returns that edge.  If the edge already
     * existed in the graph, then it is not modified and the old edge is
     * returned.
     *
     * @param start The start node.
     * @param dest The destination node.
     * @return The edge between those endpoints.
     * @throws NoSuchElementException If either the start or destination nodes
     *                                do not exist.
     */
    public Edge<T> addEdge(T start, T dest) {
        /* Confirm both endpoints exist. */
        if (!graph.containsKey(start) || !graph.containsKey(dest))
            throw new NoSuchElementException("Both nodes must be in the graph.");

        /* If this edge doesn't exist, go create it. */
        if (!graph.get(start).containsKey(dest))
            graph.get(start).put(dest, new Edge<T>(start, dest, 0, 0));
        
        return graph.get(start).get(dest);
    }

    /**
     * Given a start and end node, returns the edge between those two nodes in
     * the graph.  If no edge exists, null is returned.
     *
     * @param start The start node.
     * @param end The end node.
     * @return The edge between those nodes.
     * @throws NoSuchElementException If either node is not in the graph.
     */
    public Edge<T> getEdge(T start, T dest) {
        /* Confirm both endpoints exist. */
        if (!graph.containsKey(start) || !graph.containsKey(dest))
            throw new NoSuchElementException("Both nodes must be in the graph.");
        
        return graph.get(start).get(dest);
    }

    /**
     * Removes the edge from start to dest from the graph.  If the edge does
     * not exist, this operation is a no-op.  If either endpoint does not
     * exist, this throws a NoSuchElementException.
     *
     * @param start The start node.
     * @param dest The destination node.
     * @throws NoSuchElementException If either node is not in the graph.
     */
    public void removeEdge(T start, T dest) {
        /* Confirm both endpoints exist. */
        if (!graph.containsKey(start) || !graph.containsKey(dest))
            throw new NoSuchElementException("Both nodes must be in the graph.");

        /* Remove the edge from the graph. */
        graph.get(start).remove(dest);
    }

    /**
     * Given a node in the graph, returns an immutable view of the edges
     * leaving that node.
     *
     * @param node The node whose edges should be queried.
     * @return An immutable view of the edges leaving that node.
     * @throws NoSuchElementException If the node does not exist.
     */
    public Collection<Edge<T>> edgesFrom(T node) {
        /* Check that the node exists. */
        Map<T, Edge<T>> arcs = graph.get(node);
        if (arcs == null)
            throw new NoSuchElementException("Source node does not exist.");

        return Collections.unmodifiableCollection(arcs.values());
    }

    /**
     * Returns an iterator that can traverse the nodes in the graph.
     *
     * @return An iterator that traverses the nodes in the graph.
     */
    public Iterator<T> iterator() {
        return graph.keySet().iterator();
    }

    /**
     * Returns whether a given node is contained in the graph.
     *
     * @param The node to test for inclusion.
     * @return Whether that node is contained in the graph.
     */
    public boolean containsNode(T node) {
        return graph.containsKey(node);
    }

    /**
     * Returns the number of nodes in the graph.
     *
     * @return The number of nodes in the graph.
     */
    public int size() {
        return graph.size();
    }

    /**
     * Returns whether the graph is empty.
     *
     * @return Whether the graph is empty.
     */
    public boolean isEmpty() {
        return graph.isEmpty();
    }
}
