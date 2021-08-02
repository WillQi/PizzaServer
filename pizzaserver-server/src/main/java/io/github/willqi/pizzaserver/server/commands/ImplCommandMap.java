package io.github.willqi.pizzaserver.server.commands;

import io.github.willqi.pizzaserver.api.Server;
import io.github.willqi.pizzaserver.api.commands.ImplCommand;
import io.github.willqi.pizzaserver.api.commands.CommandMap;
import io.github.willqi.pizzaserver.server.commands.defaults.SecondaryTestCommand;
import io.github.willqi.pizzaserver.server.commands.defaults.TestCommand;

import java.util.*;

public class ImplCommandMap implements CommandMap {

    private final Map<String, ImplCommand> commands = new HashMap<>();

    private final Server server;

    public ImplCommandMap(Server server) {
        this.server = server;
        registerDefaults();
    }

    private void registerDefaults() {
        this.register(new TestCommand());
        this.register(new SecondaryTestCommand());
    }

    @Override
    public void register(ImplCommand command) {
        this.register(command, command.getName());
    }

    @Override
    public void register(ImplCommand command, String label) {
        if(label == null) label = command.getName();
        label = label.trim().toLowerCase(Locale.ROOT);
        if(!commands.containsKey(label)) {
            commands.put(label, command);
        } else {
            //TODO: Show the plugin name of the command that has been overwritten
            server.getLogger().error("A command with the name " + label + " already exists! Overwritting it, it came from the plugin: ");
        }
    }

    @Override
    public void registerAll(List<ImplCommand> commands) {
        for(ImplCommand command : commands) {
            this.register(command, command.getName());
        }
    }

    @Override
    public void removeCommand(String name) {
        if(!commands.containsKey(name)) {
            throw new NullPointerException("That command doesn't exist");
        }
        commands.remove(name);
    }

    @Override
    public ImplCommand getCommand(String name) {
        if(commands.containsKey(name)) return commands.get(name);
        return null;
    }

    @Override
    public Map<String, ImplCommand> getCommands() {
        return new HashMap<>(commands);
    }
}
