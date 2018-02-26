import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

/**
 * Uva 124 : Following Orders Created by Rafael Zuniga on 2/26/2016
 */

class Main {

	// Scanner
	private Scanner scanner = new Scanner(System.in);

	// stores orderings that satisfies the constraints
	private HashSet<String> orderings;

	/*
	 * The constraints are structured in a hashtable: the key is less than any
	 * variable stored in the ArrayList<String>
	 */
	private Hashtable<String, ArrayList<String>> hashCons;

	// load data and solve
	public Main() throws IOException {

		while (scanner.hasNextLine()) {
			// load the variables
			String[] vars = scanner.nextLine().split("\\ ");

			// load the constraints into a string array
			String[] allConstraints = scanner.nextLine().split("\\ ");

			// initialize hashtable
			hashCons = new Hashtable<String, ArrayList<String>>();

			for (int i = 0, k = 0; i < allConstraints.length / 2; i++, k += 2) {
				// new constraint
				if (!hashCons.containsKey(allConstraints[k])) {
					hashCons.put(allConstraints[k], new ArrayList<String>());
					hashCons.get(allConstraints[k]).add(allConstraints[k + 1]);
				} else
					hashCons.get(allConstraints[k]).add(allConstraints[k + 1]);
			}

			// convert variables to char array
			char[] varsCharArray = new char[vars.length];
			for (int i = 0; i < vars.length; i++) {
				varsCharArray[i] = vars[i].charAt(0);
			}

			// treat variables as a string and get all the corresponding
			// anagrams minus the anagrams that do not satisfy the constraints
			orderings = new HashSet<String>();
			getAnagrams(varsCharArray, 0);

			// sort
			List<String> toSort = new ArrayList<String>();
			toSort.addAll(orderings);
			Collections.sort(toSort);

			// print
			for (String st : toSort)
				System.out.println(st);

			// avoid printing a new line on the last specification
			if (scanner.hasNextLine())
				System.out.println();

		}

	}

	/*
	 * isOrdered takes any char array and returns true if the order of the
	 * characters satisfies the constraints described within the hashtable,
	 * returns false otherwise.
	 */
	private boolean isOrdered(char[] ordering) {
		for (int i = 0; i < ordering.length; i++) {
			for (int j = i + 1; j < ordering.length; j++) {
				if (hashCons.containsKey(String.valueOf(ordering[j]))) {
					ArrayList<String> temp = hashCons.get(String.valueOf(ordering[j]));
					for (String greater : temp)
						if (greater.equals(String.valueOf(ordering[i]))) {
							// ordering[i] is greater than ordering[j]
							return false;
						}
				}
			}
		}

		return true;
	}

	/*
	 * produces all the anagrams of the given string as a character array and
	 * stores them in a Hashset.
	 */
	private void getAnagrams(char[] a, int i) {
		if (i == a.length - 1) {
			if (isOrdered(a))
				orderings.add(String.copyValueOf(a));
		} else {
			for (int j = i; j < a.length; j++) {
				// switches a[i] with a[j]. both i and j start at the same index
				char c = a[i];
				a[i] = a[j];
				a[j] = c;
				getAnagrams(a, i + 1); // recursive call
				// switches a[i] and a[j] back
				c = a[i];
				a[i] = a[j];
				a[j] = c;
			}
		}
	}

	public static void main(String args[]) throws IOException {
		Main solver = new Main();
	}
}
