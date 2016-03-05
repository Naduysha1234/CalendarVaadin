package view;

import backend.basicevent.ConsultationBasicEvent;
import backend.entity.Consultation;
import com.vaadin.data.Property;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.components.calendar.CalendarComponentEvents;
import com.vaadin.ui.components.calendar.event.BasicEventProvider;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.handler.BasicDateClickHandler;
import com.vaadin.ui.components.calendar.handler.BasicWeekClickHandler;
import com.vaadin.ui.themes.ValoTheme;
import model.ConsultationModel;

import java.text.DateFormatSymbols;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by user on 20.02.2016.
 */
public class CalendarView   extends GridLayout implements View {

    private static final long serialVersionUID = -5436777475398410597L;

    private static final String DEFAULT_ITEMID = "DEFAULT";

    public final ConsultationModel consultationModel;

    // три режима - неделя месяц день
    private enum Mode
    {
        MONTH, WEEK, DAY;
    }

    // Календарь чтобы рассчитывать время
    private GregorianCalendar calendar;

    // Календарь
    public Calendar calendarComponent;
   // Контроль за временем
    private Date currentMonthsFirstDate;

    private final Label captionLabel = new Label("");

    // Кнопка-> Отображение месяца
    private Button monthButton;

    // Кнопка - Отображение недели
    private Button weekButton;

    // Кнопка отображения дня
    private Button dayButton;

   // Вперед
    private Button nextButton;

    // Назад
    private Button prevButton;

    // Добавить новую консультацию
    private Button addNewEvent;


    EditConsultationForm editConsultationForm;

    // Сокрытие выходных
     private CheckBox hideWeekendsButton;

     // Скрытие радиохирургии
    private  CheckBox radiosurgery;
    //
    private CheckBox och;
    private CheckBox zaoch;
    private CheckBox oncology;

    public BasicEventProvider dataSource;

    private Mode viewMode = Mode.WEEK;

    public CalendarView(ConsultationModel consultationModel) {
        this.consultationModel = consultationModel;

        setSizeFull();
        setHeight("1000px");
        setMargin(true);
        setSpacing(true);
        setLocale(Locale.getDefault());

        initCalendar();
        initLayoutContent();
    }

    private void initCalendar()
    {
        dataSource = new BasicEventProvider();
        calendarComponent = new Calendar(dataSource);
        calendarComponent.setLocale(getLocale());
        calendarComponent.setImmediate(true);
        calendarComponent.setSizeFull();
        calendarComponent.setContainerDataSource(consultationModel.getConsultationBAsicEventContainer());
        editConsultationForm = new EditConsultationForm(this);
        Date today = getToday();
        calendar = new GregorianCalendar(getLocale());
        calendar.setTime(today);
        calendarComponent.getInternalCalendar().setTime(today);
        calendarComponent.setFirstVisibleHourOfDay(9);
        calendarComponent.setLastVisibleHourOfDay(18);
        calendarComponent.setTimeFormat(Calendar.TimeFormat.Format24H);

        // Calendar getStartDate (and getEndDate) has some strange logic which
        // returns Monday of the current internal time if no start date has been set

        calendarComponent.setStartDate(calendarComponent.getStartDate());
        calendarComponent.setEndDate(calendarComponent.getEndDate());
        int rollAmount = calendar.get(GregorianCalendar.DAY_OF_MONTH) - 1;
        calendar.add(GregorianCalendar.DAY_OF_MONTH, -rollAmount);
        currentMonthsFirstDate = calendar.getTime();

        updateCaptionLabel();

        addCalendarEventListeners();
    }

    private void initLayoutContent()
    {
        initNavigationButtons();
        initHideWeekEndButton();
        initAddNewEventButton();

        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth("100%");
        hl.setSpacing(true);
        hl.addComponent(prevButton);
        hl.addComponent(captionLabel);

        CssLayout group = new CssLayout();
        group.addStyleName("v-component-group");
        group.addComponent(dayButton);
        group.addComponent(weekButton);
        group.addComponent(monthButton);
        hl.addComponent(group);

        hl.addComponent(nextButton);
        hl.setComponentAlignment(prevButton, Alignment.MIDDLE_LEFT);
        hl.setComponentAlignment(captionLabel, Alignment.MIDDLE_CENTER);
        hl.setComponentAlignment(group, Alignment.MIDDLE_CENTER);
        hl.setComponentAlignment(nextButton, Alignment.MIDDLE_RIGHT);

        // monthButton.setVisible(viewMode == Mode.WEEK);
        // weekButton.setVisible(viewMode == Mode.DAY);

        HorizontalLayout controlPanel = new HorizontalLayout();
        controlPanel.setSpacing(true);
        controlPanel.setWidth("100%");
        controlPanel.setMargin(true);
        controlPanel.addComponents(hideWeekendsButton,addNewEvent);

        Label viewCaption = new Label("Calendar");
        viewCaption.setStyleName(ValoTheme.LABEL_H1);
        addComponent(viewCaption);
        addComponent(controlPanel);
        addComponent(hl);
        addComponent(calendarComponent);
        setRowExpandRatio(getRows() - 1, 1.0f);

    }
    private void initNavigationButtons() {

        monthButton = new Button("Месяц", new Button.ClickListener() {
            private static final long serialVersionUID = 1L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                switchToMonthView();
            }
        });
        weekButton = new Button("Неделя", new Button.ClickListener() {
            private static final long serialVersionUID = 1L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                // simulate week click
                CalendarComponentEvents.WeekClickHandler handler = (CalendarComponentEvents.WeekClickHandler) calendarComponent
                        .getHandler(CalendarComponentEvents.WeekClick.EVENT_ID);
                handler.weekClick(new CalendarComponentEvents.WeekClick(calendarComponent, calendar
                        .get(GregorianCalendar.WEEK_OF_YEAR), calendar
                        .get(GregorianCalendar.YEAR)));
            }
        });
        dayButton = new Button("День", new Button.ClickListener() {
            private static final long serialVersionUID = 1L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                // simulate day click
                BasicDateClickHandler handler = (BasicDateClickHandler) calendarComponent
                        .getHandler(CalendarComponentEvents.DateClickEvent.EVENT_ID);
                handler.dateClick(new CalendarComponentEvents.DateClickEvent(calendarComponent,
                        calendar.getTime()));
            }
        });
        nextButton = new Button("Вперед", new Button.ClickListener() {
            private static final long serialVersionUID = 1L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                handleNextButtonClick();
            }
        });
        prevButton = new Button("Назад", new Button.ClickListener() {
            private static final long serialVersionUID = 1L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                handlePreviousButtonClick();
            }
        });
    }

    private void initAddNewEventButton()
    {

        addNewEvent = new Button("Новая консультация");
        addNewEvent.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = -8307244759142541067L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Date start = getToday();
                start.setHours(0);
                start.setMinutes(0);
                start.setSeconds(0);

                Date end = getEndOfDay(calendar, start);
                editConsultationForm.showEventPopup(createNewEvent(start, end), true);
            }
        });

    }
    private ConsultationBasicEvent getNewEvent(String caption, Date start, Date end) {
        Consultation consultation = new Consultation(new Date(),0,"","","",start,end,"");
        ConsultationBasicEvent event = new ConsultationBasicEvent(caption,"новая консультация",consultation,"");
        event.setStyleName("mycolor");
       // consultationModel.consultationBasicEventBeanItemContainer.addBean(event);
        return event;
    }

    private CalendarEvent createNewEvent(Date start, Date end) {
        Consultation consultation = new Consultation(new Date(),0,"","","",start,end,"");
        ConsultationBasicEvent event = new ConsultationBasicEvent("Новая консультация","описание",consultation,"");
        event.setStyleName("mycolor");
   //     consultationModel.consultationBasicEventBeanItemContainer.addBean(event);
        return event;
    }

    private void initHideWeekEndButton() {
        hideWeekendsButton = new CheckBox("Выходные");
        hideWeekendsButton.addStyleName("small");
        hideWeekendsButton.setImmediate(true);
        hideWeekendsButton
                .addValueChangeListener(new Property.ValueChangeListener() {
                    private static final long serialVersionUID = 1L;
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        setWeekendsHidden(hideWeekendsButton.getValue());
                    }
                });

    }
    private void addCalendarEventListeners() {
        // Register week clicks by changing the schedules start and end dates.
        calendarComponent.setHandler(new BasicWeekClickHandler() {
            @Override
            public void weekClick(CalendarComponentEvents.WeekClick event) {
                // let BasicWeekClickHandler handle calendar dates, and update
                // only the other parts of UI here
                super.weekClick(event);
                updateCaptionLabel();
                switchToWeekView();
            }
        });

        calendarComponent.setHandler(new CalendarComponentEvents.EventClickHandler() {
            @Override
            public void eventClick(CalendarComponentEvents.EventClick event) {
                editConsultationForm.showEventPopup(event.getCalendarEvent(), false);
            }
        });

        calendarComponent.setHandler(new BasicDateClickHandler() {

            @Override
            public void dateClick(CalendarComponentEvents.DateClickEvent event) {
                // let BasicDateClickHandler handle calendar dates, and update
                // only the other parts of UI here
                super.dateClick(event);
                switchToDayView();
            }
        });
        calendarComponent.setHandler(new CalendarComponentEvents.RangeSelectHandler() {
            @Override
            public void rangeSelect(CalendarComponentEvents.RangeSelectEvent event) {
                handleRangeSelect(event);
            }
        });
    }

    private void updateCaptionLabel() {
        DateFormatSymbols s = new DateFormatSymbols(getLocale());
        String month = s.getShortMonths()[calendar.get(GregorianCalendar.MONTH)];
        captionLabel.setValue(month + " "
                + calendar.get(GregorianCalendar.YEAR));
    }

    public void switchToDayView() { viewMode = Mode.DAY; }

    public  void switchToWeekView()
    {
        viewMode = Mode.WEEK;
    }

    public void switchToMonthView()
    {
        viewMode = Mode.MONTH;

        int rollAmount = calendar.get(GregorianCalendar.DAY_OF_MONTH) - 1;
        calendar.add(GregorianCalendar.DAY_OF_MONTH, -rollAmount);

        calendarComponent.setStartDate(calendar.getTime());

        updateCaptionLabel();

        calendar.add(GregorianCalendar.MONTH, 1);
        calendar.add(GregorianCalendar.DATE, -1);

        calendarComponent.setEndDate(calendar.getTime());

        calendar.setTime(getToday());
    }

    public void  handleRangeSelect(CalendarComponentEvents.RangeSelectEvent event)
    {
        Date start = event.getStart();
        Date end = event.getEnd();

        /*
         * If a range of dates is selected in monthly mode, we want it to end at
         * the end of the last day.
         */
        if (event.isMonthlyMode()) {
            end = getEndOfDay(calendar, end);
        }

        editConsultationForm.showEventPopup(createNewEvent(start, end), true);

    }
    private Date getToday() {

        return new Date();
    }

    public void  handleNextButtonClick()
    {
        switch (viewMode) {
            case MONTH:
                nextMonth();
                break;
            case WEEK:
                nextWeek();
                break;
            case DAY:
                nextDay();
                break;
        }

    }

    private void nextDay()
    {
        rollDate(1);
    }

    private void nextWeek()
    {
        rollWeek(1);
    }

    private void nextMonth()
    {
        rollMonth(1);
    }

    public  void  handlePreviousButtonClick()
   {
       switch (viewMode) {
           case MONTH:
               previousMonth();
               break;
           case WEEK:
               previousWeek();
               break;
           case DAY:
               previousDay();
               break;
       }
   }
    private void previousDay() {rollDate(-1); }

    private void previousWeek() { rollWeek(-1); }

    private void previousMonth() {
        rollMonth(-1);
    }

    private void rollDate(int direction) {
        calendar.add(GregorianCalendar.DATE, direction);
        resetCalendarTime(false);
        resetCalendarTime(true);
    }

    private void rollWeek(int direction)
    {
        calendar.add(GregorianCalendar.WEEK_OF_YEAR, direction);
        calendar.set(GregorianCalendar.DAY_OF_WEEK,
                calendar.getFirstDayOfWeek());
        resetCalendarTime(false);
        resetTime(true);
        calendar.add(GregorianCalendar.DATE, 6);
        calendarComponent.setEndDate(calendar.getTime());

    }

    private void rollMonth(int direction)
    {
        calendar.setTime(currentMonthsFirstDate);
        calendar.add(GregorianCalendar.MONTH, direction);
        resetTime(false);
        currentMonthsFirstDate = calendar.getTime();
        calendarComponent.setStartDate(currentMonthsFirstDate);

        updateCaptionLabel();

        calendar.add(GregorianCalendar.MONTH, 1);
        calendar.add(GregorianCalendar.DATE, -1);
        resetCalendarTime(true);

    }
    private void resetCalendarTime(boolean resetEndTime)
    {
        resetTime(resetEndTime);
        if (resetEndTime) {
            calendarComponent.setEndDate(calendar.getTime());
        } else {
            calendarComponent.setStartDate(calendar.getTime());
            updateCaptionLabel();
        }
    }
    private void resetTime(boolean max) {

        if (max) {
            calendar.set(GregorianCalendar.HOUR_OF_DAY,
                    calendar.getMaximum(GregorianCalendar.HOUR_OF_DAY));
            calendar.set(GregorianCalendar.MINUTE,
                    calendar.getMaximum(GregorianCalendar.MINUTE));
            calendar.set(GregorianCalendar.SECOND,
                    calendar.getMaximum(GregorianCalendar.SECOND));
            calendar.set(GregorianCalendar.MILLISECOND,
                    calendar.getMaximum(GregorianCalendar.MILLISECOND));
        } else {
            calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
            calendar.set(GregorianCalendar.MINUTE, 0);
            calendar.set(GregorianCalendar.SECOND, 0);
            calendar.set(GregorianCalendar.MILLISECOND, 0);
        }

    }

    private void setWeekendsHidden(boolean weekendsHidden) {
        if (weekendsHidden) {
            int firstToShow = (GregorianCalendar.MONDAY - calendar
                    .getFirstDayOfWeek()) % 7;
            calendarComponent.setFirstVisibleDayOfWeek(firstToShow + 1);
            calendarComponent.setLastVisibleDayOfWeek(firstToShow + 5);
        } else {
            calendarComponent.setFirstVisibleDayOfWeek(1);
            calendarComponent.setLastVisibleDayOfWeek(7);
        }

    }

    private static Date getEndOfDay (java.util.Calendar calendar, Date date)
    {
        java.util.Calendar calendarClone = (java.util.Calendar) calendar
                .clone();

        calendarClone.setTime(date);
        calendarClone.set(java.util.Calendar.MILLISECOND, 0);
        calendarClone.set(java.util.Calendar.SECOND, 0);
        calendarClone.set(java.util.Calendar.MINUTE, 0);
        calendarClone.set(java.util.Calendar.HOUR, 0);
        calendarClone.set(java.util.Calendar.HOUR_OF_DAY, 0);

        return calendarClone.getTime();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
