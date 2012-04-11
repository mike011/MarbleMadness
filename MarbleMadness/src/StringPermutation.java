import java.util.Set;
import java.util.TreeSet;

public class StringPermutation {
	
	static final String colours[] = {"red", "green", "blue"}; 

	public static void main(String[] args) {
		String s = "012";

		Set<String> all = new TreeSet<String>();
		Set<String> permutation = permutation(s);
		for (String p : permutation) {
			for (int y = 0; y < s.length(); y++) {
				all.addAll(permutation(p.substring(y)));
			}
		}
		for(String a : all) {
			StringBuffer out = new StringBuffer();
			for(char c :a.toCharArray()) {
				out.append(colours[c]);
				out.append(' ');
			}
			System.out.println(out.toString());
		}
		
	}

	public static Set<String> permutation(String str) {
		return permutation("", str, new TreeSet<String>());
	}

	private static Set<String> permutation(String prefix, String str,
			Set<String> result) {
		int n = str.length();
		if (n == 0) {
			result.add(prefix);
		} else {
			for (int i = 0; i < n; i++) {
				String prefix2 = prefix + str.charAt(i);
				String str2 = str.substring(0, i) + str.substring(i + 1, n);
				permutation(prefix2, str2, result);
			}
		}
		return result;

	}
}