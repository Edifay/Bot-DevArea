package devarea.bot.commands.inLine;

import devarea.bot.commands.*;
import devarea.bot.presets.TextMessage;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class Start extends LongCommand implements SlashCommand {

    public Start(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        chatInteraction.deferReply().subscribe();

        Stape java = new EndStape() {
            @Override
            protected boolean onCall(Message message) {
                editEmbed(TextMessage.startJava);
                delete(false, this.message);
                return end;
            }
        };

        Stape python = new EndStape() {
            @Override
            protected boolean onCall(Message message) {
                editEmbed(TextMessage.startPython);
                delete(false, this.message);
                return end;
            }
        };
        Stape CSharp = new EndStape() {
            @Override
            protected boolean onCall(Message message) {
                editEmbed(TextMessage.startCSharp);
                delete(false, this.message);
                return end;
            }
        };
        Stape HtmlCss = new EndStape() {
            @Override
            protected boolean onCall(Message message) {
                editEmbed(TextMessage.startHtmlCss);
                delete(false, this.message);
                return end;
            }
        };

        this.firstStape = new FirstStape(this.channel, java, python, CSharp, HtmlCss) {
            @Override
            public void onFirstCall(MessageCreateSpec spec) {
                super.onFirstCall(MessageCreateSpec.builder().addEmbed(TextMessage.startCommandExplain).build());
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                String content = event.getMessage().getContent();
                if (content.equalsIgnoreCase("java"))
                    return callStape(0);
                else if (content.equalsIgnoreCase("python"))
                    return callStape(1);
                else if (content.equalsIgnoreCase("csharp") || content.equalsIgnoreCase("c#"))
                    return callStape(2);
                else if (content.equalsIgnoreCase("html/css") || content.equalsIgnoreCase("htmlcss") || content.equalsIgnoreCase("html") || content.equalsIgnoreCase("css"))
                    return callStape(3);
                else
                    sendErrorEntry();
                return next;
            }
        };
        this.lastMessage = this.firstStape.getMessage();
    }

    public Start() {
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("start")
                .description("Donne un petit texte explicatif pour bien commencer le langage souhaité !")
                .build();
    }
}