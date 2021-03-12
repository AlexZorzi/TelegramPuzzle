package com.alles.puzzle;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {

    public static void main(String[] args) {
        System.out.println(args.toString());
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new Bot(args[0],args[1],args[2]));
            //telegramBotsApi.registerBot(new Bot("botusername","bottoken","/path/to/chrome/driver"));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
