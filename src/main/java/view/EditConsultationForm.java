package view;

import backend.PatientContainer;
import backend.basicevent.ConsultationBasicEvent;
import backend.entity.Patient;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.components.calendar.event.BasicEvent;
import com.vaadin.ui.components.calendar.event.BasicEventProvider;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import presenter.ConsultationPresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by user on 23.02.2016.
 */
public class EditConsultationForm implements BaseView
{

    private TextField captionField;

    public Window scheduleEventPopup;

    private final FormLayout scheduleEventFieldLayout = new FormLayout();

    private FieldGroup scheduleEventFieldGroup = new FieldGroup();

    public Button deleteEventButton;

    public Button applyEventButton;

    private DateField startDateField;

    private DateField endDateField;

    private Calendar calendarComponent;

    private BasicEventProvider dataSource;

    public ConsultationBasicEvent basicEvent;

    private boolean useSecondResolution;

    public final PatientCombobox patientCombobox;

    private final PatientContainer patientContainer ;

    private final ComboboxView comboboxView;

    public CalendarView calendarView;

    public  final TextField nameField;

    public final TextField surnameField;

    public final TextField patronymicField;

    public final DateField  birthdayField;

    public final TextField casHisField;

    private CalendarEvent calendarEvent;

    private NativeSelect selectProcedure;

    private NativeSelect selectStyle;

    private NativeSelect selectDiagnosis;

    private  FieldGroup fieldGroup;

    public static final List<String> styles = new ArrayList<>(Arrays.asList("color1", "color2", "color3", "color4", "color5"));

    public static final List <String> diagnosis = new ArrayList<>(Arrays.asList("Сосудистые заболевания",
        "АВМ-Артерио-венозная мальформация","Другое сосудистое заболевание","Доброкачественные опухоли",
        "Менингиома","Множественные метастазы"));
    public EditConsultationForm(CalendarView calendarView)
    {
        this.calendarView = calendarView;
        this.calendarComponent = calendarView.calendarComponent;
        this.dataSource = calendarView.dataSource;
        patientCombobox = new PatientCombobox("Быстрый ввод");
        patientContainer = new PatientContainer();
        comboboxView = new ComboboxView(this);
        nameField = createTextField("Имя");
        surnameField = createTextField("Фамилия");
        patronymicField = createTextField("Отчество");
        birthdayField = createDateField("Дата рождения");
        casHisField = createTextField("Номер истории");

    }

    /*
    *
    *
     */
    public void showEventPopup(CalendarEvent event, boolean newEvent)
    {
        calendarEvent = event;
        if (event == null) {
            return;
        }
        updateCalendarEventPopup(newEvent);
       updateCalendarEventForm(calendarEvent);

        if (!calendarView.getUI().getWindows().contains(scheduleEventPopup)) {
            calendarView.getUI().addWindow(scheduleEventPopup);
        }
    }
    private void updateCalendarEventPopup(boolean newEvent)
    {
        if (scheduleEventPopup == null) {
            createCalendarEventPopup();
        }
        if (newEvent) {
            scheduleEventPopup.setCaption("Новая консультация");
            patientCombobox.removeAllItems();


        } else {
            scheduleEventPopup.setCaption("Редактирование консультации");

        }
        deleteEventButton.setVisible(!newEvent);
        deleteEventButton.setEnabled(!calendarView.calendarComponent.isReadOnly());
        applyEventButton.setEnabled(! calendarView.calendarComponent.isReadOnly());
    }


    /*
     *
     *
     */
    private void createCalendarEventPopup() {
        VerticalLayout layout = new VerticalLayout();
         layout.setMargin(true);
        layout.setSpacing(true);

        scheduleEventPopup = new Window(null, layout);
        scheduleEventPopup.setSizeFull();
        scheduleEventPopup.setModal(true);
        scheduleEventPopup.center();

        scheduleEventFieldLayout.addStyleName("light");
        scheduleEventFieldLayout.setMargin(false);
        layout.addComponent(scheduleEventFieldLayout);
        applyEventButton = new Button("Применить", new Button.ClickListener() {
            private static final long serialVersionUID = 1L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {

                    if (basicEvent != null)
                    {
                        comboboxView.commitConsultationEvent();
                    }
                    else
                    {
                        commitCalendarEvent();
                    }

                } catch (FieldGroup.CommitException e) {
                    e.printStackTrace();
                }
            }
        });
        applyEventButton.addStyleName("primary");
        Button cancel = new Button("Отмена", new Button.ClickListener() {
            private static final long serialVersionUID = 1L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
           //     if (basicEvent != null)
           //    {
           //        comboboxView.discardConsultationBasicEvent();
            //   }
            //    else {
                    discardCalendarEvent();
            //    }
            }
        });
        deleteEventButton = new Button("Удалить", new Button.ClickListener() {
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
        patientCombobox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        patientCombobox.setItemCaptionPropertyId("name,surname,patronymic,birthday");
        patientCombobox.setImmediate(true);
        onItemSelected();

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
    /*
    *
    *
    *
    */
    @Override
    public void onItemSelected() {
        patientCombobox.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                Notification.show("Selected item: " + event.getProperty().getValue(), Notification.Type.HUMANIZED_MESSAGE);
                // tell the custom container that a value has been selected. This is necessary to ensure that the
                // selected value is displayed by the ComboBox
                Patient patient = (Patient) event.getProperty().getValue();
                if ( patient == null)
                {
                    patientCombobox.setValue(null);
                }
                else {
                    patientContainer.setSelectedPatientBean(patient);
                    if (calendarView.getPresenter() != null) {
                        basicEvent = calendarView.getPresenter().onItemSelected(calendarEvent, patient);
                        System.out.println(basicEvent);
                        comboboxView.bindField(basicEvent);
                    }
                }
            }
        });
        patientCombobox.setContainerDataSource(patientContainer);
    }



    private void updateCalendarEventForm(CalendarEvent event)
    {
        BeanItem<CalendarEvent> item = new BeanItem<CalendarEvent>(event);
        scheduleEventFieldLayout.removeAllComponents();
        scheduleEventFieldGroup = new FieldGroup();
        initFormFields(scheduleEventFieldLayout, event.getClass());
        scheduleEventFieldGroup.setBuffered(true);
        scheduleEventFieldGroup.setItemDataSource(item);
    }

    /*
    *
    *
    */
    private void initFormFields(Layout formLayout, Class<? extends CalendarEvent> eventClass)
    {
        startDateField = createDateField("Конец события");
        startDateField.setRequired(true);
        endDateField = createDateField("Начало события");
        endDateField.setRequired(true);
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

        selectProcedure = createNativeSelect(ConsultationPresenter.PROCEDURES, "Вид консультации");
        selectProcedure.setRequired(true);
        selectStyle = createNativeSelect(styles, "Цвет");
        captionField = createTextField("Заголовок");
        captionField.setInputPrompt("Название события");
        captionField.setRequired(true);
        final TextField executorField = createTextField("Исполнитель");
        executorField.setInputPrompt("человек,отвественный за процедуру");
        final TextArea descriptionField = createTextArea("Описание");
        descriptionField.setInputPrompt("");
        descriptionField.setRows(3);
        nameField.setInputPrompt("имя пациента");
        surnameField.setInputPrompt("фамилия пациента");
        patronymicField.setInputPrompt("отчество пациента");

        HorizontalLayout hlLayout = new HorizontalLayout(startDateField,endDateField);
        formLayout.addComponent(hlLayout);
        formLayout.addComponent(allDayField);
        formLayout.addComponent(captionField);
        formLayout.addComponent(patientCombobox);
        formLayout.addComponent(selectProcedure);
        formLayout.addComponent(selectStyle);
        formLayout.addComponent(executorField);
        HorizontalLayout horizontalLayout = new HorizontalLayout(nameField,surnameField,
            patronymicField,birthdayField,casHisField);
         formLayout.addComponent(horizontalLayout);

        /*
       if (eventClass == ConsultationBasicEvent.class) {
           formLayout.addComponent(executorField);
           formLayout.addComponent(nameField);
           formLayout.addComponent(surnameField);
           formLayout.addComponent(patronymicField);
          formLayout.addComponent(birthdayField);
       }
       */
        scheduleEventFieldGroup.bind(startDateField, "start");
        scheduleEventFieldGroup.bind(endDateField, "end");
        scheduleEventFieldGroup.bind(captionField, "caption");
        scheduleEventFieldGroup.bind(descriptionField, "description");
        scheduleEventFieldGroup.bind(executorField, "executor");
        scheduleEventFieldGroup.bind(nameField,"name");
        scheduleEventFieldGroup.bind(surnameField,"surname");
        scheduleEventFieldGroup.bind(patronymicField,"patronymic");
        scheduleEventFieldGroup.bind(birthdayField,"birthday");
        scheduleEventFieldGroup.bind(casHisField,"case_history_num");
        scheduleEventFieldGroup.bind(selectStyle, "styleName");
        scheduleEventFieldGroup.bind(allDayField, "allDay");

        /*
        if (eventClass == ConsultationBasicEvent.class) {
            scheduleEventFieldGroup.bind(executorField, "executor");
            scheduleEventFieldGroup.bind(nameField,"name");
            scheduleEventFieldGroup.bind(surnameField,"surname");
            scheduleEventFieldGroup.bind(patronymicField,"patronymic");
            scheduleEventFieldGroup.bind(birthdayField,"birthday");

        }
        */
    }

    /*
    *
    *
    *
     */
    private NativeSelect createNativeSelect(List<String> strings, String caption) {
        NativeSelect nativeSelect = new NativeSelect(caption, strings);
        nativeSelect.setRequired(true);
        nativeSelect.setBuffered(true);
        return nativeSelect;
    }

    private void setFormDateResolution(Resolution resolution) {
        if (startDateField != null && endDateField != null) {
            startDateField.setResolution(resolution);
            endDateField.setResolution(resolution);
        }
    }
    @SuppressWarnings("unchecked")
    public BasicEvent getFormCalendarEvent() {
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
    private void commitCalendarEvent() throws FieldGroup.CommitException {
        scheduleEventFieldGroup.commit();
        BasicEvent event = getFormCalendarEvent();
        if (event.getEnd() == null) {
            event.setEnd(event.getStart());
        }
        if (!dataSource.containsEvent(event)) {
            dataSource.addEvent(event);
        }

        calendarView.getUI().removeWindow(scheduleEventPopup);
    }

    private void deleteCalendarEvent() {
        BasicEvent event = getFormCalendarEvent();
        if (dataSource.containsEvent(event)) {
            dataSource.removeEvent(event);
        }
     //   patientCombobox.clear();
        calendarView.getUI().removeWindow(scheduleEventPopup);
    }

    private void discardCalendarEvent() {
        scheduleEventFieldGroup.discard();

        calendarView.getUI().removeWindow(scheduleEventPopup);
    }

}
