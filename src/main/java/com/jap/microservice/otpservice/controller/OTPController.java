package com.jap.microservice.otpservice.controller;

import com.jap.microservice.otpservice.dto.DataDto;
import com.jap.microservice.otpservice.dto.RegisterCheckDto;
import com.jap.microservice.otpservice.dto.RegisterVerificationDto;
import com.jap.microservice.otpservice.feignClient.ACCOUNTClient;
import com.jap.microservice.otpservice.service.OTPService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
public class OTPController {
    private final OTPService otpService;
    private final Environment environment;

    private final ACCOUNTClient accountClient;

//    @Value("${db.string}")
//    private String dbName;

    @Autowired
    public OTPController(OTPService otpService, Environment environment, ACCOUNTClient accountClient) {
        this.otpService = otpService;
        this.environment = environment;
        this.accountClient = accountClient;
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestOTP(@RequestBody RegisterCheckDto registerCheckDto) {
        log.debug("request OTP {}", registerCheckDto);
        otpService.requestOTP(registerCheckDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test-loadBalancer")
    public String testLoadBalancer() {
        String port = environment.getProperty("local.server.port");
        log.debug("port : {}", port);
//        return "ok with port: " + port;
        return "tes ngrok";
    }

    @PostMapping("/verification")
    public ResponseEntity<?> verificationOTP(@RequestBody RegisterVerificationDto registerVerificationDto) {
        return otpService.verificationOTP(registerVerificationDto);
    }

//    @GetMapping("/test-profile")
//    public String testProfile() {
//        return dbName;
//    }

    @PostMapping("/testotp")
    public ResponseEntity<?> testaja(@RequestBody DataDto dataDto) {
        accountClient.tes2(dataDto);
        return ResponseEntity.ok().build();
    }
}
