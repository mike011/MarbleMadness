import Testing
@testable import MarbleMachine

@Suite("Parsing")
struct ParsingTests {
    @Test("Rejects invalid input", arguments: [
        [], [""], ["ab"], ["a"], ["0"], ["9"], ["10"], ["-1"], ["1", "2"],
    ])
    func rejects(_ arguments: [String]) {
        #expect(throws: MarbleMachine.InputError.invalid) {
            try MarbleMachine(arguments: arguments)
        }
    }

    @Test func acceptsDigit() throws {
        let machine = try MarbleMachine(arguments: ["2"])
        #expect(machine.colours == ["blue", "green"])
    }
}

/// Replaces the old JUnitPerf timed test. The old algorithm took hours for 8
/// colours; this one streams all 801,704 sequences in well under the limit,
/// even in a debug build.
@Suite("Performance")
struct PerformanceTests {
    @Test("8 colours finishes within 10 seconds")
    func eightColoursIsFast() throws {
        let machine = try MarbleMachine(colourCount: 8)
        var count = 0
        let elapsed = ContinuousClock().measure {
            machine.forEachSequence { _ in count += 1 }
        }
        #expect(count == 801_704)
        #expect(elapsed < .seconds(10))
    }
}

@Suite("Generation")
struct GenerationTests {
    @Test func one() throws {
        #expect(try MarbleMachine(colourCount: 1).sequences() == ["blue"])
    }

    @Test("Matches the README sample output exactly, including order")
    func twoMatchesReadmeSample() throws {
        #expect(try MarbleMachine(colourCount: 2).sequences() == [
            "blue", "green", "blue+green", "blue green", "green blue",
        ])
    }

    @Test func three() throws {
        #expect(try MarbleMachine(colourCount: 3).sequences() == [
            // one marble
            "blue", "green", "red", "blue+green", "blue+red", "green+red",
            // two marbles
            "blue green", "blue red", "blue green+red",
            "green blue", "green red", "green blue+red",
            "red blue", "red green", "red blue+green",
            "blue+green red", "blue+red green", "green+red blue",
            // three marbles
            "blue green red", "blue red green", "green blue red",
            "green red blue", "red blue green", "red green blue",
        ])
    }

    @Test("Includes sequences with several two-colour marbles (the old bug)")
    func fourIncludesMultiplePairMarbles() throws {
        let sequences = Set(try MarbleMachine(colourCount: 4).sequences())
        #expect(sequences.contains("blue+green red+yellow"))
        #expect(sequences.contains("red+yellow blue+green"))
        #expect(sequences.contains("blue+yellow green+red"))
    }

    @Test("Counts match the closed-form formula", arguments: zip(
        1...8, [1, 5, 24, 136, 935, 7671, 73360, 801704]
    ))
    func counts(colours: Int, expected: Int) throws {
        #expect(MarbleMachine.expectedCount(colours: colours) == expected)
        var count = 0
        try MarbleMachine(colourCount: colours).forEachSequence { _ in count += 1 }
        #expect(count == expected)
    }

    @Test("Every sequence is valid and unique", arguments: 1...8)
    func validAndUnique(colours n: Int) throws {
        let machine = try MarbleMachine(colourCount: n)
        let order = Dictionary(uniqueKeysWithValues:
            machine.colours.enumerated().map { ($1, $0) })
        var seen = Set<String>()
        var failures = 0

        machine.forEachSequence { sequence in
            if !seen.insert(sequence).inserted { failures += 1 }
            var used = Set<String>()
            for marble in sequence.split(separator: " ", omittingEmptySubsequences: false) {
                let parts = marble.split(separator: "+", omittingEmptySubsequences: false)
                    .map(String.init)
                guard parts.count == 1 || parts.count == 2 else { failures += 1; continue }
                if parts.count == 2,
                   (order[parts[0]] ?? .max) >= (order[parts[1]] ?? .max) {
                    failures += 1 // pairs must print in palette order
                }
                for colour in parts {
                    if order[colour] == nil || !used.insert(colour).inserted {
                        failures += 1
                    }
                }
            }
        }
        #expect(failures == 0)
        #expect(seen.count == MarbleMachine.expectedCount(colours: n))
    }
}
