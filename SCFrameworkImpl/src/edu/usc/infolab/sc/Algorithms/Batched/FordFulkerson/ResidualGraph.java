package edu.usc.infolab.sc.Algorithms.Batched.FordFulkerson;

/*****************************************************************************
 * File: ResidualGraph.java
 * Author: Keith Schwarz (htiek@cs.stanford.edu)
 *
 * A class representing the residual graph of a flow network.  The residual
 * graph is the graph formed as follows:
 *
 * Given an input graph G = (V, E), the residual graph G' = (V', E') is the
 * graph where
 *
 *  * V' = V  (All the nodes from the original graph are present in the new
 *             graph)
 *  * For all (u, v) in E, there are two edges (u, v) and (v, u) in E' where
 *    the capacity of (u, v) is the capacity of (u, v) minus the flow across 
 *    (u, v), and the capacity of (v, u) is the flow across (u, v).
 *
 * The intution behind this graph is that the residual graph is a flow network
 * in which pushing a given unit of flow across an edge either corresponds to
 * pushing a unit of flow across an edge in the original graph OR retracting a
 * unit of flow across an edge in the original graph.  Rather than associating
 * with each edge a capacity and a flow, only a capacity is associated.  The
 * flow across an edge is represented implicitly by the amount of capacity
 * remaining on its residual edge.
 */

import java.util.*; // For HashMap, ArrayList, Iterable

public final class ResidualGraph<T> implements Iterable<T> {
    /**
     * A utility class representing an edge in the flow graph.  Each edge keeps
     * track of its endpoints, its capacity, and its residual edge.  Adding or
     * removing a unit of flow from an edge works by decreasing or increasing
     * the capacity of this edge as appropriate, then increasing or decreasing
     * the capacity of its residual edge appropriately.
     */
    public static final class Edge<T> {
        private final T start;            // The start and end nodes of the edge
        private final T end;
        private final boolean isResidual; // Whether this is a real or residual
                                          // edge
        private Edge<T> reverse;          // The reverse edge for this edge
        private int capacity;             // The capacity of this edge

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
         * Adds or subtracts the indicated number of flow units across this
         * edge.  If the amount of flow added or removed from this edge 
         * exceeds the capacity of the edge, throws an IllegalArgumentException.
         *
         * @param amount The amount of flow to add or subtract.
         */
        public void addFlow(int amount) {
            /* We maintain an invariant that the amount of flow being pushed
             * across this edge is positive.  If we want to retract flow, we
             * do it instead by adding that amount of flow to the dual edge.
             */
            if (amount < 0) {
                reverse.addFlow(-amount);
                return;
            }

            /* Check whether we have enough capacity to support this flow. */
            if (amount > capacity)
                throw new IllegalArgumentException("Cannot push " + amount + " units of flow across edge of capacity " + capacity);

            /* Subtract the appropriate amount from this edge, then add it in
             * to the reverse edge.
             */
            capacity -= amount;
            reverse.capacity += amount;
        }

        /**
         * Returns the reverse edge for this particular edge.
         *
         * @return The reverse edge for this edge.
         */
        public Edge<T> getReverse() {
            return reverse;
        }

        /**
         * Returns whether this node is an original edge in the graph (true)
         * or a residual edge (false).
         *
         * @return Whether this node is an original edge in the graph.
         */
        public boolean isOriginal() {
            return !isResidual;
        }

        /**
         * Sets the reverse edge associated with this edge.  This function is
         * marked private because the invariant is maintained internally by
         * the class.
         */
        private void setReverse(Edge<T> reverse) {
            this.reverse = reverse;
        }

        /**
         * Constructs a new Edge between the given endpoints and with the
         * given capacity.
         *
         * @param start The start node for the edge.
         * @param end The end node for the edge.
         * @param capacity The capacity of this edge.
         * @param isOriginal Whether this is an original edge in the graph.
         */
        private Edge(T start, T end, int capacity, boolean isOriginal) {
            this.start = start;
            this.end = end;
            this.capacity = capacity;
            this.isResidual = !isOriginal;
        }
    }

    /* Internally, the residual graph is represented as a mapping from nodes
     * to the list of edges leaving that node.
     */
    private final Map<T, List<Edge<T>>> graph = new HashMap<T, List<Edge<T>>>();

    /**
     * Constructs a new residual graph from a given flow network.
     *
     * @param g The flow network from which the graph should be constructed.
     */
    public ResidualGraph(FlowNetwork<T> g) {
        /* Begin by constructing a new entry in the graph for each node in the
         * source graph.
         */
        for (T node: g)
            graph.put(node, new ArrayList<Edge<T>>());

        /* Now, for each edge in the graph, add a new edge/residual edge pair
         * to the graph.
         */
        for (T node: g) {
            for (FlowNetwork.Edge<T> edge: g.edgesFrom(node)) {
                /* Construct the forward edge, which has capacity equal to the
                 * remaining capacity on this edge.
                 */
                Edge<T> forward = new Edge<T>(edge.getStart(), edge.getEnd(),
                                              edge.getCapacity() - edge.getFlow(),
                                              true);

                /* Construct the reverse edge, whose capacity is the flow on
                 * this edge.
                 */
                Edge<T> reverse = new Edge<T>(edge.getEnd(), edge.getStart(),
                                              edge.getFlow(), false);

                /* Link the two together. */
                forward.setReverse(reverse);
                reverse.setReverse(forward);

                /* Add both to the graph. */
                graph.get(edge.getStart()).add(forward);
                graph.get(edge.getEnd()).add(reverse);
            }
        }
    }

    /**
     * Returns an iterator that can traverse all the nodes in the graph.
     *
     * @return An iterator that traverses the nodes of the graph.
     */
    public Iterator<T> iterator() {
        return graph.keySet().iterator();
    }

    /**
     * Returns an immutable view of the edges leaving a particular node.
     *
     * @param node The node to look up.
     * @return The set of edges leaving that node.
     * @throws NoSuchElementException If the node does not exist.
     */
    public List<Edge<T>> edgesFrom(T node) {
        /* Look up the list of edges, reporting an error if nothing is found. */
        List<Edge<T>> edges = graph.get(node);
        if (edges == null)
            throw new NoSuchElementException("Node " + node + " does not exist.");

        return Collections.unmodifiableList(edges);
    }
}
