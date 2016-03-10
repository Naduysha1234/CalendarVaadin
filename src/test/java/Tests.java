import backend.ConsultationManager;
import backend.entity.Patient;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by user on 22.02.2016.
 */
public class Tests {



    /*
    @Ignore
    @Test
    public void patients(){
        GregorianCalendar calendar= new GregorianCalendar(2016, 1, 1);
        Date startDay = calendar.getTime();
        calendar.add(calendar.MONTH, 1);
        Date endDay = calendar.getTime();
        Collection<? extends Consultation>  consultations =  new ConsultationManager().listConsultation(startDay,endDay);
        System.out.println(consultations);
    }

*/


    @Ignore
    @Test
    public  void selectpatient() {
        GregorianCalendar calendar = new GregorianCalendar(1972, 9, 5);
        Date startDay = calendar.getTime();
        System.out.println(startDay);
        String name = "Сергей";
        String surname = "Савельев";
        String patronymic = "Сергеевич";
        Patient patient = new ConsultationManager().selectpatient(name, surname, patronymic,startDay);
        System.out.println(patient);
    }





}
