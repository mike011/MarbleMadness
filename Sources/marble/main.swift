import Foundation
import MarbleMachine

// Usage: marble <1-8>
let machine: MarbleMachine
do {
    machine = try MarbleMachine(arguments: Array(CommandLine.arguments.dropFirst()))
} catch {
    FileHandle.standardError.write(Data("\(error)\n".utf8))
    exit(1)
}

// Buffer output: 8 colours produces about 800,000 lines.
var buffer = ""
buffer.reserveCapacity(1 << 16)
machine.forEachSequence { sequence in
    buffer += sequence
    buffer += "\n"
    if buffer.utf8.count >= 1 << 16 {
        FileHandle.standardOutput.write(Data(buffer.utf8))
        buffer.removeAll(keepingCapacity: true)
    }
}
FileHandle.standardOutput.write(Data(buffer.utf8))
