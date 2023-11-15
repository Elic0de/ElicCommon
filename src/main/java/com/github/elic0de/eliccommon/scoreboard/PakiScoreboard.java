package pakira.scoreboard;

import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class PakiScoreboard extends FastBoard {

    public PakiScoreboard(Player player) {
        super(player);
    }

    @Override
    public synchronized void updateLine(int line, String text) {
        super.updateLine(line, colored(text));
    }

    @Override
    public void updateLines(String... lines) {
        super.updateLines(Arrays.stream(lines).map(this::colored).collect(Collectors.toList()));
    }

    @Override
    public void updateTitle(String title) {
        super.updateTitle(colored(title));
    }

    @Override
    public synchronized void updateLines(Collection<String> lines) {
        super.updateLines(lines.stream().map(this::colored).collect(Collectors.toList()));
    }

    private String colored(String text) {
        return translateAlternateColorCodes('&', text);
    }
}
