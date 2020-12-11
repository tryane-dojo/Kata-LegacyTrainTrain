package trainTrain.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.traintrain.Reservation;
import com.traintrain.WebTicketManager;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ReservationsController {

    @RequestMapping(method = RequestMethod.POST, value = "api/reservations")
    public String update(@RequestBody RequestDto requestDto) throws IOException, InterruptedException {
        WebTicketManager webTicketManager = new WebTicketManager();
        Reservation reservation = webTicketManager.attemptToBook(requestDto.getTrain_id(), requestDto.getNumber_of_seats());
        return new ObjectMapper().writeValueAsString(reservation);
    }

    @RequestMapping(method = RequestMethod.GET, value = "api/value")
    public String get() {
        return "Reservations";
    }
}
