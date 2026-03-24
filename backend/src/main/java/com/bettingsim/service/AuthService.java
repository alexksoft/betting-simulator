package com.bettingsim.service;

import com.bettingsim.dto.AuthResponse;
import com.bettingsim.dto.LoginRequest;
import com.bettingsim.dto.RegisterRequest;
import com.bettingsim.exception.AppException;
import com.bettingsim.model.BankrollHistory;
import com.bettingsim.model.User;
import com.bettingsim.repository.BankrollHistoryRepository;
import com.bettingsim.repository.UserRepository;
import com.bettingsim.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BankrollHistoryRepository bankrollHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${app.initial.bankroll}") private BigDecimal initialBankroll;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new AppException("Email already registered", HttpStatus.CONFLICT);
        }
        String userId = UUID.randomUUID().toString();
        User user = User.create(userId, req.getEmail(), req.getUsername(),
                passwordEncoder.encode(req.getPassword()), initialBankroll);
        userRepository.save(user);

        BankrollHistory initial = new BankrollHistory();
        initial.setUserId(userId);
        initial.setTimestamp(Instant.now().toString());
        initial.setBalance(initialBankroll);
        initial.setReason("INITIAL_DEPOSIT");
        bankrollHistoryRepository.save(initial);

        String token = jwtUtil.generateToken(userId, req.getEmail());
        return new AuthResponse(token, userId, req.getUsername(), req.getEmail(), initialBankroll);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new AppException("Invalid credentials", HttpStatus.UNAUTHORIZED));
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new AppException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
        String token = jwtUtil.generateToken(user.getUserId(), user.getEmail());
        return new AuthResponse(token, user.getUserId(), user.getUsername(), user.getEmail(), user.getBankroll());
    }
}
