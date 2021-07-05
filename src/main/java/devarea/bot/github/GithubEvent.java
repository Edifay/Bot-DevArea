package devarea.bot.github;

import devarea.bot.Init;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.TextChannel;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class GithubEvent {

    private static ArrayList<devarea.bot.github.EventGitHub> events = new ArrayList<>();
    private static int number = 0;

    public static void init() {
        try {
            GitHub gitHub = new GitHubBuilder().withOAuthToken(new Scanner(new File("github.token")).nextLine()).build();
            GHRepository repo = gitHub.getMyself().getRepository("DevArea_Site");
            number = repo.listCommits().toList().size();
            addEvent(commit -> ((TextChannel) Init.devarea.getChannelById(Snowflake.of("768782153802055710")).block()).createMessage(msg -> {
                msg.setContent(commit.getHtmlUrl().toString());
            }).subscribe());
            new Thread(() -> {
                try {
                    while (true) {
                        try {
                            Thread.sleep(10000);
                            int actualNumber = repo.listCommits().toList().size();
                            if (actualNumber != number) {
                                emit(repo.listCommits().toList().get(0));
                                number = actualNumber;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (InterruptedException e) {
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addEvent(devarea.bot.github.EventGitHub event) {
        events.add(event);
    }

    public static void emit(GHCommit commit) {
        for (devarea.bot.github.EventGitHub event : events) {
            event.run(commit);
        }
    }

}
