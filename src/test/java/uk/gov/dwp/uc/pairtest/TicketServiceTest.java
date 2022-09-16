package uk.gov.dwp.uc.pairtest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceTest {

    @Mock
    private TicketPaymentService ticketPaymentService;

    @Mock
    private SeatReservationService seatReservationService;

    @InjectMocks
    private TicketServiceImpl ticketService;


    @Test
    public void validPurchaseAdultWithInfantTickets() {
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[] { new TicketTypeRequest(TicketTypeRequest.Type.ADULT,1), new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)};
        ticketService.purchaseTickets(1l, ticketTypeRequests );
        verify(ticketPaymentService).makePayment(1l, 20);
        verify(seatReservationService).reserveSeat(1l, 1);
    }

    @Test
    public void validPurchaseAdultsWithChildrenAndInfantTickets() {
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[] { new TicketTypeRequest(TicketTypeRequest.Type.ADULT,4), new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2), new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2)};
        ticketService.purchaseTickets(1l, ticketTypeRequests );
        verify(ticketPaymentService).makePayment(1l, 100);
        verify(seatReservationService).reserveSeat(1l, 6);
    }

    @Test
    public void inValidPurchaseWithInvalidAccountId() {
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[] { new TicketTypeRequest(TicketTypeRequest.Type.ADULT,1), new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)};
        try {
            ticketService.purchaseTickets(0l, ticketTypeRequests);
        } catch (InvalidPurchaseException pe) {
            assertEquals("Account id is invalid", pe.getMessage());
        }
        verify(ticketPaymentService, never()).makePayment(0l, 20);
        verify(seatReservationService, never()).reserveSeat(0l, 1);
    }

    @Test
    public void invalidPurchaseInfantOnlyTicket() {
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[] { new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)};
        try {
            ticketService.purchaseTickets(1l, ticketTypeRequests);
        } catch (InvalidPurchaseException pe) {
            assertEquals("Infant ticket requests are not allowed without adults", pe.getMessage());
        }
        verify(ticketPaymentService, never()).makePayment(1l, 0);
        verify(seatReservationService, never()).reserveSeat(1l, 0);
    }

    @Test
    public void invalidPurchaseChildrenOnlyTicket() {
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[] { new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 4)};
        try {
            ticketService.purchaseTickets(1l, ticketTypeRequests);
        } catch (InvalidPurchaseException pe) {
            assertEquals("Child ticket requests are not allowed without adults", pe.getMessage());
        }
        verify(ticketPaymentService, never()).makePayment(1l, 40);
        verify(seatReservationService, never()).reserveSeat(1l, 4);
    }
}
