package devarea.global.handlers;

import devarea.Main;
import devarea.global.cache.ChannelCache;
import devarea.bot.Init;
import devarea.bot.commands.Command;
import devarea.bot.commands.commandTools.FreeLance;
import devarea.bot.commands.commandTools.MessageSeria;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.MessageCreateSpec;

import java.io.IOException;
import java.util.*;

import static devarea.bot.commands.Command.delete;
import static devarea.global.utils.ThreadHandler.*;
import static devarea.bot.presets.TextMessage.freelanceBottomMessage;

public class FreeLanceHandler {
    /*
        Time defined between each bump
     */
    private static final long TIME_BETWEEN_BUMP = 86400000L;

    private static Message bottomMessage;
    private static LinkedHashMap<String, FreeLance> freeLances = new LinkedHashMap<>();

    private static TextChannel freelanceChannel;

    /*
        This is a temporary Array don't save anything here. Used for the cooldown of bumps !
     */
    private final static ArrayList<FreeLance> bumpedFreeLance = new ArrayList<>();

    /*
        Initialise FreeLanceHandler
     */
    public static void init() {
        try {
            load();

            freelanceChannel = (TextChannel) ChannelCache.fetch(Init.initial.freelance_channel.asString());

            if (freelanceChannel == null) {
                System.err.println("Le channel freelance n'a pas pu être trouvé !");
                return;
            }

            setupLastMessage();

            repeatEachMillis(FreeLanceHandler::save, 86400000L, false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
        Transfer freelances to UserDataHandler
     */
    private static void save() {
        UserDataHandler.setFreelancesHashMap(freeLances);
    }

    /*
        Load data from UserDataHandler
     */
    private static void load() throws IOException {
        freeLances = (LinkedHashMap<String, FreeLance>) UserDataHandler.getFreelances();
        System.out.println("FreeLance loaded : " + freeLances.size() + " detected !");
    }

    /*
        Secure the stop of the bot
     */
    public static void stop() {
        save();
    }

    /*
        Setup the bottomMessage
     */
    private static void setupLastMessage() {
        Message msg = freelanceChannel.getLastMessage().block();
        if (msg == null || msg.getEmbeds().size() == 0 || !msg.getEmbeds().get(0).getTitle().get().equals("Proposez vos services !"))
            sendLastMessage();
        else
            bottomMessage = msg;
    }

    /*
        Resend BottomMessage to keep it at the bottom of the channel
     */
    public static void updateBottomMessage() {
        delete(false, bottomMessage);
        sendLastMessage();
    }

    /*
        Send a new BottomMessage
     */
    private static void sendLastMessage() {
        bottomMessage = Command.send(freelanceChannel, freelanceBottomMessage, true);
    }

    /*
        Bump the freelance of member -> id
     */
    public static boolean bumpFreeLance(String id) {

        if (!hasFreelance(id))
            return false;

        FreeLance freeLance = FreeLanceHandler.getFreelance(id);

        if (bumpedFreeLance.contains(freeLance))
            return false;

        // Delete old message
        delete(false, freeLance.getMessage().getMessage());

        Message newMessage = sendFreelanceMessage(freeLance);
        freeLance.setMessage(new MessageSeria(newMessage));
        freeLance.setLast_bump(System.currentTimeMillis());

        updateBottomMessage();

        bumpedFreeLance.add(freeLance);
        startAwayIn(() -> bumpedFreeLance.remove(freeLance), TIME_BETWEEN_BUMP, false);

        return true;
    }

    /*
        Clear the freelance
     */
    public static void left(String userID) {
        if (FreeLanceHandler.hasFreelance(userID))
            FreeLanceHandler.remove(FreeLanceHandler.getFreelance(userID));
    }


    /*
        Send the message of the freelance param
     */
    public static Message sendFreelanceMessage(final FreeLance freeLance) {
        return Command.send((TextChannel) ChannelCache.watch(Init.initial.freelance_channel.asString()),
                MessageCreateSpec.builder()
                        .content("**Freelance de <@" + freeLance.getMemberId() + "> :**")
                        .addEmbed(freeLance.getEmbed())
                        .addComponent(ActionRow.of(Button.link(Main.domainName + "member-profile?member_id=" + freeLance.getMemberId() +
                                        "&open=1",
                                "devarea.fr")))
                        .build(),
                true);
    }


    // ------------------- UTILS -------------------

    public static boolean hasFreelance(String id) {
        return freeLances.containsKey(id);
    }

    public static boolean hasFreelance(Member member) {
        return hasFreelance(member.getId().asString());
    }

    public static FreeLance getFreelance(String id) {
        return freeLances.get(id);
    }

    public static FreeLance getFreelance(Member member) {
        return getFreelance(member.getId().asString());
    }

    /*
        Add a new freelance, if a member had already a freelance it override it !
    */
    public static void add(FreeLance freelance) {
        freeLances.put(freelance.getMemberId(), freelance);
        save();
    }

    /*
        Remove the freelance
     */
    public static void remove(FreeLance freeLance) {
        startAway(() -> delete(false, freeLance.getMessage().getMessage()));
        freeLances.remove(freeLance.getMemberId(), freeLance);
        save();
    }

    /*
        Get a sorted list by last_bump time of all freelances !
     */
    public static FreeLance[] getFreelances(int start, int end) {
        ArrayList<FreeLance> freelanceArray = new ArrayList<>(freeLances.values());

        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }

        if (start < 0)
            start = 0;
        if (end > freelanceArray.size())
            end = freelanceArray.size();

        Collections.sort(freelanceArray);

        return freelanceArray.subList(start, end).toArray(new FreeLance[0]);
    }

}
