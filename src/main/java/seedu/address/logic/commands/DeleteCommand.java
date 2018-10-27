package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.IsMergedPredicate;
import seedu.address.model.person.IsNotSelfOrMergedPredicate;
import seedu.address.model.person.Person;

/**
 * Deletes a person identified using it's displayed index from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by the index number used in the displayed person list.\n"
            + "Parameters: LIST (Main, Merged) INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + "merged " + " 1";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";
    public static final String MESSAGE_DELETE_MERGED_SUCCESS = "Deleted Group: %1$s";
    private final Index targetIndex;
    private final String targetList;

    public DeleteCommand(String targetList, Index targetIndex) {
        this.targetIndex = targetIndex;
        this.targetList = targetList;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();
        if (targetList.equalsIgnoreCase("main")) {
            lastShownList = ((ObservableList<Person>) lastShownList).filtered(new IsNotSelfOrMergedPredicate());
        } else if (targetList.equalsIgnoreCase("merged")) {
            lastShownList = ((ObservableList<Person>) lastShownList).filtered(new IsMergedPredicate());
        }
        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToDelete = lastShownList.get(targetIndex.getZeroBased());
        model.deletePerson(personToDelete);
        model.commitAddressBook();
        if(targetList.equalsIgnoreCase("main")) {
            return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, personToDelete));
        }
        else{
            return new CommandResult(String.format(MESSAGE_DELETE_MERGED_SUCCESS, personToDelete.getName()));
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteCommand // instanceof handles nulls
                && targetIndex.equals(((DeleteCommand) other).targetIndex)); // state check
    }
}
