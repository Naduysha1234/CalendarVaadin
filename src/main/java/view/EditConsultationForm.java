package view;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.components.calendar.event.BasicEvent;
import com.vaadin.ui.components.calendar.event.CalendarEvent;

/**
 * Created by user on 23.02.2016.
 */
public class EditConsultationForm
{

    private TextField captionField;

    private Window scheduleEventPopup;

    private final FormLayout scheduleEventFieldLayout = new FormLayout();

    private FieldGroup scheduleEventFieldGroup = new FieldGroup();

    private Button deleteEventButton;

    private Button applyEventButton;

    private DateField startDateField;

    private DateField endDateField;

    private boolean useSecondResolution;

    CalendarView calendarView;

    public EditConsultationForm(CalendarView calendarView)
    {
            this.calendarView = calendarView;
    }

    private void initFormFields(Layout formLayout,
                                Class<? extends CalendarEvent> eventClass) {

        startDateField = createDateField("Start date");

        endDateField = createDateField("End date");

        final CheckBox allDayField = createCheckBox("All-day");

        allDayField.addValueChangeListener(new Property.ValueChangeListener() {

            private static final long serialVersionUID = -7104996493482558021L;

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                Object value = event.getProperty().getValue();
                if (value instanceof Boolean && Boolean.TRUE.equals(value)) {
                    setFormDateResolution(Resolution.DAY);

                } else {
                    setFormDateResolution(Resolution.MINUTE);
                }
            }

        });
        captionField = createTextField("Caption");
        captionField.setInputPrompt("Event name");
        captionField.setRequired(true);
        //final TextField whereField = createTextField("Where");
      //  whereField.setInputPrompt("Address or location");
        final TextArea descriptionField = createTextArea("Description");
        descriptionField.setInputPrompt("Describe the event");
        descriptionField.setRows(3);
        // descriptionField.setRequired(true);


  //      final ComboBox styleNameField = createStyleNameComboBox();
    //    styleNameField.setInputPrompt("Choose calendar");
    //    styleNameField.setTextInputAllowed(false);

        formLayout.addComponent(startDateField);
        // startDateField.setRequired(true);
        formLayout.addComponent(endDateField);
        formLayout.addComponent(allDayField);
        formLayout.addComponent(captionField);
        // captionField.setComponentError(new UserError("Testing error"));


   //     if (eventClass == CalendarTestEvent.class) {
    //        formLayout.addComponent(whereField);
    //    }

        formLayout.addComponent(descriptionField);
    //   formLayout.addComponent(styleNameField);

        scheduleEventFieldGroup.bind(startDateField, "start");
        scheduleEventFieldGroup.bind(endDateField, "end");
        scheduleEventFieldGroup.bind(captionField, "caption");
        scheduleEventFieldGroup.bind(descriptionField, "description");

   //     if (eventClass == CalendarTestEvent.class) {
   //         scheduleEventFieldGroup.bind(whereField, "where");
   //     }


     //   scheduleEventFieldGroup.bind(styleNameField, "styleName");
        scheduleEventFieldGroup.bind(allDayField, "allDay");
    }

    private void updateCalendarEventPopup(boolean newEvent) {
        if (scheduleEventPopup == null) {
            createCalendarEventPopup();
        }

        if (newEvent) {
            scheduleEventPopup.setCaption("New event");
        } else {
            scheduleEventPopup.setCaption("Edit event");
        }

        deleteEventButton.setVisible(!newEvent);
        deleteEventButton.setEnabled(!calendarView.calendarComponent.isReadOnly());
        applyEventButton.setEnabled(! calendarView.calendarComponent.isReadOnly());
    }

    private void updateCalendarEventForm(CalendarEvent event) {
        BeanItem<CalendarEvent> item = new BeanItem<CalendarEvent>(event);
        scheduleEventFieldLayout.removeAllComponents();
        scheduleEventFieldGroup = new FieldGroup();
        initFormFields(scheduleEventFieldLayout, event.getClass());
        scheduleEventFieldGroup.setBuffered(true);
        scheduleEventFieldGroup.setItemDataSource(item);
    }

    private void setFormDateResolution(Resolution resolution) {
        if (startDateField != null && endDateField != null) {
            startDateField.setResolution(resolution);
            endDateField.setResolution(resolution);
        }
    }


    @SuppressWarnings("unchecked")
    private BasicEvent getFormCalendarEvent() {
        BeanItem<CalendarEvent> item = (BeanItem<CalendarEvent>) scheduleEventFieldGroup
                .getItemDataSource();
        CalendarEvent event = item.getBean();
        return (BasicEvent) event;
    }

    private CheckBox createCheckBox(String caption) {
        CheckBox cb = new CheckBox(caption);
        cb.setImmediate(true);
        return cb;
    }

    private TextField createTextField(String caption) {
        TextField f = new TextField(caption);
        f.setNullRepresentation("");
        return f;
    }

    private TextArea createTextArea(String caption) {
        TextArea f = new TextArea(caption);
        f.setNullRepresentation("");
        return f;
    }

    private DateField createDateField(String caption) {
        DateField f = new DateField(caption);
        if (useSecondResolution) {
            f.setResolution(Resolution.SECOND);
        } else {
            f.setResolution(Resolution.MINUTE);
        }
        return f;
    }

    /*
    private ComboBox createStyleNameComboBox() {
        ComboBox s = new ComboBox("Calendar");
        s.addContainerProperty("c", String.class, "");
        s.setItemCaptionPropertyId("c");
        Item i = s.addItem("color1");
        i.getItemProperty("c").setValue("Work");
        i = s.addItem("color2");
        i.getItemProperty("c").setValue("Personal");
        i = s.addItem("color3");
        i.getItemProperty("c").setValue("Family");
        i = s.addItem("color4");
        i.getItemProperty("c").setValue("Hobbies");
        return s;
    }
    */

    public void showEventPopup(CalendarEvent event, boolean newEvent) {
        if (event == null) {
            return;
        }

        updateCalendarEventPopup(newEvent);
        updateCalendarEventForm(event);
        // TODO this only works the first time
        captionField.focus();

        if (!calendarView.getUI().getWindows().contains(scheduleEventPopup)) {
            calendarView.getUI().addWindow(scheduleEventPopup);
        }

    }

    /* Initializes a modal window to edit schedule event. */
    private void createCalendarEventPopup() {
        VerticalLayout layout = new VerticalLayout();
        // layout.setMargin(true);
        layout.setSpacing(true);

        scheduleEventPopup = new Window(null, layout);
        scheduleEventPopup.setWidth("300px");
        scheduleEventPopup.setModal(true);
        scheduleEventPopup.center();

        scheduleEventFieldLayout.addStyleName("light");
        scheduleEventFieldLayout.setMargin(false);
        layout.addComponent(scheduleEventFieldLayout);

        applyEventButton = new Button("Apply", new Button.ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    commitCalendarEvent();
                } catch (FieldGroup.CommitException e) {
                    e.printStackTrace();
                }
            }
        });
        applyEventButton.addStyleName("primary");
        Button cancel = new Button("Cancel", new Button.ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                discardCalendarEvent();
            }
        });
        deleteEventButton = new Button("Delete", new Button.ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                deleteCalendarEvent();
            }
        });
        deleteEventButton.addStyleName("borderless");
        scheduleEventPopup.addCloseListener(new Window.CloseListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void windowClose(Window.CloseEvent e) {
                discardCalendarEvent();
            }
        });

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addStyleName("v-window-bottom-toolbar");
        buttons.setWidth("100%");
        buttons.setSpacing(true);
        buttons.addComponent(deleteEventButton);
        buttons.addComponent(applyEventButton);
        buttons.setExpandRatio(applyEventButton, 1);
        buttons.setComponentAlignment(applyEventButton, Alignment.TOP_RIGHT);
        buttons.addComponent(cancel);
        layout.addComponent(buttons);

    }



    /* Removes the event from the data source and fires change event. */
    private void deleteCalendarEvent() {
        BasicEvent event = getFormCalendarEvent();
        if (calendarView.consultationModel.consultationBasicEventBeanItemContainer.containsId(event)) {
            calendarView.consultationModel.consultationBasicEventBeanItemContainer.removeItem(event);
        }
         calendarView.getUI().removeWindow(scheduleEventPopup);
    }

    /* Adds/updates the event in the data source and fires change event. */
    private void commitCalendarEvent() throws FieldGroup.CommitException {
        scheduleEventFieldGroup.commit();
        BasicEvent event = getFormCalendarEvent();
        if (event.getEnd() == null) {
            event.setEnd(event.getStart());
        }
        if (  !calendarView.consultationModel.consultationBasicEventBeanItemContainer.containsId(event)) {
            calendarView.consultationModel.consultationBasicEventBeanItemContainer.addItem(event);

        }

       calendarView.getUI().removeWindow(scheduleEventPopup);
    }

    private void discardCalendarEvent() {
        scheduleEventFieldGroup.discard();
        calendarView.getUI().removeWindow(scheduleEventPopup);
    }

}
