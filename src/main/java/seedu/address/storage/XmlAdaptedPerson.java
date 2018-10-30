package seedu.address.storage;

import java.util.*;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.enrolledModule.EnrolledModule;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.TimeSlots;
import seedu.address.model.tag.Tag;

/**
 * JAXB-friendly version of the Person.
 */
public class XmlAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private String phone;
    @XmlElement(required = true)
    private String email;
    @XmlElement(required = true)
    private String address;

    @XmlElement
    private List<XmlAdaptedTag> tagged = new ArrayList<>();

    @XmlElement
    private List<XmlAdaptedEnrolledModule> enrolled = new ArrayList<>();

    @XmlElementWrapper
    private Map<String, ListWrapper> timeslots = new HashMap<>();


    /**
     * Constructs an XmlAdaptedPerson.
     * This is the no-arg constructor that is required by JAXB.
     */
    public XmlAdaptedPerson() {}

    /**
     * Constructs an {@code XmlAdaptedPerson} with the given person details.
     */
    public XmlAdaptedPerson(String name, String phone, String email, String address,
                            List<XmlAdaptedTag> tagged, List<XmlAdaptedEnrolledModule> enrolled, Map<String,
            ListWrapper> timeslots) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        if (tagged != null) {
            this.tagged = new ArrayList<>(tagged);
        }

        if (enrolled != null) {
            this.enrolled = new ArrayList<>(enrolled);
        }
        this.timeslots = new HashMap<>(timeslots);

    }

    /**
     * Converts a given Person into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created XmlAdaptedPerson
     */
    public XmlAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
        tagged = source.getTags().stream()
                .map(XmlAdaptedTag::new)
                .collect(Collectors.toList());
        XmlAdaptedEnrolledModule tempXmlClass;
        for(String nameTemp : source.getEnrolledModules().keySet()){
            tempXmlClass = new XmlAdaptedEnrolledModule(nameTemp);
            enrolled.add(tempXmlClass);
        }
        timeslots = toXmlAdaptedTimeSlots(source.getTimeSlots());
    }
    public static Map<String, ListWrapper>toXmlAdaptedTimeSlots (Map<String, List<TimeSlots>> source) {
        Map<String, ListWrapper> timeslots = new HashMap<>();
        String[] days ={"mon", "tue", "wed", "thu", "fri"};
        for(String day :days) {
            ArrayList<XmlAdaptedTimeSlots> toAdd= new ArrayList<>();
            ListWrapper wrapper = new ListWrapper();
            wrapper.setList(toAdd);
            for (TimeSlots i : source.get(day)) {
                toAdd.add(new XmlAdaptedTimeSlots(i));
            }
            timeslots.put(day, wrapper);
        }
        return timeslots;
    }

    public static List<XmlAdaptedEnrolledModule> toXmlAdaptedEnrolledModules(Map<String, EnrolledModule> source){
        Iterator<Map.Entry<String, EnrolledModule>> it = source.entrySet().iterator();
        List<XmlAdaptedEnrolledModule> enrolledModules = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry<String, EnrolledModule> pair = it.next();
             enrolledModules.add(new XmlAdaptedEnrolledModule(pair.getValue()));
        }
        return enrolledModules;
    }

    /**
     * Converts this jaxb-friendly adapted person object into the model's Person object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (XmlAdaptedTag tag : tagged) {
            personTags.add(tag.toModelType());
        }

        final List<EnrolledModule> personEnrolledModules = new ArrayList<>();
        for (XmlAdaptedEnrolledModule enrolledClass : enrolled) {
            personEnrolledModules.add(enrolledClass.toModelType());
        }

        final Map<String ,List<TimeSlots> >personTimeSlots = new HashMap<>();
        String[] days ={"mon", "tue", "wed", "thu", "fri"};
        for(String day: days){
            List<TimeSlots> toAdd = new ArrayList<>();
            for (XmlAdaptedTimeSlots i : timeslots.get(day).getList()) {
                toAdd.add(i.toModelType());
            }
            personTimeSlots.put(day, toAdd);
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_NAME_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_PHONE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_EMAIL_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        if (address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_ADDRESS_CONSTRAINTS);
        }
        final Address modelAddress = new Address(address);

        final Set<Tag> modelTags = new HashSet<>(personTags);

        final Map<String, EnrolledModule> modelEnrolledClasses = new TreeMap<>();
        for(EnrolledModule tempClass: personEnrolledModules){
            modelEnrolledClasses.put(tempClass.enrolledModuleName, tempClass);
        }
        final Map<String ,List<TimeSlots> >modelTimeSlots = new  HashMap<>(personTimeSlots);

        return new Person(modelName, modelPhone, modelEmail, modelAddress, modelTags, modelEnrolledClasses,
                modelTimeSlots);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof XmlAdaptedPerson)) {
            return false;
        }

        XmlAdaptedPerson otherPerson = (XmlAdaptedPerson) other;
        return Objects.equals(name, otherPerson.name)
                && Objects.equals(phone, otherPerson.phone)
                && Objects.equals(email, otherPerson.email)
                && Objects.equals(address, otherPerson.address)
                && tagged.equals(otherPerson.tagged)
                && enrolled.equals(otherPerson.enrolled)
                && timeslots.equals(otherPerson.timeslots);
    }
}
