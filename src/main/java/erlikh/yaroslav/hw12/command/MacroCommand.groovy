package erlikh.yaroslav.hw12.command

import erlikh.yaroslav.hw12.exceptions.CommandException
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

class MacroCommand implements BaseCommand {

    protected final List<BaseCommand> commands

    MacroCommand(List<BaseCommand> commands) {
        this.commands = commands;
    }

    @Override
    void execute() {
        try {
            commands.forEach(BaseCommand::execute)
        } catch (Exception ex) {
            throw new CommandException(ex)
        }
    }
}
