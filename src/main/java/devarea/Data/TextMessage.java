package devarea.Data;

import devarea.Main;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;

import java.time.Instant;
import java.util.Calendar;
import java.util.function.Consumer;

public class TextMessage {

    /*
        Ce message est envoyer aux personnes qui envoient des messages au bot en privé.
     */
    public static final String messageDisableInPrivate = "`Les commandes ne sont pas activé dans les messages privés.`";
    /*
        Ce message est envoyer dans un embed quand la commande n'existe pas
     */
    public static final String commandNotFound = "La commande que vous avez demandé n'existe pas !";
    /*
        Message quand l'utiliseteur n'as pas la permissions de faire la commande qu'il a demandé
     */
    public static final String haventPermission = "Vous n'avez pas la permission d'éxécuter cette commande !";
    /*
        Quand le bot est arrêter !
     */
    public static final String stopCommand = "Le bot a été arréter ! :o:";
    /*
        Quand l'utilisateur n'as pas mit d'argument alors que la commande en demandais
     */
    public static final String errorNeedArguments = "Vous devez mettre du texte après la commande !";
    /*
        Le premier message lors du questionnaire de bienvenue
     */
    public static final String firstText = "Pour que tu puisses bien t'intégrer au serveur je vais de donner quelques informations, et t'en demander quelques une pour que je puisse bien te diriger !\n\nTu en as pour maximum **2min**, mais attention tu seras kick après 10min sans avoir compléter le questionnaire !\n\nPour passer à la suite il te faut réagir <:ayy:" + Main.idYes.asBigInteger().toString() + "> ! Bonne chance !";
    /*
        Les règles pour que la communication lors du code sois correcte
     */
    public static final String rulesForSpeakCode = "Pour pouvoir bien discuter efficacement avec la communautée voici quelques règles de base :\n\n- Essayes de parler avec un bon français.\n\n" + "- Le code ne doit pas être envoyé en brut. Tu peux utiliser \\`\\`\\`code\\`\\`\\` => ```code``` pour les petits codes (moins de 2000 caractères). Pour les codes plus grands tu peux utiliser des sites externes comme hastbin/pastebin.\n\n" + "- Poses ta question directement, pas de \"**Quelqu'un peut m'aider ?**\" ou autres questions sans intérêt. Poses directement ta question dans le channel adapté, avec du code et les recherches que tu as effectuées (avant de poser une question vas regarder rapidement sur google peut être que la réponse y sera).\n\n" + "- Fais attention où tu parles ! **ATTENTION** : le serveur est très structuré pour la bonne compréhension essayes de respecter les channels.\n\nSi tu as bien lu ces règles réagis avec <:ayy:" + Main.idYes.asString() + ">";
    /*
        Les règles pour demander des missions
     */
    public static final String rulesForAskCode = "Si tu es sur ce serveur c'est donc que tu as besoin de développeurs. \nLes channels : <#" + Main.idMissionsGratuites.asString() + "> et <#" + Main.idMissionsPayantes.asString() + "> te permettent de proposer des missions/projets. \n\nCliques sur : <:ayy:" + Main.idYes.asString() + "> pour accéder à la suite.";
    /*
        Le message de règles
     */
    public static final String rules = "Tu dois maintenant accepter les règles :" + "\n\nLes règles classique s'applique sur ce serveur, toutes forme de violation de ces règles de vie, sera pour tout le monde le bannissement." + "\n\nNous n'aiderons en aucun cas, à la production de logiciel malveillant, programme lié au darknet, recherche de failles de sécurité ou autres mauvaises intentions." + "\n\nLe serveur ne prend pas en charge la sûreté des missions payantes, en effet le serveur ne s'implique en aucun cas de la fiabilité du client ou du développeur." + "\n\nN'hésitez pas à venir dire Salut dans le channel général certainement d'autres personnes seront là pour vous expliquer le fonctionnement du discord." + "\n\nCliques sur : <:ayy:" + Main.idYes.asString() + "> pour accéder à la suite.";
    /*
        Le message méttant en avant le channels présentation
     */
    public static final String presentation = "Avant d'aller parler dans les channels et de rencontrer les membres de la communauté, essaye de faire une présentation de toi qui permettra d'entamer la discussion, et d'en savoir un peu plus sur toi ! <#" + Main.idPresentation.asString() + ">" + "\n\nCliques sur : <:ayy:" + Main.idYes.asString() + "> pour accéder à la suite.";
    /*
        Le message mmettant en avant le channels des rôles
     */
    public static final String roles = "Tu as maintenant accès au <#" + Main.idRolesChannel.asString() + ">, tu dois choisir tes rôles avec précision, **attention cela est la base du serveur**.\n\nJe te donne accès au serveur dans 30 secondes (ne t'inquiète pas si tu prends plus que 30secondes tu as tout le temps qu'il te faut) tu as donc le temps de prendre tes <#" + Main.idRolesChannel.asString() + ">.\n\nBienvenue !";
    /*
        le message lors de l'help command
     */
    public static final Consumer<? super EmbedCreateSpec> helpEmbed = embedCreateSpec -> {
        embedCreateSpec.setTitle("Voici la liste des commandes :");
        embedCreateSpec.setDescription("`//help` -> donne cette liste.\n`//ping` -> donne le temps de latence du bot.\n`//meetup` -> permet de créer un meetup autour d'un sujet.\n`//start` -> envois un message qui permet de bien commencer dans une langage.\n`//rank` -> donne l'xp et le rang de la personne (mentionnable).\n`//leaderboard` -> permet de voir le classement des membres du serveur en xp.\n`//devhelp` -> mentionne les développeurs ayant pris le rôle DevHelper.\n`//bumps` -> permet simplement de voir le nombre de bumps effectué.\n\n`creationMissions` -> ne se lance pas comme une commande classique, une réaction dans le channel : <#"+Main.idMissionsPayantes.asString()+"> permet de commencer la commande.");
        embedCreateSpec.setColor(ColorsUsed.just);
        embedCreateSpec.setTimestamp(Instant.now());
    };
    /*
        Le message lord de l'help command pour les admins
     */
    public static final Consumer<? super EmbedCreateSpec> helpEmbedAdmin = embedCreateSpec -> {
        embedCreateSpec.setTitle("Voici la liste des commandes admin:");
        embedCreateSpec.setDescription("`//send` -> permet de faire envoyer des message aux bots.\n`//stop` -> arrête le skript du bot.");
        embedCreateSpec.setColor(ColorsUsed.just);
        embedCreateSpec.setTimestamp(Instant.now());
    };
    /*
        Le message lors de la commande start pour le java
     */
    public static final Consumer<? super EmbedCreateSpec> startJava = embed -> {
        embed.setTitle("Commencer en Java");
        embed.setDescription("Tout d'abord il faut");
        embed.setTimestamp(Instant.now());
        User user = Main.client.getSelf().block();
        embed.setAuthor(user.getUsername(), user.getAvatarUrl(), user.getAvatarUrl());
        embed.setColor(ColorsUsed.same);
    };
    /*
        Le message expliquant la commande start, et en disant quels sont les langages qui sont disponibles.
     */
    public static final Consumer<? super EmbedCreateSpec> startCommandExplain = embed -> {
        embed.setTitle("Start");
        embed.setDescription("Il y a acutellement 1 langage qui possède un starter : java.\nQuel langage voulez vous voir ? (écrivez son nom)\n\nPour annuler la commande utilisez le mot `cancel`.");
        embed.setColor(ColorsUsed.just);
    };

    public static final Consumer<? super EmbedCreateSpec> meetupCommandExplain = embed -> {
        embed.setAuthor("Les meetups sont des rencontres vocales/écrits sur un sujet créer par la communauté.", null, null);
        embed.setTitle("Commandes");
        embed.setDescription("`create` => Permet de créer un nouveau meetup.\n`delete` => Permet de supprimer un meetup que vous avez créé.\n`channel` => renvois vers le channel des meetups.\n\nVous pouvez annuler la commande à tout moment avec `cancel` ou `annuler`.");
        embed.setTimestamp(Instant.now());
        embed.setColor(ColorsUsed.just);
    };

    public static final Consumer<? super EmbedCreateSpec> meetupCreateGetDescription = embedCreateSpec -> {
        embedCreateSpec.setTitle("Quel est le sujet de meetup ?");
        embedCreateSpec.setDescription("Vous pouvez faire une petite description (quelques lignges) sur qu'est ce que va être ce meetup.");
        embedCreateSpec.setFooter("cancel | annuler pour quitter.", null);
        embedCreateSpec.setColor(ColorsUsed.just);
    };

    public static final Consumer<? super EmbedCreateSpec> meetupCreateGetDate = embedCreateSpec -> {
        embedCreateSpec.setTitle("Quand voulez vous faire le meetup ?");
        Calendar calendar = Calendar.getInstance();
        embedCreateSpec.setDescription("Vous devez répondre sous la forme **dd/MM/yyyy hh:mm**.\nPar exemple `" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + "`.");
        embedCreateSpec.setFooter("cancel | annuler pour quitter.", null);
        embedCreateSpec.setColor(ColorsUsed.just);
    };

    public static final Consumer<? super EmbedCreateSpec> meetupCreateGetImage = embed -> {
        embed.setTitle("Image");
        embed.setDescription("Vous avez la possibilité d'ajouter une image de présentation au meeetup !\nSi vous ne voulez pas ajouter d'image répondez `non`, sinon envoyez votre image.");
        embed.setFooter("cancel | annuler pour quitter.", null);
        embed.setColor(ColorsUsed.just);
    };

    public static final Consumer<? super EmbedCreateSpec> meetupCreateAsk = embed -> {
        embed.setTitle("Votre demande a été transmise !");
        embed.setDescription("Le staff va analyser votre demande, vous recevrez un message pour vous tenir au courant de son avancement !");
        embed.setTimestamp(Instant.now());
        embed.setColor(ColorsUsed.just);
    };

    public static final Consumer<? super EmbedCreateSpec> missionTitle = embed -> {
        embed.setTitle("Creation d'une mission");
        embed.setDescription("Donnez un titre à votre mission !");
        embed.setFooter("Vous pouvez annuler | cancel", null);
        embed.setColor(ColorsUsed.just);
    };

    public static final Consumer<? super EmbedCreateSpec> missionDescription = embed -> {
        embed.setTitle("Creation d'une mission");
        embed.setDescription("Donnez une description complète de votre mission, les aspect difficiles, et le contexte.");
        embed.setFooter("Vous pouvez annuler | cancel", null);
        embed.setColor(ColorsUsed.just);
    };

    public static final Consumer<? super EmbedCreateSpec> missionSupport = embed -> {
        embed.setTitle("Creation d'une mission");
        embed.setDescription("Donnez le support sur le quel vous voulez que votre programme fonctionne. Web, linux, windows, discord.js....");
        embed.setFooter("Vous pouvez annuler | cancel", null);
        embed.setColor(ColorsUsed.just);
    };

    public static final Consumer<? super EmbedCreateSpec> missionLangage = embed -> {
        embed.setTitle("Creation d'une mission");
        embed.setDescription("Donnez le langage de programmation que vous voulez (si vous n'en avez pas vous pouvez marquer `aucune préférence`).");
        embed.setFooter("Vous pouvez annuler | cancel", null);
        embed.setColor(ColorsUsed.just);
    };

    public static final Consumer<? super EmbedCreateSpec> missionPrix = embed -> {
        embed.setTitle("Creation d'une mission");
        embed.setDescription("Donnez le prix/bugdet que vous pouvez mettre dans cette mission ! (Pensez à mettre la devise)");
        embed.setFooter("Vous pouvez annuler | cancel", null);
        embed.setColor(ColorsUsed.just);
    };

    public static final Consumer<? super EmbedCreateSpec> missionDate = embed -> {
        embed.setTitle("Creation d'une mission");
        embed.setDescription("Donnez la date de retour de la mission. Elle peut être `non définie`.");
        embed.setFooter("Vous pouvez annuler | cancel", null);
        embed.setColor(ColorsUsed.just);
    };

    public static final Consumer<? super EmbedCreateSpec> missionNiveau = embed -> {
        embed.setTitle("Creation d'une mission");
        embed.setDescription("Donnez le niveau du difficulté de la mission (estimation).");
        embed.setFooter("Vous pouvez annuler | cancel", null);
        embed.setColor(ColorsUsed.just);
    };
}