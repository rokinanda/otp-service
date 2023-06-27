package com.jap.microservice.otpservice.service;

import com.jap.microservice.otpservice.db.entity.TempOTP;
import com.jap.microservice.otpservice.db.repository.TempOTPRepository;
import com.jap.microservice.otpservice.dto.EmailDto;
import com.jap.microservice.otpservice.dto.RegisterCheckDto;
import com.jap.microservice.otpservice.dto.RegisterVerificationDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Random;

@Log4j2
@Service
public class OTPService {
    private final TempOTPRepository tempOTPRepository;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;

    @Autowired
    public OTPService(TempOTPRepository tempOTPRepository, RedisTemplate redisTemplate, ChannelTopic channelTopic) {
        this.tempOTPRepository = tempOTPRepository;
        this.redisTemplate = redisTemplate;
        this.channelTopic = channelTopic;
    }

    public void requestOTP(RegisterCheckDto registerCheckDto) {
        // check OTP Redis
        TempOTP tempOTPbyEmail = tempOTPRepository.getFirstByEmail(registerCheckDto.getEmail());
        if (tempOTPbyEmail != null) {
            tempOTPRepository.delete(tempOTPbyEmail);
        }
        // generate random number / OTP
        String randomOTP = generateOTP();
        // save to Redis
        TempOTP tempOTP = new TempOTP();
        tempOTP.setEmail(registerCheckDto.getEmail());
        tempOTP.setOtp(randomOTP);
        tempOTPRepository.save(tempOTP);

        //send message broker
        sendEmail(registerCheckDto.getEmail(), "Kode Verifikasi Anda Adalah: " + randomOTP);
    }

    private void sendEmail(String to, String body) {
        EmailDto emailDto = new EmailDto();
        emailDto.setTo(to);
        emailDto.setSubject("kode verifikasi");
        emailDto.setBody(body);
        redisTemplate.convertAndSend(channelTopic.getTopic(), emailDto);
        System.out.println("kelar");
    }

    private String generateOTP() {
        return new DecimalFormat("0000").format(new Random().nextInt(9999));
    }

    public ResponseEntity<?> verificationOTP(RegisterVerificationDto registerVerificationDto) {
        // check by email
        TempOTP tempOTPbyEmail = tempOTPRepository.getFirstByEmail(registerVerificationDto.getEmail());
        if(tempOTPbyEmail == null) return ResponseEntity.notFound().build();

        // verification / validasi otp
        if(!tempOTPbyEmail.getOtp().equals(registerVerificationDto.getOtp())) return ResponseEntity.unprocessableEntity().build();

        return ResponseEntity.ok().build();
    }
}
