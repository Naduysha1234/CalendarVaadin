package presenter;

import backend.basicevent.ConsultationBasicEvent;
import backend.entity.Patient;
import com.vaadin.ui.components.calendar.event.CalendarEvent;

/**
 * Created by user on 07.03.2016.
 */
public interface Presenter {
	ConsultationBasicEvent onItemSelected(CalendarEvent event,Patient item);
}
