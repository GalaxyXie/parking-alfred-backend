package com.alfred.parkingalfred.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alfred.parkingalfred.dto.CreateOrderDto;
import com.alfred.parkingalfred.entity.Order;
import com.alfred.parkingalfred.entity.ParkingLot;
import com.alfred.parkingalfred.enums.OrderStatusEnum;
import com.alfred.parkingalfred.form.ParkingLotForm;
import com.alfred.parkingalfred.service.ParkingLotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ParkingLotControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private ParkingLotService parkingLotService;
  @Test
  public void should_return_parkingLot_when_add_new_parkingLot() throws Exception {
    ParkingLotForm parkingLotForm = new ParkingLotForm();
    parkingLotForm.setCapacity(100);
    parkingLotForm.setName("lot1");
    parkingLotForm.setOccupied(99);

    ParkingLot parkingLotExpected = new ParkingLot();
    BeanUtils.copyProperties(parkingLotForm,parkingLotExpected);
    when(parkingLotService.createParkingLot(parkingLotForm)).thenReturn(parkingLotExpected);

    mockMvc.perform(post("/parking-lots")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(parkingLotForm))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  public void should_return_parkingLot_and_totalCount_when_call_getAllParkingLots_API()
      throws Exception {
    int page=1,size=10;
    List<ParkingLot> parkingLotList = new ArrayList<ParkingLot>(){
      {
        add(new ParkingLot());
        add(new ParkingLot());
      }
    };
    when(parkingLotService.getAllParkingLotsByPageAndSize(page,size)).thenReturn(parkingLotList);
    when(parkingLotService.getParkingLotCount()).thenReturn(2);
    mockMvc.perform(get("/parking-lots"))
        .andExpect(status().isOk());
  }
}