package com.alten.booking.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.alten.booking.domain.StatusEnum;
import com.alten.booking.dto.ReservationCreateDto;
import com.alten.booking.dto.ReservationDto;
import com.alten.booking.exceptions.MaxReserveAdvanceDaysException;
import com.alten.booking.exceptions.MaxReserveDaysException;
import com.alten.booking.exceptions.NotFoundException;
import com.alten.booking.service.ReservationService;

@SpringBootTest
@AutoConfigureMockMvc
public class ReservationControllerTests {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService service;

    private static ReservationCreateDto newReservationDto;
    private static ReservationCreateDto invalidReservationDto;
    private static ReservationDto existingReservation;
    

    @BeforeAll
    public static void setUp(){
        //Mock a new reservation
        newReservationDto =  new ReservationCreateDto("guest@gmail.com", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), 
        LocalDate.now().plusDays(3).format(DateTimeFormatter.ISO_LOCAL_DATE), 1L);
        //Mock an existing reservation
        existingReservation = new ReservationDto(1L,1L,"guest@gmail.com", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), 
        LocalDate.now().plusDays(3).format(DateTimeFormatter.ISO_LOCAL_DATE), StatusEnum.RESERVED.toString());
        //Mock an invalid reservation
        invalidReservationDto = new ReservationCreateDto(null, LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), 
        LocalDate.now().plusDays(3).format(DateTimeFormatter.ISO_LOCAL_DATE), null);
    }

    @Test
    @DisplayName("GET /reservation/1 - Success")
    public void givenReservationId_whenGetReservationById_thenReturnJsonArray() throws Exception{
        given(service.findDtoById(1L)).willReturn(existingReservation);
        
        mockMvc.perform(get("/reservation/1")
           .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.reservationId").value(1L))
           .andExpect(jsonPath("$.guestEmail").value("guest@gmail.com"))
           .andExpect(jsonPath("$.status").value(StatusEnum.RESERVED.toString()));
           
    }

    @Test
    @DisplayName("GET /reservation/2 - Not Found")
    public void givenUnknownReservationId_whenGetReservationById_thenReturnNotFound() throws Exception{
        given(service.findDtoById(2L)).willThrow(new NotFoundException("Reservation not found for id: 2"));

        mockMvc.perform(get("/reservation/{id}", 2L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /reservation -  Success")
    public void givenValidData_whenCreateReservation_thenReturnJsonArray() throws Exception{
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        given(service.validateAndCreateReservation(newReservationDto)).willReturn(existingReservation);

        mockMvc.perform(post("/reservation")
           .contentType(MediaType.APPLICATION_JSON)
           .content(ow.writeValueAsString(newReservationDto)) 
           .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isCreated())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.reservationId").value(1L)) 
           .andExpect(jsonPath("$.guestEmail").value("guest@gmail.com")) 
           .andExpect(jsonPath("$.status").value(StatusEnum.RESERVED.toString()));
    }

    @Test
    @DisplayName("POST /reservation - Bad Request - payload is not valid")
    public void givenInvalidData_whenCreateReservation_thenReturnBadRequest() throws Exception{
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        given(service.validateAndCreateReservation(invalidReservationDto)).willThrow(new RuntimeException("Invalid reservation payload"));

        mockMvc.perform(post("/reservation")
           .contentType(MediaType.APPLICATION_JSON)
           .content(ow.writeValueAsString(invalidReservationDto)) 
           .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /reservation - Bad Request - More than 3 days")
    public void givenReservationWithMoreThan3Days_whenCreateReservation_thenReturnBadRequest() throws Exception{
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        invalidReservationDto.setEndDate(LocalDate.now().plusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE));

        given(service.validateAndCreateReservation(invalidReservationDto)).willThrow(new MaxReserveDaysException("Your reservation can't be longer than 3 days"));

        mockMvc.perform(post("/reservation")
           .contentType(MediaType.APPLICATION_JSON)
           .content(ow.writeValueAsString(invalidReservationDto)) 
           .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /reservation - Bad Request - More than 30 days in advance")
    public void givenReservationWithMoreThan30DaysInAdvance_whenCreateReservation_thenReturnBadRequest() throws Exception{
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        invalidReservationDto.setStartDate(LocalDate.now().plusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE));
        invalidReservationDto.setEndDate(LocalDate.now().plusDays(33).format(DateTimeFormatter.ISO_LOCAL_DATE));

        given(service.validateAndCreateReservation(invalidReservationDto)).willThrow(new MaxReserveAdvanceDaysException("Your reservation can't be longer than 30 days in advance"));

        mockMvc.perform(post("/reservation")
           .contentType(MediaType.APPLICATION_JSON)
           .content(ow.writeValueAsString(invalidReservationDto)) 
           .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());
    }

    
}
