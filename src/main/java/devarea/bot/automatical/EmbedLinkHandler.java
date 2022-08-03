package devarea.bot.automatical;

import devarea.Main;
import devarea.backend.controllers.rest.requestContent.RequestHandlerUserData;
import devarea.global.badges.Badges;
import devarea.bot.Init;
import devarea.global.cache.ChannelCache;
import devarea.global.cache.MemberCache;
import devarea.bot.commands.Command;
import devarea.global.handlers.XPHandler;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static devarea.global.utils.ThreadHandler.startAway;

public class EmbedLinkHandler {
    public final static String style = "https://devarea.fr/member-profile?member_id=";
    public static Font initial;

    static {
        try {
            initial = Font.createFont(Font.TRUETYPE_FONT,
                    Init.class.getResource("/assets/font.otf").openStream());
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void onReceive(final MessageCreateEvent event) {
        // Transform message to Members
        String content = event.getMessage().getContent();
        if (!content.contains(style))
            return;

        String[] occurs = content.split("https://devarea.fr/member-profile?");

        boolean other = false;

        ArrayList<Member> members = new ArrayList<>();
        for (String occur : occurs)
            if (occur.startsWith("?member_id=") && occur.length() >= 29) {
                Member member;
                if ((member = MemberCache.get(occur.substring(11, 29))) != null && !members.contains(member))
                    members.add(member);
            } else if (!occur.isBlank())
                other = true;

        // Delete message if it contains only links
        if (!other)
            Command.delete(false, event.getMessage());

        if (members.size() == 0)
            return;

        generateLinkEmbed((TextChannel) ChannelCache.watch(event.getMessage().getChannelId().asString()), members);
    }

    public static void generateLinkEmbed(TextChannel channel, ArrayList<Member> members) {
        long ms = System.currentTimeMillis();
        for (Member member : members) {
            startAway(() -> {
                try {
                    MessageCreateSpec.Builder msgBuilder = MessageCreateSpec.builder()
                            .addEmbed(EmbedCreateSpec.builder()
                                    .title("Profil de : " + member.getDisplayName())
                                    .image("attachment://profil.png")
                                    .color(discord4j.rest.util.Color.of(255, 87, 51)).build()
                            );

                    // Creating components to draw !
                    ByteArrayInputStream image_stream = generateImageStreamForMember(member);

                    System.out.println("Profile image took : " + (System.currentTimeMillis() - ms) + "ms.");

                    // Finalize builder
                    msgBuilder
                            .addFile("profil.png", image_stream)
                            .addComponent(ActionRow.of(Button.link(Main.domainName + "member-profile?member_id=" + member.getId().asString(), "devarea.fr")));

                    channel.createMessage(msgBuilder.build()).subscribe();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static ByteArrayInputStream generateImageStreamForMember(Member member) throws IOException {
        BufferedImage img = new BufferedImage(1200, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();

        // Setup Graphics
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw background
        g.drawImage(Init.assetsImages.get("profile_background"), 0, 0, 1200, 600, null);

        // Draw Member Icon !
        g.drawImage(makeRoundedCorner(ImageIO.read(new URL(member.getAvatarUrl())), 10000), 25, 20, 210,
                210,
                null);

        // Draw UserName
        g.setFont(initial);
        drawCentredAndCentred(g, member.getDisplayName(), 730, 90, getFontSize(member.getDisplayName(), g));

        // Draw Xp Part
        Integer xp = XPHandler.getXpOfMember(member.getId());
        if (xp == null)
            xp = 0;
        int level = XPHandler.getLevelForXp(xp);

        float percentage =
                (float) (xp - XPHandler.getAmountForLevel(level)) / (XPHandler.getAmountForLevel(level + 1) - XPHandler.getAmountForLevel(level));

        // Xp bar
        g.setColor(Color.black);
        g.setStroke(new BasicStroke(38.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, null,
                0.0f));
        g.draw(new Line2D.Float(249, 300, 1151, 300));

        g.setColor(Color.white);
        g.setStroke(new BasicStroke(35.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, null,
                0.0f));
        g.draw(new Line2D.Float(250, 300, 1150, 300));
        g.setColor(new Color(248, 176, 86));
        g.draw(new Line2D.Float(250, 300, 250 + (percentage * 900), 300));

        // Xp text
        Font xp_font = initial.deriveFont(60f);
        g.setFont(xp_font);
        g.setColor(Color.white);

        g.drawString(level + "", 240, 272);
        drawLeft(g, (level + 1) + "", 1155, 272, g.getFont());
        drawCenteredString(g, "XP-" + xp, 700, 272, g.getFont());

        // Badges part
        Badges[] badges =
                RequestHandlerUserData.requestGetMemberProfile(member.getId().asString()).getBadges();

        Font badges_font = initial.deriveFont(30f);
        g.setFont(badges_font);

        int inset_x = 50;
        int inset_y = 380;

        // Draw max of 4 badges inline
        for (int i = 0; i < badges.length && i < 4; i++) {
            g.drawImage(badges[i].getLocal_icon(), inset_x + 68 + (i * 250),
                    inset_y + 10, 107, 107, null);
            drawCenteredString(g, badges[i].getName(), inset_x + 120 + (i * 250), inset_y + 150,
                    badges_font);
        }

        // Transform image to output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(img, "png", outputStream);
        ByteArrayInputStream image_stream = new ByteArrayInputStream(outputStream.toByteArray());
        return image_stream;
    }

    private static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();

        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, output.getWidth(), output.getHeight(), cornerRadius, cornerRadius));

        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, output.getWidth(), output.getHeight(), null);

        g2.dispose();

        return output;
    }

    private static void drawCenteredString(Graphics g, String text, int x, int y, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int posX = x - (metrics.stringWidth(text) / 2);
        g.setFont(font);
        g.drawString(text, posX, y);
    }

    private static void drawCentredAndCentred(Graphics g, String text, int x, int y, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int posX = x - (metrics.stringWidth(text) / 2);
        int poxY = y + (metrics.getHeight() / 2);
        g.setFont(font);
        g.drawString(text, posX, poxY);
    }

    private static Font getFontSize(String text, Graphics2D g) {
        Font font = g.getFont().deriveFont(140f);
        FontMetrics metrics = g.getFontMetrics(font);
        while (950 < metrics.stringWidth(text)) {
            font = font.deriveFont((float) (font.getSize() - 1));
            metrics = g.getFontMetrics(font);
        }
        return font;
    }

    private static void drawLeft(Graphics g, String text, int x, int y, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int posX = x - metrics.stringWidth(text);
        g.setFont(font);
        g.drawString(text, posX, y);
    }
}
