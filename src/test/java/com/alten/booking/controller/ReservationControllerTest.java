package com.alten.booking.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
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
import com.alten.booking.exceptions.NotFoundException;
import com.alten.booking.service.ReservationService;

@SpringBootTest
@AutoConfigureMockMvc
public class ReservationControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService service;

    @Test
    @DisplayName("GET /reservation/1 - Success")
    public void givenReservationId_whenGetReservationById_thenReturnJsonArray() throws Exception{
        ReservationDto reservationDto = new ReservationDto(1L,1L,"guest@gmail.com", 
                                                    LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), 
                                                    LocalDate.now().plusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE), 
                                                    StatusEnum.RESERVED.toString());
       
        given(service.findDtoById(1L)).willReturn(reservationDto);
        
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
        when(service.findDtoById(2L)).thenThrow(new NotFoundException("Reservation not found for id: 2"));
        mockMvc.perform(get("/reservation/{id}", 2L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /reservation -  Success")
    public void givenValidData_whenCreateReservation_thenReturnJsonArray() throws Exception{
        ReservationCreateDto reservationCreateDto = new ReservationCreateDto("guest@gmail.com", 
                                            LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), 
                                            LocalDate.now().plusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE), 
                                            1L);
        ReservationDto reservationDto = new ReservationDto(1L,1L,"guest@gmail.com", 
                                            LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), 
                                            LocalDate.now().plusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE), 
                                            StatusEnum.RESERVED.toString());
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        given(service.validateAndCreateReservation(reservationCreateDto)).willReturn(reservationDto);

        mockMvc.perform(post("/reservation")
           .contentType(MediaType.APPLICATION_JSON)
           .content(ow.writeValueAsString(reservationCreateDto)) 
           .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isCreated())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.reservationId").value(1L)) 
           .andExpect(jsonPath("$.guestEmail").value("guest@gmail.com")) 
           .andExpect(jsonPath("$.status").value(StatusEnum.RESERVED.toString()));
    }

    @Test
    @DisplayName("POST /reservation - Bad Request")
    public void givenInvalidData_whenCreateReservation_thenReturnBadRequest() throws Exception{
        ReservationCreateDto reservationCreateDto = new ReservationCreateDto(null, 
                                            LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), 
                                            LocalDate.now().plusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE),
                                            null);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        when(service.validateAndCreateReservation(reservationCreateDto)).thenThrow(new RuntimeException("Invalid reservation data"));

        mockMvc.perform(post("/reservation")
           .contentType(MediaType.APPLICATION_JSON)
           .content(ow.writeValueAsString(reservationCreateDto)) 
           .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());
    }

    
}
