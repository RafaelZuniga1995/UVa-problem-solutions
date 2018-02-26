import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Main {
	// Scanner
	private Scanner scanner = new Scanner(System.in);

	private int R; // number of access points
	private int H; // number of houses
	private float[] allHouses;
	List<Float> all;

	public Main() {
		int numCases = scanner.nextInt();
		for (int i = 0; i < numCases; i++) {
			if (!scanner.hasNextInt()) {
				System.out.println(0.f);
				continue;
			}
			this.R = scanner.nextInt();
			this.H = scanner.nextInt();
			this.allHouses = new float[H];

			all = new LinkedList<Float>();

			// read H houses
			for (int h = 0; h < H; h++) {
				allHouses[h] = scanner.nextInt();
				all.add(allHouses[h]);
			}

			// if R == 1 then just end it here
			if (R == 1) {
				System.out.println((allHouses[H - 1] - allHouses[0]) / 2);
				continue;
			}

			if (R >= H) {
				System.out.println(0.f);
				continue;
			}

			// all segments are stored here
			Comparator<Segment> cmp = new Segment(all, 0, H - 1);

			// add first segment (all the houses)
			PriorityQueue<Segment> queue = new PriorityQueue<Segment>(cmp);
			queue.add(new Segment(all, 0, H - 1));

			// load information of first segment
			// float currentLargestLength = allSegments.get(0).length;
			float currentRLocation = queue.peek().router;
			Segment currentSeg = queue.peek();

			Segment prevSeg = null;

			for (int r = 2; r <= R; r++) {
				// last index of left segment

				// first find segment of longest size
				// note: if current router is on top of a house
				// it doesnt matter what segment we choose to add a router to
				// next

				currentSeg = queue.remove();
				System.out.println("pq chose: " + currentSeg.houses + " router at: " + currentSeg.router
						 + " left: " + currentSeg.left + " right: " + currentSeg.right);
				int endOfLeftSeg = search(currentSeg.router, allHouses, currentSeg.left, currentSeg.right);

				// left partition
				int lb = currentSeg.left;
				int rb = endOfLeftSeg + 1;
				// System.out.println("lb: " + lb + " rb: " + rb);
				Segment left = new Segment(all.subList(lb, rb), lb, rb - 1);
				
				System.out.println("left");
				print(left);

				// right partition
				int lb1 = endOfLeftSeg + 1;
				int rb1 = currentSeg.right;
				Segment right;
				if (lb1 == currentSeg.right)
					right = new Segment(all.subList(lb1, rb1 + 1), lb1, rb1);
				else
					right = new Segment(all.subList(lb1, rb1 + 1), lb1, rb1);

				System.out.println("right");
				print(right);
				// remove parent segment

				// add the two new segments.
				queue.add(left);
				queue.add(right);

				if (left.maxDistance > right.maxDistance)
					prevSeg = left;
				else
					prevSeg = right;

			}

			
			float maxD = Float.MIN_VALUE;
			for (Segment s : queue)
				if (s.maxDistance > maxD)
					maxD = s.maxDistance;
			System.out.println(maxD);
			//System.out.println(prevSeg.maxDistance);

		}

	}
	
	private void print(Segment left) {
		System.out.println("maxDist: " + left.maxDistance);
		System.out.println("Length: " + left.length);
		System.out.println("router: " + left.router);
		System.out.println("left: " + left.left + " right: " + left.right);
		for (Float f : left.houses)
			System.out.print(f + " ");
		System.out.println("\n");

	}

	private class Segment implements Comparator<Segment> {
		List<Float> houses;
		int left;
		int right;
		private float length;
		private float maxDistance; // max dist between any house
		private float router; // where the access point of this segment lies

		private Segment(List<Float> all, int left, int right) {
			this.houses = all;
			this.left = left;
			this.right = right;

			if (left == right) { // if left == right then only there is only 1
									// house in this segment
				maxDistance = 0; // router is right on the house
				router = houses.get(0); // only house in list
			} else {
				router = (houses.get(0) + houses.get(houses.size() - 1)) / 2;
				maxDistance = houses.get(houses.size() - 1) - router;
				length = houses.get(houses.size() - 1) - houses.get(0);

			}
		}

		@Override
		public int compare(Segment a, Segment b) {
			if (a.length < b.length)
				return 1;
			else if (a.length > b.length)
				return -1;
			else
				return 0;
		}
	}

	/*
	 * This binary search is modified so if it misses, it returns the index of
	 * the next lowest value closest to the key.
	 */
	public int binarySearch(float key, float[] a) {
		return search(key, a, 0, a.length);
	}

	// binary search
	private int search(float key, float[] a, int lo, int hi) {

		int mid = lo + (hi - lo) / 2;
		if (mid >= a.length)
			return -1;
		if (hi <= lo) // not found right
			return mid - 1;

		if (key < a[mid]) // if it hits value, it still keeps looking
			return search(key, a, lo, mid);
		else if (key > a[mid])
			return search(key, a, mid + 1, hi);
		else // search hit
			return mid;
	}

	public static void main(String args[]) throws IOException {
		Main2 solver = new Main2();
	}
}
