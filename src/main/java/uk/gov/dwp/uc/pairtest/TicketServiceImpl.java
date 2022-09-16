package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.util.TicketServiceValidator;
import java.util.*;


public class TicketServiceImpl implements TicketService {
    private TicketPaymentService ticketPaymentService;
    private SeatReservationService seatReservationService;

    private static final int ADULT_PRICE = 20;
    private static final int CHILD_PRICE = 10;

    /**
     * Should only have private methods other than the one below.
     */
    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        TicketServiceValidator.validateTicketTypeRequestsAndAccountId(accountId, ticketTypeRequests).ifPresent((errorMessage) -> {
            throw new InvalidPurchaseException(errorMessage);
        });

        Map<TicketTypeRequest.Type, Integer> mapCounts = getTicketTypeCounts(ticketTypeRequests);

        // it has been assumed that there will always be seats available and payment is taken last
        seatReservationService.reserveSeat(accountId, mapCounts.get(TicketTypeRequest.Type.ADULT) + mapCounts.get(TicketTypeRequest.Type.CHILD));
        ticketPaymentService.makePayment(accountId, getTotalTicketCost(mapCounts));
    }

    private static int getTotalTicketCost(Map<TicketTypeRequest.Type, Integer> mapCounts) {
        return (mapCounts.get(TicketTypeRequest.Type.ADULT) * ADULT_PRICE) + (mapCounts.get(TicketTypeRequest.Type.CHILD) * CHILD_PRICE);
    }

    private static Map<TicketTypeRequest.Type, Integer> getTicketTypeCounts(TicketTypeRequest[] ticketTypeRequests) {
        final Map<TicketTypeRequest.Type, Integer> mapCounts = new HashMap<>();
        for (TicketTypeRequest.Type type : TicketTypeRequest.Type.values() ) {
            mapCounts.put(type, countTickets(ticketTypeRequests, type));
        }
        return mapCounts;
    }

    private static int countTickets(TicketTypeRequest[] ticketTypeRequests, TicketTypeRequest.Type type) {
        return Arrays.stream(ticketTypeRequests).filter(p -> p.getTicketType().equals(type)).mapToInt(m ->  m.getNoOfTickets()).sum();
    }

    // added as means access TicketPaymentService
    public void setTicketPaymentService(TicketPaymentService ticketPaymentService) {
        this.ticketPaymentService = ticketPaymentService;
    }

    // added as means access SeatReservationService
    public void setSeatReservationService(SeatReservationService seatReservationService) {
        this.seatReservationService = seatReservationService;
    }
}
