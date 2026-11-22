/// Generates every marble sequence a marble machine can produce from the
/// first N colours (1 to 8).
///
/// Rules:
/// 1. Each colour is used at most once per sequence.
/// 2. A marble is made of one or two colours. A two-colour marble is
///    unordered: "blue+green" and "green+blue" are the same marble, printed in
///    palette order.
/// 3. A sequence has at least one marble. Marble order matters:
///    "blue green" and "green blue" are different sequences.
///
/// Sequences are built marble by marble with a bitmask of used colours, so
/// no duplicate is ever created and nothing needs filtering. Output is grouped
/// by marble count. Within each position, single-colour marbles come before
/// two-colour marbles, which reproduces the README's sample output order.
public struct MarbleMachine: Sendable {

    public static let palette = [
        "blue", "green", "red", "yellow", "orange", "purple", "brown", "white",
    ]

    public enum InputError: Error, Equatable, CustomStringConvertible {
        case invalid
        public var description: String { "Please enter a number between 1 and 8" }
    }

    /// The colours available for this input selection.
    public let colours: [String]

    /// Creates a machine for `count` colours (1...8).
    public init(colourCount count: Int) throws(InputError) {
        guard (1...Self.palette.count).contains(count) else { throw .invalid }
        colours = Array(Self.palette.prefix(count))
    }

    /// Parses command-line arguments (excluding the program name).
    /// Exactly one argument that is a single digit from 1 to 8 is accepted.
    public init(arguments: [String]) throws(InputError) {
        guard arguments.count == 1,
              let argument = arguments.first,
              argument.count == 1,
              let count = Int(argument)
        else { throw .invalid }
        try self.init(colourCount: count)
    }

    /// All sequences, in output order.
    public func sequences() -> [String] {
        var result: [String] = []
        forEachSequence { result.append($0) }
        return result
    }

    /// Streams each sequence, in output order, without holding them all.
    public func forEachSequence(_ body: (String) -> Void) {
        // A sequence holds at most one marble per colour.
        for marbles in 1...colours.count {
            var prefix = ""
            build(&prefix, used: 0, remaining: marbles, body)
        }
    }

    /// Appends `remaining` more marbles to `prefix` using only colours not set
    /// in the `used` bitmask, and emits each finished sequence.
    private func build(
        _ prefix: inout String, used: UInt8, remaining: Int,
        _ body: (String) -> Void
    ) {
        if remaining == 0 {
            body(prefix)
            return
        }
        // Every marble needs at least one colour.
        guard colours.count - used.nonzeroBitCount >= remaining else { return }

        let saved = prefix
        let separator = prefix.isEmpty ? "" : " "
        let n = colours.count

        // Single-colour marbles.
        for i in 0..<n where used & (1 << i) == 0 {
            prefix += separator + colours[i]
            build(&prefix, used: used | (1 << i), remaining: remaining - 1, body)
            prefix = saved
        }

        // Two-colour marbles, with i < j so each pair is produced once.
        for i in 0..<n where used & (1 << i) == 0 {
            for j in (i + 1)..<max(i + 1, n) where used & (1 << j) == 0 {
                prefix += separator + colours[i] + "+" + colours[j]
                build(&prefix, used: used | (1 << i) | (1 << j),
                      remaining: remaining - 1, body)
                prefix = saved
            }
        }
    }

    /// Independent closed-form count used to check the generator.
    ///
    /// f(k) is the number of sequences that use exactly k given colours. The
    /// first marble is one of k colours or one of C(k,2) pairs, so
    /// f(k) = k·f(k-1) + C(k,2)·f(k-2). The total sums over every non-empty
    /// subset of the n colours.
    public static func expectedCount(colours n: Int) -> Int {
        var f = [1] + Array(repeating: 0, count: n)
        for k in stride(from: 1, through: n, by: 1) {
            f[k] = k * f[k - 1] + (k >= 2 ? k * (k - 1) / 2 * f[k - 2] : 0)
        }
        var total = 0
        var choose = 1 // C(n, k)
        for k in stride(from: 1, through: n, by: 1) {
            choose = choose * (n - k + 1) / k
            total += choose * f[k]
        }
        return total
    }
}
