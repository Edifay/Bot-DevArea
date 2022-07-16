package devarea.bot.commands.inLine;

import devarea.bot.Init;
import devarea.bot.commands.ShortCommand;
import devarea.bot.presets.ColorsUsed;
import devarea.bot.presets.TextMessage;
import devarea.judge.JudgeException;
import devarea.judge.JudgeManager;
import devarea.judge.JudgeResponse;
import devarea.judge.JudgeSubmission;
import devarea.judge.JudgeSubmissionBuilder;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Run extends ShortCommand {

    final static int MAX_LINES = 20;
    final static int MAX_CHARS = 900;
    // Regex to extract the different parts of the message.
    final static String PATTERN = "^(.*)\\n```(.+)\\n(?s)(.*\\S.*)```\\n?(.*)$";
    final static String ZERO_WIDTH_SPACE = "\u200b";
    final static int ID_ACCEPTED = 3;

    public Run(final Member member, final TextChannel channel, final Message message) {
        super(member, channel);

        String content = message.getContent().substring((Init.initial.prefix + "run").length());

        if (content.isBlank()) {
            this.sendEmbed(TextMessage.runCommandExplain, false);
            this.endCommand();
            return;
        }

        if (content.strip().equals("languages")) {
            sendListLanguages();
            this.endCommand();
            return;
        }

        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            message.addReaction(ReactionEmoji.custom(Init.idLoading)).subscribe();

            JudgeSubmission submission = new JudgeSubmissionBuilder()
                    .args(matcher.group(1))
                    .languageAlias(matcher.group(2))
                    .code(matcher.group(3))
                    .stdin(matcher.group(4))
                    .build();

            JudgeManager.get().executeAsync(submission).thenAccept(response -> {
                this.sendEmbed(embedResponse(response), false);
            }).whenComplete((res, e) -> {
                message.removeSelfReaction(ReactionEmoji.custom(Init.idLoading)).subscribe();
                if (e != null) {
                    if (e.getCause() instanceof JudgeException) {
                        sendError(e.getCause().getMessage());
                    } else {
                        e.getCause().printStackTrace();
                    }
                }
            });
        } else {
            this.sendError("La commande n'est pas correctement formatée ! `" + Init.initial.prefix +
                           "run` pour voir comment utiliser cette commande.");
        }
        this.endCommand();
    }

    private void sendListLanguages() {
        try {
            Map<String, List<String>> languages = JudgeManager.get().getConfig().languages();

            StringBuilder field = new StringBuilder("```\n");
            for (Map.Entry<String, List<String>> entry : languages.entrySet()) {
                field.append(String.format("> %s --> %s\n", entry.getKey(), String.join(", ", entry.getValue())));
            }
            field.append("```");

            EmbedCreateSpec.Builder embed = EmbedCreateSpec.builder()
                    .addField("Langages supportés", field.toString(), false)
                    .color(ColorsUsed.same);

            this.sendEmbed(embed.build(), false);

        } catch (JudgeException e) {
            sendError(e.getMessage());
        }
    }

    private void embedCodeOutput(EmbedCreateSpec.Builder embed, String judgeMessage, String... values) {
        StringBuilder rawOutput = new StringBuilder();
        for (String s : values) {
            if (s != null) {
                rawOutput.append(s);
            }
        }

        String[] lines = rawOutput.toString().split("\n");
        String output = Arrays.stream(lines)
                .limit(MAX_LINES)
                .collect(Collectors.joining("\n"))
                .replace("`", "`" + ZERO_WIDTH_SPACE);

        if (output.length() > MAX_CHARS || lines.length > MAX_LINES) {
            embed.footer("La sortie a été réduite", null);
        }

        if (output.length() > MAX_CHARS) {
            output = output.substring(0, MAX_CHARS);
        } else if (output.isEmpty()) {
            output = " ";
        }

        StringBuilder formattedOutput = new StringBuilder("```\n")
                .append(output)
                .append("```");

        if (judgeMessage != null) {
            formattedOutput.append("\n").append(judgeMessage);
        }

        embed.addField("Résultat", formattedOutput.toString(), false);
    }

    private EmbedCreateSpec embedResponse(JudgeResponse response) {
        String author = String.format("Code de %s | %s %s",
                member.getUsername(),
                response.getLanguage().getName(),
                response.getLanguage().getVersion());

        EmbedCreateSpec.Builder embed = EmbedCreateSpec.builder()
                .author(author, null, member.getAvatarUrl())
                .color(response.getStatusId() == ID_ACCEPTED ? ColorsUsed.just : ColorsUsed.wrong);

        embedCodeOutput(embed, response.getMessage(), response.getStdout(), response.getStderr(), response.getCompileOutput());

        if (response.getTime() != null) {
            embed.addField("Temps", response.getTime() + " s", true);
        }
        if (response.getMemory() != 0) {
            embed.addField("Mémoire", Math.round(response.getMemory() / 10D) / 100D + " MB", true);
        }
        if (response.getStatusDescription() != null) {
            embed.addField("Status", response.getStatusDescription(), true);
        }

        return embed.build();
    }
}