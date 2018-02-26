import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	// Scanner
	private static Scanner scanner = new Scanner(System.in);

	int ferryLength;
	ArrayList<Integer> cars;
	boolean[] maximizingPort;
	int[][] dp;

	// if any vehicle is larger than ferry
	public Main() {
		int numCases = scanner.nextInt();

		for (int i = 0; i < numCases; i++) {

			ferryLength = scanner.nextInt() * 100;
			// System.out.println("max " + maxLengthCM);
			cars = new ArrayList<Integer>();
			// start a case
			int carsThatFit = 0;
			int metersCurrentlyRead = 0;
			int currentLength;
			while (true) {
				currentLength = scanner.nextInt();
				if (currentLength == 0) { // read until a 0 is read;
					break;
				}

				// figure out how many cars can fit
				metersCurrentlyRead += currentLength;
				if (metersCurrentlyRead <= ferryLength * 2) {
					carsThatFit++;
					cars.add(currentLength);
				}
			}

			int starboardUsed = 0;

			// cars that should go to port
			maximizingPort = maximizePort();

			// make sure all other cars fit in starboard
			for (int p = 0; p < carsThatFit; p++) {
				//System.out.println("starboard: " + starboardUsed + " carsN: " + carsThatFit);

				if (!maximizingPort[p])
					starboardUsed += cars.get(p);
				if (starboardUsed > ferryLength)
					carsThatFit--;
			}

			// printing starts here.
			System.out.println(carsThatFit);
			if (carsThatFit == 0) {
				System.out.println();
				continue;
			}

			for (int k = 0; k < carsThatFit; k++) {
				if (maximizingPort[k]) {
					if (i == numCases - 1 && k == carsThatFit - 1)
						System.out.print("port");
					else
						System.out.println("port");
				} else {
					if (i == numCases - 1 && k == carsThatFit - 1)
						System.out.print("starboard");
					else
						System.out.println("starboard");

				}
			}

			// remember to skip a line
			if (scanner.hasNextLine())
				System.out.println();
		}
	}

	private boolean[] maximizePort() {
		dp = new int[cars.size() + 1][cars.size() + 1];

		// fill first row.. col = 0 to col = cars we can choose from.
		// fill first col.. row = number of cars we can use
		for (int i = 0; i < dp[0].length; i++) {
			dp[0][i] = 0;
			dp[i][0] = 0;// when 0 cars used, max weight = 0.
		}

		// fill dp
		for (int i = 1; i < dp.length; i++) {
			for (int j = 1; j < dp[i].length; j++) {
				if (dp[i - 1][j - 1] + cars.get(i - 1) <= ferryLength) {
					dp[i][j] = Math.max(dp[i - 1][j - 1] + cars.get(i - 1), Math.max(dp[i - 1][j], dp[i][j - 1]));
				} else {
					dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
				}
			}
		}

		// backtrack
		boolean[] maximizingSet = new boolean[cars.size()];
		int i = dp.length - 1;
		int j = dp[0].length - 1;

		while (i != 0 && j != 0) {

			// case 1: diagonal -> picked current car
			if (dp[i][j] == dp[i - 1][j - 1] + cars.get(i - 1)) {
				maximizingSet[i - 1] = true;
				i--;
				j--;
			} else if (dp[i][j] == dp[i - 1][j]) {
				i--;

			} else if (dp[i][j] == dp[i][j - 1]) {
				j--;
			}
		}
		return maximizingSet;
	}

	public static void main(String args[]) throws IOException {
		Main solver = new Main();
	}
}