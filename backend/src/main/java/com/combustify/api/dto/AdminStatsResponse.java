package com.combustify.api.dto;

public record AdminStatsResponse(
    Long totalStations,
    Long totalPrices,
    Long totalUsers,
    Long activeSubscriptions,
    Long pricesReportedToday
) {}
