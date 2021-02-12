package com.bot.youtube;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

import static com.bot.youtube.Harakiri.VID_ID_PATTERN;

@Component
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }


    @Override
    public void onUpdateReceived(Update update) {
        try {

            Message message = update.getMessage();

            if (message.getText().equals("/start")) {
                execute(new SendMessage().setChatId(message.getChatId()).setText("Hi! Send me link to youtube video"));
            } else {

                String youtubeUrl = message.getText();
                Matcher m = VID_ID_PATTERN.matcher(youtubeUrl);
                if (!m.find()) {
                    execute(new SendMessage().setChatId(message.getChatId()).setText("No valid! Try again =)"));
                }else {

                    File mp3 = null;
                    System.out.println(youtubeUrl);
                    try {
                        mp3 = Harakiri.load(youtubeUrl);
                        System.out.println("read");

                        SendAudio sendAudio = new SendAudio().setAudio(mp3).setChatId(message.getChatId());
                        execute(sendAudio);

                        mp3.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                        execute(new SendMessage().setChatId(message.getChatId()).setText(e.getMessage()));
                    }
                }
            }

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
