import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * @author Rafael
 *	Uva 11506
 */
public class Main {

	private Scanner scanner = new Scanner(System.in);
	private int M;
	private int W;

	// table<ComputerId, Cost>
	Hashtable<Integer, Integer> compCosts;

	public Main() {
		while (scanner.hasNext()) {

			// solve case
			this.M = scanner.nextInt(); // # computers, counting boss and server
			this.W = scanner.nextInt(); // # wires
			
			if (M == 0 && W == 0)
				return;
			if (W == 0)
				System.out.println(0);
			compCosts = new Hashtable<Integer, Integer>();
			
			// read in computers
			for (int m = 0; m < M - 2; m++) {
				int id = scanner.nextInt();
				int costOfComp = scanner.nextInt();
				compCosts.put(id, costOfComp);
			}

			// start Flow network for edges and computers
			FlowNetwork fl = new FlowNetwork(M);
			// read in wires
			for (int w = 1; w <= W; w++) {
				// this wire connect j with k
				int j = scanner.nextInt();
				int k = scanner.nextInt();
				int costOfWire = scanner.nextInt(); // cost to destroy

				// if edge to server
				if (k == M) {
					// there is no computer to check
					FlowEdge edge = new FlowEdge(j, k, costOfWire);
					fl.addEdge(edge);
				} else {
					// add min of edge cost or computer cost
					// same j k, but with cost of destroying computer k
					int minCost = Math.min(costOfWire, compCosts.get(k));
					FlowEdge edge = new FlowEdge(j, k, minCost);
					FlowEdge edge2 = new FlowEdge(k, j, minCost);
					fl.addEdge(edge);
					fl.addEdge(edge2);
				}
			}

			
			// get Max flow
			FordFulkerson ff = new FordFulkerson(fl, 1, M);
			System.out.println((int) ff.value());
		}
	}

	public static void main(String args[]) {
		Main main = new Main();
	}

	/**
	 * The <tt>FordFulkerson</tt> class represents a data type for computing a
	 * <em>maximum st-flow</em> and <em>minimum st-cut</em> in a flow network.
	 * <p>
	 * This implementation uses the <em>Ford-Fulkerson</em> algorithm with the
	 * <em>shortest augmenting path</em> heuristic. The constructor takes time
	 * proportional to <em>E V</em> (<em>E</em> + <em>V</em>) in the worst case
	 * and extra space (not including the network) proportional to <em>V</em>,
	 * where <em>V</em> is the number of vertices and <em>E</em> is the number
	 * of edges. In practice, the algorithm will run much faster. Afterwards,
	 * the <tt>inCut()</tt> and <tt>value()</tt> methods take constant time.
	 * <p>
	 * If the capacities and initial flow values are all integers, then this
	 * implementation guarantees to compute an integer-valued maximum flow. If
	 * the capacities and floating-point numbers, then floating-point roundoff
	 * error can accumulate.
	 * <p>
	 * For additional documentation, see
	 * <a href="http://algs4.cs.princeton.edu/64maxflow">Section 6.4</a> of
	 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
	 *
	 * @author Robert Sedgewick
	 * @author Kevin Wayne
	 */
	private class FordFulkerson {
		private static final double FLOATING_POINT_EPSILON = 1E-11;

		private boolean[] marked; // marked[v] = true iff s->v path in residual
									// graph
		private FlowEdge[] edgeTo; // edgeTo[v] = last edge on shortest residual
									// s->v path
		private double value; // current value of max flow

		/**
		 * Compute a maximum flow and minimum cut in the network <tt>G</tt> from
		 * vertex <tt>s</tt> to vertex <tt>t</tt>.
		 *
		 * @param G
		 *            the flow network
		 * @param s
		 *            the source vertex
		 * @param t
		 *            the sink vertex
		 * @throws IndexOutOfBoundsException
		 *             unless 0 <= s < V
		 * @throws IndexOutOfBoundsException
		 *             unless 0 <= t < V
		 * @throws IllegalArgumentException
		 *             if s = t
		 * @throws IllegalArgumentException
		 *             if initial flow is infeasible
		 */
		public FordFulkerson(FlowNetwork G, int s, int t) {
			validate(s, G.V());
			validate(t, G.V());
			if (s == t)
				throw new IllegalArgumentException("Source equals sink");
			if (!isFeasible(G, s, t))
				throw new IllegalArgumentException("Initial flow is infeasible");

			// while there exists an augmenting path, use it
			value = excess(G, t);
			while (hasAugmentingPath(G, s, t)) {

				// compute bottleneck capacity
				double bottle = Double.POSITIVE_INFINITY;
				for (int v = t; v != s; v = edgeTo[v].other(v)) {
					bottle = Math.min(bottle, edgeTo[v].residualCapacityTo(v));
				}

				// augment flow
				for (int v = t; v != s; v = edgeTo[v].other(v)) {
					edgeTo[v].addResidualFlowTo(v, bottle);
				}

				value += bottle;
			}

			// check optimality conditions
			assert check(G, s, t);
		}

		/**
		 * Returns the value of the maximum flow.
		 *
		 * @return the value of the maximum flow
		 */
		public double value() {
			return value;
		}

		/**
		 * Returns true if the specified vertex is on the <tt>s</tt> side of the
		 * mincut.
		 *
		 * @return <tt>true</tt> if vertex <tt>v</tt> is on the <tt>s</tt> side
		 *         of the micut; <tt>false</tt> otherwise
		 * @throws IndexOutOfBoundsException
		 *             unless 0 <= v < V
		 */
		public boolean inCut(int v) {
			validate(v, marked.length);
			return marked[v];
		}

		// throw an exception if v is outside prescibed range
		private void validate(int v, int V) {
			if (v < 0 || v >= V)
				throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V - 1));
		}

		// is there an augmenting path?
		// if so, upon termination edgeTo[] will contain a parent-link
		// representation of such a path
		// this implementation finds a shortest augmenting path (fewest number
		// of edges),
		// which performs well both in theory and in practice
		private boolean hasAugmentingPath(FlowNetwork G, int s, int t) {
			edgeTo = new FlowEdge[G.V()];
			marked = new boolean[G.V()];

			// breadth-first search
			Queue<Integer> queue = new LinkedList<Integer>(); // edited
			queue.add(s); // to use our own queue
			marked[s] = true;
			while (!queue.isEmpty() && !marked[t]) {
				int v = queue.remove();

				for (FlowEdge e : G.adj(v)) {
					int w = e.other(v);

					// if residual capacity from v to w
					if (e.residualCapacityTo(w) > 0) {
						if (!marked[w]) {
							edgeTo[w] = e;
							marked[w] = true;
							queue.add(w);
						}
					}
				}
			}

			// is there an augmenting path?
			return marked[t];
		}

		// return excess flow at vertex v
		private double excess(FlowNetwork G, int v) {
			double excess = 0.0;
			for (FlowEdge e : G.adj(v)) {
				if (v == e.from())
					excess -= e.flow();
				else
					excess += e.flow();
			}
			return excess;
		}

		// return excess flow at vertex v
		private boolean isFeasible(FlowNetwork G, int s, int t) {

			// check that capacity constraints are satisfied
			for (int v = 0; v < G.V(); v++) {
				for (FlowEdge e : G.adj(v)) {
					if (e.flow() < -FLOATING_POINT_EPSILON || e.flow() > e.capacity() + FLOATING_POINT_EPSILON) {
						System.err.println("Edge does not satisfy capacity constraints: " + e);
						return false;
					}
				}
			}

			// check that net flow into a vertex equals zero, except at source
			// and sink
			if (Math.abs(value + excess(G, s)) > FLOATING_POINT_EPSILON) {
				System.err.println("Excess at source = " + excess(G, s));
				System.err.println("Max flow         = " + value);
				return false;
			}
			if (Math.abs(value - excess(G, t)) > FLOATING_POINT_EPSILON) {
				System.err.println("Excess at sink   = " + excess(G, t));
				System.err.println("Max flow         = " + value);
				return false;
			}
			for (int v = 0; v < G.V(); v++) {
				if (v == s || v == t)
					continue;
				else if (Math.abs(excess(G, v)) > FLOATING_POINT_EPSILON) {
					System.err.println("Net flow out of " + v + " doesn't equal zero");
					return false;
				}
			}
			return true;
		}

		// check optimality conditions
		private boolean check(FlowNetwork G, int s, int t) {

			// check that flow is feasible
			if (!isFeasible(G, s, t)) {
				System.err.println("Flow is infeasible");
				return false;
			}

			// check that s is on the source side of min cut and that t is not
			// on source side
			if (!inCut(s)) {
				System.err.println("source " + s + " is not on source side of min cut");
				return false;
			}
			if (inCut(t)) {
				System.err.println("sink " + t + " is on source side of min cut");
				return false;
			}

			// check that value of min cut = value of max flow
			double mincutValue = 0.0;
			for (int v = 0; v < G.V(); v++) {
				for (FlowEdge e : G.adj(v)) {
					if ((v == e.from()) && inCut(e.from()) && !inCut(e.to()))
						mincutValue += e.capacity();
				}
			}

			if (Math.abs(mincutValue - value) > FLOATING_POINT_EPSILON) {
				System.err.println("Max flow value = " + value + ", min cut value = " + mincutValue);
				return false;
			}

			return true;
		}
	}

	/**
	 * Flow network
	 */
	private class FlowNetwork {
		private final String NEWLINE = System.getProperty("line.separator");

		private final int V;
		private int E;
		private ArrayList<ArrayList<FlowEdge>> adj;

		/**
		 * Initializes an empty flow network with <tt>V</tt> vertices and 0
		 * edges. param V the number of vertices
		 * 
		 * @throws java.lang.IllegalArgumentException
		 *             if <tt>V</tt> < 0
		 */
		public FlowNetwork(int V) {
			if (V < 0)
				throw new IllegalArgumentException("Number of vertices in a Graph must be nonnegative");
			this.V = V + 1; // let node 0 be skipped
			this.E = 0;
			//System.out.println("V: " + this.V);
			adj = (ArrayList<ArrayList<FlowEdge>>) new ArrayList<ArrayList<FlowEdge>>();
			for (int v = 0; v < this.V; v++)
				adj.add(new ArrayList<FlowEdge>());
		}

		/**
		 * Returns the number of vertices in the edge-weighted graph.
		 * 
		 * @return the number of vertices in the edge-weighted graph
		 */
		public int V() {
			return V;
		}

		/**
		 * Returns the number of edges in the edge-weighted graph.
		 * 
		 * @return the number of edges in the edge-weighted graph
		 */
		public int E() {
			return E;
		}

		// throw an IndexOutOfBoundsException unless 0 <= v < V
		private void validateVertex(int v) {
			if (v < 0 || v >= V)
				throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V - 1));
		}

		/**
		 * Adds the edge <tt>e</tt> to the network.
		 * 
		 * @param e
		 *            the edge
		 * @throws java.lang.IndexOutOfBoundsException
		 *             unless endpoints of edge are between 0 and V-1
		 */
		public void addEdge(FlowEdge e) {
			int v = e.from();
			int w = e.to();
			validateVertex(v);
			validateVertex(w);
			adj.get(v).add(e);
			adj.get(w).add(e);
			E++;
		}

		/**
		 * Returns the edges incident on vertex <tt>v</tt> (includes both edges
		 * pointing to and from <tt>v</tt>).
		 * 
		 * @param v
		 *            the vertex
		 * @return the edges incident on vertex <tt>v</tt> as an Iterable
		 * @throws java.lang.IndexOutOfBoundsException
		 *             unless 0 <= v < V
		 */
		public Iterable<FlowEdge> adj(int v) {
			validateVertex(v);
			return adj.get(v);
		}

		// return list of all edges - excludes self loops
		public Iterable<FlowEdge> edges() {
			ArrayList<FlowEdge> list = new ArrayList<FlowEdge>();
			for (int v = 0; v < V; v++)
				for (FlowEdge e : adj(v)) {
					if (e.to() != v)
						list.add(e);
				}
			return list;
		}

		/**
		 * Returns a string representation of the flow network. This method
		 * takes time proportional to <em>E</em> + <em>V</em>.
		 * 
		 * @return the number of vertices <em>V</em>, followed by the number of
		 *         edges <em>E</em>, followed by the <em>V</em> adjacency lists
		 */
		public String toString() {
			StringBuilder s = new StringBuilder();
			s.append(V + " " + E + NEWLINE);
			for (int v = 0; v < V; v++) {
				s.append(v + ":  ");
				for (FlowEdge e : adj.get(v)) {
					if (e.to() != v)
						s.append(e + "  ");
				}
				s.append(NEWLINE);
			}
			return s.toString();
		}

	}

	/**
	 * Flow edge
	 */
	private class FlowEdge {
		private final int v; // from
		private final int w; // to
		private final double capacity; // capacity
		private double flow; // flow

		/**
		 * Initializes an edge from vertex <tt>v</tt> to vertex <tt>w</tt> with
		 * the given <tt>capacity</tt> and zero flow.
		 * 
		 * @param v
		 *            the tail vertex
		 * @param w
		 *            the head vertex
		 * @param capacity
		 *            the capacity of the edge
		 * @throws java.lang.IndexOutOfBoundsException
		 *             if either <tt>v</tt> or <tt>w</tt> is a negative integer
		 * @throws java.lang.IllegalArgumentException
		 *             if <tt>capacity</tt> is negative
		 */
		public FlowEdge(int v, int w, double capacity) {
			if (v < 0)
				throw new IndexOutOfBoundsException("Vertex name must be a nonnegative integer");
			if (w < 0)
				throw new IndexOutOfBoundsException("Vertex name must be a nonnegative integer");
			if (!(capacity >= 0.0))
				throw new IllegalArgumentException("Edge capacity must be nonnegaitve");
			this.v = v;
			this.w = w;
			this.capacity = capacity;
			this.flow = 0.0;
		}

		/**
		 * Initializes an edge from vertex <tt>v</tt> to vertex <tt>w</tt> with
		 * the given <tt>capacity</tt> and <tt>flow</tt>.
		 * 
		 * @param v
		 *            the tail vertex
		 * @param w
		 *            the head vertex
		 * @param capacity
		 *            the capacity of the edge
		 * @param flow
		 *            the flow on the edge
		 * @throws java.lang.IndexOutOfBoundsException
		 *             if either <tt>v</tt> or <tt>w</tt> is a negative integer
		 * @throws java.lang.IllegalArgumentException
		 *             if <tt>capacity</tt> is negative
		 * @throws java.lang.IllegalArgumentException
		 *             unless <tt>flow</tt> is between <tt>0.0</tt> and
		 *             <tt>capacity</tt>.
		 */
		public FlowEdge(int v, int w, double capacity, double flow) {
			if (v < 0)
				throw new IndexOutOfBoundsException("Vertex name must be a nonnegative integer");
			if (w < 0)
				throw new IndexOutOfBoundsException("Vertex name must be a nonnegative integer");
			if (!(capacity >= 0.0))
				throw new IllegalArgumentException("Edge capacity must be nonnegaitve");
			if (!(flow <= capacity))
				throw new IllegalArgumentException("Flow exceeds capacity");
			if (!(flow >= 0.0))
				throw new IllegalArgumentException("Flow must be nonnnegative");
			this.v = v;
			this.w = w;
			this.capacity = capacity;
			this.flow = flow;
		}

		/**
		 * Initializes a flow edge from another flow edge.
		 * 
		 * @param e
		 *            the edge to copy
		 */
		public FlowEdge(FlowEdge e) {
			this.v = e.v;
			this.w = e.w;
			this.capacity = e.capacity;
			this.flow = e.flow;
		}

		/**
		 * Returns the tail vertex of the edge.
		 * 
		 * @return the tail vertex of the edge
		 */
		public int from() {
			return v;
		}

		/**
		 * Returns the head vertex of the edge.
		 * 
		 * @return the head vertex of the edge
		 */
		public int to() {
			return w;
		}

		/**
		 * Returns the capacity of the edge.
		 * 
		 * @return the capacity of the edge
		 */
		public double capacity() {
			return capacity;
		}

		/**
		 * Returns the flow on the edge.
		 * 
		 * @return the flow on the edge
		 */
		public double flow() {
			return flow;
		}

		/**
		 * Returns the endpoint of the edge that is different from the given
		 * vertex (unless the edge represents a self-loop in which case it
		 * returns the same vertex).
		 * 
		 * @param vertex
		 *            one endpoint of the edge
		 * @return the endpoint of the edge that is different from the given
		 *         vertex (unless the edge represents a self-loop in which case
		 *         it returns the same vertex)
		 * @throws java.lang.IllegalArgumentException
		 *             if <tt>vertex</tt> is not one of the endpoints of the
		 *             edge
		 */
		public int other(int vertex) {
			if (vertex == v)
				return w;
			else if (vertex == w)
				return v;
			else
				throw new IllegalArgumentException("Illegal endpoint");
		}

		/**
		 * Returns the residual capacity of the edge in the direction to the
		 * given <tt>vertex</tt>.
		 * 
		 * @param vertex
		 *            one endpoint of the edge
		 * @return the residual capacity of the edge in the direction to the
		 *         given vertex If <tt>vertex</tt> is the tail vertex, the
		 *         residual capacity equals <tt>capacity() - flow()</tt>; if
		 *         <tt>vertex</tt> is the head vertex, the residual capacity
		 *         equals <tt>flow()</tt>.
		 * @throws java.lang.IllegalArgumentException
		 *             if <tt>vertex</tt> is not one of the endpoints of the
		 *             edge
		 */
		public double residualCapacityTo(int vertex) {
			if (vertex == v)
				return flow; // backward edge
			else if (vertex == w)
				return capacity - flow; // forward edge
			else
				throw new IllegalArgumentException("Illegal endpoint");
		}

		/**
		 * Increases the flow on the edge in the direction to the given vertex.
		 * If <tt>vertex</tt> is the tail vertex, this increases the flow on the
		 * edge by <tt>delta</tt>; if <tt>vertex</tt> is the head vertex, this
		 * decreases the flow on the edge by <tt>delta</tt>.
		 * 
		 * @param vertex
		 *            one endpoint of the edge
		 * @throws java.lang.IllegalArgumentException
		 *             if <tt>vertex</tt> is not one of the endpoints of the
		 *             edge
		 * @throws java.lang.IllegalArgumentException
		 *             if <tt>delta</tt> makes the flow on on the edge either
		 *             negative or larger than its capacity
		 * @throws java.lang.IllegalArgumentException
		 *             if <tt>delta</tt> is <tt>NaN</tt>
		 */
		public void addResidualFlowTo(int vertex, double delta) {
			if (vertex == v)
				flow -= delta; // backward edge
			else if (vertex == w)
				flow += delta; // forward edge
			else
				throw new IllegalArgumentException("Illegal endpoint");
			if (Double.isNaN(delta))
				throw new IllegalArgumentException("Change in flow = NaN");
			if (!(flow >= 0.0))
				throw new IllegalArgumentException("Flow is negative");
			if (!(flow <= capacity))
				throw new IllegalArgumentException("Flow exceeds capacity");
		}

		/**
		 * Returns a string representation of the edge.
		 * 
		 * @return a string representation of the edge
		 */
		public String toString() {
			return v + "->" + w + " " + flow + "/" + capacity;
		}
	}
}
