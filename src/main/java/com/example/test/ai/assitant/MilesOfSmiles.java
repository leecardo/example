package com.example.test.ai.assitant;

public class MilesOfSmiles {
    private final GreetingExpert greetingExpert;
    private final ChatBot chatBot;

    public MilesOfSmiles(GreetingExpert greetingExpert, ChatBot chatBot) {
        this.greetingExpert = greetingExpert;
        this.chatBot = chatBot;
    }



    public String handle(String userMessage) {
        if (greetingExpert.isGreeting(userMessage)) {
            return "Greetings from Miles of Smiles! How can I make your day better?";
        } else {
            return chatBot.reply(userMessage);
        }
    }
}
