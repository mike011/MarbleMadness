// swift-tools-version: 6.0
import PackageDescription

let package = Package(
    name: "MarbleMadness",
    platforms: [.macOS(.v13)],
    products: [
        .library(name: "MarbleMachine", targets: ["MarbleMachine"]),
        .executable(name: "marble", targets: ["marble"]),
    ],
    targets: [
        .target(name: "MarbleMachine"),
        .executableTarget(name: "marble", dependencies: ["MarbleMachine"]),
        .testTarget(name: "MarbleMachineTests", dependencies: ["MarbleMachine"]),
    ]
)
