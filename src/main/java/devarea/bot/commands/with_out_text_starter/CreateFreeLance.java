package devarea.bot.commands.with_out_text_starter;

import devarea.bot.Init;
import devarea.bot.automatical.FreeLanceManager;
import devarea.bot.commands.object_for_stock.MessageSeria;
import devarea.bot.commands.*;
import devarea.bot.commands.object_for_stock.FreeLance;
import devarea.bot.data.ColorsUsed;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.MessageCreateSpec;

import java.util.function.Consumer;

public class CreateFreeLance extends LongCommand {

    protected Message messageAtEdit;
    protected FreeLance freeLance;
    protected FreeLance.FieldSeria actualFied;
    private Stape stapeAskForField = null;
    private Stape link;


    public CreateFreeLance(ReactionAddEvent event) {
        super(event);
        this.freeLance = new FreeLance();
        this.freeLance.setMemberId(event.getMember().get().getId().asString());
        this.deletedCommand(21600000L);

        this.createLocalChannel("creation de freelance", Init.idMissionsCategory);

        Stape validateAll = new EndStape() {
            @Override
            protected boolean onCall(Message message) {
                addYesNoEmoji();
                setText(embed -> {
                    embed.setTitle("Voulez vous poster votre présentation ?");
                    embed.setDescription("La gestion par la suite de l'offre est en développement, si vous voulez la retirer par la suite demandez à un membre du staff !");
                    embed.setColor(ColorsUsed.just);
                    embed.setFooter("Vous pouvez annuler | cancel", null);
                });
                messageAtEdit.edit(messageEditSpec -> {
                    messageEditSpec.setEmbed(freeLance.getEmbed());
                }).subscribe();
                return false;
            }

            @Override
            protected boolean onReceiveReact(ReactionAddEvent event) {
                if (isYes(event)) {
                    freeLance.setMessage(new MessageSeria(Command.sendEmbed((TextChannel) Init.devarea.getChannelById(Init.idFreeLance).block(), freeLance.getEmbed(), true)));
                    FreeLanceManager.add(freeLance);
                    FreeLanceManager.update();
                    return super.onReceiveReact(event);
                } else if (isNo(event)) {
                    return super.onReceiveReact(event);
                }
                return next;
            }
        };

        Stape validateInfo = new Stape(validateAll) {
            @Override
            protected boolean onCall(Message message) {
                addYesNoEmoji();
                setText(embed -> {
                    embed.setTitle("Liens");
                    embed.setDescription("Voulez vous conserver ces liens. Si vous voulez les refaires choississez non !");
                    embed.setColor(ColorsUsed.same);
                    embed.setFooter("Vous pouvez annuler | cancel", null);
                });
                return false;
            }

            @Override
            protected boolean onReceiveReact(ReactionAddEvent event) {
                if (isYes(event)) {
                    freeLance.addField(actualFied);
                    return callStape(0);
                } else if (isNo(event)) {
                    return call(link);
                }
                return super.onReceiveReact(event);
            }
        };

        Stape linkGetInfo = new Stape(validateInfo) {
            @Override
            protected boolean onCall(Message message) {
                removeAllEmoji();
                actualFied = new FreeLance.FieldSeria();
                actualFied.setTitle("Liens");
                setText(embed -> {
                    embed.setTitle("Liens");
                    embed.setDescription("Donnez moi la description des liens que vous voulez joindre");
                    embed.setFooter("Vous pouvez annuler | cancel", null);
                    embed.setColor(ColorsUsed.same);
                });
                messageAtEdit.edit(msg -> msg.setEmbed(embed -> {
                    embed.setTitle(freeLance.getFreeLanceName());
                    embed.setDescription(freeLance.getDescription());
                    for (int i = 0; i < freeLance.getFieldNumber(); i++) {
                        embed.addField(freeLance.getField(i).getTitle(), freeLance.getField(i).getValue(), freeLance.getField(i).getInline());
                    }
                    embed.addField(actualFied.getTitle(), actualFied.getValue(), actualFied.getInline());
                    embed.setColor(ColorsUsed.same);
                })).subscribe();
                return false;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                String content = event.getMessage().getContent();
                if (!content.isEmpty() && !content.isBlank()) {
                    actualFied.setDescription(content);
                    messageAtEdit.edit(msg -> msg.setEmbed(embed -> {
                        embed.setTitle(freeLance.getFreeLanceName());
                        embed.setDescription(freeLance.getDescription());
                        for (int i = 0; i < freeLance.getFieldNumber(); i++) {
                            embed.addField(freeLance.getField(i).getTitle(), freeLance.getField(i).getValue(), freeLance.getField(i).getInline());
                        }
                        embed.addField(actualFied.getTitle(), actualFied.getValue(), actualFied.getInline());
                        embed.setColor(ColorsUsed.same);
                    })).block();
                    return callStape(0);
                }
                return super.onReceiveMessage(event);
            }
        };

        link = new Stape(linkGetInfo, validateAll) {
            @Override
            protected boolean onCall(Message message) {
                setText(embed -> {
                    embed.setTitle("Voulez-vous ajouter l'onglet liens ?");
                    embed.setDescription("Cet onglet permet de partager un portfolio ou autre lien de documentation sur vous.");
                    embed.setFooter("Vous pouvez annuler | cancel", null);
                    embed.setColor(ColorsUsed.just);
                });
                addYesNoEmoji();
                return false;
            }

            @Override
            protected boolean onReceiveReact(ReactionAddEvent event) {
                if (isYes(event)) {
                    return callStape(0);
                } else if (isNo(event)) {
                    return callStape(1);
                }
                return super.onReceiveReact(event);
            }
        };

        Stape keepField = new Stape() {
            @Override
            protected boolean onCall(Message message) {
                addYesNoEmoji();
                setText(embed -> {
                    embed.setTitle("Voulez-vous concerver cette offre ?");
                    embed.setDescription("Vous pouvez mettre le nombre d'offres que vous voulez, ne vous limitez pas ! Mais si votre offre ne vous convient pas ne la concervez pas !");
                    embed.setColor(ColorsUsed.just);
                    embed.setFooter("Vous pouvez annuler | cancel", null);
                });
                return false;
            }

            @Override
            protected boolean onReceiveReact(ReactionAddEvent event) {
                if (isYes(event)) {
                    freeLance.addField(actualFied);
                    return call(stapeAskForField);
                } else if (isNo(event)) {
                    return call(stapeAskForField);
                }
                return super.onReceiveReact(event);
            }
        };

        Stape getTemps = new Stape(keepField) {
            @Override
            protected boolean onCall(Message message) {
                setText(embed -> {
                    embed.setTitle("Temps de retour");
                    embed.setDescription("Proposez un temps de retour viable. Si vous n'avez pas de temps de retour précis tapez `empty`.");
                    embed.setColor(ColorsUsed.same);
                    embed.setFooter("Vous pouvez annuler | cancel", null);
                });
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
                setText(embed -> {
                    embed.setTitle("Prix");
                    embed.setDescription("Proposez un prix que vous pensez juste, vous pouvez préciser que cela est variable. Si vous n'avez pas de prix tapez `empty`.");
                    embed.setColor(ColorsUsed.same);
                    embed.setFooter("Vous pouvez annuler | cancel", null);
                });
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
                setText(embed -> {
                    embed.setTitle("L'offre");
                    embed.setDescription("Description de l'offre, essayez de donner de nombreux détails, attention le Prix, et le Temps de retour de l'offre vous seront demmandé après.");
                    embed.setFooter("Vous pouvez annuler | cancel", null);
                    embed.setColor(ColorsUsed.same);
                });
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
                removeAllEmoji();
                setText(embed -> {
                    embed.setTitle("Donnez moi le titre de la compétence/offre.");
                    embed.setDescription("Titre rapide, vous pourrez faire une présentation plus en détails par la suite");
                    embed.setFooter("Vous pouvez annuler | cancel", null);
                    embed.setColor(ColorsUsed.same);
                });
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
                messageAtEdit.edit(msg -> msg.setEmbed(embed -> {
                    embed.setTitle(freeLance.getFreeLanceName());
                    embed.setDescription(freeLance.getDescription());
                    if (freeLance.getFieldNumber() == 0)
                        embed.addField("Titre de la compétence/titre de l'offre. Par exemple: Bot discordJS 25 commandes personnalisées", "Les informations sur votre offre globale,\n\n(toutes les informations ci-dessous vous seront demandé)\n prix : le prix approximatif de l'offre\n temps de réalisation: le temps de rendu de l'offre", false);
                    else
                        for (int i = 0; i < freeLance.getFieldNumber(); i++) {
                            embed.addField(freeLance.getField(i).getTitle(), freeLance.getField(i).getValue(), freeLance.getField(i).getInline());
                        }
                    embed.addField("Les offres", "Vous pouvez ajouter le nombre d'offre que vous souhaitez", false);
                    embed.addField("Liens:", "Vous pouvez insérer ici tout vos liens vers linkin, portfolio, ou autre...", false);
                    embed.setColor(ColorsUsed.same);
                })).subscribe();
                addYesNoEmoji();
                setText(embed -> {
                    embed.setTitle("Voulez vous ajouter une compétence/offre ?");
                    embed.setDescription("Vous pouvez ajouter une offre/compétence avec un titre, description, prix, date de retour.");
                    embed.setFooter("Vous pouvez annuler | cancel", null);
                    embed.setColor(ColorsUsed.same);
                });
                return false;
            }

            @Override
            protected boolean onReceiveReact(ReactionAddEvent event) {
                if (isYes(event)) {
                    return callStape(0);
                } else if (isNo(event)) {
                    return callStape(1);
                }
                return super.onReceiveReact(event);
            }
        };

        Stape getDescription = new Stape(stapeAskForField) {
            @Override
            protected boolean onCall(Message message) {
                setText(embed -> {
                    embed.setTitle("Description");
                    embed.setDescription("Donnez votre expérience dans le milieu / diplôme (Brevet, Bac, études supérieures) etc...");
                    embed.setColor(ColorsUsed.same);
                    embed.setFooter("Vous pouvez annuler | cancel", null);
                });
                return false;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                String content = event.getMessage().getContent();
                if (!content.isEmpty() && !content.isBlank()) {
                    freeLance.setDescription(content);
                    messageAtEdit.edit(msg -> msg.setEmbed(embed -> {
                        embed.setTitle(freeLance.getFreeLanceName());
                        embed.setDescription(freeLance.getDescription());
                        embed.addField("Titre de la compétence/titre de l'offre. Par exemple: Bot discordJS 25 commandes personnalisées", "Les informations sur votre offre globale,\n\n(toutes les informations ci-dessous vous seront demandé)\n prix : le prix approximatif de l'offre\n temps de réalisation: le temps de rendu de l'offre", false);
                        embed.addField("Les offres", "Vous pouvez ajouter le nombre d'offre que vous souhaitez", false);
                        embed.addField("Liens:", "Vous pouvez insérer ici tout vos liens vers linkin, portfolio, ou autre...", false);
                        embed.setColor(ColorsUsed.same);
                    })).subscribe();
                    return callStape(0);
                }
                return super.onReceiveMessage(event);
            }
        };

        Stape getName = new Stape(getDescription) {
            @Override
            protected boolean onCall(Message message) {
                removeAllEmoji();
                setText(embed -> {
                    embed.setTitle("Nom Prenom");
                    embed.setDescription("Donnez moi le `Nom Prenom` que vous voulez afficher sur la présentation. (Il est conseiller de ne pas donner de pseudo)");
                    embed.setColor(ColorsUsed.just);
                    embed.setFooter("Vous pouvez annuler | cancel", null);
                });
                return false;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                String content = event.getMessage().getContent();
                if (!content.isEmpty() && !content.isBlank()) {
                    freeLance.setFreeLanceName(content);
                    messageAtEdit.edit(msg -> msg.setEmbed(embed -> {
                        embed.setTitle(freeLance.getFreeLanceName());
                        embed.setDescription("Depuis comment de temps etes vous en FreeLance, vos dîplomes, etes vous en auto-entreprise sous contract ? Et toutes informations générales importantes...");
                        embed.addField("Titre de la compétence/titre de l'offre. Par exemple: Bot discordJS 25 commandes personnalisées", "Les informations sur votre offre globale,\n\n(toutes les informations ci-dessous vous seront demandé)\n prix : le prix approximatif de l'offre\n temps de réalisation: le temps de rendu de l'offre", false);
                        embed.addField("Les offres", "Vous pouvez ajouter le nombre d'offre que vous souhaitez", false);
                        embed.addField("Liens:", "Vous pouvez insérer ici tout vos liens vers linkin, portfolio, ou autre...", false);
                        embed.setColor(ColorsUsed.same);
                    })).subscribe();
                    return callStape(0);
                }

                return super.onReceiveMessage(event);
            }
        };

        Stape showEmbed = new Stape(getName) {
            @Override
            protected boolean onCall(Message message) {
                setText(embed -> {
                    embed.setTitle("Le fonctionnement.");
                    embed.setDescription("Voici l'embed que vous allez modifier pour créer votre présentation.\n\n**Cliquez sur : <:ayy:"+Init.idYes.getId().asString()+"> pour passer à la suite !**");
                    embed.setFooter("Vous pouvez annuler | cancel", null);
                    embed.setColor(ColorsUsed.just);
                });
                messageAtEdit = channel.createMessage(msg -> {
                    msg.setEmbed(embed -> {
                        embed.setTitle("Nom Prénom");
                        embed.setDescription("Depuis comment de temps etes vous en FreeLance, vos dîplomes, etes vous en auto-entreprise sous contract ? Et toutes informations générales importantes...");
                        embed.addField("Titre de la compétence/titre de l'offre. Par exemple: Bot discordJS 25 commandes personnalisées", "Les informations sur votre offre globale,\n\n(toutes les informations ci-dessous vous seront demandé)\n prix : le prix approximatif de l'offre\n temps de réalisation: le temps de rendu de l'offre", false);
                        embed.addField("Les offres", "Vous pouvez ajouter le nombre d'offre que vous souhaitez", false);
                        embed.addField("Liens:", "Vous pouvez insérer ici tout vos liens vers linkin, portfolio, ou autre...", false);
                        embed.setColor(ColorsUsed.same);
                    });
                }).block();
                return false;
            }

            @Override
            protected boolean onReceiveReact(ReactionAddEvent event) {
                if (isYes(event))
                    return callStape(0);
                return super.onReceiveReact(event);
            }
        };

        this.firstStape = new FirstStape(this.channel, showEmbed) {
            @Override
            public void onFirstCall(Consumer<? super MessageCreateSpec> deleteThisVariableAndSetYourOwnMessage) {
                super.onFirstCall(msg -> {
                    msg.setEmbed(embed -> {
                        embed.setTitle("Présentation !");
                        embed.setDescription("Vous allez vous même construire votre embed de présentation, je vais essayer de vous aiguiller tout le long de la création !");
                        embed.setFooter("Vous pouvez annuler | cancel", null);
                        embed.setColor(ColorsUsed.same);
                    });
                });
                addYesEmoji();
            }

            @Override
            protected boolean onReceiveReact(ReactionAddEvent event) {
                if (isYes(event)) {
                    return callStape(0);
                }
                return super.onReceiveReact(event);
            }
        }

        ;
        this.lastMessage = this.firstStape.getMessage();
    }

    public void updateEmbed() {
        messageAtEdit.edit(msg -> msg.setEmbed(embed -> {
            embed.setTitle(freeLance.getFreeLanceName());
            embed.setDescription(freeLance.getDescription());
            for (int i = 0; i < freeLance.getFieldNumber(); i++)
                embed.addField(freeLance.getField(i).getTitle(), freeLance.getField(i).getValue(), freeLance.getField(i).getInline());
            embed.addField(actualFied.getTitle(), actualFied.getValue(), actualFied.getInline());
            embed.addField("Liens:", "Vous pouvez insérer ici tout vos liens vers linkin, portfolio, github, ou autre...", false);
            embed.setColor(ColorsUsed.same);
        })).block();
    }

}