package com.combustify.domain.service;

import com.combustify.domain.entity.GasStation;
import com.combustify.domain.entity.Price;
import com.combustify.domain.entity.User;
import com.combustify.domain.repository.PriceRepository;
import com.combustify.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GasStationService gasStationService;

    @InjectMocks
    private PriceService priceService;

    private UUID stationId;
    private UUID userId;
    private User testUser;
    private GasStation testStation;

    @BeforeEach
    void setUp() {
        stationId = UUID.randomUUID();
        userId = UUID.randomUUID();

        testUser = User.builder()
                .id(userId)
                .email("test@example.com")
                .build();

        testStation = GasStation.builder()
                .id(stationId)
                .name("Test Station")
                .city("São Paulo")
                .build();
    }

    @Test
    void testReportPriceSuccess() {
        BigDecimal price = new BigDecimal("5.99");

        when(gasStationService.findById(stationId)).thenReturn(testStation);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(priceRepository.save(any(Price.class))).thenAnswer(invocation -> {
            Price p = invocation.getArgument(0);
            p.setId(UUID.randomUUID());
            return p;
        });

        Price result = priceService.reportPrice(stationId, "gasolina", price, userId);

        assertNotNull(result);
        assertEquals(price, result.getPrice());
        assertEquals(Price.FuelType.GASOLINA, result.getFuelType());
        verify(priceRepository, times(1)).save(any(Price.class));
    }

    @Test
    void testReportPriceWithInvalidFuelType() {
        BigDecimal price = new BigDecimal("5.99");

        when(gasStationService.findById(stationId)).thenReturn(testStation);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        assertThrows(IllegalArgumentException.class,
                () -> priceService.reportPrice(stationId, "invalidFuel", price, userId));
    }

    @Test
    void testReportPriceWithZeroPrice() {
        BigDecimal price = BigDecimal.ZERO;

        when(gasStationService.findById(stationId)).thenReturn(testStation);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        assertThrows(IllegalArgumentException.class,
                () -> priceService.reportPrice(stationId, "gasolina", price, userId));
    }

    @Test
    void testReportPriceUserNotFound() {
        BigDecimal price = new BigDecimal("5.99");

        when(gasStationService.findById(stationId)).thenReturn(testStation);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> priceService.reportPrice(stationId, "gasolina", price, userId));
    }
}
