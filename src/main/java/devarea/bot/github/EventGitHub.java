package devarea.bot.github;

import org.kohsuke.github.GHCommit;

public interface EventGitHub {

    void run(GHCommit commit);

}
