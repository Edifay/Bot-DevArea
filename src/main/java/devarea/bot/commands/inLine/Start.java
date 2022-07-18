package devarea.bot.commands.inLine;

import devarea.bot.commands.*;
import devarea.bot.presets.TextMessage;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class Start extends LongCommand implements SlashCommand {

    public Start(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);

        Step java = new EndStep() {
            @Override
            protected boolean onCall(Message message) {
                endEditMessageForChatInteractionLongCommand(TextMessage.startJava);
                return end;
            }
        };

        Step python = new EndStep() {
            @Override
            protected boolean onCall(Message message) {
                endEditMessageForChatInteractionLongCommand(TextMessage.startPython);
                return end;
            }
        };
        Step CSharp = new EndStep() {
            @Override
            protected boolean onCall(Message message) {
                endEditMessageForChatInteractionLongCommand(TextMessage.startCSharp);
                return end;
            }
        };
        Step HtmlCss = new EndStep() {
            @Override
            protected boolean onCall(Message message) {
                endEditMessageForChatInteractionLongCommand(TextMessage.startHtmlCss);
                return end;
            }
        };

        this.firstStep = new FirstStep(this.channel, java, python, CSharp, HtmlCss) {
            @Override
            public void onFirstCall(MessageCreateSpec spec) {
                super.onFirstCall(MessageCreateSpec.builder().addEmbed(TextMessage.startCommandExplain).build());
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                String content = event.getMessage().getContent();
                if (content.equalsIgnoreCase("java"))
                    return callStep(0);
                else if (content.equalsIgnoreCase("python"))
                    return callStep(1);
                else if (content.equalsIgnoreCase("csharp") || content.equalsIgnoreCase("c#"))
                    return callStep(2);
                else if (content.equalsIgnoreCase("html/css") || content.equalsIgnoreCase("htmlcss") || content.equalsIgnoreCase("html") || content.equalsIgnoreCase("css"))
                    return callStep(3);
                else
                    sendErrorEntry();
                return next;
            }
        };
        this.lastMessage = this.firstStep.getMessage();
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