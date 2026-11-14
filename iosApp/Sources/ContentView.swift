import SwiftUI
import CompAIShared

struct ChatMessage: Identifiable {
    let id = UUID()
    let text: String
    let isUser: Bool
}

struct ContentView: View {
    private let engine = SharedEngine()

    @State private var messages = [
        ChatMessage(
            text: "Hello! I'm CompAI. How can I help you today?",
            isUser: false
        )
    ]

    @State private var input = ""

    private var currentTime: String {
        Date.now.formatted(date: .omitted, time: .shortened)
    }

    private var currentDate: String {
        Date.now.formatted(date: .long, time: .omitted)
    }

    var body: some View {
        VStack(spacing: 0) {
            // Header
            HStack(spacing: 12) {
                Image(systemName: "leaf.fill")
                    .font(.system(size: 25))
                    .foregroundStyle(.green)

                VStack(alignment: .leading, spacing: 3) {
                    Text("CompAI")
                        .font(.title2.bold())
                        .foregroundStyle(.white)

                    Text("Your intelligent assistant")
                        .font(.caption)
                        .foregroundStyle(.gray)
                }

                Spacer()

                Circle()
                    .fill(.green)
                    .frame(width: 9, height: 9)

                Text("Online")
                    .font(.caption)
                    .foregroundStyle(.green)
            }
            .padding()
            .background(Color(red: 0.07, green: 0.12, blue: 0.09))

            // Chat messages
            ScrollView {
                LazyVStack(spacing: 14) {
                    ForEach(messages) { message in
                        HStack {
                            if message.isUser {
                                Spacer(minLength: 50)
                            }

                            Text(message.text)
                                .padding(13)
                                .foregroundStyle(.white)
                                .background(
                                    message.isUser
                                    ? Color.green.opacity(0.65)
                                    : Color.white.opacity(0.10)
                                )
                                .clipShape(RoundedRectangle(cornerRadius: 15))

                            if !message.isUser {
                                Spacer(minLength: 50)
                            }
                        }
                    }
                }
                .padding()
            }

            // Input area
            HStack(spacing: 10) {
                TextField("Message CompAI...", text: $input)
                    .padding(13)
                    .background(Color.white.opacity(0.08))
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                    .foregroundStyle(.white)
                    .onSubmit(sendMessage)

                Button(action: sendMessage) {
                    Image(systemName: "arrow.up")
                        .font(.system(size: 17, weight: .bold))
                        .foregroundStyle(.white)
                        .frame(width: 45, height: 45)
                        .background(.green)
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                }
            }
            .padding()
            .background(Color(red: 0.07, green: 0.12, blue: 0.09))
        }
        .background(Color(red: 0.035, green: 0.055, blue: 0.045))
        .preferredColorScheme(.dark)
    }

    private func sendMessage() {
        let command = input.trimmingCharacters(in: .whitespacesAndNewlines)

        guard !command.isEmpty else { return }

        messages.append(ChatMessage(text: command, isUser: true))
        input = ""

        if engine.normalizeCommand(command: command) == "clear" {
            messages.removeAll()
            return
        }

        let normalized = engine.normalizeCommand(command: command)

        let response = engine.processNormalizedCommand(
            lower: normalized,
            currentTime: currentTime,
            currentDate: currentDate
        )

        messages.append(
            ChatMessage(
                text: response ?? "I don't understand that command yet. Type 'help' to see what I can do.",
                isUser: false
            )
        )
    }
}
