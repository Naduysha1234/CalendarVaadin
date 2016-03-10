package presenter;

import backend.ConsultationManager;
import backend.basicevent.ConsultationBasicEvent;
import backend.entity.Consultation;
import backend.entity.Patient;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import model.ConsultationModel;

import java.util.*;

/**
 * Created by user on 20.02.2016.
 */
public class ConsultationPresenter implements  Presenter {


    public final ConsultationModel consultationModel;

    public final ConsultationManager consultationManager;

    public ConsultationBasicEvent consultationBasicEvent;

    public Consultation  consultation;

    private List<Consultation> consultations = new ArrayList<>();

    private  List<Consultation> patient = new ArrayList<>();

    public static final ArrayList<String> PROCEDURES = new ArrayList<>(Arrays.asList("Радиохирургия","Заочная консультация","Очная консультация","Оннкология"));
    private List<String> executor = new ArrayList<>(Arrays.asList("физик", "онколог", "планировщик", "врач", "лечащий врач"));


    public ConsultationPresenter(ConsultationModel consultationModel, ConsultationManager consultationManager) {
        this.consultationModel = consultationModel;
        this.consultationManager = consultationManager;
    }

    /*
     *
     *
     *
     */

    public void start() {

        GregorianCalendar calendar = new GregorianCalendar(2016, 1, 1);
        Date startDay = calendar.getTime();
        calendar.add(calendar.MONTH, 1);
        Date endDay = calendar.getTime();


        consultations = consultationManager.listConsultation(startDay, endDay);

        for (int i = 0; i < consultations.size(); i++) {
            Random random = new Random();
            int value = random.nextInt(executor.size());
            consultationBasicEvent = new ConsultationBasicEvent("Радиохирургия", "Some description.", consultations.get(i),
                    executor.get(value));
            consultationBasicEvent.setStyleName("mycolor");
            consultationBasicEvent.getStart().setHours(9);
            consultationBasicEvent.getEnd().setHours(18);
            consultationModel.consultationBasicEventBeanItemContainer.addBean(consultationBasicEvent);
        }

    }
    /*
    *
    *
    */
    @Override
    public ConsultationBasicEvent onItemSelected(CalendarEvent calendarEvent,Patient item) {
        Patient patient = consultationManager.selectpatient(item.getName(),item.getSurname(),item.getPatronymic(),item.getBirthday());
        Consultation consultation = new Consultation(patient,calendarEvent.getStart(),calendarEvent.getEnd());
        ConsultationBasicEvent basicEvent = new ConsultationBasicEvent
            (calendarEvent.getCaption(),calendarEvent.getDescription(),consultation,"");
        return basicEvent;
    }
}


