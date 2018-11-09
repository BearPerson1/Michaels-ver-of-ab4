package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.model.util.SampleDataUtil.getTagSet;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.TreeMap;

import org.junit.Test;

import seedu.address.logic.CommandHistory;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.*;


public class ClearCommandTest {

    private CommandHistory commandHistory = new CommandHistory();

    @Test
    public void execute_emptyAddressBook_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();
        Person self = new Person(new Name("Self"), new Phone("000"), new Email("self@self"), new Address("self"),
                getTagSet("self"), new TreeMap<>(), TimeSlots.initTimeSlots());
        expectedModel.addPerson(self);
        expectedModel.commitAddressBook();

        assertCommandSuccess(new ClearCommand(), model, commandHistory, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyAddressBook_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.resetAddressBookData(new AddressBook());
        Person self = new Person(new Name("Self"), new Phone("000"), new Email("self@self"), new Address("self"),
                getTagSet("self"), new TreeMap<>(), TimeSlots.initTimeSlots());
        expectedModel.addPerson(self);
        expectedModel.commitAddressBook();

        assertCommandSuccess(new ClearCommand(), model, commandHistory, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

}
