package ca.charland.marble;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Consider a marble machine that produces sequences of marbles. This machine
 * takes as input, one parameter, an integer from 1 to 8 which determines the
 * set of colours available for making a marble sequence.
 * 
 * Your task is to write a Java program that outputs all of the possible marble
 * sequences that this machine could possibly generate. Please return to us: -
 * the source code - a chosen input and the output your program produced.
 * 
 * Input:
 * 
 * Your program must read an input selection from the command line from 1 to 8..
 * The colours available to the machine for each input selection are as follows:
 * 
 * 1 = [blue]<br>
 * 2 = [blue, green]<br>
 * 3 = [blue, green, red]<br>
 * 4 = [blue, green, red, yellow]<br>
 * 5 = [blue, green, red, yellow, orange]<br>
 * 6 = [blue, green, red, yellow, orange, purple]<br>
 * 7 = [blue, green, red, yellow, orange, purple, brown]<br>
 * 8 = [blue, green, red, yellow, orange, purple, brown, white]
 * 
 * Constraints:
 * 
 * 1. The machine is allowed to use any colour only once per sequence, or not at
 * all.<br>
 * 2. A marble can be made of either one or two colours.<br>
 * 3. The machine must generate at least one marble per sequence.
 * 
 * Output:
 * 
 * Your program should generate the list of all possible marble sequences that
 * could be generated for the given input selection from 1 to 8.
 * 
 * Each sequence must be output one per line. A marble made of a single colour
 * is represented by the name of the colour and a marble made of two colours is
 * represented by the names of each colour seperated by '+'. Each marble must be
 * seperated by a single space.
 * 
 * @author mcharland
 * 
 */
public class MarbleMachine {

    /**
     * @param args
     *            This machine takes as input, one parameter, an integer from 1
     *            to 8 which determines the set of colours available for making
     *            a marble sequence.
     */
    public static void main(String[] args) {

	MarbleMachine mm = new MarbleMachine();
	mm.parse(args);
	mm.generateSequences();
	mm.print();

    }

    private final static String COLOURS[] = { "blue", "green", "red", "yellow",
	    "orange", "purple", "brown", "white" };

    private int amountOfMarbles;

    private Set<String> sequences;

    public void parse(String args[]) {

	// Make sure you have one argument passed in that is a number.
	String errorMessage = "Please enter a number between 1 and 8";
	if (args == null || args.length == 0 || args[0] == ""
		|| args[0].length() > 1
		|| !Character.isDigit(args[0].charAt(0))) {
	    throw new IllegalArgumentException(errorMessage);
	}

	// You can now safely assume you have a one digit number, but we have to
	// make sure the number is in a valid range.
	int marbles = Character.digit(args[0].charAt(0), 10);
	if (marbles < 1 || marbles > 8) {
	    throw new IllegalArgumentException(errorMessage);
	}

	amountOfMarbles = marbles;
    }

    int getMarbles() {
	return amountOfMarbles;
    }

    public void generateSequences() {
	int[] s = new int[amountOfMarbles];
	for (int x = 0; x < amountOfMarbles; x++) {
	    s[x] = x;
	}

	// First generate all the combinations for the marbles based off of the
	// indexes.
	Set<String> allIndexes = generatePermutations(s);

	// Now convert the indexes to colours.
	sequences = convertToColours(allIndexes);

	// Now make all the combinations.
	addAllCombos(sequences);
    }

    private Set<String> generatePermutations(int[] s) {
	Set<String> result = new TreeSet<String>();
	Set<String> permutation = permutation(s);

	// Keep a set of strings you've already found.
	Set<String> lookedFors = new TreeSet<String>();

	// Now go through the permutations and create shorter ones so you'll
	// have all the possible combinations.
	for (String p : permutation) {
	    for (int index = 0; index < s.length; index++) {
		String substring = p.substring(index);
		char[] ca = substring.toCharArray();
		int[] ints = new int[substring.length()];
		for (int a = 0; a < ints.length; a++) {
		    ints[a] = Character.digit(ca[a], 10);
		}

		// Only generate new permutations for new combinations of
		// strings.
		if (!lookedFors.contains(substring)) {
		    result.addAll(permutation(ints));
		    lookedFors.add(p.substring(index));
		}
	    }
	}
	return result;
    }

    Set<String> convertToColours(Set<String> allIndexes) {
	Set<String> allColours = new TreeSet<String>();
	for (String a : allIndexes) {
	    StringBuffer out = new StringBuffer();
	    for (char c : a.toCharArray()) {
		int digit = Character.digit(c, 10);
		out.append(COLOURS[digit]).append(' ');
	    }
	    allColours.add(out.toString().trim());
	}
	return allColours;
    }

    private void addAllCombos(Set<String> allColours) {
	List<String> allCombos = new ArrayList<String>();
	for (String out : allColours) {
	    String[] split = out.split(" ");
	    for (int index = 0; index < split.length - 1; index++) {

		// Create the original marble to look for.
		StringBuffer original = new StringBuffer();
		original.append(split[index]).append(' ')
			.append(split[index + 1]);

		// Create a combined colour marble.
		StringBuffer combo = new StringBuffer();
		combo.append(split[index]).append('+').append(split[index + 1]);

		// Create a reverse colour marble.
		StringBuffer reversed = new StringBuffer();
		reversed.append(split[index + 1]).append('+')
			.append(split[index]);

		// Now create a new string with the colour marble.
		String newCombination = out.replace(original, combo);

		// Let's check to make sure the reversed colour marble hasn't
		// been added.
		String nor = out.replace(original, reversed);
		if (!allCombos.contains(nor)) {

		    // Add the new unique combination to the collection.
		    allCombos.add(newCombination);
		}
	    }
	}
	allColours.addAll(allCombos);
    }

    Set<String> permutation(int[] s) {
	return permutationGenerator("", s, new TreeSet<String>());
    }

    Set<String> permutationGenerator(String possiblePermutation, int[] ints,
	    Set<String> result) {
	if (ints.length == 0) {
	    result.add(possiblePermutation);
	} else {
	    for (int i = 0; i < ints.length; i++) {
		StringBuffer newPartialPermutation = new StringBuffer();
		newPartialPermutation.append(possiblePermutation).append(
			ints[i]);

		int[] newInt = new int[ints.length - 1];
		for (int a = 0; a < i; a++) {
		    newInt[a] = ints[a];
		}
		for (int a = i + 1; a < ints.length; a++) {
		    newInt[a - 1] = ints[a];
		}

		permutationGenerator(newPartialPermutation.toString(), newInt,
			result);
	    }
	}
	return result;
    }

    Set<String> getSequences() {
	return sequences;
    }

    public void print() {
	for (String s : sequences) {
	    System.out.println(s);
	}
    }
}