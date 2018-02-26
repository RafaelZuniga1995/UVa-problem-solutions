import java.io.IOException;
import java.util.Scanner;

public class Main2 {
	// Scanner
	private Scanner scanner = new Scanner(System.in);
	private int[][] nums;
	private int[][] sums;
	int N;

	/*
	 * like ferry problem, for every row u gotta find the subset of greatest
	 * sum.
	 */
	public Main2() {
		N = scanner.nextInt();
		nums = new int[N][N];
		sums = new int[N][N];

		int max = Integer.MIN_VALUE;

		// load numbers
		for (int i = 0; i < N; i++) {
			int currentMax = 0; // max value rectangle up to row i
			int leadingCo = 0;

			// have
			currentMax = 0;
			int maxSum = 0;
			int maxLeft = 0;
			int maxRight = 0;
			int maxUp = 0;
			int maxDown = 0;
			int lb = 0;
			int ub = 0;

			// read ith row first
			for (int j = 0; j < N; j++) {
				// read number
				nums[i][j] = scanner.nextInt();
				System.out.print(nums[i][j] + " ");

				// first copy nums to sums
				sums[i][j] = nums[i][j];

				if (i > 0)
					sums[i][j] = sums[i - 1][j] + nums[i][j];

			}

			// now that we have the ith row of sums ready
			// lets find the largest subset of this row
			// and keep track of their indices
			// i is L and k is R// so lb and up
			for (int k = i; k < N; k++) {
				for (int j = 0; j < N; j++) {

				}
			}

			// update total max

			System.out.println(" area: " + currentMax + "  Max: " + max);
		}

		// have
		int currentMax = 0; // local max
		int maxSum = 0; // overall max
		int maxLeft = 0; // for rect
		int maxRight = 0;
		int maxUp = 0; // for rectangle
		int maxDown = 0;
		int lb = 0; // for sub arrays
		int ub = 0;

		// we have nums and sums loaded up
		System.out.println("-------------");

		// left pointer
		for (int i = 0; i < N; i++) {

			// right pointer
			for (int j = i; j < N; j++) {

				// kadane
				int[] toKadane = sums[j];
				//currentMax = toKadane[0];
				//maxSum = toKadane[0];
				
				currentMax = toKadane[0];
				maxSum = Integer.MIN_VALUE;

				printRow(toKadane);

				// if we find a potential rectangle after we already have one
				// we cannot update maxLeft unless we are sure this new rect
				// is bigger, so we use this to check that if the new found rect
				// ever becomes bigger than maxSum, then we update maxLeft
				boolean possibleFind = false;
				for (int k = 1; k < N; k++) {
					// currentMax += toKadane[k];
					// System.out.println();
					// System.out.println("now cMax " + currentMax + " maxSum "
					// + maxSum );
					if (toKadane[k] > currentMax && currentMax < 0 && toKadane[k] > maxSum) {
						currentMax = toKadane[k];
						maxSum = toKadane[k];
						lb = k;
						ub = k;
						maxLeft = k;
						maxRight = k;
						//possibleFind = false;
						// System.out.println("pt1");
					} else if (toKadane[k] > currentMax && currentMax < 0) {
						currentMax = toKadane[k];
						possibleFind = true;
						lb = k;
						ub = k;
					} else {
						currentMax += toKadane[k];
						// System.out.println("pt2 toK[" + k + "], currentMax =
						// " + currentMax);
					}

					if (currentMax > maxSum) {
						if (maxLeft == 1 && toKadane[0] > 0)
							maxLeft = 0;
						// System.out.println("pt3");
						if (possibleFind){
							maxLeft = lb;
						}
						maxSum = currentMax;
						maxRight = k;
					}
					// System.out.println("then" + currentMax + " maxSum " +
					// maxSum );
				}
				System.out.print(" area: " + currentMax + "  Max: " + maxSum + "  L: " + lb + " R: " + ub);
				System.out.println("  | maxLeft: " + maxLeft + "  maxRight: " + maxRight);
			}
		}

		printSums();
		System.out.println(max);

	}

	private void printRow(int[] toKadane) {
		// TODO Auto-generated method stub
		for (int i = 0; i < toKadane.length; i++)
			System.out.print(toKadane[i] + " ");
	}

	private void printSums() {
		System.out.print("--------------------");
		// find every single possible rectangle
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				System.out.print(sums[i][j] + " ");
			}
			System.out.println();
		}

	}

	public static void main(String args[]) throws IOException {
		Main3 solver = new Main3();
	}
}
