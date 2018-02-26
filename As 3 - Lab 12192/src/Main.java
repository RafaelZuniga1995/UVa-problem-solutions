import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Uva 12192: Grapevine
 * created by: Rafael Zuniga
 */

class Main {

	private int[][] mountainHeights;
	private Scanner scanner = new Scanner(System.in);

	private int N; // col,
	private int M; // row
	private int lower;
	private int upper;

	// load data and solve
	public Main() throws IOException {

		while (scanner.hasNextLine()) {
			N = scanner.nextInt();
			M = scanner.nextInt();
			if (N == 0 || M == 0)
				return;

			loadAreaOfInterest();
			int numQueries = scanner.nextInt();
			for (int i = 0; i < numQueries; i++) {
				solveQuery();
			}
			System.out.println("-");
		}
	}

	/*
	 * reads the lower bound and the upper bound and determines the largest side
	 * of a contiguous square area with heights within the bounds
	 */
	private void solveQuery() {
		lower = scanner.nextInt();
		upper = scanner.nextInt();

		// current largest side of a square
		int maxLength = 0;

		// for every row in the 2d array O(N)
		for (int row = 0; row < N; row++) {

			// find top left corner of the square O(logM)
			int col = binarySearch(lower, mountainHeights[row]);
			if (col == -1) // then no number in this row satisfies the bounds
				continue;

			// avoid checking smaller squares than the current largest one
			int startingRow = row + maxLength;
			int startingCol = col + maxLength;

			if (mountainHeights[row][col] >= lower && startingRow < N && startingCol < M
					&& mountainHeights[startingRow][startingCol] <= upper) {
				// found potential larger square

				// travel array diagonally starting from
				// [startingRow][startingCol]
				int currLength = maxLength; // account for the max length already
										// found
				for (int col1 = startingCol, row1 = startingRow; col1 < M && row1 < N; col1++, row1++) {
					if (mountainHeights[row1][col1] <= upper) {
						currLength++;
					} else
						break;
					if (currLength > maxLength)
						maxLength = currLength;
				}
			}

		}
		System.out.println(maxLength);
	}

	/*
	 * This binary search is modified so that either it finds the lowest index
	 * of the given key or the index of the next biggest number after key if key
	 * is not found.
	 */
	public int binarySearch(int key, int[] a) {
		return search(key, a, 0, a.length);
	}

	// binary search
	private int search(int key, int[] a, int lo, int hi) {

		int mid = lo + (hi - lo) / 2;
		if (mid >= a.length)
			return -1;
		if (hi <= lo)
			if (a[mid] >= lower && a[mid] <= upper)
				return mid;
			else
				return -1;
		if (key <= a[mid]) // if it hits value, it still keeps looking
			return search(key, a, lo, mid);
		else
			return search(key, a, mid + 1, hi);
	}

	/*
	 * loads the input specifying N rows and M columns into a 2D array we call
	 * mountainHeights.
	 */
	private void loadAreaOfInterest() {
		mountainHeights = new int[N][M];
		for (int row = 0; row < N; row++)
			for (int col = 0; col < M; col++)
				mountainHeights[row][col] = scanner.nextInt();
	}

	public static void main(String args[]) throws IOException {
		Main solver = new Main();
	}
}
