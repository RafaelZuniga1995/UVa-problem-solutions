import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class MainFirst {

	private int[][] mountainHeights;
	private int[][] copy;
	private Scanner scanner = new Scanner(System.in);

	private int N; // col,
	private int M; // row

	// load data and solve
	public MainFirst() throws IOException {

		while (scanner.hasNextLine()) {
			N = scanner.nextInt();
			M = scanner.nextInt();
			if (N == 0 || M == 0)
				return;

			loadAreaOfInterest();
			int numQueries = scanner.nextInt();
			for (int i = 0; i < numQueries; i++)
				solveQuery();
			System.out.println("-");
		}
	}

	private void solveQuery() {
		int lowerBound = scanner.nextInt();
		int upperBound = scanner.nextInt();

		ArrayList<Integer> xValues = new ArrayList<Integer>();
		ArrayList<Integer> yValues = new ArrayList<Integer>();


		getFinalLength(lowerBound, upperBound);

	}

	/*
	 * gets possible corners and computes the square right away
	 */
	private void getFinalLength(int lower, int upper) {
		int maxLength = 0;

		for (int row = 0; row < N; row++)
			for (int col = 0; col < M; col++) {
				int sideS = 1;
				if (copy[row][col] > lower) { // found potential top left corner
					// find the square here
					for (int col1 = col + 1, row1 = row + 1; col1 < M && row1 < N; col1++, row1++) {
						// System.out.println("now checking: " + "(" + col + ",
						// " + row + ") " + copy[row][col]);
						if (copy[row1][col1] != -1) {
							sideS++;
						} else {
							if (sideS > maxLength)
								maxLength = sideS;
							break;
						}
						if (sideS > maxLength)
							maxLength = sideS;
					}

					break;
				}

			}
		System.out.println(maxLength);
	}



	// loads the N by M 2d array
	private void loadAreaOfInterest() {

		mountainHeights = new int[N][M];
		copy = new int[N][M];
		for (int row = 0; row < N; row++)
			for (int col = 0; col < M; col++)
				mountainHeights[row][col] = scanner.nextInt();
	}

	public static void main(String args[]) throws IOException {
		MainFirst solver = new MainFirst();
	}
}
