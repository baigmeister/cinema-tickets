package uk.gov.dwp.uc.pairtest;

import org.junit.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.util.TicketServiceValidator;

import static org.junit.Assert.assertEquals;

public class TicketServiceValidatorTest {

    @Test
    public void validTicketTypeRequestsAndAccountId() {
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[]{new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1), new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)};
        assertEquals(true, TicketServiceValidator.validateTicketTypeRequestsAndAccountId(1l, ticketTypeRequests).isEmpty());
    }

    @Test
    public void invalidAccountId() {
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[]{new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1), new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)};
        assertEquals("Account id is invalid", TicketServiceValidator.validateTicketTypeRequestsAndAccountId(0l, ticketTypeRequests).get());
    }

    @Test
    public void missingTypeinTicketTypeRequest() {
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[]{new TicketTypeRequest(null,0)};
        assertEquals("Ticket request is invalid with missing ticket Type", TicketServiceValidator.validateTicketTypeRequestsAndAccountId(1l, ticketTypeRequests).get());
    }

    @Test
    public void invalidPurchaseInfantOnlyTicket() {
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[]{new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)};
        assertEquals("Infant ticket requests are not allowed without adults", TicketServiceValidator.validateTicketTypeRequestsAndAccountId(1l, ticketTypeRequests).get());
    }

    @Test
    public void invalidPurchaseChildOnlyTicket() {
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[]{new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1)};
        assertEquals("Child ticket requests are not allowed without adults", TicketServiceValidator.validateTicketTypeRequestsAndAccountId(1l, ticketTypeRequests).get());
    }

    @Test
    public void invalidPurchaseExceeding20Tickets() {
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[]{new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 21)};
        assertEquals("Over 20 tickets is not allowed", TicketServiceValidator.validateTicketTypeRequestsAndAccountId(1l, ticketTypeRequests).get());
    }
}
