import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ArrayPermutationFast {

	static final String colours[] = { "blue", "green", "red", "yellow", "orange", "purple", "brown", "white" }; // ,

	public static void main(String[] args) {

		StringBuffer s = new StringBuffer();
		for (int x = 0; x < 8; x++) {
			s.append(x);
		}

		// First generate all the combinations for the marbles based off of the indexes.
		Set<String> allIndexes = generateAllPermutations(s.toString());

		// Now convert the colours to indexes.
		Set<String> allColours = addAllColours(allIndexes);

		// Now make all the combinations.
		addAllCombos(allColours);

		print(allColours);
	}

	/**
	 * Prints the combinations.
	 *
	 * @param allColours the all colours
	 */
	private static void print(Set<String> allColours) {
		for (String out : allColours) {
			 System.out.println(out);
		}
		System.out.println("AC=" + allColours.size());
	}

	/**
	 * Generate all permutations.
	 * 
	 * @param strings
	 *            the strings
	 * @param allIndexes
	 *            the all indexes
	 * @param permutation
	 *            the permutation
	 */
	private static Set<String> generateAllPermutations(String strings) {
		Set<String> result = new TreeSet<String>();
		Set<String> permutation = permutation(strings.toString());

		// Keep a set of strings you've already found.
		Set<String> lookedFors = new TreeSet<String>();
		
		// Now go through the permutations and create shorter ones.
		for (String p : permutation) {
			for (int y = 0; y < strings.length(); y++) {
				String substring = p.substring(y);
				if (!lookedFors.contains(substring)) {
					result.addAll(permutation(substring));
					lookedFors.add(substring);
				}
			}
		}
		return result;
	}

	/**
	 * Adds the all colours by converting them from the indexes to to the strings.
	 * 
	 * @param allIndexes
	 *            the all indexes
	 * @return the sets the
	 */
	private static Set<String> addAllColours(Set<String> allIndexes) {
		Set<String> allColours = new TreeSet<String>();
		for (String a : allIndexes) {
			StringBuffer out = new StringBuffer();
			
			for (char c : a.toCharArray()) {
				out.append(colours[Character.digit(c, 10)]);
				out.append(' ');
			}
			allColours.add(out.toString().trim());
		}
		return allColours;
	}

	/**
	 * Adds the all combinations.
	 * 
	 * @param allColours
	 *            the all colours
	 */
	private static void addAllCombos(Set<String> allColours) {
		List<String> allCombos = new ArrayList<String>();
		for (String out : allColours) {
			String[] split = out.split(" ");
			for (int x = 0; x < split.length - 1; x++) {
				
				// Create the original marble to look for.
				StringBuffer original = new StringBuffer();
				original.append(split[x]).append(' ').append(split[x + 1]);
				
				// Create a combined colour marble.
				StringBuffer combo = new StringBuffer();
				combo.append(split[x]).append('+').append(split[x + 1]);

				// Create a reverse colour marble.
				StringBuffer reversed = new StringBuffer();
				reversed.append(split[x + 1]).append('+').append(split[x]);

				// Now create a new string with the colour marble.
				String newCombination = out.replace(original, combo);
				
				// Let's check to make sure the reversed colour marble hasn't been added.
				String nor = out.replace(original, reversed);
				if (!allCombos.contains(nor)) {
					
					// Add the new unique combination to the collection.
					allCombos.add(newCombination);
				}
			}
		}
		allColours.addAll(allCombos);
	}

	/**
	 * Generate all the permutations for a given string.
	 * 
	 * @param str
	 *            The String to find all the permutations for.
	 * @return The set of permutations.
	 */
	public static Set<String> permutation(String str) {
		return permutation("", str, new TreeSet<String>());
	}

	/**
	 * Permutation.
	 * 
	 * @param prefix
	 *            The prefix to parse.
	 * @param str
	 *            The string passed in to parse.
	 * @param result
	 *            The result set passed.
	 * @return The set of permutations.
	 */
	private static Set<String> permutation(String prefix, String str, Set<String> result) {
		int n = str.length();
		if (n == 0) {
			result.add(prefix);
		} else {
			for (int i = 0; i < n; i++) {
				StringBuffer newPrefix = new StringBuffer();
				newPrefix.append(prefix).append(str.charAt(i));

				StringBuffer newStr = new StringBuffer();
				newStr.append(str.substring(0, i)).append(str.substring(i + 1, n));

				permutation(newPrefix.toString(), newStr.toString(), result);
			}
		}
		return result;
	}
}