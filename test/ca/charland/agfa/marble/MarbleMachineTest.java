package ca.charland.agfa.marble;

import static org.junit.Assert.assertEquals;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

/**
 * Test the Marble Machine class.
 * 
 * @author mcharland
 * 
 */
public class MarbleMachineTest {

	/** The Constant WRONG_COLOUR_COMBINATION. */
	private static final String WRONG_COLOUR_COMBINATION = "wrong colour combination";

	/**
	 * Tests passing in null as the argument to parse.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testParseNull() {
		// Setup
		MarbleMachine mm = new MarbleMachine();

		// Exercise
		mm.parse(null);
	}

	/**
	 * Tests passing in an empty List as the argument to parse.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testParseEmptyList() {
		// Setup
		MarbleMachine mm = new MarbleMachine();
		String args[] = {};

		// Exercise
		mm.parse(args);
	}

	/**
	 * Tests passing in an empty string as the argument to parse.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testParseEmptyString() {
		// Setup
		MarbleMachine mm = new MarbleMachine();
		String args[] = { "" };

		// Exercise
		mm.parse(args);
	}

	/**
	 * Tests passing in multiple characters as the argument to parse.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testParseMultipleCharacters() {
		// Setup
		MarbleMachine mm = new MarbleMachine();
		String args[] = { "ab" };

		// Exercise
		mm.parse(args);
	}

	/**
	 * Tests passing in a letter as the argument to parse.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testParseLetter() {
		// Setup
		MarbleMachine mm = new MarbleMachine();
		String args[] = { "a" };

		// Exercise
		mm.parse(args);
	}

	/**
	 * Tests passing in a number that is too low.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testParseOutOfBoundsLow() {
		// Setup
		MarbleMachine mm = new MarbleMachine();
		String args[] = { "0" };

		// Exercise
		mm.parse(args);
	}

	/**
	 * Tests passing in a number that is too high.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testParseOutOfBoundsHigh() {
		// Setup
		MarbleMachine mm = new MarbleMachine();
		String args[] = { "9" };

		// Exercise
		mm.parse(args);
	}

	/**
	 * Tests passing in a valid number.
	 */
	@Test
	public void testParse() {
		// Setup
		MarbleMachine mm = new MarbleMachine();
		String args[] = { "2" };

		// Exercise
		mm.parse(args);

		// Verify
		assertEquals("two marbles", 2, mm.getMarbles());
	}

	/**
	 * Tests generating the possible sequences for one marble.
	 */
	@Test
	public void testGenerateSequencesOne() {
		// Setup
		MarbleMachine mm = new MarbleMachine();
		String args[] = { "1" };
		mm.parse(args);

		// Exercise
		mm.generateSequences();

		// Verify
		Set<String> sequences = mm.getSequences();
		assertEquals("only one sequence", 1, sequences.size());
		assertEquals(WRONG_COLOUR_COMBINATION, "blue", sequences.toArray()[0]);
	}

	/**
	 * Tests generating the possible sequences for two marbles.
	 */
	@Test
	public void testGenerateSequencesTwo() {
		// Setup
		MarbleMachine mm = new MarbleMachine();
		String args[] = { "2" };
		mm.parse(args);

		// Exercise
		mm.generateSequences();

		// Verify
		Set<String> sequences = mm.getSequences();
		int x = 0;
		assertEquals(WRONG_COLOUR_COMBINATION, "blue", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "blue green", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "blue+green", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "green", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "green blue", sequences.toArray()[x++]);

		assertEquals("amount of sequences", 5, sequences.size());
	}

	/**
	 * Tests generating the possible sequences for three marbles.
	 */
	@Test
	public void testGenerateSequencesThree() {
		// Setup
		MarbleMachine mm = new MarbleMachine();
		String args[] = { "3" };
		mm.parse(args);

		// Exercise
		mm.generateSequences();

		// Verify
		Set<String> sequences = mm.getSequences();
		int x = 0;
		assertEquals(WRONG_COLOUR_COMBINATION, "blue", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "blue green", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "blue green red", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "blue green+red", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "blue red", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "blue red green", sequences.toArray()[x++]);

		assertEquals(WRONG_COLOUR_COMBINATION, "blue+green", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "blue+green red", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "blue+red", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "blue+red green", sequences.toArray()[x++]);

		assertEquals(WRONG_COLOUR_COMBINATION, "green", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "green blue", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "green blue red", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "green blue+red", sequences.toArray()[x++]);

		assertEquals(WRONG_COLOUR_COMBINATION, "green red", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "green red blue", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "green+red", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "green+red blue", sequences.toArray()[x++]);

		assertEquals(WRONG_COLOUR_COMBINATION, "red", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "red blue", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "red blue green", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "red blue+green", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "red green", sequences.toArray()[x++]);
		assertEquals(WRONG_COLOUR_COMBINATION, "red green blue", sequences.toArray()[x++]);

		assertEquals("amount of sequences", 24, sequences.size());
	}

	/**
	 * Tests generating the possible sequences for four marbles.
	 */
	@Test
	public void testGenerateSequencesFour() {
		// Setup
		MarbleMachine mm = new MarbleMachine();
		String args[] = { "4" };
		mm.parse(args);

		// Exercise
		mm.generateSequences();

		// Verify
		Set<String> sequences = mm.getSequences();
		assertEquals("amount of sequences", 130, sequences.size());
	}

	/**
	 * Tests generating the possible sequences for four marbles.
	 */
	@Test
	public void testGenerateSequencesFive() {
		// Setup
		MarbleMachine mm = new MarbleMachine();
		String args[] = { "5" };
		mm.parse(args);

		// Exercise
		mm.generateSequences();

		// Verify
		Set<String> sequences = mm.getSequences();
		assertEquals("amount of sequences", 815, sequences.size());
	}

	/**
	 * Tests generating the possible sequences for four marbles.
	 */
	@Test
	public void testGenerateSequencesSix() {
		// Setup
		MarbleMachine mm = new MarbleMachine();
		String args[] = { "6" };
		mm.parse(args);

		// Exercise
		mm.generateSequences();

		// Verify
		Set<String> sequences = mm.getSequences();
		assertEquals("amount of sequences", 5871, sequences.size());
	}
	
	/**
	 * Test the permutation method.
	 */
	@Test
	public void testPermutationOne() {

		// Setup
		MarbleMachine mm = new MarbleMachine();
		int[] ints = {0};

		// Exercise
		Set<String> permutation = mm.permutation(ints);
		
		// Verify
		assertEquals(permutation.size(), 1);
		assertEquals("0", permutation.toArray()[0]);
	}

	/**
	 * Test the permutation method.
	 */
	@Test
	public void testPermutationTwo() {

		// Setup
		MarbleMachine mm = new MarbleMachine();
		int[] ints = {0,1};

		// Exercise
		Set<String> permutation = mm.permutation(ints);
		
		// Verify
		assertEquals(permutation.size(), 2);
		int x = 0;
		assertEquals("01", permutation.toArray()[x++]);
		assertEquals("10", permutation.toArray()[x++]);
	}
	
	/**
	 * Test the permutation method.
	 */
	@Test
	public void testPermutationThree() {

		// Setup
		MarbleMachine mm = new MarbleMachine();
		int[] ints = {0,1,2};

		// Exercise
		Set<String> permutation = mm.permutation(ints);
		
		// Verify
		assertEquals(permutation.size(), 6);
		int x= 0;
		assertEquals("012", permutation.toArray()[x++]);
		assertEquals("021", permutation.toArray()[x++]);
		assertEquals("102", permutation.toArray()[x++]);
		assertEquals("120", permutation.toArray()[x++]);
		assertEquals("201", permutation.toArray()[x++]);
		assertEquals("210", permutation.toArray()[x++]);
	}
	
	@Test
	public void testConvertToColours() {

		// Setup
		MarbleMachine mm = new MarbleMachine();
		Set<String> allIndexes = new TreeSet<String>();
		allIndexes.add("012");
		
		// Exercise 
		Set<String> convertToColours = mm.convertToColours(allIndexes);
		
		// Verify
		assertEquals(1, convertToColours.size());
		assertEquals("blue green red", convertToColours.toArray()[0]);
	}
}
