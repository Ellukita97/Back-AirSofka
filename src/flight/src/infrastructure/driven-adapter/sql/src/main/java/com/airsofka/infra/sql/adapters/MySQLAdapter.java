package com.airsofka.infra.sql.adapters;

import com.airsofka.flight.application.shared.flight.FlightListResponse;
import com.airsofka.flight.application.shared.ports.IFlightRepositoryPort;
import com.airsofka.flight.domain.flight.Flight;

import com.airsofka.infra.sql.entities.FlightEntity;
import com.airsofka.infra.sql.entities.PriceEntity;
import com.airsofka.infra.sql.repositories.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MySQLAdapter implements IFlightRepositoryPort {
    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private FlightAdapter flightAdapter;

    public MySQLAdapter(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Override
    public void saveFlight(Flight flight) {
        FlightEntity flightEntity = FlightAdapter.toEntity(flight);
        flightRepository.save(flightEntity);
    }
    @Override
    public void updateFlight(Flight flight) {
        FlightEntity  flightFound= flightRepository.findById(flight.getIdentity().getValue()).orElseThrow(()-> new RuntimeException("Flight not found"));
        flightFound.setFlightNumber(flight.getFlightNumber().getValue());
        flightFound.setDepartureTime(flight.getDepartureTime().getValue());
        flightFound.setArrivalTime(flight.getArrivalTime().getValue());
        flightFound.setStatus(flight.getStatusFlight().getValue());
        flightFound.setRouteId(flight.getRouteId().getValue());
        flightFound.setPrice(new PriceEntity(flight.getPrices().getPriceStandard()));
        flightRepository.save(flightFound);
    }
    @Override
    public List<FlightListResponse> findAll() {
        return flightRepository.findAll().stream().map(FlightAdapter::toResponse).collect(Collectors.toList());
    }

    @Override
    public FlightListResponse findById(String aggregateId) {
        FlightEntity flight = flightRepository.findById(aggregateId).orElseThrow(() -> new RuntimeException("Flight not found"));
        return FlightAdapter.toResponse(flight);
    }

}
