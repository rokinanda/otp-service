package com.jap.microservice.otpservice.feignClient;
import com.jap.microservice.otpservice.dto.DataDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "account")
public interface ACCOUNTClient {
    @PostMapping("/account/updateamount")
    ResponseEntity<?> tes2(@RequestBody DataDto dataDto);

}
