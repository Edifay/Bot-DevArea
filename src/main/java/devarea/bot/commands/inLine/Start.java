package devarea.bot.commands.inLine;

import devarea.bot.commands.EndStape;
import devarea.bot.commands.FirstStape;
import devarea.bot.commands.LongCommand;
import devarea.bot.commands.Stape;
import devarea.bot.presets.TextMessage;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.MessageCreateSpec;

public class Start extends LongCommand {

    public Start(final Member member, final TextChannel channel, final Message message) {
        super(member, channel);

        Stape java = new EndStape() {
            @Override
            protected boolean onCall(Message message) {
                setText(TextMessage.startJava);
                return end;
            }
        };

        Stape python = new EndStape() {
            @Override
            protected boolean onCall(Message message) {
                setText(TextMessage.startPython);
                return end;
            }
        };
        Stape CSharp = new EndStape() {
            @Override
            protected boolean onCall(Message message) {
                setText(TextMessage.startCSharp);
                return end;
            }
        };
        Stape HtmlCss = new EndStape() {
            @Override
            protected boolean onCall(Message message) {
                setText(TextMessage.startHtmlCss);
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

}