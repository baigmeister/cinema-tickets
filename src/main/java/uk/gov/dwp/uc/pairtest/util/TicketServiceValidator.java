package uk.gov.dwp.uc.pairtest.util;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import java.util.Arrays;
import java.util.Optional;

public final class TicketServiceValidator {

    /**
     * Validate accountId
     *      - greater than 0
     * Validate ticket type request
     *      - for blank Type field
     *      - 20 tickets requested is the maximum
     * For rules
     *      - child type can not have a ticket without adult
     *      - infant type can not have a ticket without adult
     *
     * @param ticketTypeRequests
     * @param accountId
     * @return
     *
     */
    public static Optional<String> validateTicketTypeRequestsAndAccountId(final Long accountId, final TicketTypeRequest[] ticketTypeRequests) {

        if (accountId < 1l)
            return Optional.of("Account id is invalid");

        final var ticketTotal = Arrays.stream(ticketTypeRequests).mapToInt(i -> i.getNoOfTickets()).sum();
        if (ticketTotal > 20)
            return Optional.of("Over 20 tickets is not allowed");

        if (Arrays.stream(ticketTypeRequests).filter(p -> null == (p.getTicketType())).count() > 0) {
            return Optional.of("Ticket request is invalid with missing ticket Type");
        }

        final String inValidTicketTypeRequests;

        final var noAdultInTickets = Arrays.stream(ticketTypeRequests)
                .noneMatch(j -> j.getTicketType().equals(TicketTypeRequest.Type.ADULT));
        final var childInTickets = Arrays.stream(ticketTypeRequests)
                .anyMatch(j -> j.getTicketType().equals(TicketTypeRequest.Type.CHILD));
        final var infantInTickets = Arrays.stream(ticketTypeRequests)
                .anyMatch(j -> j.getTicketType().equals(TicketTypeRequest.Type.INFANT));

        boolean isInvalidTicketRequests = noAdultInTickets && ticketTotal > 0 ? true : false;

        if (isInvalidTicketRequests && childInTickets) {
            inValidTicketTypeRequests = "Child ticket requests are not allowed without adults";
        } else if (isInvalidTicketRequests && infantInTickets) {
            inValidTicketTypeRequests = "Infant ticket requests are not allowed without adults";
        } else {
            inValidTicketTypeRequests = "";
        }
        if(!inValidTicketTypeRequests.isBlank()) {
            return Optional.of(inValidTicketTypeRequests);
        }
        return Optional.empty();
    }
}
