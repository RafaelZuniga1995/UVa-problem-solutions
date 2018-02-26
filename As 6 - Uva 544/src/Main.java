import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;

/**
 * @author Rafael
 * Uva 544- Heavy Cargo
 */
public class Main {

	private Scanner sc = new Scanner(System.in);

	private int n; // number of cities
	private int r; // number of road segments
	private Hashtable<String, ArrayList<String>> cities; // key is a city, value
															// are the cities
	
	private Hashtable<String, Integer> enumerate;
	int scenario;

	public Main() {
		scenario = 1;
		while (true) {
			this.n = sc.nextInt(); // # of cities
			this.r = sc.nextInt(); // # of road segments
			if (n == 0 && r == 0)
				return;

			System.out.println("Scenario #" + scenario);

			cities = new Hashtable<String, ArrayList<String>>();
			enumerate = new Hashtable<String, Integer>();

			// store data
			int V = 0;
			for (int i = 0; i < r; i++) {
				String city1 = sc.next();
				String city2 = sc.next();
				String weight = sc.next();

				// enumarate the cities
				if (!enumerate.containsKey(city1)) {
					enumerate.put(city1, V++);

				}
				if (!enumerate.containsKey(city2)) {
					enumerate.put(city2, V++);
				}

				// if city1 still not in table
				if (!cities.containsKey(city1)) {
					ArrayList<String> edgeTo = new ArrayList<String>();
					edgeTo.add(city2); // add city it leads to
					edgeTo.add(weight); // right after, add the weight of this
										// segment
					cities.put(city1, edgeTo);
				} else {
					// get edgeTo list of city1 and add city2 to it
					cities.get(city1).add(city2);
					cities.get(city1).add(weight);
				}

			}

			// use data to make a Edge weighted directed graph.
			EdgeWeightedDigraph G = new EdgeWeightedDigraph(n);
			for (String s : cities.keySet()) {
				ArrayList<String> edgeTo = cities.get(s);

				for (int i = 0; i < edgeTo.size(); i += 2) {
					// edgeTo.get(i + 1) is the weight from city s to the city
					// in edgeTo.get(i)
					int weight = Integer.parseInt(edgeTo.get(i + 1));

					// edges should go back and forth
					DirectedEdge e = new DirectedEdge(enumerate.get(s), enumerate.get(edgeTo.get(i)), weight);
					G.addEdge(e);
					DirectedEdge e1 = new DirectedEdge(enumerate.get(edgeTo.get(i)), enumerate.get(s), weight);
					G.addEdge(e1);
				}

			}

			String start = sc.next();
			String dest = sc.next();
			DijkstraSP dij = new DijkstraSP(G, enumerate.get(start), enumerate.get(dest));
			System.out.println(dij.bottleToSink() + " tons\n");
			scenario++;
		}
	}
	
	public static void main(String[] args) {
		Main m = new Main();
	}

	/**
	 * The <tt>DijkstraSP</tt> class represents a data type for solving the
	 * single-source shortest paths problem in edge-weighted digraphs where the
	 * edge weights are nonnegative.
	 * <p>
	 * This implementation uses Dijkstra's algorithm with a binary heap. The
	 * constructor takes time proportional to <em>E</em> log <em>V</em>, where
	 * <em>V</em> is the number of vertices and <em>E</em> is the number of
	 * edges. Afterwards, the <tt>distTo()</tt> and <tt>hasPathTo()</tt> methods
	 * take constant time and the <tt>pathTo()</tt> method takes time
	 * proportional to the number of edges in the shortest path returned.
	 * <p>
	 * For additional documentation, see
	 * <a href="http://algs4.cs.princeton.edu/44sp">Section 4.4</a> of
	 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
	 *
	 * @author Robert Sedgewick
	 * @author Kevin Wayne
	 */
	public class DijkstraSP {
		//private double[] distTo; // distTo[v] = distance of shortest s->v path
		private DirectedEdge[] edgeTo; // edgeTo[v] = last edge on shortest s->v
										// path
		private IndexMinPQ<Double> pq; // priority queue of vertices
		private double[] bottleTo;

		private double bottleToSink;

		/**
		 * Computes a shortest-paths tree from the source vertex <tt>s</tt> to
		 * every other vertex in the edge-weighted digraph <tt>G</tt>.
		 *
		 * @param G
		 *            the edge-weighted digraph
		 * @param s
		 *            the source vertex
		 * @throws IllegalArgumentException
		 *             if an edge weight is negative
		 * @throws IllegalArgumentException
		 *             unless 0 &le; <tt>s</tt> &le; <tt>V</tt> - 1
		 */
		public DijkstraSP(EdgeWeightedDigraph G, int s, int t) {
			for (DirectedEdge e : G.edges()) {
				if (e.weight() < 0)
					throw new IllegalArgumentException("edge " + e + " has negative weight");
			}

			edgeTo = new DirectedEdge[G.V()];
			bottleTo = new double[G.V()];
			for (int v = 0; v < G.V(); v++) {
				bottleTo[v] = 0;
			}
			
			bottleTo[s] = Double.POSITIVE_INFINITY;

			// relax vertices in order of distance from s
			pq = new IndexMinPQ<Double>(G.V());
			pq.insert(s, bottleTo[s]);
			
			while (!pq.isEmpty()) {
				int v = pq.delMin();
				for (DirectedEdge e : G.adj(v))
					relax(e);
			}
			bottleToSink = bottleTo[t];
		}

		// relax edge e and update pq if changed
		private void relax(DirectedEdge e) {
			int v = e.from(), w = e.to();

			// instead of computing the shortest path, we change how we update the
			// values so that we end up keeping track of the "bottle neck distance"
			// from source to each other vertice
			if (bottleTo[w] < Math.max(bottleTo[w], Math.min(bottleTo[v], e.weight))) {
				
				bottleTo[w] = Math.max(bottleTo[w], Math.min(bottleTo[v], e.weight));
				edgeTo[w] = e;
				if (pq.contains(w))
					pq.increaseKey(w, bottleTo[w]);
				else
					pq.insert(w, bottleTo[w]);
			}
		}
		
		public int bottleToSink(){
			return (int) bottleToSink;
		}
	}

	/**
	 * The <tt>EdgeWeightedDigraph</tt> class represents a edge-weighted digraph
	 * of vertices named 0 through <em>V</em> - 1, where each directed edge is
	 * of type {@link DirectedEdge} and has a real-valued weight. It supports
	 * the following two primary operations: add a directed edge to the digraph
	 * and iterate over all of edges incident from a given vertex. It also
	 * provides methods for returning the number of vertices <em>V</em> and the
	 * number of edges <em>E</em>. Parallel edges and self-loops are permitted.
	 * <p>
	 * This implementation uses an adjacency-lists representation, which is a
	 * vertex-indexed array of @link{Bag} objects. All operations take constant
	 * time (in the worst case) except iterating over the edges incident from a
	 * given vertex, which takes time proportional to the number of such edges.
	 * <p>
	 * For additional documentation, see
	 * <a href="http://algs4.cs.princeton.edu/44sp">Section 4.4</a> of
	 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
	 *
	 * @author Robert Sedgewick
	 * @author Kevin Wayne
	 */
	public class EdgeWeightedDigraph {

		private final int V; // number of vertices in this digraph
		private int E; // number of edges in this digraph
		private ArrayList<ArrayList<DirectedEdge>> adj; // adj[v] = adjacency
														// list for vertex v
		private int[] indegree; // indegree[v] = indegree of vertex v

		/**
		 * Initializes an empty edge-weighted digraph with <tt>V</tt> vertices
		 * and 0 edges.
		 *
		 * @param V
		 *            the number of vertices
		 * @throws IllegalArgumentException
		 *             if <tt>V</tt> < 0
		 */
		public EdgeWeightedDigraph(int V) {
			if (V < 0)
				throw new IllegalArgumentException("Number of vertices in a Digraph must be nonnegative");
			this.V = V;
			this.E = 0;
			this.indegree = new int[V];
			adj = (ArrayList<ArrayList<DirectedEdge>>) new ArrayList<ArrayList<DirectedEdge>>();
			for (int v = 0; v < this.V; v++)
				adj.add(new ArrayList<DirectedEdge>());
		}

		/**
		 * Returns the number of vertices in this edge-weighted digraph.
		 *
		 * @return the number of vertices in this edge-weighted digraph
		 */
		public int V() {
			return V;
		}

		/**
		 * Returns the number of edges in this edge-weighted digraph.
		 *
		 * @return the number of edges in this edge-weighted digraph
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
		 * Adds the directed edge <tt>e</tt> to this edge-weighted digraph.
		 *
		 * @param e
		 *            the edge
		 * @throws IndexOutOfBoundsException
		 *             unless endpoints of edge are between 0 and V-1
		 */
		public void addEdge(DirectedEdge e) {
			int v = e.from();
			int w = e.to();
			validateVertex(v);
			validateVertex(w);
			adj.get(v).add(e);
			indegree[w]++;
			E++;
		}

		/**
		 * Returns the directed edges incident from vertex <tt>v</tt>.
		 *
		 * @param v
		 *            the vertex
		 * @return the directed edges incident from vertex <tt>v</tt> as an
		 *         Iterable
		 * @throws IndexOutOfBoundsException
		 *             unless 0 <= v < V
		 */
		public Iterable<DirectedEdge> adj(int v) {
			validateVertex(v);
			return adj.get(v);
		}

		/**
		 * Returns the number of directed edges incident from vertex <tt>v</tt>.
		 * This is known as the <em>outdegree</em> of vertex <tt>v</tt>.
		 *
		 * @param v
		 *            the vertex
		 * @return the outdegree of vertex <tt>v</tt>
		 * @throws IndexOutOfBoundsException
		 *             unless 0 <= v < V
		 */
		public int outdegree(int v) {
			validateVertex(v);
			return adj.get(v).size();
		}

		/**
		 * Returns the number of directed edges incident to vertex <tt>v</tt>.
		 * This is known as the <em>indegree</em> of vertex <tt>v</tt>.
		 *
		 * @param v
		 *            the vertex
		 * @return the indegree of vertex <tt>v</tt>
		 * @throws IndexOutOfBoundsException
		 *             unless 0 <= v < V
		 */
		public int indegree(int v) {
			validateVertex(v);
			return indegree[v];
		}

		/**
		 * Returns a string representation of this edge-weighted digraph.
		 *
		 * @return the number of vertices <em>V</em>, followed by the number of
		 *         edges <em>E</em>, followed by the <em>V</em> adjacency lists
		 *         of edges
		 */
		public String toString() {
			StringBuilder s = new StringBuilder();
			s.append(V + " " + E + "\n");
			for (int v = 0; v < V; v++) {
				s.append(v + ": ");
				for (DirectedEdge e : adj.get(v)) {
					s.append(e + "  ");
				}
				s.append("\n");
			}
			return s.toString();
		}

		/**
		 * Returns all directed edges in this edge-weighted digraph. To iterate
		 * over the edges in this edge-weighted digraph, use foreach notation:
		 * <tt>for (DirectedEdge e : G.edges())</tt>.
		 *
		 * @return all edges in this edge-weighted digraph, as an iterable
		 */
		public Iterable<DirectedEdge> edges() {
			ArrayList<DirectedEdge> list = new ArrayList<DirectedEdge>();
			for (int v = 0; v < V; v++) {
				for (DirectedEdge e : adj.get(v)) {
					list.add(e);
				}
			}
			return list;
		}
	}

	public class DirectedEdge {
		private final int v;
		private final int w;
		private final double weight;

		/**
		 * Initializes a directed edge from vertex <tt>v</tt> to vertex
		 * <tt>w</tt> with the given <tt>weight</tt>.
		 * 
		 * @param v
		 *            the tail vertex
		 * @param w
		 *            the head vertex
		 * @param weight
		 *            the weight of the directed edge
		 * @throws IndexOutOfBoundsException
		 *             if either <tt>v</tt> or <tt>w</tt> is a negative integer
		 * @throws IllegalArgumentException
		 *             if <tt>weight</tt> is <tt>NaN</tt>
		 */
		public DirectedEdge(int v, int w, double weight) {
			if (v < 0)
				throw new IndexOutOfBoundsException("Vertex names must be nonnegative integers");
			if (w < 0)
				throw new IndexOutOfBoundsException("Vertex names must be nonnegative integers");
			if (Double.isNaN(weight))
				throw new IllegalArgumentException("Weight is NaN");
			this.v = v;
			this.w = w;
			this.weight = weight;
		}

		/**
		 * Returns the tail vertex of the directed edge.
		 * 
		 * @return the tail vertex of the directed edge
		 */
		public int from() {
			return v;
		}

		/**
		 * Returns the head vertex of the directed edge.
		 * 
		 * @return the head vertex of the directed edge
		 */
		public int to() {
			return w;
		}

		/**
		 * Returns the weight of the directed edge.
		 * 
		 * @return the weight of the directed edge
		 */
		public double weight() {
			return weight;
		}

		/**
		 * Returns a string representation of the directed edge.
		 * 
		 * @return a string representation of the directed edge
		 */
		public String toString() {
			return v + "->" + w + " " + String.format("%5.2f", weight);
		}
	}

	/**
	 * The <tt>IndexMinPQ</tt> class represents an indexed priority queue of
	 * generic keys. It supports the usual <em>insert</em> and
	 * <em>delete-the-minimum</em> operations, along with <em>delete</em> and
	 * <em>change-the-key</em> methods. In order to let the client refer to keys
	 * on the priority queue, an integer between 0 and maxN-1 is associated with
	 * each key&mdash;the client uses this integer to specify which key to
	 * delete or change. It also supports methods for peeking at the minimum
	 * key, testing if the priority queue is empty, and iterating through the
	 * keys.
	 * <p>
	 * This implementation uses a binary heap along with an array to associate
	 * keys with integers in the given range. The <em>insert</em>,
	 * <em>delete-the-minimum</em>, <em>delete</em>, <em>change-key</em>,
	 * <em>decrease-key</em>, and <em>increase-key</em> operations take
	 * logarithmic time. The <em>is-empty</em>, <em>size</em>,
	 * <em>min-index</em>, <em>min-key</em>, and <em>key-of</em> operations take
	 * constant time. Construction takes time proportional to the specified
	 * capacity.
	 * <p>
	 * For additional documentation, see
	 * <a href="http://algs4.cs.princeton.edu/24pq">Section 2.4</a> of
	 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
	 *
	 * @author Robert Sedgewick
	 * @author Kevin Wayne
	 *
	 * @param <Key>
	 *            the generic type of key on this priority queue
	 */
	public class IndexMinPQ<Key extends Comparable<Key>> implements Iterable<Integer> {
		private int maxN; // maximum number of elements on PQ
		private int N; // number of elements on PQ
		private int[] pq; // binary heap using 1-based indexing
		private int[] qp; // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
		private Key[] keys; // keys[i] = priority of i

		/**
		 * Initializes an empty indexed priority queue with indices between
		 * <tt>0</tt> and <tt>maxN - 1</tt>.
		 * 
		 * @param maxN
		 *            the keys on this priority queue are index from <tt>0</tt>
		 *            <tt>maxN - 1</tt>
		 * @throws IllegalArgumentException
		 *             if <tt>maxN</tt> &lt; <tt>0</tt>
		 */
		public IndexMinPQ(int maxN) {
			if (maxN < 0)
				throw new IllegalArgumentException();
			this.maxN = maxN;
			keys = (Key[]) new Comparable[maxN + 1]; // make this of length
														// maxN??
			pq = new int[maxN + 1];
			qp = new int[maxN + 1]; // make this of length maxN??
			for (int i = 0; i <= maxN; i++)
				qp[i] = -1;
		}

		/**
		 * Returns true if this priority queue is empty.
		 *
		 * @return <tt>true</tt> if this priority queue is empty; <tt>false</tt>
		 *         otherwise
		 */
		public boolean isEmpty() {
			return N == 0;
		}

		/**
		 * Is <tt>i</tt> an index on this priority queue?
		 *
		 * @param i
		 *            an index
		 * @return <tt>true</tt> if <tt>i</tt> is an index on this priority
		 *         queue; <tt>false</tt> otherwise
		 * @throws IndexOutOfBoundsException
		 *             unless 0 &le; <tt>i</tt> &lt; <tt>maxN</tt>
		 */
		public boolean contains(int i) {
			if (i < 0 || i >= maxN)
				throw new IndexOutOfBoundsException();
			return qp[i] != -1;
		}

		/**
		 * Returns the number of keys on this priority queue.
		 *
		 * @return the number of keys on this priority queue
		 */
		public int size() {
			return N;
		}

		/**
		 * Associates key with index <tt>i</tt>.
		 *
		 * @param i
		 *            an index
		 * @param key
		 *            the key to associate with index <tt>i</tt>
		 * @throws IndexOutOfBoundsException
		 *             unless 0 &le; <tt>i</tt> &lt; <tt>maxN</tt>
		 * @throws IllegalArgumentException
		 *             if there already is an item associated with index
		 *             <tt>i</tt>
		 */
		public void insert(int i, Key key) {
			if (i < 0 || i >= maxN)
				throw new IndexOutOfBoundsException();
			if (contains(i))
				throw new IllegalArgumentException("index is already in the priority queue");
			N++;
			qp[i] = N;
			pq[N] = i;
			keys[i] = key;
			swim(N);
		}

		/**
		 * Returns an index associated with a minimum key.
		 *
		 * @return an index associated with a minimum key
		 * @throws NoSuchElementException
		 *             if this priority queue is empty
		 */
		public int minIndex() {
			if (N == 0)
				throw new NoSuchElementException("Priority queue underflow");
			return pq[1];
		}

		/**
		 * Returns a minimum key.
		 *
		 * @return a minimum key
		 * @throws NoSuchElementException
		 *             if this priority queue is empty
		 */
		public Key minKey() {
			if (N == 0)
				throw new NoSuchElementException("Priority queue underflow");
			return keys[pq[1]];
		}

		/**
		 * Removes a minimum key and returns its associated index.
		 * 
		 * @return an index associated with a minimum key
		 * @throws NoSuchElementException
		 *             if this priority queue is empty
		 */
		public int delMin() {
			if (N == 0)
				throw new NoSuchElementException("Priority queue underflow");
			int min = pq[1];
			exch(1, N--);
			sink(1);
			assert min == pq[N + 1];
			qp[min] = -1; // delete
			keys[min] = null; // to help with garbage collection
			pq[N + 1] = -1; // not needed
			return min;
		}

		/**
		 * Returns the key associated with index <tt>i</tt>.
		 *
		 * @param i
		 *            the index of the key to return
		 * @return the key associated with index <tt>i</tt>
		 * @throws IndexOutOfBoundsException
		 *             unless 0 &le; <tt>i</tt> &lt; <tt>maxN</tt>
		 * @throws NoSuchElementException
		 *             no key is associated with index <tt>i</tt>
		 */
		public Key keyOf(int i) {
			if (i < 0 || i >= maxN)
				throw new IndexOutOfBoundsException();
			if (!contains(i))
				throw new NoSuchElementException("index is not in the priority queue");
			else
				return keys[i];
		}

		/**
		 * Change the key associated with index <tt>i</tt> to the specified
		 * value.
		 *
		 * @param i
		 *            the index of the key to change
		 * @param key
		 *            change the key associated with index <tt>i</tt> to this
		 *            key
		 * @throws IndexOutOfBoundsException
		 *             unless 0 &le; <tt>i</tt> &lt; <tt>maxN</tt>
		 * @throws NoSuchElementException
		 *             no key is associated with index <tt>i</tt>
		 */
		public void changeKey(int i, Key key) {
			if (i < 0 || i >= maxN)
				throw new IndexOutOfBoundsException();
			if (!contains(i))
				throw new NoSuchElementException("index is not in the priority queue");
			keys[i] = key;
			swim(qp[i]);
			sink(qp[i]);
		}

		/**
		 * Change the key associated with index <tt>i</tt> to the specified
		 * value.
		 *
		 * @param i
		 *            the index of the key to change
		 * @param key
		 *            change the key associated with index <tt>i</tt> to this
		 *            key
		 * @throws IndexOutOfBoundsException
		 *             unless 0 &le; <tt>i</tt> &lt; <tt>maxN</tt>
		 * @deprecated Replaced by {@link #changeKey(int, Key)}.
		 */
		public void change(int i, Key key) {
			changeKey(i, key);
		}

		/**
		 * Decrease the key associated with index <tt>i</tt> to the specified
		 * value.
		 *
		 * @param i
		 *            the index of the key to decrease
		 * @param key
		 *            decrease the key associated with index <tt>i</tt> to this
		 *            key
		 * @throws IndexOutOfBoundsException
		 *             unless 0 &le; <tt>i</tt> &lt; <tt>maxN</tt>
		 * @throws IllegalArgumentException
		 *             if key &ge; key associated with index <tt>i</tt>
		 * @throws NoSuchElementException
		 *             no key is associated with index <tt>i</tt>
		 */
		public void decreaseKey(int i, Key key) {
			if (i < 0 || i >= maxN)
				throw new IndexOutOfBoundsException();
			if (!contains(i))
				throw new NoSuchElementException("index is not in the priority queue");
			if (keys[i].compareTo(key) <= 0)
				throw new IllegalArgumentException(
						"Calling decreaseKey() with given argument would not strictly decrease the key");
			keys[i] = key;
			swim(qp[i]);
		}

		/**
		 * Increase the key associated with index <tt>i</tt> to the specified
		 * value.
		 *
		 * @param i
		 *            the index of the key to increase
		 * @param key
		 *            increase the key associated with index <tt>i</tt> to this
		 *            key
		 * @throws IndexOutOfBoundsException
		 *             unless 0 &le; <tt>i</tt> &lt; <tt>maxN</tt>
		 * @throws IllegalArgumentException
		 *             if key &le; key associated with index <tt>i</tt>
		 * @throws NoSuchElementException
		 *             no key is associated with index <tt>i</tt>
		 */
		public void increaseKey(int i, Key key) {
			if (i < 0 || i >= maxN)
				throw new IndexOutOfBoundsException();
			if (!contains(i))
				throw new NoSuchElementException("index is not in the priority queue");
			if (keys[i].compareTo(key) >= 0)
				throw new IllegalArgumentException(
						"Calling increaseKey() with given argument would not strictly increase the key");
			keys[i] = key;
			sink(qp[i]);
		}

		/**
		 * Remove the key associated with index <tt>i</tt>.
		 *
		 * @param i
		 *            the index of the key to remove
		 * @throws IndexOutOfBoundsException
		 *             unless 0 &le; <tt>i</tt> &lt; <tt>maxN</tt>
		 * @throws NoSuchElementException
		 *             no key is associated with index <t>i</tt>
		 */
		public void delete(int i) {
			if (i < 0 || i >= maxN)
				throw new IndexOutOfBoundsException();
			if (!contains(i))
				throw new NoSuchElementException("index is not in the priority queue");
			int index = qp[i];
			exch(index, N--);
			swim(index);
			sink(index);
			keys[i] = null;
			qp[i] = -1;
		}

		/***************************************************************************
		 * General helper functions.
		 ***************************************************************************/
		private boolean greater(int i, int j) {
			return keys[pq[i]].compareTo(keys[pq[j]]) > 0;
		}

		private void exch(int i, int j) {
			int swap = pq[i];
			pq[i] = pq[j];
			pq[j] = swap;
			qp[pq[i]] = i;
			qp[pq[j]] = j;
		}

		/***************************************************************************
		 * Heap helper functions.
		 ***************************************************************************/
		private void swim(int k) {
			while (k > 1 && greater(k / 2, k)) {
				exch(k, k / 2);
				k = k / 2;
			}
		}

		private void sink(int k) {
			while (2 * k <= N) {
				int j = 2 * k;
				if (j < N && greater(j, j + 1))
					j++;
				if (!greater(k, j))
					break;
				exch(k, j);
				k = j;
			}
		}

		/***************************************************************************
		 * Iterators.
		 ***************************************************************************/

		/**
		 * Returns an iterator that iterates over the keys on the priority queue
		 * in ascending order. The iterator doesn't implement <tt>remove()</tt>
		 * since it's optional.
		 *
		 * @return an iterator that iterates over the keys in ascending order
		 */
		public Iterator<Integer> iterator() {
			return new HeapIterator();
		}

		private class HeapIterator implements Iterator<Integer> {
			// create a new pq
			private IndexMinPQ<Key> copy;

			// add all elements to copy of heap
			// takes linear time since already in heap order so no keys move
			public HeapIterator() {
				copy = new IndexMinPQ<Key>(pq.length - 1);
				for (int i = 1; i <= N; i++)
					copy.insert(pq[i], keys[pq[i]]);
			}

			public boolean hasNext() {
				return !copy.isEmpty();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}

			public Integer next() {
				if (!hasNext())
					throw new NoSuchElementException();
				return copy.delMin();
			}
		}
	}
}