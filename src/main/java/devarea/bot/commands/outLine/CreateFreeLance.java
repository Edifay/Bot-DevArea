package devarea.bot.commands.outLine;

import devarea.Main;
import devarea.bot.Init;
import devarea.bot.automatical.FreeLanceHandler;
import devarea.bot.cache.ChannelCache;
import devarea.bot.commands.commandTools.MessageSeria;
import devarea.bot.commands.*;
import devarea.bot.commands.commandTools.FreeLance;
import devarea.bot.presets.ColorsUsed;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.*;

import java.util.Objects;

public class CreateFreeLance extends LongCommand {

    protected Message messageAtEdit;
    protected FreeLance freeLance;
    protected FreeLance.FieldSeria actualFied;
    private Stape stapeAskForField = null;
    private final Stape link;


    public CreateFreeLance(final Member member) {
        super(member);
        this.freeLance = new FreeLance();
        this.freeLance.setMemberId(member.getId().asString());
        this.deletedCommand(21600000L);

        this.createLocalChannel("creation de freelance", Init.initial.missions_category);

        Stape validateAll = new EndStape() {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Voulez vous poster votre présentation ?")
                                .description("La gestion par la suite de l'offre est en développement, si vous voulez" +
                                        " la retirer par la suite demandez à un membre du staff !")
                                .color(ColorsUsed.just)
                                .footer("Vous pouvez annuler | cancel", null).build()
                        ).addComponent(getYesButton())
                        .build());

                messageAtEdit.edit(MessageEditSpec.builder().addEmbed(freeLance.getEmbed()).build()).subscribe();
                return false;
            }

            @Override
            protected boolean onReceiveInteract(ButtonInteractionEvent event) {
                if (isYes(event)) {
                    freeLance.setMessage(new MessageSeria(Objects.requireNonNull(Command.send((TextChannel) ChannelCache.watch(Init.initial.freelance_channel.asString()), MessageCreateSpec.builder()
                            .content("**Freelance de <@" + freeLance.getMemberId() + "> :**")
                            .addEmbed(freeLance.getEmbed())
                            .addComponent(ActionRow.of(Button.link(Main.domainName + "member-profile?member_id=" + member.getId() + "&open=1",
                                    "devarea.fr")))
                            .build(), true))));
                    FreeLanceHandler.add(freeLance);
                    FreeLanceHandler.update();
                    return super.onReceiveInteract(event);
                } else if (isNo(event)) {
                    return super.onReceiveInteract(event);
                }
                return next;
            }
        };

        Stape validateInfo = new Stape(validateAll) {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Liens")
                                .description("Voulez vous conserver ces liens. Si vous voulez les refaires " +
                                        "choississez non !")
                                .color(ColorsUsed.same)
                                .footer("Vous pouvez annuler | cancel", null).build())
                        .addComponent(getYesNoButton())
                        .build());
                return false;
            }

            @Override
            protected boolean onReceiveInteract(ButtonInteractionEvent event) {
                if (isYes(event)) {
                    freeLance.addField(actualFied);
                    return callStape(0);
                } else if (isNo(event)) {
                    return call(link);
                }
                return super.onReceiveInteract(event);
            }
        };

        Stape linkGetInfo = new Stape(validateInfo) {
            @Override
            protected boolean onCall(Message message) {
                actualFied = new FreeLance.FieldSeria();
                actualFied.setTitle("Liens");
                setMessage(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Liens")
                                .description("Donnez moi la description des liens que vous voulez joindre")
                                .footer("Vous pouvez annuler | cancel", null)
                                .color(ColorsUsed.same).build())
                        .components(getEmptyButton())
                        .build());

                EmbedCreateSpec.Builder builder =
                        EmbedCreateSpec.builder().title(freeLance.getFreeLanceName()).description(freeLance.getDescription()).color(ColorsUsed.same);

                for (int i = 0; i < freeLance.getFieldNumber(); i++) {
                    builder.addField(freeLance.getField(i).getTitle(), freeLance.getField(i).getValue(),
                            freeLance.getField(i).getInline());
                }
                builder.addField(actualFied.getTitle(), actualFied.getValue(), actualFied.getInline());

                messageAtEdit.edit(MessageEditSpec.builder().addEmbed(builder.build()).build()).subscribe();
                return false;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                String content = event.getMessage().getContent();
                if (!content.isEmpty() && !content.isBlank()) {
                    actualFied.setDescription(content);
                    EmbedCreateSpec.Builder builder = EmbedCreateSpec.builder()
                            .title(freeLance.getFreeLanceName())
                            .description(freeLance.getDescription());
                    for (int i = 0; i < freeLance.getFieldNumber(); i++) {
                        builder.addField(freeLance.getField(i).getTitle(), freeLance.getField(i).getValue(),
                                freeLance.getField(i).getInline());
                    }
                    builder.addField(actualFied.getTitle(), actualFied.getValue(), actualFied.getInline())
                            .color(ColorsUsed.same);
                    messageAtEdit.edit(MessageEditSpec.builder()
                            .addEmbed(builder
                                    .build())
                            .build()).subscribe();
                    return callStape(0);
                }
                return super.onReceiveMessage(event);
            }
        };

        link = new Stape(linkGetInfo, validateAll) {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Voulez-vous ajouter l'onglet liens ?")
                                .description("Cet onglet permet de partager un portfolio ou autre lien de " +
                                        "documentation sur vous.")
                                .footer("Vous pouvez annuler | cancel", null)
                                .color(ColorsUsed.just).build())
                        .addComponent(getYesNoButton())
                        .build());
                return false;
            }

            @Override
            protected boolean onReceiveInteract(ButtonInteractionEvent event) {
                if (isYes(event)) {
                    return callStape(0);
                } else if (isNo(event)) {
                    return callStape(1);
                }
                return super.onReceiveInteract(event);
            }
        }

        ;

        Stape keepField = new Stape() {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Voulez-vous concerver cette offre ?")
                                .description("Vous pouvez mettre le nombre d'offres que vous voulez, ne vous limitez " +
                                        "pas ! Mais si votre offre ne vous convient pas ne la concervez pas !")
                                .color(ColorsUsed.just)
                                .footer("Vous pouvez annuler | cancel", null).build())
                        .addComponent(getYesNoButton())
                        .build());
                return false;
            }

            @Override
            protected boolean onReceiveInteract(ButtonInteractionEvent event) {
                if (isYes(event)) {
                    freeLance.addField(actualFied);
                    return call(stapeAskForField);
                } else if (isNo(event)) {
                    return call(stapeAskForField);
                }
                return super.onReceiveInteract(event);
            }
        };

        Stape getTemps = new Stape(keepField) {
            @Override
            protected boolean onCall(Message message) {
                setText(EmbedCreateSpec.builder().title("Temps de retour").description("Proposez un temps de retour " +
                        "viable. Si vous n'avez pas de temps de retour précis tapez `empty`.").color(ColorsUsed.same).footer("Vous pouvez annuler | cancel", null).build());
                return false;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                String content = event.getMessage().getContent();
                if (!content.isEmpty() && !content.isBlank()) {
                    actualFied.setTemps(content);
                    updateEmbed();
                    return callStape(0);
                }
                return super.onReceiveMessage(event);
            }
        };

        Stape getPrice = new Stape(getTemps) {
            @Override
            protected boolean onCall(Message message) {
                setText(EmbedCreateSpec.builder().title("Prix").description("Proposez un prix que vous pensez juste, " +
                        "vous pouvez préciser que cela est variable. Si vous n'avez pas de prix tapez `empty`.").color(ColorsUsed.same).footer("Vous pouvez annuler | cancel", null).build());
                return false;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                String content = event.getMessage().getContent();
                if (!content.isEmpty() && !content.isBlank()) {
                    actualFied.setPrix(content);
                    updateEmbed();
                    return callStape(0);
                }
                return super.onReceiveMessage(event);
            }
        };

        Stape getDescriptionField = new Stape(getPrice) {
            @Override
            protected boolean onCall(Message message) {
                setText(EmbedCreateSpec.builder().title("L'offre").description("Description de l'offre, essayez de " +
                        "donner de nombreux détails, attention le Prix, et le Temps de retour de l'offre vous seront " +
                        "demmandé après.").color(ColorsUsed.same).footer("Vous pouvez annuler | cancel", null).build());
                return false;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                String content = event.getMessage().getContent();
                if (!content.isEmpty() && !content.isBlank()) {
                    actualFied.setDescription(content);
                    updateEmbed();
                    return callStape(0);
                }
                return super.onReceiveMessage(event);
            }
        };

        Stape createNewField = new Stape(getDescriptionField) {
            @Override
            protected boolean onCall(Message message) {
                actualFied = new FreeLance.FieldSeria();
                setMessage(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Donnez moi le titre de la compétence/offre.")
                                .description("Titre rapide, vous pourrez faire une présentation plus en détails par " +
                                        "la suite")
                                .color(ColorsUsed.same)
                                .footer("Vous pouvez annuler | cancel", null).build())
                        .components(getEmptyButton())
                        .build());
                return false;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                String content = event.getMessage().getContent();
                if (!content.isEmpty() && !content.isBlank()) {
                    actualFied.setTitle(content);
                    updateEmbed();
                    return callStape(0);
                }
                return super.onReceiveMessage(event);
            }
        };

        stapeAskForField = new Stape(createNewField, link) {
            @Override
            protected boolean onCall(Message message) {
                EmbedCreateSpec.Builder builder =
                        EmbedCreateSpec.builder().title(freeLance.getFreeLanceName()).description(freeLance.getDescription()).color(ColorsUsed.same);

                if (freeLance.getFieldNumber() == 0)
                    builder.addField("Titre de la compétence/titre de l'offre. Par exemple: Bot discordJS 25 " +
                            "commandes personnalisées", "Les informations sur votre offre globale,\n\n(toutes les " +
                            "informations ci-dessous vous seront demandé)\n prix : le prix approximatif de l'offre\n " +
                            "temps de réalisation: le temps de rendu de l'offre", false);
                else for (int i = 0; i < freeLance.getFieldNumber(); i++) {
                    builder.addField(freeLance.getField(i).getTitle(), freeLance.getField(i).getValue(),
                            freeLance.getField(i).getInline());
                }
                builder.addField("Les offres", "Vous pouvez ajouter le nombre d'offre que vous souhaitez", false);
                builder.addField("Liens:", "Vous pouvez insérer ici tout vos liens vers linkin, portfolio, ou autre.." +
                        ".", false);

                messageAtEdit.edit(MessageEditSpec.builder().addEmbed(builder.build()).build()).subscribe();

                setMessage(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Voulez vous ajouter une compétence/offre ?")
                                .description("Vous pouvez ajouter une offre/compétence avec un titre, description, " +
                                        "prix, date de retour.")
                                .color(ColorsUsed.same)
                                .footer("Vous pouvez annuler | cancel", null)
                                .build())
                        .addComponent(getYesNoButton())
                        .build());
                return false;
            }

            @Override
            protected boolean onReceiveInteract(ButtonInteractionEvent event) {
                if (isYes(event)) {
                    return callStape(0);
                } else if (isNo(event)) {
                    return callStape(1);
                }
                return super.onReceiveInteract(event);
            }
        };

        Stape getDescription = new Stape(stapeAskForField) {
            @Override
            protected boolean onCall(Message message) {
                setText(EmbedCreateSpec.builder().title("Description").description("Donnez votre expérience dans le " +
                        "milieu / diplôme (Brevet, Bac, études supérieures) etc...").color(ColorsUsed.same).footer(
                                "Vous pouvez annuler | cancel", null).build());
                return false;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                String content = event.getMessage().getContent();
                if (!content.isEmpty() && !content.isBlank()) {
                    freeLance.setDescription(content);
                    messageAtEdit.edit(
                                    MessageEditSpec.builder().addEmbed(
                                            EmbedCreateSpec.builder()
                                                    .title(freeLance.getFreeLanceName())
                                                    .description(freeLance.getDescription())
                                                    .addField("Titre de la compétence/titre de l'offre. Par exemple: " +
                                                            "Bot discordJS 25 commandes personnalisées", "Les " +
                                                            "informations sur votre offre globale,\n\n(toutes les " +
                                                            "informations ci-dessous vous seront demandé)\n prix : le" +
                                                            " prix approximatif de l'offre\n temps de réalisation: le" +
                                                            " temps de rendu de l'offre", false)
                                                    .addField("Les offres", "Vous pouvez ajouter le nombre d'offre " +
                                                            "que vous souhaitez", false)
                                                    .addField("Liens:", "Vous pouvez insérer ici tout vos liens vers " +
                                                            "linkin, portfolio, ou autre...", false)
                                                    .color(ColorsUsed.same).build()).build())
                            .subscribe();
                    return callStape(0);
                }
                return super.onReceiveMessage(event);
            }
        };

        Stape getName = new Stape(getDescription) {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Nom Prenom")
                                .description("Donnez moi le `Nom Prenom` que vous voulez afficher sur la présentation" +
                                        ". (Il est conseiller de ne pas donner de pseudo)")
                                .color(ColorsUsed.same)
                                .footer("Vous pouvez annuler | cancel", null).build())
                        .components(getEmptyButton())
                        .build());
                return false;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                String content = event.getMessage().getContent();
                if (!content.isEmpty() && !content.isBlank()) {
                    freeLance.setFreeLanceName(content);
                    messageAtEdit.edit(MessageEditSpec.builder().addEmbed(EmbedCreateSpec.builder()
                            .title(freeLance.getFreeLanceName())
                            .description("Depuis comment de temps etes vous en FreeLance, vos dîplomes, etes vous en " +
                                    "auto-entreprise sous contract ? Et toutes informations générales importantes...")
                            .addField("Titre de la compétence/titre de l'offre. Par exemple: Bot discordJS 25 " +
                                    "commandes personnalisées", "Les informations sur votre offre globale,\n\n(toutes" +
                                    " les informations ci-dessous vous seront demandé)\n prix : le prix approximatif " +
                                    "de l'offre\n temps de réalisation: le temps de rendu de l'offre", false)
                            .addField("Les offres", "Vous pouvez ajouter le nombre d'offre que vous souhaitez", false)
                            .addField("Liens:", "Vous pouvez insérer ici tout vos liens vers linkin, portfolio, ou " +
                                    "autre...", false)
                            .color(ColorsUsed.same).build()).build()).subscribe();
                    return callStape(0);
                }

                return super.onReceiveMessage(event);
            }
        };

        Stape showEmbed = new Stape(getName) {
            @Override
            protected boolean onCall(Message message) {
                setText(EmbedCreateSpec.builder()
                        .title("Le fonctionnement.")
                        .description("Voici l'embed que vous allez modifier pour créer votre présentation" +
                                ".\n\n**Cliquez sur : <:ayy:" + Init.idYes.getId().asString() + "> pour passer à la " +
                                "suite !**")
                        .color(ColorsUsed.same)
                        .footer("Vous pouvez annuler | cancel", null).build());
                messageAtEdit = channel.createMessage(MessageCreateSpec.builder().addEmbed(EmbedCreateSpec.builder()
                        .title("Nom Prénom")
                        .description("Depuis comment de temps etes vous en FreeLance, vos dîplomes, etes vous en " +
                                "auto-entreprise sous contract ? Et toutes informations générales importantes...")
                        .addField("Titre de la compétence/titre de l'offre. Par exemple: Bot discordJS 25 commandes " +
                                "personnalisées", "Les informations sur votre offre globale,\n\n(toutes les " +
                                "informations ci-dessous vous seront demandé)\n prix : le prix approximatif de " +
                                "l'offre\n temps de réalisation: le temps de rendu de l'offre", false)
                        .addField("Les offres", "Vous pouvez ajouter le nombre d'offre que vous souhaitez", false)
                        .addField("Liens:", "Vous pouvez insérer ici tout vos liens vers linkin, portfolio, ou autre." +
                                "..", false)
                        .color(ColorsUsed.same).build()).build()).block();
                return false;
            }

            @Override
            protected boolean onReceiveInteract(ButtonInteractionEvent event) {
                if (event.getCustomId().equals("yes")) {
                    return callStape(0);
                }
                return super.onReceiveInteract(event);
            }
        };

        this.firstStape = new FirstStape(this.channel, showEmbed) {
            @Override
            public void onFirstCall(MessageCreateSpec deleteThisVariableAndSetYourOwnMessage) {
                super.onFirstCall(MessageCreateSpec.builder().addEmbed(EmbedCreateSpec.builder()
                                .title("Présentation !")
                                .description("Vous allez vous même construire votre embed de présentation, je vais " +
                                        "essayer de vous aiguiller tout le long de la création !")
                                .color(ColorsUsed.same)
                                .footer("Vous pouvez annuler | cancel", null).build())
                        .addComponent(getYesButton())
                        .build());
            }

            @Override
            protected boolean onReceiveInteract(ButtonInteractionEvent event) {
                if (event.getCustomId().equals("yes")) {
                    return callStape(0);
                }
                return super.onReceiveInteract(event);
            }

        };

        this.lastMessage = this.firstStape.getMessage();
    }

    public void updateEmbed() {
        EmbedCreateSpec.Builder embed = EmbedCreateSpec.builder()
                .title(freeLance.getFreeLanceName())
                .description(freeLance.getDescription());

        for (int i = 0; i < freeLance.getFieldNumber(); i++)
            embed.addField(freeLance.getField(i).getTitle(), freeLance.getField(i).getValue(),
                    freeLance.getField(i).getInline());

        messageAtEdit.edit(MessageEditSpec.builder()
                .addEmbed(embed
                        .addField(actualFied.getTitle(), actualFied.getValue(), actualFied.getInline())
                        .addField("Liens:", "Vous pouvez insérer ici tout vos liens vers linkin, portfolio, github, " +
                                "ou autre...", false)
                        .color(ColorsUsed.same)
                        .build())
                .build()).block();
    }

}