package com.bot.youtube;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.YoutubeVideo;
import com.github.kiulian.downloader.model.formats.AudioVideoFormat;
import com.github.kiulian.downloader.model.formats.Format;
import com.github.kiulian.downloader.model.formats.VideoFormat;
import com.github.kiulian.downloader.model.quality.VideoQuality;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Harakiri {


    public static final Pattern VID_ID_PATTERN = Pattern.compile("(?<=v\\=|youtu\\.be\\/)\\w+"),
            FILENAME_PATTERN = Pattern.compile("(?<=filename\\=\\\").+?(?=\\\")");


    private static String getID(String youtubeUrl) {
        Matcher m = VID_ID_PATTERN.matcher(youtubeUrl);
        if (!m.find()) {
            throw new IllegalArgumentException("Invalid YouTube URL.");
        }
        return m.group();
    }


    public static File load(String url) throws YoutubeException, IOException, ExecutionException, InterruptedException {
        YoutubeDownloader downloader = new YoutubeDownloader();
        downloader.setParserRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
        downloader.setParserRetryOnFailure(1);

        String code = getID(url);

        YoutubeVideo video = downloader.getVideo(code);

        List<AudioVideoFormat> audioVideoFormats = video.videoWithAudioFormats();


        AudioVideoFormat audioVideoFormat = audioVideoFormats.get(0);

        File outputDir = new File("my_videos");


        Future<File> fileFuture = video.downloadAsync(audioVideoFormat, outputDir);


        return fileFuture.get();


    }

}
