package itmo.tg.airbnb_xa.business.misc;

import itmo.tg.airbnb_xa.business.dto.*;
import itmo.tg.airbnb_xa.business.model.*;
import itmo.tg.airbnb_xa.security.model.User;

import java.util.List;

public class ModelDTOConverter {

    public static Advertisement convert(AdvertisementRequestDTO dto, User host) {
        return Advertisement.builder()
                .address(dto.getAddress())
                .rooms(dto.getRooms())
                .bookPrice(dto.getBookPrice())
                .pricePerNight(dto.getPricePerNight())
                .host(host)
                .build();
    }

    public static AdvertisementResponseDTO convert(Advertisement advertisement) {
        return AdvertisementResponseDTO.builder()
                .id(advertisement.getId())
                .address(advertisement.getAddress())
                .rooms(advertisement.getRooms())
                .bookPrice(advertisement.getBookPrice())
                .pricePerNight(advertisement.getPricePerNight())
                .status(advertisement.getStatus())
                .hostUsername(advertisement.getHost().getUsername())
                .build();
    }

    public static List<AdvertisementResponseDTO> toAdvertisementDTOList(List<Advertisement> adverts) {
        return adverts.stream().map(ModelDTOConverter::convert).toList();
    }

    public static Booking convert(BookingRequestDTO dto, Advertisement advertisement, User guest) {
        return Booking.builder()
                .advertisement(advertisement)
                .guest(guest)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();
    }

    public static BookingResponseDTO convert(Booking booking) {
        return BookingResponseDTO.builder()
                .id(booking.getId())
                .advertisementId(booking.getAdvertisement().getId())
                .guestUsername(booking.getGuest().getUsername())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingResponseDTO> toBookingDTOList(List<Booking> bookings) {
        return bookings.stream().map(ModelDTOConverter::convert).toList();
    }

    public static FineDTO convert(Fine fine) {
        return FineDTO.builder()
                .id(fine.getId())
                .amount(fine.getAmount())
                .status(fine.getStatus())
                .username(fine.getUser().getUsername())
                .build();
    }

    public static List<FineDTO> toFineDTOList(List<Fine> fines) {
        return fines.stream().map(ModelDTOConverter::convert).toList();
    }

    public static GuestComplaintResponseDTO convert(GuestComplaint complaint) {
        return GuestComplaintResponseDTO.builder()
                .id(complaint.getId())
                .guestUsername(complaint.getGuest().getUsername())
                .advertisementId(complaint.getAdvertisement().getId())
                .bookingId(complaint.getBooking().getId())
                .proofLink(complaint.getProofLink())
                .date(complaint.getDate())
                .status(complaint.getStatus())
                .resolverUsername(complaint.getResolver() == null ? null : complaint.getResolver().getUsername())
                .build();
    }

    public static List<GuestComplaintResponseDTO> toGuestComplaintDTOList(List<GuestComplaint> complaints) {
        return complaints.stream().map(ModelDTOConverter::convert).toList();
    }

    public static HostDamageComplaintResponseDTO convert(HostDamageComplaint complaint) {
        return HostDamageComplaintResponseDTO.builder()
                .id(complaint.getId())
                .hostUsername(complaint.getHost().getUsername())
                .bookingId(complaint.getBooking().getId())
                .proofLink(complaint.getProofLink())
                .compensationAmount(complaint.getCompensationAmount())
                .status(complaint.getStatus())
                .resolverUsername(complaint.getResolver() == null ? null : complaint.getResolver().getUsername())
                .build();
    }

    public static List<HostDamageComplaintResponseDTO> toHostDamageComplaintDTOList(List<HostDamageComplaint> complaints) {
        return complaints.stream().map(ModelDTOConverter::convert).toList();
    }

    public static HostJustificationResponseDTO convert(HostJustification justification) {
        return HostJustificationResponseDTO.builder()
                .id(justification.getId())
                .hostUsername(justification.getHost().getUsername())
                .guestComplaintId(justification.getComplaint().getId())
                .proofLink(justification.getProofLink())
                .status(justification.getStatus())
                .resolverUsername(justification.getResolver() == null ? null : justification.getResolver().getUsername())
                .build();
    }

    public static List<HostJustificationResponseDTO> toHostJustificationDTOList(List<HostJustification> justifications) {
        return justifications.stream().map(ModelDTOConverter::convert).toList();
    }

    public static AdvertisementBlockDTO convert(AdvertisementBlock block) {
        return AdvertisementBlockDTO.builder()
                .advertisementId(block.getAdvertisement().getId())
                .dateUntil(block.getDateUntil())
                .build();
    }

    public static List<AdvertisementBlockDTO> toAdvertisementBlockDTOList(List<AdvertisementBlock> blocks) {
        return blocks.stream().map(ModelDTOConverter::convert).toList();
    }

}
