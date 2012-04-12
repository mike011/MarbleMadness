import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ArrayPermutation {

	static final String colours[] = { "blue", "green", "red", "yellow", "orange", "purple", "brown", "white" }; // ,

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		
		//System.out.println("Getting Started: " + (start));
		StringBuffer s = new StringBuffer();
		for (int x = 0; x <8; x++) {
			s.append(x);
		}
		//System.out.println("LOOKING FOR: " + s.length());

		Set<String> allIndexes = new TreeSet<String>();
		Set<String> permutation = permutation(s.toString());
		System.out.println("amount from string=" + permutation.size());

		System.out.println("Perms Generated: " + (System.currentTimeMillis() - start));

		generateAllPermutations(s, allIndexes, permutation);
		System.out.println("Generated all Permuations: " + (System.currentTimeMillis() - start));

		Set<String> allColours = addAllColours(allIndexes);
		System.out.println("Generated all Colours: " + (System.currentTimeMillis() - start));

		addAllCombos(allColours);
		System.out.println("Generaed all Combos: " + (System.currentTimeMillis() - start));

		for (String out : allColours) {
			// System.out.println(out);
		}
		System.out.println("AC="+allColours.size());
		long end = System.currentTimeMillis();
		long duration = (end - start) / 1000;
		System.out.println("Duration in seconds: " + duration);
	}

	private static void generateAllPermutations(StringBuffer strings, Set<String> allIndexes, Set<String> permutation) {
		Set<String> lookedFors = new TreeSet<String>();
		long start = System.currentTimeMillis();
		for (String p : permutation) {
		//	System.out.println(">>Generatng[" + p + "]: " + (System.currentTimeMillis() - start));
			for (int y = 0; y < strings.length(); y++) {
				String substring = p.substring(y);
				if (!lookedFors.contains(substring)) {
					long val = System.currentTimeMillis() - start;
					//System.out.println(">>Generatng[" + p + "]: " + val);
					allIndexes.addAll(permutation(substring));
					long l = System.currentTimeMillis() - start - val;
					if(l > 25) {
						System.out.println(">>>Generatng[" + p + "]: " + l);
					}
					lookedFors.add(substring);
				}
			}
		}
		//System.out.println(allIndexes);
	}

	private static Set<String> addAllColours(Set<String> allIndexes) {
		Set<String> allColours = new TreeSet<String>();
		for (String a : allIndexes) {
			StringBuffer out = new StringBuffer();
			boolean good = lookForDups(a, out);
			if (good) {
				allColours.add(out.toString().trim());
			}
		}
		return allColours;
	}

	private static void addAllCombos(Set<String> allColours) {
		List<String> allCombos = new ArrayList<String>();
		for (String out : allColours) {
			String[] split = out.split(" ");
			// System.out.println(out);
			for (int x = 0; x < split.length - 1; x++) {
				String o = split[x] + " " + split[x + 1];
				String f = split[x] + "+" + split[x + 1];
				String r = split[x + 1] + "+" + split[x];
				String no = out.replace(o, f);
				String nor = out.replace(o, r);
				if (!allCombos.contains(nor)) {
					allCombos.add(no);
				}
			}
		}
		allColours.addAll(allCombos);
	}

	private static boolean lookForDups(String a, StringBuffer out) {
		boolean good = true;
		for (char c : a.toCharArray()) {

			String string = colours[Character.digit(c, 10)];
			if (out.toString().contains(string)) {
				good = false;
				break;
			} else if (string.contains("+")) {
				String splits[] = string.split("\\+");
				for (String split : splits) {
					if (out.toString().contains(split)) {
						good = false;
						break;
					}
				}
			}

			out.append(string);
			out.append(' ');
		}
		return good;
	}

	public static Set<String> permutation(String str) {
		return permutation("", str, new TreeSet<String>());
	}

	private static Set<String> permutation(String prefix, String str, Set<String> result) {
		int n = str.length();
		if (n == 0) {
			result.add(prefix);
		} else {
			for (int i = 0; i < n; i++) {
				StringBuffer newPrefix = new StringBuffer();
				newPrefix.append(prefix);
				newPrefix.append(str.charAt(i));
				
				StringBuffer newStr = new StringBuffer();
				newStr.append(str.substring(0, i));
				newStr.append(str.substring(i + 1, n));
				
				permutation(newPrefix.toString(), newStr.toString(), result);
			}
		}
		return result;

	}
}