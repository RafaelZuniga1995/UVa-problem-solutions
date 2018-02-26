import java.io.IOException;
import java.util.Scanner;

public class Main3 {
	// Scanner
	private Scanner scanner = new Scanner(System.in);
	private int[][] nums;
	int N;

	/*
	 * like ferry problem, for every row u gotta find the subset of greatest
	 * sum.
	 */
	public Main3() {
		while (scanner.hasNextInt()) {
			N = scanner.nextInt();
			nums = new int[N][N];

			// load numbers
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					nums[i][j] = scanner.nextInt();
					// System.out.print(nums[i][j] + " ");

				}
				// System.out.println();
			}

			printLargestRect();
		}

	}

	private void printLargestRect() {
		// have
		int rectLeft = 0;
		int rectRight = 0;
		int rectUp = 0; // for rectangle
		int rectDown = 0;

		int curRectSum = 0;
		int maxRectSum = Integer.MIN_VALUE;

		// we have nums and sums loaded up

		// left pointer
		for (int i = 0; i < N; i++) {
			int[] toKadane = new int[N];

			// right pointer
			for (int j = i; j < N; j++) {

				int currentMax = 0; // local max
				int maxsubset = 0; // max subarray sum
				int lb = 0; // for sub arrays
				int maxLeft = 0; // for subarrays
				int maxRight = 0;

				// load sums during the process
				for (int p = 0; p < nums.length; p++) {
					toKadane[p] += nums[j][p];
				}
				currentMax = toKadane[0];
				maxsubset = toKadane[0];
				//printRow(toKadane);

				// if we find a potential rectangle after we already have one
				// we cannot update maxLeft unless we are sure this new rect
				// is bigger, so we use this to check that if the new found rect
				// ever becomes bigger than maxSum, then we update maxLeft
				boolean possibleFind = false;
				for (int k = 1; k < N; k++) {

					// first case: toKadane[k] is better off by itself
					if (toKadane[k] > currentMax && currentMax < 0 && toKadane[k] > maxsubset) {
						currentMax = toKadane[k];
						maxsubset = toKadane[k];
						lb = k;
						maxLeft = k;
						maxRight = k;
						// System.out.println("pt1");
						// possibleFind = false;
					} else if (toKadane[k] > currentMax && currentMax < 0) {
						// potential new subset in subarray
						currentMax = toKadane[k];
						possibleFind = true;
						lb = k;
						// System.out.println("pt2");
					} else {
						currentMax += toKadane[k];
					}

					if (currentMax > maxsubset) {
						if (maxLeft == 1 && toKadane[0] > 0)
							maxLeft = 0;
						// System.out.println("pt3");
						if (possibleFind) {
							maxLeft = lb;
						}
						maxsubset = currentMax;
						maxRight = k;
					}
				}

				// update rectangle
				if (maxsubset > maxRectSum) {
					maxRectSum = maxsubset;
					rectLeft = maxLeft;
					rectRight = maxRight;
					rectUp = i;
					rectDown = j;

				}

				/*
				 * System.out.println("L: " + i + " R: " + j);
				 * System.out.println("currentSum: " + currentMax);
				 * System.out.println("maxSum: " + maxRectSum);
				 * System.out.println("left: " + rectUp); System.out.println(
				 * "right: " + rectDown); System.out.println("up: " + rectLeft);
				 * System.out.println("down: " + rectRight);
				 * System.out.println();
				 */
			}
		}

		System.out.println(maxRectSum);
	}

	private void printRow(int[] toKadane) {
		// TODO Auto-generated method stub
		for (int i = 0; i < toKadane.length; i++)
			System.out.print(toKadane[i] + " ");
		System.out.println();
	}

	public static void main(String args[]) throws IOException {
		Main3 solver = new Main3();
	}
}
