import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;

public class MainEdge {
	
	private Scanner scanner = new Scanner(System.in);
	private int numCases;
	
	private long[] roads;
	
	private EdgeWeightedGraph G;
	private int V;
	private int E;
	
	private int[] bits; // to generate all permutations
	
	private HashSet<Integer> Ids;
	
	private int maximalCount; // number of maximal independent sets found;
	private int sizeOfMaximum; // size of maximum independent set
	
	public MainEdge() {
		numCases = scanner.nextInt();
		
		// Solve case
		for (int i = 0; i < numCases; i++){
			V = scanner.nextInt();
			E = scanner.nextInt();
			bits = new int[V];
			//Ids = new HashSet<Integer>();
			
			G = new EdgeWeightedGraph(V);
			int idCount = 0;
			
			for (int j = 0; j < E; j++){
				int v = scanner.nextInt();
				int w = scanner.nextInt();
				
				Edge e = new Edge(v,w, 1, idCount++);
				G.addEdge(e);
			}
			
			
			maximalCount = 0;
			sizeOfMaximum = 0;
			permute(V);
			System.out.println(maximalCount + "\n" + sizeOfMaximum);
			
		}
	}

	// check whether no edge was found twice and that number
	// of edges == E. Then, it is an edgecover/ind set
	private void permute(int n) {
		if (n <= 0) {
			boolean indset = true;
			int currSizeIndSet = 0;
			
			// vertices in independent set, or vertices that are adjacent to a vertex
			// in the independent set
			HashSet<Integer> marked = new HashSet<Integer>();
			// check if bits is currently an ind set.
			Ids = new HashSet<Integer>();
			
			// for node i
			for (int i = 0; i < bits.length; i++){
				
				// if node i is in the current set/permutation
				if (bits[i] == 1){
					currSizeIndSet++;
					marked.add(i); // if bits[1] == 1, node i is marked
					// for edge adjacent to node i
					for (Edge e : G.adj(i)){
						
						// if node w in i-w is already in the hash set
						if (Ids.contains(e.other(i))){
							// return/break;
							
							indset = false;
						}else{
							// add w node to hashset
							Ids.add(i);
						}
						
						// for node marked
						if (!marked.contains(e.other(i)));
							marked.add(e.other(i));
						
						
					}
				}
				//System.out.print(bits[i] + " ");
			}
			//System.out.print(Ids.toString() + " ");
			//System.out.println("VM " + verticesMarked);
			if (indset && marked.size() == V){
				if (currSizeIndSet > sizeOfMaximum)
					sizeOfMaximum = currSizeIndSet;
				maximalCount++; // update how many maximal ind sets found.
				//System.out.println("IND SET");
				
			}
			
			//System.out.println(Arrays.toString(bits));
		} else {
			bits[n - 1] = 0;
			permute(n - 1);
			bits[n - 1] = 1;
			permute(n - 1);
		}
		
	}

	public static void main(String[] args) {
		MainEdge m = new MainEdge();
	}

	
	/**
	 *  The <tt>EdgeWeightedGraph</tt> class represents an edge-weighted
	 *  graph of vertices named 0 through <em>V</em> - 1, where each
	 *  undirected edge is of type {@link Edge} and has a real-valued weight.
	 *  It supports the following two primary operations: add an edge to the graph,
	 *  iterate over all of the edges incident to a vertex. It also provides
	 *  methods for returning the number of vertices <em>V</em> and the number
	 *  of edges <em>E</em>. Parallel edges and self-loops are permitted.
	 *  <p>
	 *  This implementation uses an adjacency-lists representation, which 
	 *  is a vertex-indexed array of @link{Bag} objects.
	 *  All operations take constant time (in the worst case) except
	 *  iterating over the edges incident to a given vertex, which takes
	 *  time proportional to the number of such edges.
	 *  <p>
	 *  For additional documentation,
	 *  see <a href="http://algs4.cs.princeton.edu/43mst">Section 4.3</a> of
	 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
	 *
	 *  @author Robert Sedgewick
	 *  @author Kevin Wayne
	 */
	public class EdgeWeightedGraph {
	    //private static final String NEWLINE = System.getProperty("line.separator");

	    private final int V;
	    private int E;
	    private Bag<Edge>[] adj;
	    
	    private int eCount;
	    
	    /**
	     * Initializes an empty edge-weighted graph with <tt>V</tt> vertices and 0 edges.
	     *
	     * @param  V the number of vertices
	     * @throws IllegalArgumentException if <tt>V</tt> < 0
	     */
	    public EdgeWeightedGraph(int V) {
	        if (V < 0) throw new IllegalArgumentException("Number of vertices must be nonnegative");
	        this.V = V;
	        this.E = 0;
	        adj = (Bag<Edge>[]) new Bag[V];
	        for (int v = 0; v < V; v++) {
	            adj[v] = new Bag<Edge>();
	        }
	    }


	    /**
	     * Initializes a new edge-weighted graph that is a deep copy of <tt>G</tt>.
	     *
	     * @param  G the edge-weighted graph to copy
	     */
	    public EdgeWeightedGraph(EdgeWeightedGraph G) {
	        this(G.V());
	        this.E = G.E();
	        for (int v = 0; v < G.V(); v++) {
	            // reverse so that adjacency list is in same order as original
	            Stack<Edge> reverse = new Stack<Edge>();
	            for (Edge e : G.adj[v]) {
	                reverse.push(e);
	            }
	            for (Edge e : reverse) {
	                adj[v].add(e);
	            }
	        }
	    }


	    /**
	     * Returns the number of vertices in this edge-weighted graph.
	     *
	     * @return the number of vertices in this edge-weighted graph
	     */
	    public int V() {
	        return V;
	    }

	    /**
	     * Returns the number of edges in this edge-weighted graph.
	     *
	     * @return the number of edges in this edge-weighted graph
	     */
	    public int E() {
	        return E;
	    }

	    // throw an IndexOutOfBoundsException unless 0 <= v < V
	    private void validateVertex(int v) {
	        if (v < 0 || v >= V)
	            throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
	    }

	    /**
	     * Adds the undirected edge <tt>e</tt> to this edge-weighted graph.
	     *
	     * @param  e the edge
	     * @throws IndexOutOfBoundsException unless both endpoints are between 0 and V-1
	     */
	    public void addEdge(Edge e) {
	        int v = e.either();
	        int w = e.other(v);
	        validateVertex(v);
	        validateVertex(w);
	        adj[v].add(e);
	        adj[w].add(e);
	        E++;
	    }

	    /**
	     * Returns the edges incident on vertex <tt>v</tt>.
	     *
	     * @param  v the vertex
	     * @return the edges incident on vertex <tt>v</tt> as an Iterable
	     * @throws IndexOutOfBoundsException unless 0 <= v < V
	     */
	    public Iterable<Edge> adj(int v) {
	        validateVertex(v);
	        return adj[v];
	    }

	    /**
	     * Returns the degree of vertex <tt>v</tt>.
	     *
	     * @param  v the vertex
	     * @return the degree of vertex <tt>v</tt>               
	     * @throws IndexOutOfBoundsException unless 0 <= v < V
	     */
	    public int degree(int v) {
	        validateVertex(v);
	        return adj[v].size();
	    }

	    /**
	     * Returns all edges in this edge-weighted graph.
	     * To iterate over the edges in this edge-weighted graph, use foreach notation:
	     * <tt>for (Edge e : G.edges())</tt>.
	     *
	     * @return all edges in this edge-weighted graph, as an iterable
	     */
	    public Iterable<Edge> edges() {
	        Bag<Edge> list = new Bag<Edge>();
	        for (int v = 0; v < V; v++) {
	            int selfLoops = 0;
	            for (Edge e : adj(v)) {
	                if (e.other(v) > v) {
	                    list.add(e);
	                }
	                // only add one copy of each self loop (self loops will be consecutive)
	                else if (e.other(v) == v) {
	                    if (selfLoops % 2 == 0) list.add(e);
	                    selfLoops++;
	                }
	            }
	        }
	        return list;
	    }

	    /**
	     * Returns a string representation of the edge-weighted graph.
	     * This method takes time proportional to <em>E</em> + <em>V</em>.
	     *
	     * @return the number of vertices <em>V</em>, followed by the number of edges <em>E</em>,
	     *         followed by the <em>V</em> adjacency lists of edges
	     */
	    public String toString() {
	        StringBuilder s = new StringBuilder();
	        s.append(V + " " + E + "\n");
	        for (int v = 0; v < V; v++) {
	            s.append(v + ": ");
	            for (Edge e : adj[v]) {
	                s.append(e + "  ");
	            }
	            s.append("\n");
	        }
	        return s.toString();
	    }

	}
	
	/**
	 *  The <tt>Bag</tt> class represents a bag (or multiset) of 
	 *  generic items. It supports insertion and iterating over the 
	 *  items in arbitrary order.
	 *  <p>
	 *  This implementation uses a singly-linked list with a static nested class Node.
	 *  See {@link LinkedBag} for the version from the
	 *  textbook that uses a non-static nested class.
	 *  The <em>add</em>, <em>isEmpty</em>, and <em>size</em> operations
	 *  take constant time. Iteration takes time proportional to the number of items.
	 *  <p>
	 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/13stacks">Section 1.3</a> of
	 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
	 *
	 *  @author Robert Sedgewick
	 *  @author Kevin Wayne
	 *
	 *  @param <Item> the generic type of an item in this bag
	 */
	public class Bag<Item> implements Iterable<Item> {
	    private Node<Item> first;    // beginning of bag
	    private int N;               // number of elements in bag

	    // helper linked list class
	    private class Node<Item> {
	        private Item item;
	        private Node<Item> next;
	    }

	    /**
	     * Initializes an empty bag.
	     */
	    public Bag() {
	        first = null;
	        N = 0;
	    }

	    /**
	     * Returns true if this bag is empty.
	     *
	     * @return <tt>true</tt> if this bag is empty;
	     *         <tt>false</tt> otherwise
	     */
	    public boolean isEmpty() {
	        return first == null;
	    }

	    /**
	     * Returns the number of items in this bag.
	     *
	     * @return the number of items in this bag
	     */
	    public int size() {
	        return N;
	    }

	    /**
	     * Adds the item to this bag.
	     *
	     * @param  item the item to add to this bag
	     */
	    public void add(Item item) {
	        Node<Item> oldfirst = first;
	        first = new Node<Item>();
	        first.item = item;
	        first.next = oldfirst;
	        N++;
	    }


	    /**
	     * Returns an iterator that iterates over the items in this bag in arbitrary order.
	     *
	     * @return an iterator that iterates over the items in this bag in arbitrary order
	     */
	    public Iterator<Item> iterator()  {
	        return new ListIterator<Item>(first);  
	    }

	    // an iterator, doesn't implement remove() since it's optional
	    private class ListIterator<Item> implements Iterator<Item> {
	        private Node<Item> current;

	        public ListIterator(Node<Item> first) {
	            current = first;
	        }

	        public boolean hasNext()  { return current != null;                     }
	        public void remove()      { throw new UnsupportedOperationException();  }

	        public Item next() {
	            if (!hasNext()) throw new NoSuchElementException();
	            Item item = current.item;
	            current = current.next; 
	            return item;
	        }
	    }

	   


	}
	
	/**
	 *  The <tt>Edge</tt> class represents a weighted edge in an 
	 *  {@link EdgeWeightedGraph}. Each edge consists of two integers
	 *  (naming the two vertices) and a real-value weight. The data type
	 *  provides methods for accessing the two endpoints of the edge and
	 *  the weight. The natural order for this data type is by
	 *  ascending order of weight.
	 *  <p>
	 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/43mst">Section 4.3</a> of
	 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
	 *
	 *  @author Robert Sedgewick
	 *  @author Kevin Wayne
	 */
	public class Edge implements Comparable<Edge> { 

	    private final int v;
	    private final int w;
	    private final double weight;
	    private final int id;

	    /**
	     * Initializes an edge between vertices <tt>v</tt> and <tt>w</tt> of
	     * the given <tt>weight</tt>.
	     *
	     * @param  v one vertex
	     * @param  w the other vertex
	     * @param  weight the weight of this edge
	     * @throws IndexOutOfBoundsException if either <tt>v</tt> or <tt>w</tt> 
	     *         is a negative integer
	     * @throws IllegalArgumentException if <tt>weight</tt> is <tt>NaN</tt>
	     */
	    public Edge(int v, int w, double weight, int id) {
	        if (v < 0) throw new IndexOutOfBoundsException("Vertex name must be a nonnegative integer");
	        if (w < 0) throw new IndexOutOfBoundsException("Vertex name must be a nonnegative integer");
	        if (Double.isNaN(weight)) throw new IllegalArgumentException("Weight is NaN");
	        this.v = v;
	        this.w = w;
	        this.weight = weight;
	        this.id = id;
	    }

	    /**
	     * Returns the weight of this edge.
	     *
	     * @return the weight of this edge
	     */
	    public double weight() {
	        return weight;
	    }

	    /**
	     * Returns either endpoint of this edge.
	     *
	     * @return either endpoint of this edge
	     */
	    public int either() {
	        return v;
	    }

	    /**
	     * Returns the endpoint of this edge that is different from the given vertex.
	     *
	     * @param  vertex one endpoint of this edge
	     * @return the other endpoint of this edge
	     * @throws IllegalArgumentException if the vertex is not one of the
	     *         endpoints of this edge
	     */
	    public int other(int vertex) {
	        if      (vertex == v) return w;
	        else if (vertex == w) return v;
	        else throw new IllegalArgumentException("Illegal endpoint");
	    }

	    /**
	     * Compares two edges by weight.
	     * Note that <tt>compareTo()</tt> is not consistent with <tt>equals()</tt>,
	     * which uses the reference equality implementation inherited from <tt>Object</tt>.
	     *
	     * @param  that the other edge
	     * @return a negative integer, zero, or positive integer depending on whether
	     *         the weight of this is less than, equal to, or greater than the
	     *         argument edge
	     */
	    @Override
	    public int compareTo(Edge that) {
	        if      (this.weight() < that.weight()) return -1;
	        else if (this.weight() > that.weight()) return +1;
	        else                                    return  0;
	    }

	    /**
	     * Returns a string representation of this edge.
	     *
	     * @return a string representation of this edge
	     */
	    public String toString() {
	        return String.format("%d-%d %.5f", v, w, weight);
	    }
	}
}
