import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Main2 {
	// Scanner
	private Scanner scanner = new Scanner(System.in);

	private int R; // number of access points
	private int H; // number of houses
	private int[] houses;

	public Main2() {
		int numCases = scanner.nextInt();
		for (int i = 0; i < numCases; i++) {
			this.R = scanner.nextInt();
			this.H = scanner.nextInt();
			this.houses = new int[H];

			// read H houses
			for (int h = 0; h < H; h++) {
				houses[h] = scanner.nextInt();
			}

			// sort: problem does not specify whether houses will be in order or
			// not
			Arrays.sort(houses);

			// if R == 1 then just end it here
			if (R == 1) {
				System.out.println((houses[H - 1] - houses[0]) / 2);
				continue;
			}

			if (R >= H) {
				System.out.println(0.f);
				continue;
			}

			double low = 0;
			// we seem to be getting the high but as in the actual number not
			// the index
			double high = houses[H - 1] - houses[0];

			// binary search
			while (high - low > 1e-2) {
				// half of all houses, where router would be for r=1
				double mid = ((low + high) / 2);

				if (chooseBottom(mid)) {
					high = mid;
				} else
					low = mid;

				//System.out.println("lo " + low + "  hi: " + high);

			}
			
			System.out.printf("%.1f\n", high);
			// we check somthing and then we decide to either check bottomhalf
			// of houses or top half of houses

		}

	}

	public boolean chooseBottom(double d) {
		int routersNeeded = 1;
		d = d * 2;
		double pivot = houses[0] + d;
		
		// mid/2.0 is mid of the lower half
		for (int i = 0; i < houses.length; i++) {
			if (pivot <= houses[i]){
				routersNeeded++;
				pivot = houses[i] + d;
			}
		}
		
		if (routersNeeded <= R) {
			return true;
		} else {
			return false;
		}
		
	}

	public static void main(String args[]) throws IOException {
		Main2 solver = new Main2();
	}
}
