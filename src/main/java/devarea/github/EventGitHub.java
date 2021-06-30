package devarea.github;

import org.kohsuke.github.GHCommit;

public interface EventGitHub {

    void run(GHCommit commit);

}
