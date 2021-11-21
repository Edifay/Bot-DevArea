package devarea.bot.data;

import devarea.bot.Init;
import discord4j.core.spec.EmbedCreateSpec;

import java.time.Instant;
import java.util.Calendar;
import java.util.function.Consumer;

public class TextMessage {

    /*
        Ce message est envoyé aux personnes qui envoient des messages au bot en privé.
     */
    public static final String messageDisableInPrivate = "`Les commandes ne sont pas activées dans les messages privés.`";
    /*
        Ce message est envoyé dans un embed quand la commande n'existe pas
     */
    public static final String commandNotFound = "La commande que vous avez demandée n'existe pas !";
    /*
        Message quand l'utiliseteur n'a pas la permissions de faire la commande qu'il a demandé
     */
    public static final String haventPermission = "Vous n'avez pas la permission d'exécuter cette commande !";
    /*
        Quand le bot s'arrète !
     */
    public static final String stopCommand = "Le bot a été arrêté ! :o:";
    /*
        Quand l'utilisateur n'a pas mis d'argument alors que la commande en demandait
     */
    public static final String errorNeedArguments = "Vous devez mettre du texte après la commande !";
    /*
        Le premier message lors du questionnaire de bienvenue
     */
    public static final String firstText = "Pour que tu puisses bien t'intégrer au serveur, je vais te donner quelques informations, et t'en demander quelques une pour que je puisse bien te diriger !\n\nTu en as pour maximum **2min**, mais attention, tu seras kick après 10min sans avoir complété le questionnaire !\n\nPour passer à la suite il te faut réagir <:ayy:" + Init.idYes.getId().asString() + "> ! Bonne chance !";
    /*
        Les règles pour que la communication lors du code sois correcte
     */
    public static final String rulesForSpeakCode = "Pour pouvoir discuter efficacement avec la communauté voici quelques règles de base :\n\n- Essaye de parler avec un bon français.\n\n" + "- Le code ne doit pas être envoyé en brut. Tu peux utiliser \\`\\`\\`code\\`\\`\\` => ```code``` pour les petits codes (moins de 2000 caractères). Pour les codes plus grands tu peux utiliser des sites externes comme hastbin/pastebin.\n\n" + "- Poses ta question directement, pas de \"**Quelqu'un peut m'aider ?**\" ou autres questions sans intérêt. Poses directement ta question dans le channel adapté, avec du code et les recherches que tu as effectuées (avant de poser une question, vas regarder rapidement sur google peut être sûr que la réponse n'y est pas déjà).\n\n" + "- Fais attention où tu parles ! **ATTENTION** : le serveur est très structuré, pour la bonne compréhension essaye de respecter les channels.\n\nSi tu as bien lu ces règles, réagis avec <:ayy:" + Init.idYes.getId().asString() + ">";
    /*
        Les règles pour demander des missions
     */
    public static final String rulesForAskCode = "Si tu es sur ce serveur c'est donc que tu as besoin de développeurs. \nLes channels : <#" + Init.idMissionsGratuites.asString() + "> et <#" + Init.idMissionsPayantes.asString() + "> te permettent de proposer des missions/projets. \n\nCliques sur : <:ayy:" + Init.idYes.getId().asString() + "> pour accéder à la suite.";
    /*
        Le message de règles
     */
    public static final String rules = "Tu dois maintenant accepter les règles :" + "\n\nLes règles classiques s'appliquent sur ce serveur, la sanction pour toute forme de violation de ces règles de vie, sera pour tout le monde le bannissement." + "\n\nNous n'aiderons en aucun cas, à la production de logiciel malveillant, programme lié au darknet, recherche de failles de sécurité ou autres mauvaises intentions." + "\n\nLe serveur ne prend pas en charge la sûreté des missions payantes, en effet le serveur ne s'implique en aucun cas de la fiabilité du client ou du développeur." + "\n\nN'hésitez pas à venir dire Salut dans le channel général. D'autres personnes seront certainement là pour vous expliquer le fonctionnement du discord." + "\n\nCliques sur : <:ayy:" + Init.idYes.getId().asString() + "> pour accéder à la suite.";
    /*
        Le message mettant en avant le channel présentation
     */
    public static final String presentation = "Avant d'aller parler dans les channels et de rencontrer les membres de la communauté, essaye de faire une présentation de toi qui permettra d'entamer la discussion, et d'en savoir un peu plus sur toi ! <#" + Init.idPresentation.asString() + ">" + "\n\nCliques sur : <:ayy:" + Init.idYes.getId().asString() + "> pour accéder à la suite.";
    /*
        Le message mmettant en avant le channel des rôles
     */
    public static final String roles = "Tu as maintenant accès au <#" + Init.idRolesChannel.asString() + ">, tu dois choisir tes rôles avec précision, **attention cela est la base du serveur**.\n\nJe te donne accès au serveur dans 30 secondes (ne t'inquiète pas si tu prends plus que 30 secondes tu as tout le temps qu'il te faut) tu as donc le temps de prendre tes <#" + Init.idRolesChannel.asString() + ">.\n\nBienvenue !";
    /*
        le message lors de l'help command
     */
    public static final Consumer<? super EmbedCreateSpec> helpEmbed = embed -> {
        embed.setTitle("Voici la liste des commandes :");
        embed.setDescription("Voici la liste de toutes les commandes :");
        embed.addField("Les commandes globales :", "`//help` -> donne cette liste.\n`//ping` -> donne le temps de latence du bot.\n`//start` -> envois un message qui permet de bien commencer dans un langage.\n", false);
        embed.addField("Communication :", "`//devhelp` -> mentionne les membres ayant pris le rôle Dev'Helper.\n`//ask` -> donne les informations pour bien poser une question.\n`//meetup` -> permet de créer un meetup autour d'un sujet.\n\nLes channels d'aides vocaux sont créé automatiquement par le bot lors de la connexion au channel vocal : \"Votre channel d'aide\".", false);
        embed.addField("XP:", "`//rank` -> donne l'xp et le rang de la personne (mentionnable).\n`//leaderboard` -> permet de voir le classement des membres du serveur en xp.\n`//askreward [mention de la personne que vous avez aidé]` -> Si vous ou plusieurs personnes avez aidé quelqu'un à résoudre son problème, vous pouvez lui demander une récompense (en xp) avec cette commande.\n`//givereward` -> Si une ou plusieurs personnes vous ont aidé à résoudre votre problème et que vous désirez lui donner une récompense (en xp), vous pouvez le faire avec cette commande. Les chiffres : pour 10xp donné 50xp reçu, ici le nombre d'xp est défini et inchangeable.", false);
        embed.addField("Développeurs <-> Clients :", "`//mission` -> permet de gérer les missions créé.\n`//freelance` permet de gérer le message freelance.\n\n`creationMissions` & `creationFreeLance` -> ne se lancent pas comme des commandes classiques, une réaction dans le channel : <#" + Init.idMissionsPayantes.asString() + "> & <#" + Init.idFreeLance.asString() + "> permet de commencer la commande.\n\n", false);
        embed.setColor(ColorsUsed.just);
        embed.setTimestamp(Instant.now());
    };
    /*
        Le message lord de l'help command pour les admins
     */
    public static final Consumer<? super EmbedCreateSpec> helpEmbedAdmin = embedCreateSpec -> {
        embedCreateSpec.setTitle("Voici la liste des commandes admin:");
        embedCreateSpec.setDescription("`//send` -> permet de faire envoyer des messages aux bots.\n`//stop` -> arrête le script du bot.");
        embedCreateSpec.setColor(ColorsUsed.just);
        embedCreateSpec.setTimestamp(Instant.now());
    };
    /*
        Le message lors de la commande start pour le java
     */
    public static final Consumer<? super EmbedCreateSpec> startJava = embed -> {
        embed.setTitle("Java");
        embed.setUrl("https://fr.wikipedia.org/wiki/Java_(langage)");
        embed.setDescription("Java est un langage de programmation orienté objet créé par James Gosling et Patrick Naughton, employés de Sun Microsystems, avec le soutien de Bill Joy, présenté officiellement le 23 mai 1995 au SunWorld. La société Sun a été ensuite rachetée en 2009 par la société Oracle qui détient et maintient désormais Java");
        embed.setColor(ColorsUsed.same);
        embed.addField("Installer Java", "Java a besoin d'une JVM (Java Virtual Machine), un programme qui va interpréter le code pour le faire exécuter par la machine. Il existe de nombreuse JVM qui ont chacune des avantages et des inconvénients.\n\nLa JVM \"officielle\" par Oracle: https://www.java.com/fr/\nUne JVM mise à jour régulièrement: https://adoptopenjdk.net/?variant=openjdk11&jvmVariant=openj9", false);
        embed.addField("Les bases", "Beaucoup de personnes veulent apprendre le java pour différentes raisons. Par exemple minecraft, backend site, par l'envie d'apprendre.... Mais pour toutes les raisons la base est le langage Java. Il faut impérativement passer par les bases pour ensuite partir dans une branche du java.\n\nJava est un langage populaire et connu, il y a donc ne nombreuses ressources pour apprendre ce langage.", false);
        embed.addField("Les cours écrits.", "OpenClassroom: https://openclassrooms.com/fr/courses/26832-apprenez-a-programmer-en-java\nDeveloppez: https://java.developpez.com/livres-collaboratifs/javaenfants/\nZeste de savoir: https://zestedesavoir.com/tutoriels/646/apprenez-a-programmer-en-java/", false);
        embed.addField("Ou des cours vidéos...", "Les Teachers Du Net: https://www.youtube.com/watch?v=fmJsqBWkXm4&list=PLlxQJeQRaKDRnvgIvfHTV6ZY8M2eurH95\nEt plein d'autres je vous laisse chercher si cette chaîne ne vous convient pas :/", false);
        embed.addField("IDE (logiciels simplifiant le développement)", "Les IDE sont des logiciels très puissant, qui rassemblent tout les outils permettant le développement. Je vais vous en proposer 2, qui sont les plus connu dans le langage java.\n\nJetBrain IntelliJ: https://www.jetbrains.com/fr-fr/idea/download/#section=windows\nEclipse: https://www.eclipse.org/downloads/\n\nChoisissez celui qui vous fait le plus envie :)", false);
        embed.addField("Bonne Chance !", "Maintenant vous pouvez naviger dans les tutos, cours, et vidéos pour apprendre le java. Le serveur est là si vous rencontrez certains problèmes.", false);
        embed.setAuthor(Init.client.getSelf().block().getUsername(), null, Init.client.getSelf().block().getAvatarUrl());
        embed.setTimestamp(Instant.now());
    };

    public static final Consumer<? super EmbedCreateSpec> startCSharp = embed -> {
        embed.setTitle("C#, CSharp");
        embed.setDescription("C# (a prononcé C-Sharp) est un langage de programmation centré sur le paradigme Orienté Objet offrant des fonctionnalités d'autres paradigmes.\nDévelopper par Microsoft au début des années 2000 pour construire des applications Windows sans avoir à utiliser Java, et inspiré par le C/C++ et Java, le C# est un langage compilé qui vise le .NET, un Framework qui aide à construire des applications de toutes sortes et ne se limitant pas qu'au C#. (F#, VB, des variantes de Python, C++, etc)");
        embed.setUrl("https://en.wikipedia.org/wiki/C_Sharp_(programming_language)");
        embed.addField("C# Pour faire quoi ?", "Le C# est un langage qui se veut simple d'utilisation, fortement typé et robuste.\nIl peut vous aider à créer tout types d'applications ; du site web monolithique utilisant Razor aux APIs performantes et facile d'implémentation, il sert aussi a créer des jeux grâce au moteur de jeu Unity, le CryEngine de CryTek ou encore le framework de jeu MonoGame basé sur le XNA de Microsoft. \nPensé avant tout pour développer des applications Windows, il peut aussi vous permettre de créer des Applications Mobile multi plateforme grâce à .NET MAUI.\n\nSupporté en premier lieu par Microsoft, depuis plusieurs années maintenant le C# tout autant que le .NET sont open source et gérer par des fondations externe a Microsoft.", false);
        embed.addField("Installer C#", "Il vous suffit de télécharger l'un des package de développement .NET sur le site de Microsoft. \n.NET Core est multi plateforme et peut être utilisé sur Windows tout autant que Linux & MacOS.\n.NET Framework est une implémentation qui tends à être remplacer par Core, et est exclusif à Windows.\nVous pouvez aussi installer Visual studio, il vous permettra d'avoir .NET sur votre machine et un IDE puissant en plus.\n\n.NET Core & Framework => https://dotnet.microsoft.com/download\nVisual studio => https://visualstudio.microsoft.com/fr/", false);
        embed.addField("Ou apprendre le C# ?", "Le C# en trois parties (FR/EN) :\nhttps://docs.microsoft.com/fr-fr/learn/paths/csharp-first-steps/\nhttps://docs.microsoft.com/fr-fr/learn/paths/csharp-data/\nhttps://docs.microsoft.com/fr-fr/learn/paths/csharp-logic/\n\nLe .Net et Xamarin aka MAUI :\nhttps://docs.microsoft.com/fr-fr/learn/paths/build-dotnet-applications-csharp/\nhttps://docs.microsoft.com/fr-fr/learn/paths/build-mobile-apps-with-xamarin-forms/\n\nSources externes à Microsoft :\nPar Mike dane => https://www.mikedane.com/programming-languages/csharp/\n\nPar Brackeyz(Setp vidéos courtes, c'est la première) => \nhttps://www.youtube.com/watch?v=N775KsWQVkw&t=1s\n\nSur OCR => https://openclassrooms.com/fr/courses/218202-apprenez-a-programmer-en-c-sur-net\n\nSur LernCS => https://www.learncs.org/", false);
        embed.addField("Les outils", "Visual studio est l'outils de développement C# par excellence, il supporte et sublime la stack de technologie Microsoft au possible. JetBrains Rider est aussi un excellent outil de développement et totalement à niveau de VS.\nVisual studio code est plus proche d'un éditeur de texte qu'un IDE tel que VS ou Rider mais il offre des options de développement digne d'un IDE et a le mérite d'être léger pour le développement.\n\nVS et VSCode => https://visualstudio.microsoft.com/fr/\nRider => https://www.jetbrains.com/fr-fr/rider/", false);
        embed.addField("Bonne Chance !", "Maintenant vous pouvez naviger dans les tutos, cours, et vidéos pour apprendre le C#. Le serveur est là si vous rencontrez certains problèmes.", false);
        embed.setAuthor(Init.client.getSelf().block().getUsername(), null, Init.client.getSelf().block().getAvatarUrl());
        embed.setTimestamp(Instant.now());
        embed.setColor(ColorsUsed.same);
    };

    public static final Consumer<? super EmbedCreateSpec> startPython = embed -> {
        embed.setTitle("Python");
        embed.setUrl("https://fr.wikipedia.org/wiki/Python_(langage)");
        embed.setDescription("Python est un langage de programmation interprété, multi-paradigme et multiplateformes. Il favorise la programmation impérative structurée, fonctionnelle et orientée objet.");
        embed.setColor(ColorsUsed.same);
        embed.addField("Installer Python", "Pour pouvoir programmer en python il faut d'abord télécharger la VM,  un programme qui va interpréter le code et pouvoir le faire fonctionner sur la machine.\n\n Il est simple d'installation, il suffit d'aller sur leur site officiel (https://www.python.org/) et de suivre les indications du téléchargement.", false);
        embed.addField("Les bases", "Ils y a différents moyen pour apprendre le python, de nombreux sites permettent l'apprentissage et la compréhension des notions lié au python.", false);
        embed.addField("Il y a de nombreux cour écrits, cela permet de lire et d'aller à votre rythme.", "OpenClassroom: https://openclassrooms.com/fr/courses/4262331-demarrez-votre-projet-avec-python\nDeveloppez: https://python.developpez.com/tutoriels/apprendre-programmation-python/les-bases/?page=le-langage-python\nZeste de Savoir: https://zestedesavoir.com/tutoriels/799/apprendre-a-programmer-avec-python-3/", false);
        embed.addField("Ou encore des cours vidéos...", "CodeAvecJonathan: https://www.youtube.com/watch?v=oUJolR5bX6g\nYvan Monka: https://www.youtube.com/watch?v=VmOPhT4HFNE", false);
        embed.addField("IDE (logiciels simplifiant le développement)", "Les IDE sont des logiciels très puissant, qui rassemblent tout les outils permettant le développement. Je vais vous en proposer 2, qui sont les plus connu dans le langage python.\n\nJetBrain PyCharm: https://www.jetbrains.com/fr-fr/pycharm/\nVisualStudioCode: https://code.visualstudio.com/\n\nChoisissez celui qui vous fait le plus envie :)", false);
        embed.addField("Bonne Chance !", "Maintenant vous pouvez naviger dans les tutos, cours, et vidéos pour apprendre le python. Le serveur est là si vous rencontrez certains problèmes.", false);
        embed.setAuthor(Init.client.getSelf().block().getUsername(), null, Init.client.getSelf().block().getAvatarUrl());
        embed.setTimestamp(Instant.now());
    };

    public static final Consumer<? super EmbedCreateSpec> startHtmlCss = embed -> {
        embed.setTitle("HTML / CSS");
        embed.setDescription("Le HTML (HyperText Markup Language), est un langage de balisage conçu pour réaliser des pages web. HTML est une des trois inventions à la base du World Wide Web, avec le HyperText Transfer Protocol et les adresses web. HTML a été inventé pour permettre d'écrire des documents hypertextuels liant les différentes ressources d’Internet avec des hyperliens.");
        embed.setColor(ColorsUsed.same);
        embed.setUrl("https://fr.wikipedia.org/wiki/HTML5");
        embed.addField("Les bases", "On apprends l'HTML et le CSS pour faire des sites web.\nCe langage est très connu pour le dev web.", false);
        embed.addField("Les cours écrits", "OpenClassroom : https://openclassrooms.com/fr/courses/1603881-apprenez-a-creer-votre-site-web-avec-html5-et-css3\nDeveloppez : https://www.developpez.com/actu/177723/Apprendre-la-programmation-Web-moins-HTML-CSS-a-travers-des-TD-un-tutoriel-de-Romain-Lebreton/\nZeste de savoir : https://zestedesavoir.com/tutoriels/599/creer-un-jeu-html5-avec-quintus/272_decouverte-de-la-librairie/1554_creer-une-page-html-basique/", false);
        embed.addField("Les cours vidéos", "Les Teachers Du Net: https://www.youtube.com/watch?v=YT7eJufmOQM\nEt plein d'autres je vous laisse chercher si cette chaîne ne vous convient pas :/", false);
        embed.addField("IDE (logiciels simplifiant le développement)", "Les IDE sont des logiciels très puissant, qui rassemblent tout les outils permettant le développement.\n\nVisual Studio Code : https://code.visualstudio.com/\nWebStorm : https://www.jetbrains.com/fr-fr/webstorm/\nSublime Text : http://www.sublimetext.com/\nAtom : https://atom.io/\nChoisissez celui qui vous fait le plus envie :)", false);
        embed.addField("Bonne Chance !", "Maintenant vous pouvez naviguer dans les tutos, cours, et vidéos pour apprendre l'HTML/CSS. Le serveur est là si vous rencontrez certains problèmes.", false);
        embed.setAuthor(Init.client.getSelf().block().getUsername(), null, Init.client.getSelf().block().getAvatarUrl());
        embed.setTimestamp(Instant.now());
    };

    /*
        Le message expliquant la commande start, et en disant quels sont les langages qui sont disponibles.
     */
    public static final Consumer<? super EmbedCreateSpec> startCommandExplain = embed -> {
        embed.setTitle("Start");
        embed.setDescription("Cette commande permet aux débutants d'avoir les liens, et les premières indications sur un langage.");
        embed.addField("Les langages :", "Java, Python, C#, html/css, en développement....\n\nPour choisir le langage, tapez simplement son nom et vous aurez toutes les informations.\n\nVous pouvez annuler la commande avec `annuler` ou `cancel`.", false);
        embed.setColor(ColorsUsed.just);
        embed.setTimestamp(Instant.now());
    };

    public static final Consumer<? super EmbedCreateSpec> meetupCommandExplain = embed -> {
        embed.setAuthor("Les meetups sont des rencontres vocales/écrits sur un sujet créé par la communauté.", null, null);
        embed.setTitle("Commandes");
        embed.setDescription("`create` => Permet de créer un nouveau meetup.\n`delete` => Permet de supprimer un meetup que vous avez créé.\n`channel` => renvoie vers le channel des meetups.\n\nVous pouvez annuler la commande à tout moment avec `cancel` ou `annuler`.");
        embed.setTimestamp(Instant.now());
        embed.setColor(ColorsUsed.just);
    };

    public static final Consumer<? super EmbedCreateSpec> meetupCreateGetDescription = embedCreateSpec -> {
        embedCreateSpec.setTitle("Sujet");
        embedCreateSpec.setDescription("Quel est le sujet de votre meetup, le but que vous voulez accomplir lors de ce regroupement ?");
        embedCreateSpec.setFooter("Vous pouvez annuler | cancel", null);
        embedCreateSpec.setColor(ColorsUsed.just);
    };

    public static final Consumer<? super EmbedCreateSpec> meetupCreateGetDate = embedCreateSpec -> {
        embedCreateSpec.setTitle("Date");
        Calendar calendar = Calendar.getInstance();
        embedCreateSpec.setDescription("Quand voulez-vous organiser ce meetup. Donnez la date et l'heure sous la forme **dd/MM/yyyy hh:mm**. Par exemple `" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + "`.");
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
