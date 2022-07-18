package devarea.bot.commands.outLine;

import devarea.bot.Init;
import devarea.global.handlers.MissionsHandler;
import devarea.bot.commands.EndStep;
import devarea.bot.commands.FirstStep;
import devarea.bot.commands.LongCommand;
import devarea.bot.commands.Step;
import devarea.bot.commands.commandTools.Mission;
import devarea.bot.presets.ColorsUsed;
import devarea.bot.presets.TextMessage;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;

import static devarea.global.utils.ThreadHandler.startAway;

public class CreateMission extends LongCommand {

    protected Mission mission;

    public CreateMission(final Member member) {
        super(member);
        this.mission = new Mission();
        this.mission.setMemberId(member.getId().asString());
        this.deletedCommand(10800000L);

        this.createLocalChannel("creation de mission", Init.initial.missions_category);

        Step niveauStep = new EndStep() {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder().addEmbed(TextMessage.missionNiveau).build());
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                mission.setNiveau(event.getMessage().getContent());
                MissionsHandler.createMission(mission.getTitle(), mission.getDescriptionText(), mission.getBudget(),
                        mission.getDeadLine(), mission.getLanguage(), mission.getSupport(), mission.getNiveau(),
                        member);
                try {
                    startAway(() -> member.getPrivateChannel().block().createMessage(MessageCreateSpec.builder().addEmbed(EmbedCreateSpec.builder().title("Suivis d'une mission").description("La commande `//mission` permet de gérer sa mission, pour par exemple la supprimer.\n\n**Le site web** permet aussi de gérer ces missions dans l'onglet options : https://devarea.fr.").color(ColorsUsed.same).build()).build()).block());
                } catch (Exception e) {
                }
                return end;
            }
        };

        Step supportStep = new Step(niveauStep) {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder().addEmbed(TextMessage.missionSupport).build());
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                mission.setSupport(event.getMessage().getContent());
                return callStape(0);
            }
        };

        Step langageStep = new Step(supportStep) {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder().addEmbed(TextMessage.missionLangage).build());
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                mission.setLanguage(event.getMessage().getContent());
                return callStape(0);
            }
        };

        Step dateRetourStep = new Step(langageStep) {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder().addEmbed(TextMessage.missionDate).build());
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                mission.setDeadLine(event.getMessage().getContent());
                return callStape(0);
            }
        };

        Step prixStage = new Step(dateRetourStep) {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder().addEmbed(TextMessage.missionPrix).build());
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                mission.setBudget(event.getMessage().getContent());
                return callStape(0);
            }
        };

        Step description = new Step(prixStage) {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder().addEmbed(TextMessage.missionDescription).build());
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                mission.setDescriptionText(event.getMessage().getContent());
                return callStape(0);
            }
        };

        this.firstStape = new FirstStep(this.channel, description) {
            @Override
            public void onFirstCall(MessageCreateSpec spec) {
                super.onFirstCall(MessageCreateSpec.builder().content("<@" + member.getId().asString() + ">,").addEmbed(TextMessage.missionTitle).build());
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                mission.setTitle(event.getMessage().getContent());
                return callStape(0);
            }
        };
        this.lastMessage = this.firstStape.getMessage();
    }

}
