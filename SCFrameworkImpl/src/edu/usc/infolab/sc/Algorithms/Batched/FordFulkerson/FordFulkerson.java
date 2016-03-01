package edu.usc.infolab.sc.Algorithms.Batched.FordFulkerson;

/*****************************************************************************
 * File: FordFulkerson.java
 * Author: Keith Schwarz (htiek@cs.stanford.edu)
 *
 * An implementation of the Ford-Fulkerson algorithm for computing maximum s-t
 * flows in a graph.  The algorithm, which dates back to 1956, is one of the
 * oldest and simplest max-flow algorithms.  The algorithm works by maintaining
 * a candidate max flow, constructing the residual graph for that max flow,
 * then searching for an augmenting s-t path in the graph.  If one is found,
 * then the maximum possible flow is pushed along that path and the process is
 * repeated.  Otherwise, because no augmenting paths exist, the flow must be
 * maximum and the algorithm reports this resulting flow.  Assuming that the
 * flow network has integral capacities, each time an augmenting path is found
 * the s-t flow increases by at least one, and so the algorithm is guaranteed
 * to terminate in after at most F searches, where F is the value of the max-
 * flow.
 *
 * Constructing and maintaining the residual graph ends up being reasonably
 * straightforward.  We can initially create the residual graph in O(m + n)
 * time by cloning the original graph and adding a reverse edge for each edge
 * in the graph.  Whenever we augment along some path of length k, we can
 * update the residual graph in O(k) time by simply adjusting the capacities
 * of the forward and reverse edges for each of the edges along the path.
 *
 * The only remaining task is finding some augmenting path from s to t.  This
 * can be done using a depth-first search in the graph, which takes O(m + n)
 * time.  As mentioned earlier, there can be at most F iterations of the
 * algorithm, and so the overall runtime for these phases is O((m + n)F).  The
 * net runtime of the algorithm is similarly O((m + n)F), since the search
 * time dominates the time required to construct the graph.  If we assume that
 * the graph is connected, m + n = O(m), and so the total runtime is O(mF).
 * This is not a polynomial-time algorithm, though, because the value of F can
 * be exponentially larger than the size of the input.  However, for graphs
 * where the flow can be easily bounded (for example, if all capacities are
 * zero or one), then the runtime of this algorithm is quite good.  In fact,
 * several polynomial-time max-flow algorithms, such as the scaling version of
 * this algorithm, use it as a subroutine.
 */

import java.util.*;        // For Deque

public final class FordFulkerson {
    /**
     * Given a graph and a pair of nodes s and t, produces a maximum s-t flow
     * in that graph.
     *
     * @param g The graph to search.
     * @param s The start node of the flow.
     * @param t The end node of the flow.
     * @return f A flow network for g containing a maximum s/t flow.
     * @throws IllegalArgumentException If any of the edges in the input graph
     *                                  have negative length.
     * @throws NoSuchElementException If s or t are not nodes in the graph.
     */
    public static <T> FlowNetwork<T> maxFlow(IntegralDirectedGraph<T> g, T s, T t) {
        /* Construct the structure of the resulting flow network. */
        FlowNetwork<T> result = new FlowNetwork<T>();

        /* Copy over nodes. */
        for (T node: g)
            result.addNode(node);

        /* Copy over edges. */
        for (T node: g)
            for (Map.Entry<T, Integer> edge: g.edgesFrom(node).entrySet())
                result.addEdge(node, edge.getKey()).setCapacity(edge.getValue());

        /* Compute a max-flow in this flow network. */
        findMaxFlow(result, s, t);
        return result;
    }

    /**
     * Given a flow network and a pair of nodes s and t, produces a maximum
     * s-t in that network.  Any flow that already exists in the input network
     * will be used as a guess of the maximum flow.
     *
     * @param g The flow graph to search.
     * @param s The start node of the flow.
     * @param t The end node of the flow.
     * @throws NoSuchElementException If s or t are not nodes in the graph.
     */
    public static <T> void findMaxFlow(FlowNetwork<T> g, T s, T t) {
        /* Confirm that s and t are valid. */
        if (!g.containsNode(s) || !g.containsNode(t))
            throw new NoSuchElementException("Start and end nodes must be in hthe flow network!");

        /* Silly edge-case check: we need to confirm that s and t aren't the
         * same node.  If they are, then the definition of a maximum s-t flow
         * forces the flow to be zero.  However, we'll always be able to find
         * an s-t path in the residual graph, since no matter how much flow we
         * push across edges we can't disconnect the node from itself.  We'll
         * therefore check for this case here and if the nodes are the same,
         * we'll hand back a flow network with zero flow in it.
         */
        if (isEqual(s, t)) return;

        /* Next, construct the residual flow graph for this flow network.  We
         * will continuously search it for augmenting paths.
         */
        ResidualGraph<T> gResidual = new ResidualGraph<T>(g);

        /* Continuously search for an augmenting path from s to t in the
         * residual graph.  When none can be found, we'll get back the empty
         * path, and so this loop will stop.
         */
        while (true) {
            /* Find an augmenting s-t path in the residual graph. */
            Deque<ResidualGraph.Edge<T>> path = findPath(s, t, gResidual);

            /* If we didn't find a path (signaled by the above function handing
             * back null), we're done.
             */
            if (path == null) break;

            /* Augment along this path. */
            augmentPath(path);
        }

        /* We now have a max flow because no augmenting paths are left.  Take
         * the data from our residual graph and use it to fill in the flow in
         * the resulting flow network.
         */
        for (T node: gResidual) {
            for (ResidualGraph.Edge<T> edge: gResidual.edgesFrom(node)) {
                /* If this is not an original edge, then its capacity is the
                 * amount of flow pushed along the original edge in the graph.
                 * Set this flow appropriately.
                 */
                if (!edge.isOriginal())
                    g.getEdge(edge.getEnd(), edge.getStart()).setFlow(edge.getCapacity());
            }
        }
    }

    /**
     * Given two objects, both possibly null, returns whether those objects
     * are either both null or equal to one another.
     *
     * @param one The first object.
     * @param two The second object.
     * @return Whether the two are equal, including the case where they're
     *         both null.
     */
    private static <T> boolean isEqual(T one, T two) {
        /* If either are null, they're equal only if they're both null. */
        if (one == null || two == null)
            return one == null && two == null;

        /* Otherwise they're both non-null and we can fall back on a standard
         * comparison.
         */
        return one.equals(two);
    }

    /**
     * Searches for a path between the start node and the destination node.
     * If such a path exists, the list of edges in it is returned.  Otherwise,
     * null is returned to signal that no path exists.
     *
     * @param start The starting node of the search.
     * @param dest The destination node of the search.
     * @param graph The residual graph to search in.
     * @return An augmenting path in the graph, or null if none exists.
     */
    private static <T> Deque<ResidualGraph.Edge<T>> findPath(T start, T dest,
                                              ResidualGraph<T> graph) {
        /* Construct a set of visited nodes so we don't hit the same node
         * twice, then fire off a standard DFS using that set.
         */
        return findPathRec(start, dest, graph, new HashSet<T>());
    }

    /**
     * Recursively explores a residual graph, starting at the node indicated
     * by start and searching for a particular destination node.  If a path is
     * found, it is returned.  Otherwise, null is returned.
     *
     * @param start The starting node of the search.
     * @param dest The destination node of the search.
     * @param graph The residual graph to search in.
     * @param visited The set of nodes previously visited by the search.
     * @return An augmenting path in the graph, or null if none exists.
     */
    private static <T> Deque<ResidualGraph.Edge<T>> findPathRec(T start, T dest,
                                                 ResidualGraph<T> graph,
                                                 Set<T> visited) {
        /* Base cases - if we've been here before, no path can be found, since
         * if one existed we would have already found it.
         */
        if (visited.contains(start)) return null;

        /* Otherwise, mark that we've been here. */
        visited.add(start);

        /* Other base case - if we hit the destination, then the path is the
         * trivial empty path from the start node to itself.
         */
        if (isEqual(start, dest))
            return new ArrayDeque<ResidualGraph.Edge<T>>();

        /* Otherwise, recursively explore outward from this node, seeing if
         * we can find any path.
         */
        for (ResidualGraph.Edge<T> edge: graph.edgesFrom(start)) {
            /* If this edge has no capacity, we can't continue the search
             * along it.
             */
            if (edge.getCapacity() == 0) continue;

            /* Recursively explore outward. */
            Deque<ResidualGraph.Edge<T>> result = findPathRec(edge.getEnd(), dest,
                                               graph, visited);

            /* If we found a path, then the real path would be this edge,
             * followed by that path.
             */
            if (result != null) {
                result.addFirst(edge);
                return result;
            }
        }

        /* If we arrived here, then none of the outgoing edges can reach the
         * destination and no path exists.
         */
        return null;
    }

    /**
     * Augments the flow along an augmenting path by the minimum remaining
     * capacity along some edge.
     *
     * @param path The augmenting path.
     */
    private static <T> void augmentPath(Deque<ResidualGraph.Edge<T>> path) {
        /* First, determine the smallest capacity of this path; that's the
         * amount we're going to augment by.  Initially we'll say that this is
         * the maximum amount possible; this value will get overwritten the
         * first time we visit a node.
         */
        int capacity = Integer.MAX_VALUE;
        for (ResidualGraph.Edge<T> edge: path)
            capacity = Math.min(capacity, edge.getCapacity());

        /* Push this much flow across all the nodes. */
        for (ResidualGraph.Edge<T> edge: path)
            edge.addFlow(capacity);
    }
}