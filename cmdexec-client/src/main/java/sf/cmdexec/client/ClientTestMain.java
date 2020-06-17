package sf.cmdexec.client;

import static sf.cmdexec.client.Utility.log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class ClientTestMain {

  public static void main(final String[] args) throws Exception {
    final String host = args[0];
    final int port = Integer.parseInt(args[1]);
    try {
      final InetAddress server = InetAddress.getByName(host);
      log(String.format("Connecting to RMI server %s:%d", server, port));

      final InetAddress address = InetAddress.getLocalHost();
      log("Connecting to RMI server from " + address);
    } catch (final UnknownHostException e) {
      log("Could not get host name", e);
    }

    final CommandExecutorClient cmdExecClient = new CommandExecutorClient(host, port) {
    };
    final List<CommandResult> commandResults = cmdExecClient.executeCommands(
        new Command("Directory ilsting for qw", "cmd", "/c", "dir", "qw"),
        new Command("Directory ilsting for current directory", "cmd", "/c", "dir", "/b"g));
    log(commandResults.toString());
  }

}
