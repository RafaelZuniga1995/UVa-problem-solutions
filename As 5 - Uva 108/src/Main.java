import java.io.IOException;
import java.util.Scanner;

public class Main {
	// Scanner
	private Scanner scanner = new Scanner(System.in);
	private int[][] nums;
	private int[][] offset;
	private int[][] sums;
	int N;

	/*
	 * like ferry problem, for every row u gotta find the subset of greatest
	 * sum.
	 */
	public Main() {
		N = scanner.nextInt();
		nums = new int[N][N];
		offset = new int[N][N];
		sums = new int[N][N];

		int max = Integer.MIN_VALUE;

		// load numbers
		for (int i = 0; i < N; i++) {
			int currentMax = 0; // max value rectangle up to row i
			int leadingCo = 0;

			for (int j = 0; j < N; j++) {
				// read number
				nums[i][j] = scanner.nextInt();
				System.out.print(nums[i][j] + " ");

				// first copy nums to sums
				sums[i][j] = nums[i][j];

				if (i > 0) {
					// second, consequetively sum with previous row if
					// it yields a higher value
					int cNum = nums[i][j];
					if (sums[i - 1][j] + cNum > cNum) {
						sums[i][j] = sums[i - 1][j] + cNum;

						// record how many rows are being used for the
						// value at sums[i][j]
						offset[i][j] += offset[i - 1][j] + 1;
					}

				}

				// update largest rectangle up to ith row
				if (j > 0) {
					int cOffset = offset[i][j - 1] - offset[i][j];
					if (cOffset != 0 && leadingCo != offset[i][j]) {

						if (cOffset > 0) {

							// System.out.print(" + offset ");
							int makeup = 0;
							for (int k = i; k >= i - cOffset; k--)
								makeup += nums[k][j];
							// System.out.print(currentMax + "makeup: " + makeup
							// + " ");
							currentMax += makeup;
						} else {
							// System.out.print(" - offset ");
							int makeup = 0;
							for (int k = i + cOffset; k >= 0; k--)
								makeup += nums[k][j];
							// System.out.print(currentMax + "makeup: " + makeup
							// + " ");
							currentMax -= makeup;
						}

					} else
						currentMax += sums[i][j];
					
					// new
					if (currentMax < 0) {
						currentMax = 0;
					}

				} else {
					
						currentMax += sums[i][j];
						leadingCo = offset[i][j];
					
						// new
						if (currentMax < 0) {
							currentMax = 0;
						}
					// System.out.print("leading: " + leadingCo + " ");
				}
				// update total max
				if (currentMax > max)
					max = currentMax;

			}

			System.out.println(" area: " + currentMax + "  Max: " + max);
		}

		System.out.println(max);

	}

	private void printoffsets() {
		System.out.println("--------------");

		// find every single possible rectangle
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				System.out.print(offset[i][j] + " ");
			}
			System.out.println();
		}

	}

	private void printSums() {
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
