import backend.ConsultationManager;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import model.ConsultationModel;
import presenter.ConsultationPresenter;
import view.CalendarView;

import javax.servlet.annotation.WebServlet;

/**
 * Created by user on 20.02.2016.
 */

@Theme("mytheme")
@Widgetset("CalendarVaadin.MyAppWidgetset")

public class App extends UI {


    @Override
    protected void init(VaadinRequest vaadinRequest)
    {

        ConsultationModel consultationModel = new ConsultationModel();
        CalendarView calendarView= new CalendarView (consultationModel);
        setContent(calendarView);

        ConsultationManager consultationManager = new ConsultationManager();
        ConsultationPresenter consultationPresenter = new ConsultationPresenter(consultationModel,consultationManager);
        calendarView.setPresenter(consultationPresenter);
        consultationPresenter.start();

    }
    @WebServlet(urlPatterns = "/*", name = "AppServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = App.class, productionMode = false)
    public static class AppServlet extends VaadinServlet
    {   }




}
