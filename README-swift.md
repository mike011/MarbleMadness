# Marble Machine (Swift)

Swift solution to the marble machine problem in `README`. It replaces the
earlier Java/Eclipse version, which missed every sequence with more than one
two-colour marble and took hours for 8 colours.

Diagrams of the old bug and the new algorithm are in `diagrams/`:

- `diagrams/1-before-old-algorithm.png`: what the old Java approach did and where it went wrong
- `diagrams/2-after-new-algorithm.png`: how the new marble-by-marble algorithm works
- `diagrams/3-missed-sequences.png`: how many valid sequences the old code missed
- `diagrams/marble-machine-animated.html`: animated, step-through comparison of both algorithms.
  View it online: https://mike011.github.io/MarbleMadness/diagrams/marble-machine-animated.html

## Usage

```
swift build -c release
.build/release/marble 3      # prints every sequence for 3 colours
swift test                   # README sample, counts 1-8, validity, performance
```

If `swift test` fails with "plugin for module 'TestingMacros' not found" under the
Command Line Tools, use an Xcode toolchain
(`DEVELOPER_DIR=/Applications/<Xcode>.app/Contents/Developer swift test`) or pass the
plugin path:
`swift test -Xswiftc -plugin-path -Xswiftc /Library/Developer/CommandLineTools/usr/lib/swift/host/plugins/testing`

`output-3.txt` is the output for input 3 (the "chosen input and output" the README asks for).

Counts for 1-8 colours: 1, 5, 24, 136, 935, 7671, 73360, 801704.
8 colours runs in about 0.2 s in a release build.

## Layout

- `Sources/MarbleMachine/`: the generator library
- `Sources/marble/`: the command-line program
- `Tests/MarbleMachineTests/`: Swift Testing suite
- `algorithms`: history of the approaches tried
- `diagrams/make_diagrams.py`: regenerates the diagrams (`python3 diagrams/make_diagrams.py diagrams`, needs matplotlib)
